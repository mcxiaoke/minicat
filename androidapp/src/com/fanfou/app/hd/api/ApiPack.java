package com.fanfou.app.hd.api;

import java.io.File;

import android.text.TextUtils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.14
 * 
 */
public final class ApiPack {
	private static final String TAG = ApiPack.class.getSimpleName();
	
	public final String url;
	public final String id;
	public final String format;
	public final String mode;
	public final String text;
	public final String sinceId;
	public final String maxId;
	public final int count;
	public final int page;
	public final int type;

	private ApiPack(Builder builder) {
		this.url=builder.url;
		this.id=builder.id;
		this.format=builder.format;
		this.mode=builder.mode;
		this.text=builder.text;
		this.sinceId=builder.sinceId;
		this.maxId=builder.maxId;
		this.count=builder.count;
		this.page=builder.page;
		this.type=builder.type;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String url;
		private String id;
		private String format;
		private String mode;
		private String text;
		private String sinceId;
		private String maxId;
		private int count;
		private int page;
		private int type;

		public Builder() {
			this.format="html";
			this.mode="lite";
		}

		public Builder url(String url) {
			if(!TextUtils.isEmpty(url)){
				this.url=url;
			}
			return this;
		}
		
		public Builder type(int type) {
			this.type=type;
			return this;
		}
		
		public Builder count(int count) {
			this.count=count;
			return this;
		}

		public Builder page(int page) {
			this.page=page;
			return this;
		}

		public Builder format(String format) {
			if(!TextUtils.isEmpty(format)){
				this.format=format;
			}
			return this;
		}

		public Builder mode(String mode) {
			if(!TextUtils.isEmpty(mode)){
				this.mode=mode;
			}
			return this;
		}

		public Builder id(String id) {
			if(!TextUtils.isEmpty(id)){
				this.id=id;
			}
			return this;
		}

		public Builder file(File file) {
			return this;
		}

		public Builder text(String text) {
			if(!TextUtils.isEmpty(text)){
				this.text=text;
			}
			return this;
		}

		public Builder sinceId(String sinceId) {
			if(!TextUtils.isEmpty(sinceId)){
				this.sinceId=sinceId;
			}
			return this;
		}

		public Builder maxId(String maxId) {
			if(!TextUtils.isEmpty(maxId)){
				this.maxId=maxId;
			}
			return this;
		}

		public ApiPack build() {
			return new ApiPack(this);
		}

	}

}
