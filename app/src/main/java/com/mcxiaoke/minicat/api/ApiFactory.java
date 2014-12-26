package com.mcxiaoke.minicat.api;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-27 上午11:42:44
 */
public final class ApiFactory {
    private static final int TWITTER = 0;
    private static final int FANFOU = 1;
    private static final int SINA = 2;
    private static final int TECENT = 3;
    private static final int NETEASE = 4;

    public static Api getDefaultApi() {
        return new FanFouApi();
    }

    public static ApiParser getDefaultParser() {
        return new FanFouParser();
    }

}
