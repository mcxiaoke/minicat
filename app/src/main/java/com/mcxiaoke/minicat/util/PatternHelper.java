package com.mcxiaoke.minicat.util;

import java.util.regex.Pattern;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.util
 * User: mcxiaoke
 * Date: 13-5-19
 * Time: 下午4:12
 */
public class PatternHelper {
    private static final Pattern MENTION_FANFOU = Pattern.compile("@[\\p{Alnum}\\p{InCJKUnifiedIdeographs}-.]{1,12}");

    private static final Pattern TOPIC_SINA = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^#]]+#");
    private static final String RETWEET_SEPARATOR_FANFOU = "转";
    private static final String RETWEET_FORMAT_FANFOU = " %1$s%2$s %3$s";
    private static final Pattern URL_TWITTER = Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]");

    public static Pattern getMentionPattern() {
        return MENTION_FANFOU;
    }

    /**
     * 获取话题的匹配模式
     *
     * @return 话题的匹配模式
     */
    public static Pattern getTopicPattern() {
        return TOPIC_SINA;
    }

    /**
     * 获取链接匹配模式
     *
     * @return 链接的匹配模式
     */
    public static Pattern getUrlPattern() {
        return URL_TWITTER;
    }

    /**
     * 获取转发时的分隔符号
     *
     * @return 转发时的分隔符号，默认为"||"
     */
    public static String getRetweetSeparator() {
        return RETWEET_SEPARATOR_FANFOU;
    }

    public static String getRetweetFormat() {
        return RETWEET_FORMAT_FANFOU;
    }
}
