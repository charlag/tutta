package com.charlag.tuta.notifications.alarms;

import androidx.annotation.NonNull;

import com.charlag.tuta.CryptoException;
import com.charlag.tuta.Cryptor;
import com.charlag.tuta.notifications.NotificationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;


public final class AlarmInfo {
    @NonNull
    private final String trigger;
    @NonNull
    private final String identifier;

    public static AlarmInfo fromJson(JSONObject jsonObject) throws JSONException {
        String trigger = jsonObject.getString("trigger");
        String alarmIdentifier = jsonObject.getString("alarmIdentifier");
        return new AlarmInfo(trigger, alarmIdentifier);
    }

    public AlarmInfo(String trigger, String identifier) {
        this.trigger = trigger;
        this.identifier = Objects.requireNonNull(identifier);
    }

    public String getTriggerDec(Cryptor cryptor, byte[] sessionKey) throws CryptoException {
        byte[] decryptedBytes = NotificationUtil
                .decrypt(cryptor, this.trigger.getBytes(), sessionKey)
                .getData();
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public String getTrigger() {
        return trigger;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmInfo alarmInfo = (AlarmInfo) o;
        return identifier.equals(alarmInfo.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}


