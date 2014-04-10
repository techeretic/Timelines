package com.pshetye.timelines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pshetye.tweets.TwitterUsers;

public class FindUsersFragment extends Fragment {
	
	EditText SearchText;
	ListView listview;
	Button FetchUsers;
	
	Toast t;
	MyUserListAdapter myladapter;
	ProgressDialog progressDialog;
	LinearLayout fetchTimeline, userProfile;
	
	boolean validHandle;
	
	OnTwitterUserSelectedListener mCallBack;

	public FindUsersFragment() {
		//Standard Constructor
	}
	
	// Container Activity must implement this interface
    public interface OnTwitterUserSelectedListener {
        public void onUserSelected(String ScreenName, String profileURL);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_search_profile, container, false);
        
        return rootView;
    }
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		try {
			mCallBack = (OnTwitterUserSelectedListener) activity;
		} catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTwitterUserSelectedListener");
        }
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
        
		FetchUsers = (Button) getActivity().findViewById(R.id.FetchTweets);
		SearchText = (EditText) getActivity().findViewById(R.id.ScreenName);
		listview = (ListView) getActivity().findViewById(R.id.list);
		fetchTimeline = (LinearLayout) getActivity().findViewById(R.id.lview);
		userProfile = (LinearLayout) getActivity().findViewById(R.id.user_lview);
		
		userProfile.setVisibility(LinearLayout.GONE);
		
		FetchUsers.setText("Add User(s)");
		SearchText.setHint("Type Name to search");

		FetchUsers.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (!SearchText.getText().toString().isEmpty()) {
					FetchTwitterUsers(SearchText.getText().toString());
				}				
				MainActivity.hideSoftKeyboard(getActivity(), getView());
			}
		});
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				mCallBack.onUserSelected(((TextView) view.findViewById(R.id.Name)).getText().toString(),
									((ImageView) view.findViewById(R.id.Profile)).getContentDescription().toString());
			}
			
		});
		
	}
	
	// download twitter timeline after first checking to see if there is a network connection
	public void FetchTwitterUsers(String SearchText) {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new DownloadTwitterTask().execute(SearchText);
		} else {
			MainActivity.showToast(t, getActivity(), "No network connection available.");
		}
	}

	// Uses an AsyncTask to download a Twitter user's timeline
	private class DownloadTwitterTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

		@Override
		protected String doInBackground(String... screenNames) {
			String result = null;

			if (screenNames.length > 0) {
				result = getTwitterUsers(screenNames[0]);
			}
			return result;
		}

		// onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
		@Override
		protected void onPostExecute(String result) {
			try {
				TwitterUsers twits = jsonToTwitter(result);
				
				if (validHandle == true) {
		
					// send the tweets to the adapter for rendering
					myladapter = new MyUserListAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, 
							getActivity(), twits);
					listview.setAdapter(myladapter);
					listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		            progressDialog.dismiss();
				}
			} catch (IllegalStateException ex) {
				// just eat the exception
			} catch (NullPointerException ex) {
				// just eat the exception
				SearchText.setText("");
			}
		}

		// converts a string of JSON data into a Twitter object
		private TwitterUsers jsonToTwitter(String result) {
			TwitterUsers twits = null;
			if (result != null && result.length() > 0) {
				try {
					Gson gson = new Gson();
					twits = gson.fromJson(result, TwitterUsers.class);
					validHandle = true;
				} catch (JsonSyntaxException ex) {
					// just eat the exception
					MainActivity.showToast(t, getActivity(), "Invalid Twitter Handle");	
					validHandle = false;	
					SearchText.setText("");				
				}
			} else {
				// just eat the exception
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
					Log.i("Pratham", "Invalid Twitter Handle 2 ");
					MainActivity.showToast(t, getActivity(), "Invalid Twitter Handle");	
					validHandle = false;	
					SearchText.setText("");
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
		
		private String getTwitterUsers(String screenName) {
			String results = null;

			// Step 1: Encode consumer key and secret
			try {
				Log.i("PRATHAM","Inside getTwitterUsers");
				// URL encode the consumer key and secret
				String urlApiKey = URLEncoder.encode(MainActivity.CONSUMER_KEY, "UTF-8");
				String urlApiSecret = URLEncoder.encode(MainActivity.CONSUMER_SECRET, "UTF-8");

				// Concatenate the encoded consumer key, a colon character, and the
				// encoded consumer secret
				String combined = urlApiKey + ":" + urlApiSecret;

				// Base64 encode the string
				String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

				// Step 2: Obtain a bearer token
				HttpPost httpPost = new HttpPost(MainActivity.TwitterTokenURL);
				httpPost.setHeader("Authorization", "Basic " + base64Encoded);
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				httpPost.setEntity(new StringEntity("grant_type=client_credentials"));
				String rawAuthorization = getResponseBody(httpPost);
				Authenticated auth = jsonToAuthenticated(rawAuthorization);
				
				Log.i("PRATHAM","auth.access_token = " + auth.access_token);
				Log.i("PRATHAM","auth.token_type = " + auth.token_type);

				// Applications should verify that the value associated with the
				// token_type key of the returned object is bearer
				if (auth != null && auth.token_type.equals("bearer")) {

					// Step 3: Authenticate API requests with bearer token
					HttpGet httpGet = new HttpGet(MainActivity.TwitterUsersURL + screenName);
					
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
}
