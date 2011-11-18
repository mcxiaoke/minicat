package com.fanfou.app.api;

import java.io.File;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.12
 * @version 1.1 2011.05.15
 * @version 1.2 2011.10.18
 * @version 1.3 2011.10.28
 * @version 1.4 2011.11.07
 * @version 1.5 2011.11.09
 * @version 1.6 2011.11.11
 * @version 2.0 2011.11.18
 * 
 */
public interface Api {

	// account verify
	// for basic auth key is username, value is password
	// for oauth key is accessToken, value is accessTokenSecret
	/**
	 * verify account
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws ApiException
	 */
	User verifyAccount(String mode) throws ApiException;

	/**
	 * update user profile
	 * 
	 * @param description
	 * @param name
	 *            realname
	 * @param location
	 * @param url
	 * @param email
	 *            account email, not recommand.
	 * @return
	 * @throws ApiException
	 */
	User updateProfile(String description, String name, String location,
			String url,String mode) throws ApiException;

	/**
	 * udpate user profile image
	 * 
	 * @param image
	 *            image file
	 * @return
	 * @throws ApiException
	 */
	User updateProfileImage(File image, String mode) throws ApiException;

	// public timeline
	// count -- status count
	/**
	 * public timeline
	 * 
	 * @param count
	 * @return
	 * @throws ApiException
	 */
	List<Status> pubicTimeline(int count, String format,String mode) throws ApiException;

	// friends timeline
	/**
	 * home timeline
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<Status> homeTimeline(int count, int page, String sinceId,
			String maxId, String format,String mode) throws ApiException;

	// user timeline
	/**
	 * user timeline
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<Status> userTimeline(int count, int page, String userId,
			String sinceId, String maxId, String format,String mode) throws ApiException;

	/**
	 * user timeline contains photos
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @param sinceId
	 * @param maxId
	 * @param isHtml
	 * @return
	 * @throws ApiException
	 */
	List<Status> photosTimeline(int count, int page, String userId,
			String sinceId, String maxId, String format,String mode) throws ApiException;

	// mention timeline
	/**
	 * mentions
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<Status> mentions(int count, int page, String sinceId, String maxId,
			String format,String mode) throws ApiException;

	// replies timeline
	/**
	 * replies
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<Status> replies(int count, int page, String userId, String sinceId,
			String maxId, String format,String mode) throws ApiException;

	/**
	 * favorites
	 * 
	 * @param count
	 * @param page
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<Status> favorites(int count, int page, String userId, String format,String mode)
			throws ApiException;
	
	/**
	 *  msg context timeline
	 * @param id status id
	 * @param isHtml
	 * @return
	 * @throws ApiException
	 */
	List<Status> contextTimeline(String id, String format,String mode) throws ApiException;

	/**
	 * action: favorite
	 * 
	 * @param statusId
	 * @return
	 * @throws ApiException
	 */
	Status statusFavorite(String statusId, String format,String mode) throws ApiException;

	/**
	 * action: unfavorite
	 * 
	 * @param statusId
	 * @return
	 * @throws ApiException
	 */
	Status statusUnfavorite(String statusId, String format,String mode) throws ApiException;

	/**
	 * show a status details
	 * 
	 * @param statusId
	 * @return
	 * @throws ApiException
	 */
	Status statusShow(String statusId,String format, String mode) throws ApiException;

	/**
	 * update a status
	 * 
	 * @param status
	 * @param inReplyToStatusId
	 * @param source
	 * @param location
	 * @param repostStatusId
	 * @return
	 * @throws ApiException
	 */
	Status statusUpdate(String status, String inReplyToStatusId, String source,
			String location, String repostStatusId,String format, String mode) throws ApiException;

	/**
	 * delete a status
	 * 
	 * @param statusId
	 * @return
	 * @throws ApiException
	 */
	Status statusDelete(String statusId, String format,String mode) throws ApiException;

	/**
	 * upload a photo
	 * 
	 * @param photo
	 * @param status
	 * @param source
	 * @param location
	 * @return
	 * @throws ApiException
	 */
	Status photoUpload(File photo, String status, String source, String location,String format, String mode)
			throws ApiException;

	/**
	 * search public timeline
	 * 
	 * @param keyword
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<Status> search(String keyword, String maxId ,String format, String mode)
			throws ApiException;

	/**
	 * get trends
	 * 
	 * @return
	 * @throws ApiException
	 */
	List<Search> trends() throws ApiException;

	/**
	 * get saved searches
	 * 
	 * @return
	 * @throws ApiException
	 */
	List<Search> savedSearches() throws ApiException;

	/**
	 * show a saved search
	 * 
	 * @param id
	 * @return
	 * @throws ApiException
	 */
	Search savedSearchShow(int id) throws ApiException;

	/**
	 * add a saved search
	 * 
	 * @param name
	 * @return
	 * @throws ApiException
	 */
	Search savedSearchCreate(String name) throws ApiException;

	/**
	 * delete a saved search
	 * 
	 * @param id
	 * @return
	 * @throws ApiException
	 */
	Search savedSearchDelete(int id) throws ApiException;

	/**
	 * friends info list
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<User> usersFriends(String userId, int count, int page,String mode) throws ApiException;

	/**
	 * followers info list
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<User> usersFollowers(String userId, int count, int page,String mode) throws ApiException;

	/**
	 * show a user's profile
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	User userShow(String userId,String mode) throws ApiException;

	/**
	 * action: follow
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	User userFollow(String userId,String mode) throws ApiException;

	/**
	 * action: unfollow
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	User userUnfollow(String userId,String mode) throws ApiException;

	/**
	 * action: block
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	User userBlock(String userId,String mode) throws ApiException;

	/**
	 * action: unblock
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	User userUnblock(String userId,String mode) throws ApiException;

	/**
	 * check user is or not blocked.
	 * 
	 * @param userId
	 *            target user id
	 * @return
	 * @throws ApiException
	 */
	User isBlocked(String userId,String mode) throws ApiException;

	/**
	 * fetch user list blocked by me
	 * 
	 * @return
	 * @throws ApiException
	 */
	List<User> userBlockedList(int count, int page,String mode) throws ApiException;

	/**
	 * fetch user ids list blocked by me
	 * 
	 * @return
	 * @throws ApiException
	 */
	List<String> userBlockedIDs() throws ApiException;

	/**
	 * check two users is or not friends, check a is or not follow b.
	 * 
	 * @param userA
	 * @param userB
	 * @return
	 * @throws ApiException
	 */
	boolean isFriends(String userA, String userB) throws ApiException;

	/**
	 * friends id list
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<String> usersFriendsIDs(String userId, int count, int page)
			throws ApiException;

	/**
	 * followers id list
	 * 
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<String> usersFollowersIDs(String userId, int count, int page)
			throws ApiException;

	/**
	 * inbox messages
	 * 
	 * @param count
	 * @param page
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<DirectMessage> messagesInbox(int count, int page, String sinceId,
			String maxId,String mode) throws ApiException;

	/**
	 * outbox messages
	 * 
	 * @param count
	 * @param page
	 * @param sinceId
	 * @param maxId
	 * @return
	 * @throws ApiException
	 */
	List<DirectMessage> messagesOutbox(int count, int page, String sinceId,
			String maxId, String mode) throws ApiException;

	/**
	 * send a dm
	 * 
	 * @param userId
	 * @param text
	 * @param inReplyToId
	 * @return
	 * @throws ApiException
	 */
	DirectMessage messageCreate(String userId, String text, String inReplyToId,String mode)
			throws ApiException;

	/**
	 * delete a dm
	 * 
	 * @param directMessageId
	 * @return
	 * @throws ApiException
	 */
	DirectMessage messageDelete(String directMessageId,String mode) throws ApiException;

}
