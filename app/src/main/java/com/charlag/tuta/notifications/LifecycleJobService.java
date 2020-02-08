package com.charlag.tuta.notifications;

import android.app.job.JobService;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

public abstract class LifecycleJobService extends JobService implements LifecycleOwner {

	private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

	@Override
	public void onCreate() {
		super.onCreate();
		lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		lifecycleRegistry.setCurrentState(Lifecycle.State.STARTED);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
		super.onDestroy();
	}

	@NonNull
	@Override
	public Lifecycle getLifecycle() {
		return lifecycleRegistry;
	}
}
