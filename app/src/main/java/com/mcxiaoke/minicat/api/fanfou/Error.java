package com.mcxiaoke.minicat.api.fanfou;

import com.google.gson.annotations.Expose;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 下午5:44
 */
public class Error {
    @Expose
    public String code;
    @Expose
    public String request;
    @Expose
    public String error;
}
