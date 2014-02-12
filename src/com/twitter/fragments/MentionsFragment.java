package com.twitter.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.twitter.TwitterApp;
import com.twitter.models.Tweet;

public class MentionsFragment extends TweetsListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
		setEndpoint("MentionsTimeLine");

	}

	@Override
	void loadMore(boolean refresh) {
		loadFeed(refresh);
	}
}
