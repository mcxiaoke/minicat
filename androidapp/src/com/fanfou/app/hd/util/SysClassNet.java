package com.fanfou.app.hd.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.27
 * 
 */
public class SysClassNet {

	private static final String SYS_CLASS_NET = "/sys/class/net/";

	private static final String CARRIER = "/carrier";

	private static final String RX_BYTES = "/statistics/rx_bytes";

	private static final String TX_BYTES = "/statistics/tx_bytes";

	/**
	 * Private constructor. This is an utility class.
	 */
	private SysClassNet() {
	}

	public static boolean isUp(String inter) {
		StringBuilder sb = new StringBuilder();
		sb.append(SYS_CLASS_NET).append(inter).append(CARRIER);
		return new File(sb.toString()).canRead();
	}

	public static long getRxBytes(String inter) throws IOException {
		return readLong(inter, RX_BYTES);
	}

	public static long getTxBytes(String inter) throws IOException {
		return readLong(inter, TX_BYTES);
	}

	private static RandomAccessFile getFile(String filename) throws IOException {
		File f = new File(filename);
		return new RandomAccessFile(f, "r");
	}

	private static long readLong(String inter, String file) {
		StringBuilder sb = new StringBuilder();
		sb.append(SYS_CLASS_NET).append(inter).append(file);
		RandomAccessFile raf = null;
		try {
			raf = getFile(sb.toString());
			return Long.valueOf(raf.readLine());
		} catch (Exception e) {
			return 0;
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
