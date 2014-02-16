package com.pshetye.timelines;

import com.pshetye.timelines.TwitterUser;
import com.google.gson.annotations.SerializedName;

public class Tweet {

	@SerializedName("created_at")
	private String DateCreated;

	@SerializedName("id")
	private String Id;

	@SerializedName("text")
	private String Text;

	@SerializedName("in_reply_to_status_id")
	private String InReplyToStatusId;

	@SerializedName("in_reply_to_user_id")
	private String InReplyToUserId;

	@SerializedName("in_reply_to_screen_name")
	private String InReplyToScreenName;

	@SerializedName("user")
	private TwitterUser User;
	
	@SerializedName("geo")
	private GeoLocation Geo;

	public String getDateCreated() {
		return DateCreated;
	}
	
	public String getId() {
		return Id;
	}

	public String getInReplyToScreenName() {
		return InReplyToScreenName;
	}

	public String getInReplyToStatusId() {
		return InReplyToStatusId;
	}

	public String getInReplyToUserId() {
		return InReplyToUserId;
	}

	public String getText() {
		return Text;
	}

	public void setDateCreated(String dateCreated) {
		DateCreated = dateCreated;
	}

	public void setId(String id) {
		Id = id;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		InReplyToScreenName = inReplyToScreenName;
	}
	
	public void setInReplyToStatusId(String inReplyToStatusId) {
		InReplyToStatusId = inReplyToStatusId;
	}
	
	public void setInReplyToUserId(String inReplyToUserId) {
		InReplyToUserId = inReplyToUserId;
	}
	
	public void setText(String text) {
		Text = text;
	}

	public void setUser(TwitterUser user) {
		User = user;
	}
	
	public void setGeo(GeoLocation geo) {
		Geo = geo;
	}
	
	public GeoLocation getGeo() {
		return Geo;
	}

	public TwitterUser getUser() {
		return User;
	}

	@Override
	public String  toString(){
		return getText();
	}
	
	public class GeoLocation {
		float latitude;
		float longitude;
		
		public GeoLocation(float l1, float l2) {
			latitude = l1;
			longitude = l2;
		}
		
		public float getLatitude() {
			return latitude;
		}
		
		public float getLongitude() {
			return longitude;
		}
		
		public void setLatitude(float l1) {
			latitude = l1;
		}
		
		public void setLongitude(float l2) {
			longitude = l2;
		}
	}
}