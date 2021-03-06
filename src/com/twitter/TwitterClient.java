package com.twitter;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;

import android.content.Context;
import android.util.Log;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
    public static final String 	REST_CONSUMER_KEY = "R10N5iPhxZLeCGmsQ0iFiA";       // Change this
    public static final String REST_CONSUMER_SECRET = "EYf14bOBq2mp76shwD2YLFZqrMFvzKrI9NwqRYBLkjg"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://simpletwitterclient"; // Change this (here and in manifest)
        
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
	public void invalidateToken(AsyncHttpResponseHandler handler){
    	Token t = client.getAccessToken();
    	Log.e("Token", t.getToken());
    	String url = "https://api.twitter.com/oauth2/invalidate_token";
    	client.post(url, new RequestParams("access_token", t.getToken()), handler);	
    }
	
	public Token getAccessToken(){
    	return client.getAccessToken();
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler){
    	String url = getApiUrl("statuses/home_timeline.json");
    	client.get(url, new RequestParams("count", "25"), handler);	
    }

	public void getMoreHomeTimeline( Long id, AsyncHttpResponseHandler handler ){
    	RequestParams params = new  RequestParams(); 
		if (id == 0) {
			params.put("count", "25");
		}
		else {
			params.put("max_id", id.toString());
		}
		String url = getApiUrl("statuses/home_timeline.json");
    	client.get(url, params, handler);	
    }

	public void getMentions(AsyncHttpResponseHandler handler){
    	String url = getApiUrl("statuses/mentions_timeline.json");
    	client.get(url, new RequestParams("count", "25"), handler);	
    }
	public void loadFeed(String endpoint, String screenName, long id, AsyncHttpResponseHandler handler ){
		if (endpoint.equals("HomeTimeLine")) {
			getMoreHomeTimeline(id, handler);
		}
		else if (endpoint.equals("UserTimeLine")) {
			getMoreUserTimeline(id, screenName , handler);
		}
		else if (endpoint.equals("MentionsTimeLine")) {
			getMoreMentions(id, handler);
		}
		
	}
	public void getMoreMentions( Long id, AsyncHttpResponseHandler handler ){
    	RequestParams params = new  RequestParams(); 
		if (id == 0) {
			params.put("count", "25");
		}
		else {
			params.put("max_id", id.toString());
		}
		String url = getApiUrl("statuses/mentions_timeline.json");
    	client.get(url, params, handler);	
    }
	
	public void postTweet(String tweet, AsyncHttpResponseHandler handler){
    	String url = getApiUrl("statuses/update.json");
    	client.post(url, new RequestParams("status", tweet), handler);
    }
    
	public void getUser(String userHdl, AsyncHttpResponseHandler handler){
		String url = getApiUrl("users/show.json");
		client.get(url, new RequestParams("screen_name", userHdl), handler);
    }
	
	public void getLoggedinUser(AsyncHttpResponseHandler handler){
		String url = getApiUrl("account/verify_credentials.json");
		client.get(url, null, handler);
    }
	public void getMoreUserTimeline( Long id, String screenName, AsyncHttpResponseHandler handler ){
		Log.e("USER TIMELINE", "ScreeenName :" + screenName);
    	RequestParams params = new  RequestParams(); 
		if (id == 0) {
			params.put("count", "25");
		}
		else {
			params.put("max_id", id.toString());
		}
		params.put("screen_name", screenName);
		String url = getApiUrl("statuses/user_timeline.json");
    	client.get(url, params, handler);	
    }

	public void getUserTimeline(String screenName, AsyncHttpResponseHandler handler){
        String currentAPI="statuses/user_timeline.json";
        String url = getApiUrl(currentAPI);
        client.get(url,null, handler);
    }
}