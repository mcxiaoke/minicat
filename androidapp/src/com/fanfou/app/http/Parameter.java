package com.fanfou.app.http;

import java.io.File;
import org.apache.http.NameValuePair;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.04
 * @version 2.0 2011.11.03
 * 
 */
public final class Parameter implements NameValuePair,
		Comparable<Parameter> {
	private String name = null;
	private String value = null;
	private File file = null;

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Parameter(String name, int value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public Parameter(String name, long value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public Parameter(String name, double value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public Parameter(String name, boolean value) {
		this.name = name;
		this.value = String.valueOf(value);
	}

	public Parameter(String name, File file) {
		assert (file != null);
		this.name = name;
		this.file = file;
		this.value = file.getName();
	}

	public Parameter(NameValuePair pair) {
		this.name = pair.getName();
		this.value = pair.getValue();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	public File getFile() {
		return file;
	}

	public boolean isFile() {
		return file != null;
	}

	@Override
	public int compareTo(Parameter that) {
		int compared;
		compared = name.compareTo(that.getName());
		if (0 == compared) {
			compared = value.compareTo(that.getValue());
		}
		return compared;
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (file != null ? file.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "[" + name + ":" + value + "]";
	}
}
