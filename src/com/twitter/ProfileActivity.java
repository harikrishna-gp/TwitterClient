package com.twitter;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.models.User;

import eu.erikw.PullToRefreshListView;

public class ProfileActivity extends FragmentActivity  {
	private PullToRefreshListView  lvTweets;
	ImageView profilePic = null;
	TextView profileName = null;
	TextView status      = null;
	TextView followers   = null;
	TextView following	 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		loadProfileInfo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}
	
	public void loadProfileInfo() {
		profilePic  = (ImageView) findViewById(R.id.iv_profile_pic);
		profileName = (TextView) findViewById(R.id.tv_profile_firstname);
		status      = (TextView) findViewById(R.id.tv_profile_status);
		followers   = (TextView) findViewById(R.id.tv_profile_followers);
		following	= (TextView) findViewById(R.id.tv_profile_following);
        profilePic  = (ImageView) findViewById(R.id.iv_profile_pic);
        String 	screenName  = null;	

        if(getIntent().hasExtra("screenName")){
            screenName = getIntent().getStringExtra("screenName");
			TwitterApp.getRestClient().getUser(screenName, new JsonHttpResponseHandler() {
				public void onSuccess(JSONObject userJson) {
					User usr = new User(userJson);
					getActionBar().setTitle("@" + usr.getScreenName());
					ProfileActivity.this.profileName.setText(usr.getName());
					ProfileActivity.this.status.setText(usr.getDescription());
					ProfileActivity.this.followers.setText(String.valueOf(usr.getFollowersCount() + " Followers"));
					ProfileActivity.this.following.setText(String.valueOf(usr.getFollowingCount())+ " Following");
					ImageLoader.getInstance().displayImage(usr.getProfilePicUrl(), profilePic);
				}
				
				public void onFailure(Throwable e, JSONObject error) {
					Log.e("USER_CREDENTIALS", "Failed to get user credentials. "+error);
				}
			}); 
        }
        else{
			TwitterApp.getRestClient().getLoggedinUser(new JsonHttpResponseHandler() {
				public void onSuccess(JSONObject userJson) {
					User usr = new User(userJson);
					getActionBar().setTitle("@" + usr.getScreenName());
					ProfileActivity.this.profileName.setText(usr.getName());
					ProfileActivity.this.status.setText(usr.getDescription());
					ProfileActivity.this.followers.setText(String.valueOf(usr.getFollowersCount() + " Followers"));
					ProfileActivity.this.following.setText(String.valueOf(usr.getFollowingCount())+ " Following");
					ImageLoader.getInstance().displayImage(usr.getProfilePicUrl(), profilePic);
				}
				
				public void onFailure(Throwable e, JSONObject error) {
					Log.e("USER_CREDENTIALS", "Failed to get user credentials. "+error);
				}
			}); 
        }
	}
	

}
