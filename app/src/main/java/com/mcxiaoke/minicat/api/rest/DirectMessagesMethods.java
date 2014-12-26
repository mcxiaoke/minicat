package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.02
 */
public interface DirectMessagesMethods {

    List<DirectMessageModel> getDirectMessagesInbox(Paging paging) throws ApiException;

    List<DirectMessageModel> getDirectMessagesOutbox(Paging paging) throws ApiException;

    List<DirectMessageModel> getConversationList(Paging paging) throws ApiException;

    List<DirectMessageModel> getConversation(String id, Paging paging) throws ApiException;

    //	DirectMessageModel showDirectMessage(String id) throws ApiException;
    DirectMessageModel deleteDirectMessage(String id) throws ApiException;

    DirectMessageModel createDirectmessage(String id, String text, String replyId) throws ApiException;

}
