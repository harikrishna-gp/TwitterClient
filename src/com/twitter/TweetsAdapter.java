package com.twitter;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.models.Tweet;
import com.twitter.models.User;

public class TweetsAdapter extends ArrayAdapter<Tweet> {
	
	public TweetsAdapter(Context context, ArrayList<Tweet> tweets) {
		super(context, 0, tweets);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		
		// If convertView is null, we need to inflate a new view instead of reusing one.
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.tweet_item, null);
		}
		
		// Retrieve the tweet.
		Tweet tweet = getItem(position);
		
		// Finally, update the view with the tweet information and return the view.
		ImageView ivProfilePic = (ImageView)view.findViewById(R.id.ivProfilePic);
		TextView tvMessage = (TextView)view.findViewById(R.id.tvMessage);
		TextView tvName = (TextView)view.findViewById(R.id.tvName);
		TextView tvScreenName = (TextView)view.findViewById(R.id.tvScreenName);
		TextView tvTimestamp = (TextView)view.findViewById(R.id.tvTimestamp);
		
		User user = tweet.getUser();
		ImageLoader.getInstance().displayImage(user.getProfilePicUrl(), ivProfilePic);
		tvMessage.setText(tweet.getMessage());
		tvName.setText(user.getName());
		if (user.getScreenName() != "") {
			tvScreenName.setText("@" + user.getScreenName());
		}
		tvTimestamp.setText(tweet.getTimestamp());
		ivProfilePic.setTag(user.getScreenName());
		ivProfilePic.setOnClickListener(new View.OnClickListener() {
			@Override
	        public void onClick(View v)
	        {
	            Intent intnt = new Intent(getContext(), ProfileActivity.class);
	            Log.e("INTENT USER", v.getTag().toString() );
	            intnt.putExtra("screenName", v.getTag().toString() );
	            getContext().startActivity(intnt);
	        }
	    });


		return view;
	}
}

