package com.pshetye.timelines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import com.pshetye.timelines.Authenticated;
import com.pshetye.timelines.Tweet;
import com.pshetye.timelines.Twitter;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText ScreenName;
	ListView listview;
	Button FetchTweets;
	MyListAdapter myladapter;
	Toast t;
	private static boolean failure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		failure = false;
		
		FetchTweets = (Button) findViewById(R.id.FetchTweets);
		ScreenName = (EditText) findViewById(R.id.ScreenName);
		
		listview = (ListView) findViewById(R.id.list);
		
		FetchTweets.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!ScreenName.getText().toString().isEmpty()) {
					downloadTweets();
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// download twitter timeline after first checking to see if there is a network connection
	private void downloadTweets() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadTwitterTask().execute(ScreenName.getText().toString());
		} else {
			showToast("No network connection available.");
		}
		
		hideSoftKeyboard(this,ScreenName);
	}
	

	// Uses an AsyncTask to download a Twitter user's timeline
	private class DownloadTwitterTask extends AsyncTask<String, Void, String> {
		final static String CONSUMER_KEY = "W4I3aTIbbWeQ60gjBprQ";
		final static String CONSUMER_SECRET = "Fo1vW5VYehSqTr7ZrMMJlGqVcD688Szy4dDdJYI4k";
		final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
		final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=";

		@Override
		protected String doInBackground(String... screenNames) {
			String result = null;

			if (screenNames.length > 0) {
				result = getTwitterStream(screenNames[0]);
			}
			return result;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(String result) {
			try {
				Twitter twits = jsonToTwitter(result);
	
				// lets write the results to the console as well
				//for (Tweet tweet : twits) {
					
					//Log.i(LOG_TAG, tweet.getText());
				//}
	
				// send the tweets to the adapter for rendering
				myladapter = new MyListAdapter(MainActivity.this, android.R.layout.simple_list_item_activated_2, twits);
				listview.setAdapter(myladapter);
				listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			} catch (IllegalStateException ex) {
				// just eat the exception
			}
		}

		// converts a string of JSON data into a Twitter object
		private Twitter jsonToTwitter(String result) {
			Twitter twits = null;
			if (result != null && result.length() > 0) {
				try {
					Gson gson = new Gson();
					twits = gson.fromJson(result, Twitter.class);
				} catch (IllegalStateException ex) {
					// just eat the exception
					failure = true;
					showToast("Invaid Twitter Handle");
				}
			}
			return twits;
		}

		// convert a JSON authentication object into an Authenticated object
		private Authenticated jsonToAuthenticated(String rawAuthorization) {
			Authenticated auth = null;
			if (rawAuthorization != null && rawAuthorization.length() > 0) {
				try {
					Gson gson = new Gson();
					auth = gson.fromJson(rawAuthorization, Authenticated.class);
				} catch (IllegalStateException ex) {
					// just eat the exception
				}
			}
			return auth;
		}

		private String getResponseBody(HttpRequestBase request) {
			StringBuilder sb = new StringBuilder();
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String reason = response.getStatusLine().getReasonPhrase();

				if (statusCode == 200) {

					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();

					BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					String line = null;
					while ((line = bReader.readLine()) != null) {
						sb.append(line);
					}
				} else {
					sb.append(reason);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (ClientProtocolException ex1) {
			} catch (IOException ex2) {
			}
			return sb.toString();
		}

		private String getTwitterStream(String screenName) {
			String results = null;

			// Step 1: Encode consumer key and secret
			try {
				// URL encode the consumer key and secret
				String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
				String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

				// Concatenate the encoded consumer key, a colon character, and the
				// encoded consumer secret
				String combined = urlApiKey + ":" + urlApiSecret;

				// Base64 encode the string
				String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

				// Step 2: Obtain a bearer token
				HttpPost httpPost = new HttpPost(TwitterTokenURL);
				httpPost.setHeader("Authorization", "Basic " + base64Encoded);
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
				String rawAuthorization = getResponseBody(httpPost);
				Authenticated auth = jsonToAuthenticated(rawAuthorization);

				// Applications should verify that the value associated with the
				// token_type key of the returned object is bearer
				if (auth != null && auth.token_type.equals("bearer")) {

					// Step 3: Authenticate API requests with bearer token
					HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

					// construct a normal HTTPS request and include an Authorization
					// header with the value of Bearer <>
					httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
					httpGet.setHeader("Content-Type", "application/json");
					// update the results with the body of the response
					results = getResponseBody(httpGet);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (IllegalStateException ex1) {
			}
			return results;
		}
	}

    private void showToast(String ToastText){
    	if (t != null)
			t.cancel();
		t = Toast.makeText(this, ToastText, Toast.LENGTH_SHORT);
		t.show();
    }
    
    private static void hideSoftKeyboard (Activity activity, View view) {
    	InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    	imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}    
    
    private class MyListAdapter extends ArrayAdapter<Tweet> {
		 private List<Tweet> Tweets;
		 
		 public MyListAdapter(Context context, int textViewResourceId, Twitter twits) {
			 super(context, textViewResourceId, twits);
			 this.Tweets = twits;
		 }

		public View getView(int position, View convertView, ViewGroup parent) {
			 View v = convertView;

			 if (v == null) {
				 LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
}
