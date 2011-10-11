package com.fanfou.app.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * Exception report delivery via email and user interaction. Avoids giving an
 * app the permission to access the Internet.
 * 
 * @author Ryan Fischbach <br>
 *         Blackmoon Info Tech Services<br>
 * 
 *         Source has been released to the public as is and without any
 *         warranty.
 */
public class ReportExceptionHandler implements UncaughtExceptionHandler,
		Runnable {
	public static final String ExceptionReportFilename = "postmortem.trace";

	private static final String MSG_SUBJECT_TAG = "Exception Report"; // "app label + this tag"
																		// =
																		// email
																		// subject
	private static final String MSG_SENDTO = "support@mysite.com"; // email will
																	// be sent
																	// to this
																	// account
	// the following may be something you wish to consider localizing
	private static final String MSG_BODY = "Please help by sending this email. "
			+ "No personal information is being sent (you can check by reading the rest of the email).";

	private Thread.UncaughtExceptionHandler mDefaultUEH;
	private Activity mAct = null;

	public ReportExceptionHandler(Activity aAct) {
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		mAct = aAct;
	}

	/**
	 * Call this method after creation to start protecting all code thereafter.
	 */
	public void initialize() {
		if (mAct == null)
			throw new NullPointerException();
		sendDebugReportToAuthor(); // in case a previous error did not get sent
									// to the email app
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * Call this method at the end of the protected code, usually in {@link
	 * finalize()}.
	 */
	public void restoreOriginalHandler() {
		if (Thread.getDefaultUncaughtExceptionHandler().equals(this))
			Thread.setDefaultUncaughtExceptionHandler(mDefaultUEH);
	}

	@Override
	protected void finalize() throws Throwable {
		restoreOriginalHandler();
		super.finalize();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		submit(e);
		// do not forget to pass this exception through up the chain
		bubbleUncaughtException(t, e);
	}

	/**
	 * Send the Exception up the chain, skipping other handlers of this type so
	 * only 1 report is sent.
	 * 
	 * @param t
	 *            - thread object
	 * @param e
	 *            - exception being handled
	 */
	protected void bubbleUncaughtException(Thread t, Throwable e) {
		if (mDefaultUEH != null) {
			if (mDefaultUEH instanceof ReportExceptionHandler)
				((ReportExceptionHandler) mDefaultUEH).bubbleUncaughtException(
						t, e);
			else
				mDefaultUEH.uncaughtException(t, e);
		}
	}

	/**
	 * Return a string containing the device environment.
	 * 
	 * @return Returns a string with the device info used for debugging.
	 */
	public String getDeviceEnvironment() {
		// app environment
		PackageManager pm = mAct.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mAct.getPackageName(), 0);
		} catch (NameNotFoundException nnfe) {
			// doubt this will ever run since we want info about our own package
			pi = new PackageInfo();
			pi.versionName = "unknown";
			pi.versionCode = 69;
		}
		Date theDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_zzz");
		String s = "-------- Environment --------\n";
		s += "Time\t= " + sdf.format(theDate) + "\n";
		s += "Device\t= " + Build.FINGERPRINT + "\n";
		try {
			Field theMfrField = Build.class.getField("MANUFACTURER");
			s += "Make\t=" + theMfrField.get(null) + "\n";
		} catch (SecurityException e) {
		} catch (NoSuchFieldException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		}
		s += "Model\t= " + Build.MODEL + "\n";
		s += "Product\t= " + Build.PRODUCT + "\n";
		s += "App\t\t= " + mAct.getPackageName() + ", version "
				+ pi.versionName + " (build " + pi.versionCode + ")\n";
		s += "Locale\t= "
				+ mAct.getResources().getConfiguration().locale
						.getDisplayName() + "\n";
		s += "Res\t\t= " + mAct.getResources().getDisplayMetrics().toString()
				+ "\n"; // toString() useful in Android 1.6+
		s += "-----------------------------\n\n";
		return s;
	}

	/**
	 * Return the application's friendly name.
	 * 
	 * @return Returns the application name as defined by the android:name
	 *         attribute.
	 */
	public CharSequence getAppName() {
		PackageManager pm = mAct.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mAct.getPackageName(), 0);
			return pi.applicationInfo.loadLabel(pm);
		} catch (NameNotFoundException nnfe) {
			// doubt this will ever run since we want info about our own package
			return mAct.getPackageName();
		}
	}

	/**
	 * If subactivities create their own report handler, report all Activities
	 * as a trace list. A separate line is included if a calling
	 * activity/package is detected with the Intent it supplied.
	 * 
	 * @param aTrace
	 *            - pass in null to force a new list to be created
	 * @return Returns the list of Activities in the handler chain.
	 */
	public LinkedList<CharSequence> getActivityTrace(
			LinkedList<CharSequence> aTrace) {
		if (aTrace == null)
			aTrace = new LinkedList<CharSequence>();
		aTrace.add(mAct.getLocalClassName() + " (" + mAct.getTitle() + ")");
		if (mAct.getCallingActivity() != null)
			aTrace.add(mAct.getCallingActivity().toString() + " ("
					+ mAct.getIntent().toString() + ")");
		else if (mAct.getCallingPackage() != null)
			aTrace.add(mAct.getCallingPackage().toString() + " ("
					+ mAct.getIntent().toString() + ")");
		if (mDefaultUEH != null
				&& mDefaultUEH instanceof ReportExceptionHandler)
			((ReportExceptionHandler) mDefaultUEH).getActivityTrace(aTrace);
		return aTrace;
	}

	/**
	 * Create a report based on the given exception.
	 * 
	 * @param aException
	 *            - exception to report on
	 * @return Returns a string with a lot of debug information.
	 */
	public String getDebugReport(Throwable aException) {
		NumberFormat theFormatter = new DecimalFormat("#0.");
		String theErrReport = "";

		theErrReport += getAppName() + " generated the following exception:\n";
		theErrReport += aException.toString() + "\n\n";

		// activity stack trace
		List<CharSequence> theActivityTrace = getActivityTrace(null);
		if (theActivityTrace != null && theActivityTrace.size() > 0) {
			theErrReport += "--------- Activity Stack Trace ---------\n";
			for (int i = 0; i < theActivityTrace.size(); i++) {
				theErrReport += theFormatter.format(i + 1) + "\t"
						+ theActivityTrace.get(i) + "\n";
			}// for
			theErrReport += "----------------------------------------\n\n";
		}

		if (aException != null) {
			// instruction stack trace
			StackTraceElement[] theStackTrace = aException.getStackTrace();
			if (theStackTrace.length > 0) {
				theErrReport += "--------- Instruction Stack trace ---------\n";
				for (int i = 0; i < theStackTrace.length; i++) {
					theErrReport += theFormatter.format(i + 1) + "\t"
							+ theStackTrace[i].toString() + "\n";
				}// for
				theErrReport += "-------------------------------------------\n\n";
			}

			// if the exception was thrown in a background thread inside
			// AsyncTask, then the actual exception can be found with getCause
			Throwable theCause = aException.getCause();
			if (theCause != null) {
				theErrReport += "----------- Cause -----------\n";
				theErrReport += theCause.toString() + "\n\n";
				theStackTrace = theCause.getStackTrace();
				for (int i = 0; i < theStackTrace.length; i++) {
					theErrReport += theFormatter.format(i + 1) + "\t"
							+ theStackTrace[i].toString() + "\n";
				}// for
				theErrReport += "-----------------------------\n\n";
			}
		}

		theErrReport += getDeviceEnvironment();
		theErrReport += "END REPORT.";
		return theErrReport;
	}

	/**
	 * Write the given debug report to the file system.
	 * 
	 * @param aReport
	 *            - the debug report
	 */
	protected void saveDebugReport(String aReport) {
		// save report to file
		try {
			FileOutputStream theFile = mAct.openFileOutput(
					ExceptionReportFilename, Context.MODE_PRIVATE);
			theFile.write(aReport.getBytes());
			theFile.close();
		} catch (IOException ioe) {
			// error during error report needs to be ignored, do not wish to
			// start infinite loop
		}
	}

	/**
	 * Read in saved debug report and send to email app.
	 */
	public void sendDebugReportToAuthor() {
		String theLine = "";
		String theTrace = "";
		try {
			BufferedReader theReader = new BufferedReader(
					new InputStreamReader(
							mAct.openFileInput(ExceptionReportFilename)));
			while ((theLine = theReader.readLine()) != null) {
				theTrace += theLine + "\n";
			}
			if (sendDebugReportToAuthor(theTrace)) {
				mAct.deleteFile(ExceptionReportFilename);
			}
		} catch (FileNotFoundException eFnf) {
			// nothing to do
		} catch (IOException eIo) {
			// not going to report
		}
	}

	/**
	 * Send the given report to email app.
	 * 
	 * @param aReport
	 *            - the debug report to send
	 * @return Returns true if the email app was launched regardless if the
	 *         email was sent.
	 */
	public Boolean sendDebugReportToAuthor(String aReport) {
		if (aReport != null) {
			Intent theIntent = new Intent(Intent.ACTION_SEND);
			String theSubject = getAppName() + " " + MSG_SUBJECT_TAG;
			String theBody = "\n" + MSG_BODY + "\n\n" + aReport + "\n\n";
			theIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { MSG_SENDTO });
			theIntent.putExtra(Intent.EXTRA_TEXT, theBody);
			theIntent.putExtra(Intent.EXTRA_SUBJECT, theSubject);
			theIntent.setType("message/rfc822");
			Boolean hasSendRecipients = (mAct.getPackageManager()
					.queryIntentActivities(theIntent, 0).size() > 0);
			if (hasSendRecipients) {
				mAct.startActivity(theIntent);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void run() {
		sendDebugReportToAuthor();
	}

	/**
	 * Create an exception report and start an email with the contents of the
	 * report.
	 * 
	 * @param e
	 *            - the exception
	 */
	public void submit(Throwable e) {
		String theErrReport = getDebugReport(e);
		saveDebugReport(theErrReport);
		// try to send file contents via email (need to do so via the UI thread)
		mAct.runOnUiThread(this);
	}
}
