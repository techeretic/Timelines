package com.pshetye.timelines;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
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
	 TextView title;
	 TextView description;
	 ImageView profile;
	 String profileUrl;
	 
	 static int count;
	 
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
			 title = (TextView) v.findViewById(R.id.Name);
			 description = (TextView) v.findViewById(R.id.ScreenName);
			 profile = (ImageView) v.findViewById(R.id.Profile);
			 
			 if (title != null) {
				 title.setText(user.getScreenName());
				 if (!user.getLocation().isEmpty())
					 description.setText(user.getName() + " from " + user.getLocation());
				 else
					 description.setText(user.getName() + " don't share their location");
				 
				 profileUrl = user.getProfileImageUrl();
				 new DownloadProfileTask().execute(profileUrl);
			 }
			 title = null;
			 description = null;
		 }

		 return v;
	 }
	
	 public static Bitmap getBitmapFromURL(String src) {
	    try {
	    	count++;
	    	Log.i("PRATHAM","1 count = " + count);
	        Log.e("src",src);
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (Exception e) {
	        Log.e("PRATHAM Exception --> ",e.getMessage());
        	Log.i("PRATHAM","2 count = " + count);
	        /*if (count == 1) {
	        	count++;
	        	return getBitmapFromURL(cleanURL(src));
	        }*/
	        return null;
	    }
	 }
	 
	 public static String cleanURL(String url) {
		 if(url.contains("https")) {
			 return url.replaceFirst("https", "http");
		 } else 
		 if (url.contains("http")) {
			 return url.replaceFirst("http", "https");
		 }
		 
		 return "";
	 }
	 
	 private class DownloadProfileTask extends AsyncTask<String, Void, String> {
		 
		Bitmap bp;

		@Override
		protected String doInBackground(String... profileUrl) {
			// TODO Auto-generated method stub
			if (!profileUrl[0].isEmpty())
				bp = getBitmapFromURL(profileUrl[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (bp != null)
				profile.setImageBitmap(bp);
		}
	 
	 }
	 
}