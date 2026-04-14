package com.jorexdeveloper.eh;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;

public class EHActivity extends Activity {

	private String eFullLog;
	private String eStackTrace;
	private String eActivityLog;
	private String[] eEmailAddresses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.EHTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eh_layout);
		getActionBar().setDisplayUseLogoEnabled(false);
		Intent intent = getIntent();
		if (intent != null) {
			eStackTrace = intent.getStringExtra(EH.EXTRA_STACK_TRACE);
			eActivityLog = intent.getStringExtra(EH.EXTRA_ACTIVITY_LOG);
			eEmailAddresses = intent.getStringArrayExtra(EH.EXTRA_EMAIL_ADDRESSES);
		}
		TextView textView = findViewById(R.id.activity_eh_layout_details_text_view_0);
		textView.setText(getMetaInfo());
		textView = findViewById(R.id.activity_eh_layout_details_text_view_1);
		textView.setText(eActivityLog == null ? "" : eActivityLog);
		textView = findViewById(R.id.activity_eh_layout_details_text_view_2);
		textView.setText(eStackTrace == null ? "" : eStackTrace);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(R.string.activity_eh_options_menu_copy);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem p1) {
					copyLogToClipboard();
					return true;
				}
			});
		item = menu.add(R.string.activity_eh_options_menu_save);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem p1) {
					saveLogToFile();
					return true;
				}
			});
		item = menu.add(R.string.activity_eh_options_menu_share);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem p1) {
					shareLog();
					return true;
				}
			});
		item = menu.add(R.string.activity_eh_options_menu_exit);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem p1) {
					onBackPressed();
					return true;
				}
			});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		EH.closeApplication(this);
	}

	public void onSendErrorLogClick(View view) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setData(Uri.parse("mailto:"));
		intent.putExtra(Intent.EXTRA_EMAIL, eEmailAddresses);
		String packageName = getApplicationContext().getPackageName();
		String txt = " Crash log for " + packageName;
		intent.putExtra(Intent.EXTRA_TITLE, txt);
		intent.putExtra(Intent.EXTRA_SUBJECT, txt);
		intent.putExtra(Intent.EXTRA_TEXT, new StringBuffer(getString(R.string.activity_eh_email_message, packageName))
						.append("\n\n\n")
						.append(getFullLog())
						.toString());
		startActivity(Intent.createChooser(intent, "Send log to developers with..."));
	}

	private void copyLogToClipboard() {
		ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		if (manager != null) {
			manager.setPrimaryClip(ClipData.newPlainText(getApplicationContext().getPackageName() + " crash log", getFullLog()));
			Toast.makeText(this, "Log copied to clipboard!", Toast.LENGTH_SHORT).show();
		}
	}

	private File saveLogToFile() {
		File parent = Environment.getExternalStorageDirectory();
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && parent != null && parent.exists() && parent.canRead() && parent.canWrite()) {
				Context context = getApplicationContext();
				File file = new File(parent, "CrashLogs");
				file.mkdirs();
				file = new File(file, String.format(context.getPackageName() + "-%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.LOG", new Date()));
				try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
					writer.write(getFullLog());
				}
				Toast.makeText(context, "Log saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				return file;
			} else
				Toast.makeText(getApplicationContext(), "Log NOT saved! (storage access required)", Toast.LENGTH_SHORT).show();
		} catch (Throwable t) {
			Toast.makeText(getApplicationContext(), "Log NOT saved! " + t.getMessage(), Toast.LENGTH_LONG).show();
		}
		return null;
	}

	private void shareLog() {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			Context context = getApplicationContext();
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
			String pkg=getApplicationContext().getPackageName();
			intent.putExtra(Intent.EXTRA_SUBJECT, packageManager.getApplicationLabel(packageInfo.applicationInfo) + " crash log");
			intent.putExtra(Intent.EXTRA_TITLE, pkg + " crash log");
			intent.putExtra(Intent.EXTRA_TEXT, getFullLog());
			startActivity(Intent.createChooser(intent, "Share log with..."));
		} catch (Throwable t) {
			Toast.makeText(getApplicationContext(), "Failed to get app information! " + t.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private String getMetaInfo() {
		StringBuffer buffer = new StringBuffer();
		char delimeter = '\n';
		buffer
			.append("\tDEVICE INFORMATION")
			.append(delimeter)
			.append(delimeter)
			.append("Brand        : ")
			.append(Build.BRAND)
			.append(delimeter)
			.append("Device       : ")
			.append(Build.DEVICE)
			.append(delimeter)
			.append("Model        : ")
			.append(Build.MODEL)
			.append(delimeter)
			.append("Manufacturer : ")
			.append(Build.MANUFACTURER)
			.append(delimeter)
			.append("Product      : ")
			.append(Build.PRODUCT.replace('\n', ' '))
			.append(delimeter)
			.append("SDK          : ")
			.append(Build.VERSION.SDK)
			.append(delimeter)
			.append("Release      : ")
			.append(Build.VERSION.RELEASE)
			.append(delimeter)
			.append("CPU ABI      : ")
			.append(Build.CPU_ABI)
			.append(delimeter)
			.append("CPU ABI2     : ")
			.append(Build.CPU_ABI2)
			.append(delimeter)
			.append("Display      : ")
			.append(Build.DISPLAY)
			.append(delimeter)
			.append("Fingerprint  : ")
			.append(Build.FINGERPRINT)
			.append(delimeter)
			.append("Hardware     : ")
			.append(Build.HARDWARE)
			.append(delimeter)
			.append("Host         : ")
			.append(Build.HOST)
			.append(delimeter)
			.append("ID           : ")
			.append(Build.ID)
			.append(delimeter);
		Context context = getApplicationContext();
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
			buffer
				.append(delimeter)
				.append("\tAPP INFORMATION")
				.append(delimeter)
				.append(delimeter)
				.append("Name         : ")
				.append(packageManager.getApplicationLabel(packageInfo.applicationInfo))
				.append(delimeter)
				.append("Version Code : ")
				.append(packageInfo.versionCode)
				.append(delimeter)
				.append("Version Name : ")
				.append(packageInfo.versionName)
				.append(delimeter)
				.append("Package Name : ")
				.append(packageInfo.packageName)
				.append(delimeter)
				.append("Installed On : ")
				.append(new Date(packageInfo.firstInstallTime))
				.append(delimeter)
				.append("Updated On   : ")
				.append(new Date(packageInfo.lastUpdateTime))
				.append(delimeter)
				.append(delimeter)
				.append("Permissions")
				.append(delimeter)
				.append(delimeter);
			String[] permissions = packageInfo.requestedPermissions;
			if (permissions == null || permissions.length < 1)
				buffer
					.append("\tNONE")
					.append(delimeter);
			else
				for (String permission : permissions) {
					int isGranted = context.checkSelfPermission(permission);
					buffer
						.append('\t')
						.append(permission)
						.append(" : ")
						.append(isGranted == PackageManager.PERMISSION_GRANTED ? "GRANTED" : isGranted == PackageManager.PERMISSION_DENIED ? "DENIED" : "unknown")
						.append(delimeter);
				}
		} catch (Throwable t) {
			buffer
				.append("E: Failed to retrieve app info : ")
				.append(t)
				.append(delimeter);
		}
		buffer
			.append(delimeter)
			.append("Crashed On   : ")
			.append(new Date(EH.getLastCrash(context)));
		return buffer.toString();
	}

	private String getFullLog() {
		return eFullLog == null ? new StringBuffer()
			.append(getMetaInfo())
			.append("\n\n\tACTIVITY LOG")
			.append(eActivityLog)
			.append("\n\n\tSTACK TRACE\n\n")
			.append(eStackTrace)
			.toString() : eFullLog;
	}
}
