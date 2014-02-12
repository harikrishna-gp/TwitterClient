package com.twitter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.fragments.HomeTimelineFragment;
import com.twitter.fragments.MentionsFragment;
import com.twitter.fragments.TweetsListFragment;
import com.twitter.models.Tweet;
import com.twitter.models.User;


public class TimelineActivity extends FragmentActivity implements ActionBar.TabListener {
	private static final int TWEETS_PER_LOAD = 25;
	private final int COMPOSE_CODE = 100;
	private ArrayList<Tweet> tweets;
	private TweetsAdapter tweetsAdptr;
	TweetsListFragment fragmentTweets;
	private User currentUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
        setupNavigationTabs();
    }

    public void setupNavigationTabs(){

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);

        ActionBar.Tab tabHome = actionBar.newTab().setText("HOME").setTag("TimelineFragment").
        									setIcon(R.drawable.ic_home).setTabListener(this);

        ActionBar.Tab tabMentions = actionBar.newTab().setText("MENTIONS").setTag("MentionsFragment").
        									setIcon(R.drawable.ic_mentions).setTabListener(this);

        actionBar.addTab(tabHome);
        actionBar.addTab(tabMentions);
        actionBar.selectTab(tabHome);

    }

    public void onProfileView(MenuItem mi)
    {
    	Intent intnt = new Intent(this, ProfileActivity.class);
		startActivity(intnt);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
	}

	private void triggerReLogin() {
		TwitterApp.getRestClient().invalidateToken( new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONObject response) {
						Log.e("LOGOUT" ,response.toString());
					}
					public void onFailure(Throwable e, JSONObject error) {
					    Log.e("ERROR", e.toString());
					    Log.e("ERROR", error.toString());
					}
				});
		Intent intnt = new Intent(getApplicationContext(),LoginActivity.class);
		startActivity(intnt);
		return;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.compose:
				if (currentUser == null) {
					TwitterApp.getRestClient().getLoggedinUser(new JsonHttpResponseHandler() {
						public void onSuccess(JSONObject user) {
							Log.e("USER INFO" ,"GOT IT");
						}
						
						public void onFailure(Throwable e, JSONObject error) {
							Log.e("USER_CREDENTIALS", "Failed to get user credentials. "+error);
						}
					}); 
					currentUser = new User(new JSONObject());
//					currentUser.save();
				}
				Intent intnt = new Intent(getApplicationContext(),ComposeActivity.class);
				intnt.putExtra("current_user", currentUser);
				startActivityForResult(intnt, COMPOSE_CODE);
				return true;
			case R.id.refresh:
//				loadTimeLine(true);
		        return true;
		        
			case R.id.signout:
		        triggerReLogin();
		        this.finish();
		        return true;		        
			case R.id.profile:
		        Log.d("Profile", "Got it");
		        triggerReLogin();
		        this.finish();
		        return true;		        

			default:
				return super.onOptionsItemSelected(item);
	    }
	}
	
	// Handle the result of the ComposeTweetActivity.
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Result from the ComposeTweetActivity.
		if (requestCode == COMPOSE_CODE)
		{
			if (resultCode == RESULT_OK) {
				String tweetMessage = data.getStringExtra("message");
				String tweetTimestamp = data.getStringExtra("tweet_timestamp");
				String tweetId = data.getStringExtra("tweet_id");
				currentUser = (User)data.getParcelableExtra("current_user");
				addTweetForCurrentUser(tweetMessage, tweetTimestamp, tweetId);
			}
		} 
	}
	
	private void addTweetForCurrentUser(String message, String tweetTimestamp, String tweetId) {
		Tweet tw = new Tweet(currentUser, message, tweetTimestamp, tweetId);
		tweetsAdptr.insert(tw, 0);
//		tw.save();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		FragmentManager manager = getSupportFragmentManager();
	    android.support.v4.app.FragmentTransaction fts = manager.beginTransaction();

	    if(tab.getTag() == "TimelineFragment"){
	    	fts.replace(R.id.fragment_container,new HomeTimelineFragment());
	    }
	    else
	    {
	    	fts.replace(R.id.fragment_container,new MentionsFragment());
	    }
	    fts.commit();		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}	
}
