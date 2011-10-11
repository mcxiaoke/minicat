package com.fanfou.app.util;

/**
 * Specifies all the different fields available in a install report.
 * 
 * @author mcxiaoke
 * @version 1.0 20110829
 * 
 */
public class StatsInfo {
	/**
	 * Application version code. This is the incremental integer version code
	 * used to differentiate versions on the android market. {@see
	 * PackageInfo#versionCode}
	 */
	public String APP_VERSION_CODE;
	/**
	 * Application version name. {@see PackageInfo#versionName}
	 */
	public String APP_VERSION_NAME;
	/**
	 * Device model name. {@see Build#MODEL}
	 */
	public String PHONE_MODEL;
	/**
	 * Device android version name. {@see VERSION#RELEASE}
	 */
	public String ANDROID_VERSION;
	/**
	 * Device brand (manufacturer or carrier). {@see Build#BRAND}
	 */
	public String BRAND;
	/**
	 * Device overall product code. {@see Build#PRODUCT}
	 */
	public String PRODUCT;
	/**
	 * Estimation of the total device memory size based on filesystem stats.
	 */
	public String TOTAL_MEM_SIZE;
	/**
	 * Device display specifications. {@see WindowManager#getDefaultDisplay()}
	 */
	public String DISPLAY;
	/**
	 * Device unique ID (IMEI). Requires READ_PHONE_STATE permission.
	 */
	public String DEVICE_ID;
	/**
	 * Installation unique ID. This identifier allow you to track a specific
	 * user application installation without using any personal data.
	 */
	public String INSTALLATION_ID;
	/**
	 * Fanfou Username
	 */
	public String USERNAME;
	/**
	 * install and login date;
	 */
	public String DATE;;
}
