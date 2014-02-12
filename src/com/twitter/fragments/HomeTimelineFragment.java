package com.twitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.EndlessScrollListener;
import com.twitter.R;
import com.twitter.TwitterApp;
import com.twitter.models.Tweet;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class HomeTimelineFragment extends TweetsListFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		setEndpoint("HomeTimeLine");
	}

	@Override
	void loadMore(boolean refresh) {
		loadFeed(refresh);
	}
	
}
