package com.charlag.tuta.notifications;

import com.charlag.tuta.BuildConfig;

import java.net.HttpURLConnection;

public class NetworkUtils {

	private static final int SYS_MODEL_VERSION = 55;

	public static void addCommonHeaders(HttpURLConnection urlConnection) {
		urlConnection.setRequestProperty("v", Integer.toString(SYS_MODEL_VERSION));
		// Not send it for now because our version doesn't match anyway
//		urlConnection.setRequestProperty("cv", BuildConfig.VERSION_NAME);
	}
}
