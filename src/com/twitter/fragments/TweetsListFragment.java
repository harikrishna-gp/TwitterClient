package com.twitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.EndlessScrollListener;
import com.twitter.R;
import com.twitter.TweetsAdapter;
import com.twitter.TwitterApp;
import com.twitter.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public abstract class TweetsListFragment extends Fragment {

    TweetsAdapter adapter;
	private PullToRefreshListView  lvTweets;
	private long recentTweetId=1;
	private String endpoint = "Undefined";
	private String screenName = null; 
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
        View v = inf.inflate(R.layout.fragment_tweets_list,parent, false);
        if (lvTweets != null) {
        	lvTweets.setAdapter(getAdapter());
			lvTweets.setOnScrollListener(new EndlessScrollListener() {
		        public void onLoadMore(int page, int totalItemsCount) {
		        	loadMore(false);
		        	return;
		        }
	    	});
	        lvTweets.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	            	loadMore(true);
	            }
	        });	
	        loadMore(true);
        }
        return v;

    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        adapter = new TweetsAdapter(getActivity(), tweets);
        lvTweets = (PullToRefreshListView)getActivity().findViewById(R.id.lvTweets);
		if ((lvTweets != null) && (getAdapter()!=null)){
			lvTweets.setAdapter(getAdapter());
			lvTweets.setOnScrollListener(new EndlessScrollListener() {
				public void onLoadMore(int page, int totalItemsCount) {
					loadMore(false);
					
					return;
				}
			});
			lvTweets.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					loadMore(true);
				}
			});	
			loadMore(true);
		}        
    }
 
    public TweetsAdapter getAdapter(){
        return adapter;
    }
    public void setEndpoint(String endpoint){
        this.endpoint = endpoint;
    }

    public void setScreenName(String screenName){
        this.screenName= screenName;
    }

    abstract void loadMore (boolean refresh);
    
	protected void loadFeed(final Boolean freshList) {
		long recentId  = recentTweetId - 1;
		if (freshList) {
			recentId  = 0;
		}
		
		TwitterApp.getRestClient().loadFeed(this.endpoint, this.screenName, recentId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONArray jsonTweetsArray) {
				ArrayList<Tweet> tweets = Tweet.tweetsFromJsonArray(jsonTweetsArray);
				if (freshList) {
					getAdapter().clear();
				}
				getAdapter().addAll(tweets);
				int index = getAdapter().getCount() - 1;
				if (index >= 0)
				{
					long tweet_id = getAdapter().getItem(index).getTweetId();
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
