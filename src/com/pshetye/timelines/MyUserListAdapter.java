package com.pshetye.timelines;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pshetye.tweets.TwitterUser;
import com.pshetye.tweets.TwitterUsers;

public class MyUserListAdapter extends ArrayAdapter<TwitterUser> {
	 private List<TwitterUser> Users;
	 
	 private Activity activity;
	 TextView name;
	 TextView screenname;
	 TextView description;
	 ImageView profile;
	 
	 static int count;
	 
	 private final ImageDownloader imageDownloader = new ImageDownloader();
	 
	 public MyUserListAdapter(Context context, int textViewResourceId, Activity activity, TwitterUsers users) {
		 super(context, textViewResourceId, users);
		 this.Users = users;
		 this.activity = activity;
		 count = 0;
	 }

	 public View getView(int position, View convertView, ViewGroup parent) {
		 View v = convertView;

		 if (v == null) {
			 LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v = vi.inflate(R.layout.userview, null);
		 }

		 TwitterUser user = Users.get(position);

		 if (user != null) {             
			 name = (TextView) v.findViewById(R.id.Name);
			 screenname = (TextView) v.findViewById(R.id.ScreenName);
			 description = (TextView) v.findViewById(R.id.Descrip);
			 profile = (ImageView) v.findViewById(R.id.Profile);
			 
			 if (name != null) {
				 name.setText(user.getScreenName());
				 if (!user.getLocation().isEmpty())
					 screenname.setText(user.getName() + " from " + user.getLocation());
				 else
					 screenname.setText(user.getName() + " don't share their location");
				 
				 description.setText(user.getDescription());
				 
				 imageDownloader.download(user.getProfileImageUrlHttps(), profile);
				 
				 profile.setContentDescription(user.getProfileImageUrlHttps());
			 }
			 name = null;
			 screenname = null;
			 description = null;
		 }

		 return v;
	 }
	 
}