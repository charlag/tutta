package com.charlag.tuta.notifications.push;

import android.app.Service;
import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.charlag.tuta.Cryptor;
import com.charlag.tuta.notifications.AndroidKeyStoreFacade;
import com.charlag.tuta.notifications.LifecycleJobService;
import com.charlag.tuta.notifications.NotificationUtil;
import com.charlag.tuta.notifications.alarms.AlarmNotificationsManager;
import com.charlag.tuta.notifications.alarms.SystemAlarmFacade;
import com.charlag.tuta.notifications.data.NotificationDatabase;
import com.charlag.tuta.notifications.data.SseInfo;
import com.charlag.tuta.notifications.data.User;

import java.util.HashSet;
import java.util.Set;


public final class PushNotificationService extends LifecycleJobService {
    private static final String TAG = "PushNotificationService";


    private volatile JobParameters jobParameters;
    private LocalNotificationsFacade localNotificationsFacade;
    private AlarmNotificationsManager alarmNotificationsManager;
    private SseClient sseClient;

    public static Intent startIntent(Context context, String sender) {
        Intent intent = new Intent(context, PushNotificationService.class);
        intent.putExtra("sender", sender);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NotificationDatabase appDatabase =
                NotificationDatabase.getDatabase(this, /*allowMainThreadAccess*/true);

        Cryptor cryptor = new Cryptor();
        AndroidKeyStoreFacade keyStoreFacade = new AndroidKeyStoreFacade(this);
        SseStorage sseStorage = new SseStorage(appDatabase, keyStoreFacade);

        alarmNotificationsManager = new AlarmNotificationsManager(keyStoreFacade, sseStorage,
                cryptor, new SystemAlarmFacade(this));
        localNotificationsFacade = new LocalNotificationsFacade(this);
        TutanotaNotificationsHandler tutanotaNotificationsHandler = new TutanotaNotificationsHandler(localNotificationsFacade, sseStorage,
                alarmNotificationsManager);

        alarmNotificationsManager.reScheduleAlarms();

        sseClient = new SseClient(cryptor, sseStorage, new NetworkObserver(this, this), new SseClient.SseListener() {
            @Override
            public boolean onStartingConnection() {
                return tutanotaNotificationsHandler.onConnect();
            }

            @Override
            public void onMessage(String data, SseInfo sseInfo) {
                if ("notification".equals(data)) {
                    tutanotaNotificationsHandler.onNewNotificationAvailable(sseInfo);
                }
                removeBackgroundServiceNotification();
            }

            @Override
            public void onConnectionEstablished() {
                removeBackgroundServiceNotification();
                // After establishing connection we finish in some time.
                scheduleJobFinish();
            }

            @Override
            public void onNotAuthorized() {
                tutanotaNotificationsHandler.onNotAuthorized();
                removeBackgroundServiceNotification();
                finishJobIfNeeded();
            }

            @Override
            public void onTooManyReconnectionAttempts() {
                removeBackgroundServiceNotification();
                finishJobIfNeeded();
            }
        });

        // TODO change back
        sseStorage.observeUsers().observeForever( userInfos -> {
            Log.d(TAG, "sse storage updated " + userInfos.size());
            Set<String> userIds = new HashSet<>();
            for (User userInfo : userInfos) {
                userIds.add(userInfo.getUserId());
            }
            if (userIds.isEmpty()) {
                sseClient.stopConnection();
                removeBackgroundServiceNotification();
            } else {
                sseClient.restartConnectionIfNeeded(new SseInfo(sseStorage.getPushIdentifier(), userIds, sseStorage.getSseOrigin()));
            }
        });

        if (NotificationUtil.isAtLeastOreo()) {
            localNotificationsFacade.createNotificationChannels();
            Log.d(TAG, "Starting foreground");
            startForeground(1, localNotificationsFacade.makeConnectionNotification());
        }
    }

    private void removeBackgroundServiceNotification() {
        Log.d(TAG, "Stopping foregroud");
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Received onStartCommand, sender: " + (intent == null ? null : intent.getStringExtra("sender")));

//        if (intent != null && intent.hasExtra(LocalNotificationsFacade.NOTIFICATION_DISMISSED_ADDR_EXTRA)) {
//            ArrayList<String> dissmissAddrs =
//                    intent.getStringArrayListExtra(LocalNotificationsFacade.NOTIFICATION_DISMISSED_ADDR_EXTRA);
//            localNotificationsFacade.notificationDismissed(dissmissAddrs, intent.getBooleanExtra(MainActivity.IS_SUMMARY_EXTRA, false));
//            return START_STICKY;
//        }
        return Service.START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        jobParameters = params;
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "The job is finished");
        return true;
    }

    private void scheduleJobFinish() {
        if (jobParameters != null) {
            new Thread(() -> {
                Log.d(TAG, "Scheduling jobFinished");
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException ignored) {
                }
                Log.d(TAG, "Executing scheduled jobFinished");
                finishJobIfNeeded();
            }, "FinishJobThread");
        }
    }

    private void finishJobIfNeeded() {
        if (jobParameters != null) {
            jobFinished(jobParameters, true);
            jobParameters = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}