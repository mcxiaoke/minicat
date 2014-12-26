package com.mcxiaoke.minicat.dao.model;

import java.util.Date;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:06:19
 */
public class RateLimitStatus {
    private int remainingHits;
    private int hourlyLimit;
    private int resetTimeInSeconds;
    private Date resetTime;

    public int getRemainingHits() {
        return remainingHits;
    }

    public int getHourlyLimit() {
        return hourlyLimit;
    }

    public int getResetTimeInSeconds() {
        return resetTimeInSeconds;
    }

    public Date getResetTime() {
        return resetTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RateLimitStatus{remainingHits:");
        sb.append(remainingHits);
        sb.append(";hourlyLimit:");
        sb.append(hourlyLimit);
        sb.append(";resetTimeInSeconds:");
        sb.append(resetTimeInSeconds);
        sb.append(";resetTime:");
        sb.append(resetTime);
        sb.append("}");
        return sb.toString();
    }
}
