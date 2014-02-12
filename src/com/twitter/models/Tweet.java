package com.twitter.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.twitter.TwitterApp;

@Table(name = "Tweets")
public class Tweet extends Model {
	
	private static final String JSON_PARSING = "JSON_PARSING";
	
	// This format parses dates of this form: Wed Aug 27 13:08:45 +0000 2008
	private static final String TWITTER_TIME_FORMAT = "EEE MMM dd HH:mm:ss ZZZ yyyy";
	
	@Column(name = "message")
	private String message;
	public String getMessage() {
		return message;
	}
	
	@Column(name = "tweet_timestamp")
	private String tweetTimestamp;
	
	// Convert the timestamp into a relative time such as "3 days ago".
	// 
	public String getTimestamp() {
		Log.d("TIME", tweetTimestamp);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(TWITTER_TIME_FORMAT, Locale.US);
		Date date;
		try {
			date = dateFormat.parse(tweetTimestamp);
		} catch (ParseException e) {
			return "Unknown date";
		}
		String str = (String) DateUtils.getRelativeDateTimeString(TwitterApp.context,
								date.getTime(), 	
		        				DateUtils.MINUTE_IN_MILLIS,
		        				DateUtils.WEEK_IN_MILLIS, 0);
		return str;
	}
	
	@Column(name = "User")
	private User user;
	public User getUser() {
		return user;
	}
	
	@Column(name = "tweetId")
	private String tweetId;
	public long getTweetId() {
		return Long.parseLong(tweetId);
	}
	
	// Default constructor required by ActiveAndroid.
	public Tweet() {
		super();
	}
	
	public Tweet(User user, String message, String tweetTimestamp, String tweetId) {
		this.user = new User(user);
		this.message = message;
		this.tweetTimestamp = tweetTimestamp;
		this.tweetId = tweetId;
	}
	
	public Tweet(JSONObject tweet) {
		
		setMessage(tweet);
		setTweetId(tweet);
		setTimestamp(tweet);
		
		// Time to access the user object within the tweet.
		try {
			JSONObject jsonUser = tweet.getJSONObject("user");
			user = new User(jsonUser);
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse the JSON user object.");
		}		
	}
	
	private void setTweetId(JSONObject tweet) {
		try {
			tweetId = tweet.getString("id_str");
		}
		catch (JSONException e) {
			tweetId = "0";
		}
	}

	private void setMessage(JSONObject tweet) {
		try {
			message = tweet.getString("text");
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse tweet text.");
			message = "";
		}
	}
	
	private void setTimestamp(JSONObject user) {
		try {
			tweetTimestamp = user.getString("created_at");
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse tweet timestamp.");
			tweetTimestamp = "";
		}
	}
	
	
	
	public static ArrayList<Tweet> tweetsFromJsonArray(JSONArray tweets) {
		
		ArrayList<Tweet> tweetList = new ArrayList<Tweet>(); 
		for (int i = 0; i < tweets.length(); ++i) {
			// Attempt to access the user object.
			try {
				JSONObject tweet = (JSONObject)tweets.get(i);
				tweetList.add(new Tweet(tweet));
			}
			catch (JSONException e) {
				Log.d(JSON_PARSING, "Failed to access tweet object from JSONArray.");
			}
		}
		
		return tweetList;
	}

//	public static Tweet fromJson(JSONObject jsonObject){
//		Tweet tweet = new Tweet();
//		try{
////			tweet.jsonObject = jsonObect;
//			tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
//			tweet.tweetId = String.valueOf(jsonObject.getLong("id"));
//			tweet.message = jsonObject.getString("text");
//			tweet.favorited = jsonObject.getBoolean("favorited");
//		}catch(JSONException e){
//			e.printStackTrace();
//			return null;
//		}
//		return tweet;
//	}

}
