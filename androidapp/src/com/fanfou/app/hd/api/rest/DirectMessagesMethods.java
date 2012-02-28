package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.DirectMessageModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.02
 *
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
