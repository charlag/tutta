package com.charlag.tuta.notifications.alarms;

import androidx.annotation.Nullable;

import com.charlag.tuta.CryptoException;
import com.charlag.tuta.Cryptor;
import com.charlag.tuta.notifications.NotificationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;


public final class RepeatRule {
    private final String frequency;
    private final String interval;
    private final String timeZone;
    @Nullable
    private String endType;
    @Nullable
    private String endValue;

    static RepeatRule fromJson(JSONObject jsonObject) throws JSONException {
        return new RepeatRule(
                jsonObject.getString("frequency"),
                jsonObject.getString("interval"),
                jsonObject.getString("timeZone"),
                jsonObject.getString("endType"),
                jsonObject.isNull("endValue") ? null : jsonObject.getString("endValue")
        );
    }

    public RepeatRule(String frequency, String interval, String timeZone, @Nullable String endType,
                      @Nullable String endValue) {
        this.frequency = frequency;
        this.interval = interval;
        this.timeZone = timeZone;
        this.endType = endType;
        this.endValue = endValue;
    }


    public RepeatPeriod getFrequencyDec(Cryptor crypto, byte[] sessionKey) throws CryptoException {
        long frequencyNumber = NotificationUtil.decryptNumber(frequency, crypto, sessionKey);
        return RepeatPeriod.get(frequencyNumber);
    }

    public int getIntervalDec(Cryptor crypto, byte[] sessionKey) throws CryptoException {
        return (int) NotificationUtil.decryptNumber(interval, crypto, sessionKey);
    }

    public TimeZone getTimeZoneDec(Cryptor crypto, byte[] sessionKey) throws CryptoException {
        String timeZoneString = NotificationUtil.decryptString(timeZone, crypto, sessionKey);
        return TimeZone.getTimeZone(timeZoneString);
    }

    @Nullable
    public EndType getEndTypeDec(Cryptor crypto, byte[] sesionKey) throws CryptoException {
        if (this.endType == null) {
            return null;
        }
        long endTypeNumber = NotificationUtil.decryptNumber(endType, crypto, sesionKey);
        return EndType.get(endTypeNumber);
    }

    public long getEndValueDec(Cryptor crypto, byte[] sessionKey) throws CryptoException {
        if (this.endValue == null) {
            return 0;
        }
        return NotificationUtil.decryptNumber(endValue, crypto, sessionKey);
    }

    public String getFrequency() {
        return frequency;
    }

    public String getInterval() {
        return interval;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @Nullable
    public String getEndType() {
        return endType;
    }

    @Nullable
    public String getEndValue() {
        return endValue;
    }
}
