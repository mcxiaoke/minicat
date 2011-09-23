package com.fanfou.app.http;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;

import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.04
 * 
 */
public final class Parameter implements NameValuePair, Serializable,
		Comparable<Parameter> {
	private static final long serialVersionUID = -4374460503164258750L;
	private String name = null;
	private String value = null;
	private File file = null;
	private InputStream fileBody = null;

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public Parameter(String name, File file) {
		this.name = name;
		this.file = file;
	}

	public Parameter(String name, String fileName, InputStream fileBody) {
		this.name = name;
		this.file = new File(fileName);
		this.fileBody = fileBody;
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

	public InputStream getFileBody() {
		return fileBody;
	}

	public boolean isFile() {
		return null != file;
	}

	public boolean hasFileBody() {
		return null != fileBody;
	}

	public static boolean containsFile(Parameter[] params) {
		boolean containsFile = false;
		if (null == params) {
			return false;
		}
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public static boolean containsFile(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return false;
		}
		boolean containsFile = false;
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public static Parameter[] getParameterArray(String name, String value) {
		return new Parameter[] { new Parameter(name, value) };
	}

	public static Parameter[] getParameterArray(String name, int value) {
		return getParameterArray(name, String.valueOf(value));
	}

	public static Parameter[] getParameterArray(String name1, String value1,
			String name2, String value2) {
		return new Parameter[] { new Parameter(name1, value1),
				new Parameter(name2, value2) };
	}

	public static Parameter[] getParameterArray(String name1, int value1,
			String name2, int value2) {
		return getParameterArray(name1, String.valueOf(value1), name2,
				String.valueOf(value2));
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
		result = 31 * result + (fileBody != null ? fileBody.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Parameter{" + "name='" + name + '\'' + ", value='" + value
				+ '}';
	}

	public static String encodeForGet(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < params.size(); i++) {
			Parameter p = params.get(i);
			if (p.isFile()) {
				throw new IllegalArgumentException("GET参数不能为文件");
			}
			if (i > 0) {
				buf.append("&");
			}
			buf.append(encode(p.name)).append("=")
					.append(encode(p.value));
		}
		return buf.toString();
	}

	public static MultipartEntity encodeMultipart(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			throw new IllegalArgumentException("POST参数不能为空");
		}
		MultipartEntity entity = new MultipartEntity();
		try {
			for (Parameter param : params) {
				if (param.isFile()) {
					entity.addPart(param.getName(),
							new FileBody(param.getFile()));
				} else {
					entity.addPart(
							param.getName(),
							new StringBody(param.getValue(), Charset
									.forName(HTTP.UTF_8)));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static HttpEntity encodeForPost(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			throw new IllegalArgumentException("POST参数不能为空");
		}
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return entity;
	}

    public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
}
