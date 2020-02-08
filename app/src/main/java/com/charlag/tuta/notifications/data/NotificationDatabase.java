package com.charlag.tuta.notifications.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.charlag.tuta.data.TutanotaConverters;
import com.charlag.tuta.notifications.alarms.AlarmNotification;

@Database(version = 1, entities = {KeyValue.class, PushIdentifierKey.class, AlarmNotification.class, User.class})
@TypeConverters(TutanotaConverters.class)
public abstract class NotificationDatabase extends RoomDatabase {
    public static NotificationDatabase getDatabase(Context context, boolean allowMainThreadAccess) {
        Builder<NotificationDatabase> builder = Room.databaseBuilder(context, NotificationDatabase.class, "tuta-notifications-db")
                // This is important because we access db across processes!
                .enableMultiInstanceInvalidation();
        if (allowMainThreadAccess) {
            builder.allowMainThreadQueries();
        }
        return builder.build();
    }

    public abstract KeyValueDao keyValueDao();

    public abstract UserInfoDao userInfoDao();

    public abstract AlarmInfoDao getAlarmInfoDao();
}
