package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.BitSet;
import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface FriendshipsMethods {

    public UserModel follow(String id) throws ApiException;

    public UserModel unfollow(String id) throws ApiException;

    public List<String> friendshipsRequests(Paging paging) throws ApiException;

    public UserModel acceptFriendshipsRequest(String id) throws ApiException;

    public UserModel denyFriendshipsRequest(String id) throws ApiException;

    public boolean isFriends(String userA, String userB) throws ApiException;

    public BitSet friendshipsShow(String source, String target) throws ApiException;

}
