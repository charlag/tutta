package com.charlag.tuta.notifications.push;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.charlag.tuta.CryptoException;
import com.charlag.tuta.notifications.AndroidKeyStoreFacade;
import com.charlag.tuta.notifications.NotificationUtil;
import com.charlag.tuta.notifications.alarms.AlarmNotification;
import com.charlag.tuta.notifications.data.NotificationDatabase;
import com.charlag.tuta.notifications.data.PushIdentifierKey;
import com.charlag.tuta.notifications.data.User;

import java.security.KeyStoreException;
import java.security.UnrecoverableEntryException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class SseStorage {
    private static final String TAG = SseStorage.class.getSimpleName();
    private static final String LAST_PROCESSED_NOTIFICATION_ID = "lastProcessedNotificationId";
    private static final String LAST_MISSED_NOTIFICATION_CHECK_TIME = "'lastMissedNotificationCheckTime'";
    private static final String DEVICE_IDENTIFIER = "deviceIdentifier";
    private static final String SSE_ORIGIN = "sseOrigin";
    public static final String CONNECT_TIMEOUT_SEC = "connectTimeoutSec";

    private final NotificationDatabase db;
    private final AndroidKeyStoreFacade keyStoreFacade;

    @Inject
    public SseStorage(NotificationDatabase db, AndroidKeyStoreFacade keyStoreFacade) {
        this.db = db;
        this.keyStoreFacade = keyStoreFacade;
    }

    @Nullable
    public String getPushIdentifier() {
        return db.keyValueDao().getString(DEVICE_IDENTIFIER);
    }


    public void storePushIdentifier(String identifier, String sseOrigin) {
        db.keyValueDao().putString(DEVICE_IDENTIFIER, identifier);
        db.keyValueDao().putString(SSE_ORIGIN, sseOrigin);
    }

    public void clear() {
        setLastMissedNotificationCheckTime(null);

        db.userInfoDao().clear();
        db.getAlarmInfoDao().clear();
    }

    public void storePushIdentifierSessionKey(String userId, String pushIdentiferId,
                                              String pushIdentifierSessionKeyB64) throws KeyStoreException, CryptoException {
        byte[] deviceEncSessionKey = this.keyStoreFacade.encryptKey(NotificationUtil.base64ToBytes(pushIdentifierSessionKeyB64));
        this.db.userInfoDao().insertPushIdentifierKey(new PushIdentifierKey(pushIdentiferId, deviceEncSessionKey));
        this.db.userInfoDao().insertUser(new User(userId));
    }


    public byte[] getPushIdentifierSessionKey(String pushIdentiferId) throws UnrecoverableEntryException, KeyStoreException, CryptoException {
        PushIdentifierKey userInfo = this.db.userInfoDao().getPushIdentifierKey(pushIdentiferId);
        if (userInfo == null) {
            return null;
        }
        return this.keyStoreFacade.decryptKey(userInfo.getDeviceEncPushIdentifierKey());
    }

    public LiveData<List<User>> observeUsers() {
        return this.db.userInfoDao().observeUsers();
    }

    public List<AlarmNotification> readAlarmNotifications() {
        return db.getAlarmInfoDao().getAlarmNotifications();
    }

    public void insertAlarmNotification(AlarmNotification alarmNotification) {
        db.getAlarmInfoDao().insertAlarmNotification(alarmNotification);
    }

    public void deleteAlarmNotification(String alarmIdetifier) {
        db.getAlarmInfoDao().deleteAlarmNotification(alarmIdetifier);
    }

    @Nullable
    @WorkerThread
    public String getLastProcessedNotificationId() {
        return db.keyValueDao().getString(LAST_PROCESSED_NOTIFICATION_ID);
    }

    @WorkerThread
    public void setLastProcessedNotificationId(String id) {
        db.keyValueDao().putString(LAST_PROCESSED_NOTIFICATION_ID, id);
    }

    @Nullable
    @WorkerThread
    public Date getLastMissedNotificationCheckTime() {
        long value = db.keyValueDao().getLong(LAST_MISSED_NOTIFICATION_CHECK_TIME);
        if (value == 0) {
            return null;
        }
        return new Date(value);
    }

    @WorkerThread
    public void setLastMissedNotificationCheckTime(@Nullable Date date) {
        db.keyValueDao().putLong(LAST_MISSED_NOTIFICATION_CHECK_TIME, date == null ? 0L : date.getTime());
    }

    public String getSseOrigin() {
        return this.db.keyValueDao().getString(SSE_ORIGIN);
    }

    public long getConnectTimeoutInSeconds() {
        return db.keyValueDao().getLong(CONNECT_TIMEOUT_SEC);
    }

    public void setConnectTimeoutInSeconds(long connectTimeout) {
        db.keyValueDao().putLong(CONNECT_TIMEOUT_SEC, connectTimeout);
    }

}

