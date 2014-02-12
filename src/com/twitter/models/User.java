package com.twitter.models;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Users")
public class User extends Model implements Parcelable {
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private static final String JSON_PARSING = "JSON_PARSING";
	
	@Column(name = "screen_name")
	private String screenName;
	public String getScreenName() {
		return screenName;
	}
	
	@Column(name = "name")
	private String name;
	public String getName() {
		return name;
	}
	
	@Column(name = "profile_pic_url")
	private String profilePicUrl;
	public String getProfilePicUrl() {
		return profilePicUrl;
	}
	
	@Column(name = "tweet_count")
	private int tweetCount;
	public int getNumTweets(){
		return tweetCount;
	}

	@Column(name = "followers_count")
	private int followersCount;
	public int getFollowersCount(){
		return followersCount;
	}
	
	@Column(name = "friends_count")
	private int friendsCount;
	public int getFriendsCount(){
		return this.friendsCount;
	}

	@Column(name = "following_count")
	private int followingCount;
	public int getFollowingCount(){
    	return followingCount;
    }

	@Column(name = "description")
	private String description;
    public String getDescription(){
    	return description;
    }
	
	public User() {
		super();
	}
	
	public User(User user) {
		this.screenName = user.screenName;
		this.name = user.name;
		this.profilePicUrl = user.profilePicUrl;
	}
	

	public User(JSONObject json) {
//		setName(user);
//		setScreenName(user);
//		setProfilePicUrl(user);
//		User u = new User();
		try{
			this.name = json.getString("name");
			this.screenName=json.getString("screen_name");
			this.profilePicUrl =json.getString("profile_image_url");
			this.followersCount = json.getInt("followers_count");
			this.friendsCount = json.getInt("friends_count");
			this.followingCount = json.getInt("friends_count");
			this.description = json.getString("description");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setScreenName(JSONObject user) {
		try {
			screenName = user.getString("screen_name");
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse the user screen name.");
			screenName = "";
		}
	}
	
	private void setName(JSONObject user) {
		try {
			name = user.getString("name");
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse the user name.");
			name = "";
		}
	}
	
	private void setProfilePicUrl(JSONObject user) {
		try {
			profilePicUrl = user.getString("profile_image_url");
		}
		catch (JSONException e) {
			Log.d(JSON_PARSING, "Failed to parse profile image url.");
			profilePicUrl = "";
		}	
	}
	
	/*** Everything below is intented to make this User class Parcelable. ***/
	
	// Read back fields in the order they were written.
	public User(Parcel pc) {
		screenName = pc.readString();
		name = pc.readString();
		profilePicUrl = pc.readString();
	}

	// Apparently this is used to give extra hints on how to parse contents,
	// but it is not needed right now.
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(screenName);
		dest.writeString(name);
		dest.writeString(profilePicUrl);
	}
	
	/** Static field used to regenerate object, individually or as arrays */
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel pc) {
				return new User(pc);
		}
		
		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
