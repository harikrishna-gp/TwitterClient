package com.twitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.models.Tweet;
import com.twitter.models.User;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;


public class TimelineActivity extends Activity {
	private static final int TWEETS_PER_LOAD = 25;
	private final int COMPOSE_CODE = 100;
	private ArrayList<Tweet> tweets;
	private TweetsAdapter tweetsAdptr;
	private PullToRefreshListView  lvTweets;
	private User currentUser;
	private long recentTweetId=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		tweets = new ArrayList<Tweet>();
		tweetsAdptr = new TweetsAdapter(this, tweets);
		lvTweets = (PullToRefreshListView )findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdptr);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
	        public void onLoadMore(int page, int totalItemsCount) {
	        	Log.e ("Loading", "Loading More");
	        	loadMore();
	        	return;
	        }
	        public void loadMore() {
                Log.e("DEBUG", "Scroll loading page ");
                loadTimeLine(false);
            }
    	});

        lvTweets.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//            	loadTimeLine(true);
            }
        });	
        loadTimeLine(true);
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
					TwitterApp.getRestClient().getUser(new JsonHttpResponseHandler() {
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
		        Log.d("Refresh Button", "Reloading the timeline");
				loadTimeLine(true);
		        return true;
		        
			case R.id.signout:
		        Log.d("Signout", "Got it");
		        triggerReLogin();
//		        this.finish();
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
	
	private void loadTimeLine(final Boolean freshList) {
		long recentId  = recentTweetId - 1;
		if (freshList) {
			recentId  = 0;
		}
		
		TwitterApp.getRestClient().getMoreHomeTimeline(recentId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonTweetsArray) {
				if (freshList) {
					tweetsAdptr.clear();
				}
				tweetsAdptr.addAll(Tweet.tweetsFromJsonArray(jsonTweetsArray));
				
				// Only update lowest tweet id if it is valid.
				int index = tweetsAdptr.getCount() - 1;
				if (index >= 0)
				{
					long tweet_id = tweetsAdptr.getItem(index).getTweetId();
					if (tweet_id != 0) {
						recentTweetId = tweet_id;
					}
				}
			}
			
			public void onFailure(Throwable e, JSONObject error) {
			    Log.e("ERROR", e.toString());
			    Log.e("ERROR", error.toString());
			}
		});
	}
}
