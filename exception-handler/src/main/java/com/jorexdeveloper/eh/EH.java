package com.jorexdeveloper.eh;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class EH implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {

	static final String EXTRA_EMAIL_ADDRESSES = "email_address";
	static final String EXTRA_STACK_TRACE = "stack_trace";
	static final String EXTRA_ACTIVITY_LOG = "activity_log";

	private static final String EH_TAG = "EH";
	private static final String LAST_CRASH = "last_crash_time";
	private static final String SHARED_PREFS_FILE = "com.jorexdeveloper.eh_preferences";

	private boolean eEnabled;
	private int eMaxActivityLogs;
	private boolean eAddSuppressed;
	private int eStartedActivities;
	private boolean eIsInBackground;
	private String[] eEmailAddresses;
	private int eMaxStackTraceLength;
	private boolean eRunInBackground;
	private Application eApplication;
	private LinkedList<String> eActivityLog;
	private WeakReference<Activity> eCurrentActivity;
	private Thread.UncaughtExceptionHandler eOldHandler;

	private EH(Builder builder) {
		eEnabled = builder.bEnabled;
		eAddSuppressed = builder.bAddSuppressed;
		eEmailAddresses = builder.bEmailAddresses;
		eRunInBackground = builder.bRunInBackground;
		eMaxActivityLogs = builder.bMaxActivityLogs;
		eMaxStackTraceLength = builder.bMaxStackTraceSize;
		eApplication = (Application) builder.bContext.getApplicationContext();
		eActivityLog = new LinkedList<>();
		eCurrentActivity = new WeakReference<Activity>(null);
		installEH();
	}

	private void installEH() {
		try {
			if (eApplication == null)
				Log.e(EH_TAG, "EH NOT initialized! (null context).");
			else {
				eOldHandler = Thread.getDefaultUncaughtExceptionHandler();
				if (eOldHandler instanceof EH) {
					Log.i(EH_TAG, "Re-initializing EH.");
					eOldHandler = null;
				} else
					Log.i(EH_TAG, "I: Initializing EH.");
				eApplication.registerActivityLifecycleCallbacks(this);
				Thread.setDefaultUncaughtExceptionHandler(this);
				Log.i(EH_TAG, "EH initialized successfuly!");
			}
		} catch (Throwable t) {
			Log.e(EH_TAG, "EH NOT initialized!", t);
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable t) {
		if (eEnabled) {
			Log.w(EH_TAG, "EH recieved uncaught exception.", t);
			if (hasJustCrashed()) {
				Log.wtf(EH_TAG, "Application has just crashed. EH NOT launching to avoid restart loop.");
				if (!launchDUEH(thread, t))
					finishEH();
				return;
			} else {
				setLastCrash(System.currentTimeMillis());
				if (!eIsInBackground || eRunInBackground)
					try {
						Intent intent = new Intent(eApplication, EHActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(EXTRA_EMAIL_ADDRESSES, eEmailAddresses);
						intent.putExtra(EXTRA_STACK_TRACE, getStackTrace(t));
						StringBuffer buffer = new StringBuffer();
						Iterator<String> itr = eActivityLog.iterator();
						while (itr.hasNext())
							buffer.append(itr.next());
						intent.putExtra(EXTRA_ACTIVITY_LOG, buffer.toString());
						eApplication.startActivity(intent);
						Log.i(EH_TAG, "EH launched successfully.");
					} catch (Throwable th) {
						Log.wtf(EH_TAG, "EH failed to handle exception!", th);
						launchDUEH(thread, th);
					} finally {
						finishEH();
					}
				else {
					Log.w(EH_TAG, "EH background mode disabled!");
					if (!launchDUEH(thread, t))
						finishEH();
				}
			}
		} else {
			Log.e(EH_TAG, "EH disabled.");
			if (!launchDUEH(thread, t))
				finishEH();
		}
	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n    Activity '%s' created.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		eStartedActivities++;
		eIsInBackground = eStartedActivities == 0;
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n    Activity '%s' started.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n    Activity '%s' resumed.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivityPaused(Activity activity) {
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n    Activity '%s' paused.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivityStopped(Activity activity) {
		eStartedActivities--;
		eIsInBackground = eStartedActivities == 0;
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n    Activity '%s' stopped.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n Activity '%s' saved.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		if (!(activity instanceof EHActivity)) {
			eActivityLog.addLast(String.format("\n\nI: %s\n Activity '%s' destroyed.", new Date().toString(), activity.getClass().getName()));
			int size = eActivityLog.size();
			if (size > eMaxActivityLogs)
				eActivityLog.removeFirst();
		}
	}

	private boolean launchDUEH(Thread thread, Throwable th) {
		if (eOldHandler != null) {
			Log.w(EH_TAG, "EH Launching default exception handler.");
			eOldHandler.uncaughtException(thread, th);
			return true;
		} else
			Log.wtf(EH_TAG, "F: No default exception handler found!");
		return false;
	}

	private String getStackTrace(Throwable t) {
		StringBuffer buffer = new StringBuffer(t.toString());
		StackTraceElement[] elements = t.getStackTrace();
		int size = 0;
		for (StackTraceElement e : elements) {
			if (size >= eMaxStackTraceLength)
				break;
			buffer
				.append("\n\tat  ")
				.append(e.toString());
			size++;
		}
		if (eAddSuppressed)
			buffer
				.append("\nSuppressed : ")
				.append(Arrays.toString(t.getSuppressed()));
		t = t.getCause();
		while (t != null) {
			if (size >= eMaxStackTraceLength)
				break;
			buffer
				.append("\n\nCaused by : ")
				.append(t.toString());
			for (StackTraceElement e : t.getStackTrace()) {
				if (size >= eMaxStackTraceLength)
					break;
				buffer
					.append("\n\tat  ")
					.append(e.toString());
				size++;
			}
			if (eAddSuppressed)
				buffer
					.append("\nSuppressed : ")
					.append(Arrays.toString(t.getSuppressed()));
			t = t.getCause();
			size++;
		}
		return buffer
			.append(size >= eMaxStackTraceLength ? "\n\nE: Stack trace has been trimmed because it exceeds maximum size." : "")
			.toString();
	}

	private boolean hasJustCrashed() {
		long lastCrash = getLastCrash(eApplication);
		return lastCrash < 0 ? false : System.currentTimeMillis() - lastCrash < 5000;
	}

	private boolean setLastCrash(long time) {
		return eApplication.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE).edit().putLong(LAST_CRASH, time).commit();
	}

	private void finishEH() {
		Activity activity = eCurrentActivity.get();
		if (activity != null) {
			activity.finish();
			eCurrentActivity.clear();
		}
		killCurrentProcess();
	}

	private static void killCurrentProcess() {
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}

	static long getLastCrash(Context context) {
		return context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE).getLong(LAST_CRASH, -1);
	}

	static void closeApplication(Activity activity) {
		activity.finish();
		killCurrentProcess();
	}

	public static final class Builder {

		private Context bContext;
		private boolean bEnabled;
		private int bMaxActivityLogs;
		private int bMaxStackTraceSize;
		private boolean bAddSuppressed;
		private String[] bEmailAddresses;
		private boolean bRunInBackground;

		public Builder(Context appContext) {
			bEnabled = true;
			bAddSuppressed = true;
			bContext = appContext;
			bMaxActivityLogs = 100;
			bRunInBackground = true;
			bMaxStackTraceSize = 100;
		}

		public Builder enable(boolean enable) {
			bEnabled = enable;
			return this;
		}

		public Builder runInBackground(boolean runInBackground) {
			bRunInBackground = runInBackground;
			return this;
		}

		public Builder addSuppressed(boolean add) {
			bAddSuppressed = add;
			return this;
		}

		public Builder setMaxActivityLogs(int max) {
			bMaxActivityLogs = max < 0 ? 0 : max;
			return this;
		}

		public Builder setMaxStackTraceSize(int size) {
			bMaxStackTraceSize = size < 0 ? 0 : size;
			return this;
		}

		public Builder addEmailAddresses(String... emailAddresses) {
			if (bEmailAddresses == null)
				bEmailAddresses = new String[0];
			String[] tmp = new String[bEmailAddresses.length + (emailAddresses == null ? 0 : emailAddresses.length)];
			int i = 0;
			for (int j = 0; j < bEmailAddresses.length; j++, i++)
				tmp[i] = bEmailAddresses[j];
			for (int j = 0; j < emailAddresses.length; j++, i++)
				tmp[i] = emailAddresses[j];
			bEmailAddresses = tmp;
			return this;
		}

		public void init() {
			new EH(this);
		}
	}
}
