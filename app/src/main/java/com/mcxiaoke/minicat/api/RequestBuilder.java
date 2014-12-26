package com.mcxiaoke.minicat.api;

import android.text.TextUtils;
import com.mcxiaoke.minicat.util.Assert;
import org.oauthsimple.http.OAuthRequest;
import org.oauthsimple.http.Parameter;
import org.oauthsimple.http.Verb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.3 2012.02.20
 */
final class RequestBuilder {
    private List<Parameter> params;
    private String url;
    private String fileName;
    private File file;
    private Verb verb;
    public RequestBuilder() {
        params = new ArrayList<Parameter>();
        verb = Verb.GET;
    }

    public static RequestBuilder newBuilder() {
        return new RequestBuilder();
    }

    public RequestBuilder url(String url) {
        Assert.notEmpty(url);
        this.url = url;
        return this;
    }

    public RequestBuilder verb(Verb verb) {
        if (verb != null) {
            this.verb = verb;
        }
        return this;
    }

    public RequestBuilder page(int page) {
        if (page > 0) {
            this.params.add(new Parameter("page", String.valueOf(page)));
        }
        return this;
    }

    public RequestBuilder paging(Paging p) {
        this.count(p.count);
        this.page(p.page);
        this.sinceId(p.sinceId);
        this.maxId(p.maxId);
        return this;
    }

    public RequestBuilder count(int count) {
        this.params.add(new Parameter("count", String.valueOf(count)));
        return this;
    }

    public RequestBuilder format(String format) {
        if (!TextUtils.isEmpty(format)) {
            this.params.add(new Parameter("format", format));
        }
        return this;
    }

    public RequestBuilder mode(String mode) {
        if (!TextUtils.isEmpty(mode)) {
            this.params.add(new Parameter("mode", mode));
        }
        return this;
    }

    public RequestBuilder id(String id) {
        if (!TextUtils.isEmpty(id)) {
            this.params.add(new Parameter("id", id));
        }
        return this;
    }

    public RequestBuilder status(String status) {
        Assert.notEmpty(status);
        this.params.add(new Parameter("status", status));
        return this;
    }

    public RequestBuilder location(String location) {
        if (!TextUtils.isEmpty(location)) {
            this.params.add(new Parameter("location", location));
        }
        return this;
    }

    public RequestBuilder sinceId(String sinceId) {
        if (!TextUtils.isEmpty(sinceId)) {
            this.params.add(new Parameter("since_id", sinceId));
        }
        return this;
    }

    public RequestBuilder maxId(String maxId) {
        if (!TextUtils.isEmpty(maxId)) {
            this.params.add(new Parameter("max_id", maxId));
        }
        return this;
    }

    public RequestBuilder param(String name, String value) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
            this.params.add(new Parameter(name, value));
        }
        return this;
    }

    public RequestBuilder file(String name, File value) {
        if (!TextUtils.isEmpty(name) && value != null) {
            this.fileName = name;
            this.file = value;
        }
        return this;
    }

    @Override
    public String toString() {
        final int maxLen = 5;
        StringBuilder builder = new StringBuilder();
        builder.append("RequestBuilder [params=");
        builder.append(params != null ? params.subList(0,
                Math.min(params.size(), maxLen)) : null);
        builder.append(", headers=");
        builder.append(", url=");
        builder.append(url);
        builder.append(", fileName=");
        builder.append(fileName);
        builder.append(", file=");
        builder.append(file);
        builder.append(", verb=");
        builder.append(verb);
        builder.append("]");
        return builder.toString();
    }

    public OAuthRequest build() {
        OAuthRequest request = new OAuthRequest(verb, url);
        if (Verb.GET == verb || Verb.DELETE == verb) {
            for (Parameter param : params) {
                request.addParameter(param);
            }
        } else {
            for (Parameter param : params) {
                request.addParameter(param);
            }
            if (fileName != null && file != null) {
                request.addBody(fileName, file);
            }
        }
        return request;
    }

}
