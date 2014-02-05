package com.twitter;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.twitter.models.User;

public class ComposeActivity extends Activity {
	private static final int CHARACTER_LIMIT = 120;
	private Button btnSendTweet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		btnSendTweet = (Button)findViewById(R.id.btnSendTweet);
		
		Intent data = getIntent();
		User user = (User)data.getParcelableExtra("current_user");
		
		// Display the current user's profile pic.
		ImageView ivUserPic = (ImageView)findViewById(R.id.ivUserPic);
		ImageLoader.getInstance().displayImage(user.getProfilePicUrl(), ivUserPic);
		
		// Fill in the user's name and screen name.
		TextView tvName = (TextView)findViewById(R.id.tvName);
		TextView tvScreenName = (TextView)findViewById(R.id.tvScreenName);
		tvName.setText(user.getName());
		if (user.getScreenName() != "") {
			tvScreenName.setText("@" + user.getScreenName());
		}
		
		EditText etComposeTweet = (EditText)findViewById(R.id.etComposeTweet);
		etComposeTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				TextView tvCharacterLimit = (TextView)findViewById(R.id.tvCharacterLimit);
				int charCount = s.length();
				tvCharacterLimit.setText("Characters: " + charCount);
				if (charCount > CHARACTER_LIMIT) {
					btnSendTweet.setEnabled(false);
					tvCharacterLimit.setTextColor(Color.RED);
				}
				else {
					btnSendTweet.setEnabled(true);
					tvCharacterLimit.setTextColor(Color.BLACK);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	public void handleTweet(View v) {
		
		switch(v.getId()) {
		case R.id.btnCancel:
			Intent i = new Intent();
			setResult(RESULT_CANCELED, i);
			finish();
			break;
		case R.id.btnSendTweet:
			// Extract the text from the EditText.
			EditText etComposeTweet = (EditText)findViewById(R.id.etComposeTweet);
			final String message = etComposeTweet.getText().toString();
			TwitterApp.getRestClient().postTweet(message, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					Intent i = new Intent();
					i.putExtra("message", message);
					try {
						String tweetTimestamp = response.getString("created_at");
						i.putExtra("tweet_timestamp", tweetTimestamp);
					}
					catch (JSONException e) {
						i.putExtra("tweet_timestamp", "");
					}
					
					try {
						String tweetId = response.getString("id_str");
						i.putExtra("tweet_id", tweetId);
					}
					catch (JSONException e) {
						i.putExtra("tweet_id", 0);
					}
					
					// Try to get the user object.
					try {
						i.putExtra("current_user", new User(response.getJSONObject("user")));
					}
					catch (JSONException e) {
						i.putExtra("current_user", new User(new JSONObject()));
					}
					setResult(RESULT_OK, i);
					finish();
				}
				
				public void onFailure(Throwable e, JSONObject error) {
					Toast.makeText(getBaseContext(), R.string.failed_to_tweet, Toast.LENGTH_SHORT).show();
					Intent i = new Intent();
					setResult(RESULT_CANCELED, i);
					finish();
				}
			});
			break;
		}
	}

}
