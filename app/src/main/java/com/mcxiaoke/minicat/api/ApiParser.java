/**
 *
 */
package com.mcxiaoke.minicat.api;

import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.Search;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午11:54:58
 */
public interface ApiParser {

    public void setAccount(String account);

    public List<UserModel> users(String response, int type, String owner) throws ApiException;

    public UserModel user(String response, int type, String owner) throws ApiException;

    public List<StatusModel> timeline(String response, int type, String owner) throws ApiException;

    public StatusModel status(String response, int type, String owner) throws ApiException;

    public List<DirectMessageModel> directMessageConversation(String response,
                                                              String userId) throws ApiException;

    public List<DirectMessageModel> directMessagesConversationList(String response) throws ApiException;

    public List<DirectMessageModel> directMessagesInBox(String response) throws ApiException;

    public List<DirectMessageModel> directMessagesOutBox(String response) throws ApiException;


    public DirectMessageModel directMessage(String response, int type) throws ApiException;

    public List<Search> trends(String response) throws ApiException;

    public List<Search> savedSearches(String response) throws ApiException;

    public Search savedSearch(String response) throws ApiException;

    public List<String> strings(String response) throws ApiException;

}
