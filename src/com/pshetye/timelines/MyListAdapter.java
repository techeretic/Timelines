package com.pshetye.timelines;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pshetye.tweets.Tweet;
import com.pshetye.tweets.Twitter;

public class MyListAdapter extends ArrayAdapter<Tweet> {
	 private List<Tweet> Tweets;
	 
	 private Activity activity;
	 
	 public MyListAdapter(Context context, int textViewResourceId, Activity activity, Twitter twits) {
		 super(context, textViewResourceId, twits);
		 this.Tweets = twits;
		 this.activity = activity;
	 }

	public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;

		 if (v == null) {
			 LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v = vi.inflate(R.layout.tweetview, null);
		 }

		 Tweet tweet = Tweets.get(position);

		 if (tweet != null) {             
			 TextView title = (TextView) v.findViewById(R.id.theTweet);
			 TextView description = (TextView) v.findViewById(R.id.theDate);
			 if (title != null) {
				 title.setText(tweet.getText());
			 }
			 if (description != null) {
				 description.setText(String.valueOf(tweet.getDateCreated()));
			 }
			 title = null;
			 description = null;
		 }

		 return v;
	 }
}