package com.charlag.tuta.notifications.alarms;

import android.util.Log;

import androidx.annotation.Nullable;

import com.charlag.tuta.CryptoException;
import com.charlag.tuta.Cryptor;
import com.charlag.tuta.notifications.AndroidKeyStoreFacade;
import com.charlag.tuta.notifications.NotificationUtil;
import com.charlag.tuta.notifications.OperationType;
import com.charlag.tuta.notifications.push.SseStorage;

import java.security.KeyStoreException;
import java.security.UnrecoverableEntryException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;


public class AlarmNotificationsManager {
    private static final String TAG = "AlarmNotificationsMngr";
    private final AndroidKeyStoreFacade keyStoreFacade;
    private final SseStorage sseStorage;
    private final Cryptor crypto;
    private final SystemAlarmFacade systemAlarmFacade;
    private final PushKeyResolver pushKeyResolver;

    public AlarmNotificationsManager(AndroidKeyStoreFacade androidKeyStoreFacade,
                                     SseStorage sseStorage, Cryptor cryptor,
                                     SystemAlarmFacade systemAlarmFacade) {
        keyStoreFacade = androidKeyStoreFacade;
        this.sseStorage = sseStorage;
        this.crypto = cryptor;
        this.systemAlarmFacade = systemAlarmFacade;
        this.pushKeyResolver = new PushKeyResolver(keyStoreFacade, sseStorage);
    }

    public void reScheduleAlarms() {
        PushKeyResolver pushKeyResolver =
                new PushKeyResolver(keyStoreFacade, sseStorage);
        List<AlarmNotification> alarmInfos = sseStorage.readAlarmNotifications();
        for (AlarmNotification alarmNotification : alarmInfos) {
            byte[] sessionKey = this.resolveSessionKey(alarmNotification, pushKeyResolver);
            if (sessionKey != null) {
                this.schedule(alarmNotification, sessionKey);
            } else {
                Log.d(TAG, "Failed to resolve session key for saved alarm notification: " + alarmNotification);
            }
        }
    }

    public byte[] resolveSessionKey(AlarmNotification notification, PushKeyResolver pushKeyResolver) {
        AlarmNotification.NotificationSessionKey notificationSessionKey = notification.getNotificationSessionKey();
        if (notificationSessionKey == null) {
            return null;
        }
        try {


            byte[] pushIdentifierSessionKey = pushKeyResolver
                    .resolvePushSessionKey(notificationSessionKey.getPushIdentifier()
                            .getElementId().asString());
            if (pushIdentifierSessionKey != null) {

                byte[] encNotificationSessionKeyKey = NotificationUtil.base64ToBytes(
                        notificationSessionKey.getPushIdentifierSessionEncSessionKey());
                return NotificationUtil.decryptKey(crypto, encNotificationSessionKeyKey,
                        pushIdentifierSessionKey);
            }
        } catch (UnrecoverableEntryException | KeyStoreException | CryptoException e) {
            Log.w(TAG, "could not decrypt session key", e);
        }
        return null;
    }

    public void scheduleNewAlarms(List<AlarmNotification> alarmNotifications) {

        for (AlarmNotification alarmNotification : alarmNotifications) {
            if (alarmNotification.getOperation() == OperationType.CREATE) {
                byte[] sessionKey = this.resolveSessionKey(alarmNotification, pushKeyResolver);
                if (sessionKey == null) {
                    Log.d(TAG, "Failed to resolve session key for " + alarmNotification);
                    return;
                }
                this.schedule(alarmNotification, sessionKey);
                sseStorage.insertAlarmNotification(alarmNotification);
            } else {
                this.cancelScheduledAlarm(alarmNotification, pushKeyResolver);
                sseStorage.deleteAlarmNotification(alarmNotification.getAlarmInfo().getIdentifier());
            }
        }
    }

    /**
     * Deletes user alarms for a given user. If user is null then all scheduled alarms will be removed.
     */
    public void unscheduleAlarms(@Nullable String userId) {
        List<AlarmNotification> alarmNotifications = sseStorage.readAlarmNotifications();
        for (AlarmNotification alarmNotification : alarmNotifications) {
            if (userId == null || alarmNotification.getUser().equals(userId)) {
                this.cancelSavedAlarm(alarmNotification, pushKeyResolver);
                sseStorage.deleteAlarmNotification(alarmNotification.getAlarmInfo().getIdentifier());
            }
        }
    }

    private void schedule(AlarmNotification alarmNotification, byte[] sessionKey) {
        try {
            String trigger = alarmNotification.getAlarmInfo().getTriggerDec(crypto, sessionKey);
            AlarmTrigger alarmTrigger = AlarmTrigger.get(trigger);
            String summary = alarmNotification.getSummaryDec(crypto, sessionKey);
            String identifier = alarmNotification.getAlarmInfo().getIdentifier();
            Date eventStart = alarmNotification.getEventStartDec(crypto, sessionKey);

            if (alarmNotification.getRepeatRule() == null) {
                Date alarmTime = AlarmModel.calculateAlarmTime(eventStart, null, alarmTrigger);
                Date now = new Date();
                if (alarmTime.after(now)) {
                    systemAlarmFacade.scheduleAlarmOccurrenceWithSystem(alarmTime, 0, identifier, summary, eventStart, alarmNotification.getUser());
                } else {
                    Log.d(TAG, "Alarm " + identifier + " at " + alarmTime + " is before " + now + ", skipping");
                }
            } else {
                this.iterateAlarmOccurrences(alarmNotification, crypto, sessionKey, (alarmTime, occurrence, eventStartTime) ->
                        systemAlarmFacade.scheduleAlarmOccurrenceWithSystem(alarmTime, occurrence, identifier, summary, eventStartTime,
                                alarmNotification.getUser()));
            }
        } catch (CryptoException CryptoException) {
            Log.w(TAG, "Error when decrypting alarmNotificaiton", CryptoException);
        }
    }


    /**
     * Cancel scheduled alarm with the system
     *
     * @param alarmNotification may come from the server or may be a saved one
     */
    private void cancelScheduledAlarm(AlarmNotification alarmNotification,
                                      PushKeyResolver pushKeyResolver) {
        // The DELETE notification we receive from the server has only placeholder fields and no keys. We must use our saved alarm to cancel notifications.
        List<AlarmNotification> alarmNotifications = sseStorage.readAlarmNotifications();
        int indexOfExistingAlarm = alarmNotifications.indexOf(alarmNotification);
        if (indexOfExistingAlarm == -1) {
            Log.d(TAG, "Cancelling alarm " + alarmNotification.getAlarmInfo().getIdentifier());
            systemAlarmFacade.cancelAlarm(alarmNotification.getAlarmInfo().getIdentifier(), 0);
        } else {
            cancelSavedAlarm(alarmNotifications.get(indexOfExistingAlarm), pushKeyResolver);
        }
    }

    private void cancelSavedAlarm(AlarmNotification savedAlarmNotification, PushKeyResolver pushKeyResolver) {
        if (savedAlarmNotification.getRepeatRule() != null) {
            byte[] sessionKey = resolveSessionKey(savedAlarmNotification, pushKeyResolver);
            if (sessionKey == null) {
                Log.w(TAG, "Failed to resolve session key to cancel alarm " + savedAlarmNotification);
            } else {
                try {
                    this.iterateAlarmOccurrences(savedAlarmNotification, crypto, sessionKey, (alarmTime, occurrence, eventStartTime) -> {
                        Log.d(TAG, "Cancelling alarm " + savedAlarmNotification.getAlarmInfo().getIdentifier() + " # " + occurrence);
                        systemAlarmFacade.cancelAlarm(savedAlarmNotification.getAlarmInfo().getIdentifier(), occurrence);
                    });
                } catch (CryptoException CryptoException) {
                    Log.w(TAG, "Failed to decrypt notification to cancel alarm " + savedAlarmNotification, CryptoException);
                }
            }
        } else {
            Log.d(TAG, "Cancelling alarm " + savedAlarmNotification.getAlarmInfo().getIdentifier());
            systemAlarmFacade.cancelAlarm(savedAlarmNotification.getAlarmInfo().getIdentifier(), 0);
        }
    }


    private void iterateAlarmOccurrences(AlarmNotification alarmNotification, Cryptor cryptor,
                                         byte[] sessionKey,
                                         AlarmModel.AlarmIterationCallback callback
    ) throws CryptoException {
        RepeatRule repeatRule = Objects.requireNonNull(alarmNotification.getRepeatRule());
        TimeZone timeZone = repeatRule.getTimeZoneDec(cryptor, sessionKey);

        Date eventStart = alarmNotification.getEventStartDec(cryptor, sessionKey);
        Date eventEnd = alarmNotification.getEventEndDec(cryptor, sessionKey);
        RepeatPeriod frequency = repeatRule.getFrequencyDec(cryptor, sessionKey);
        int interval = repeatRule.getIntervalDec(cryptor, sessionKey);
        EndType endType = repeatRule.getEndTypeDec(cryptor, sessionKey);
        long endValue = repeatRule.getEndValueDec(cryptor, sessionKey);
        AlarmTrigger alarmTrigger = AlarmTrigger.get(
                alarmNotification.getAlarmInfo().getTriggerDec(cryptor, sessionKey));

        AlarmModel.iterateAlarmOccurrences(new Date(),
                timeZone, eventStart, eventEnd, frequency, interval, endType,
                endValue, alarmTrigger, TimeZone.getDefault(), callback);
    }


    private static class PushKeyResolver {
        private AndroidKeyStoreFacade keyStoreFacade;
        private final SseStorage sseStorage;
        private final Map<String, byte[]> pushIdentifierToResolvedSessionKey = new HashMap<>();

        private PushKeyResolver(AndroidKeyStoreFacade keyStoreFacade, SseStorage sseStorage) {
            this.keyStoreFacade = keyStoreFacade;
            this.sseStorage = sseStorage;
        }

        @Nullable
        byte[] resolvePushSessionKey(String pushIdentifierId) throws UnrecoverableEntryException,
                KeyStoreException, CryptoException {
            byte[] resolved = pushIdentifierToResolvedSessionKey.get(pushIdentifierId);
            if (resolved != null) {
                return resolved;
            } else {
                byte[] pushIdentifierSessionKey = sseStorage.getPushIdentifierSessionKey(pushIdentifierId);
                if (pushIdentifierSessionKey == null) {
                    return null;
                }
                pushIdentifierToResolvedSessionKey.put(pushIdentifierId, pushIdentifierSessionKey);
                return pushIdentifierSessionKey;
            }
        }
    }
}
