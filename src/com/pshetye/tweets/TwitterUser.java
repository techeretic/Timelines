package com.pshetye.tweets;

import com.google.gson.annotations.SerializedName;


public class TwitterUser {

	@SerializedName("screen_name")
	private String screenName;
	
	@SerializedName("name")
	private String name;
	
	@SerializedName("profile_image_url")
	private String profileImageUrl;
	
	@SerializedName("profile_image_url_https")
	private String profileImageUrlHttps;
	
	@SerializedName("location")
	private String location;

	@SerializedName("id")
	private int Id;
	
	@SerializedName("description")
	private String description;
	
	public int getId() {
		return Id;
	}
	
	public void setId(int Id) {
		this.Id = Id;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public String getProfileImageUrlHttps() {
		return profileImageUrlHttps;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void setProfileImageUrlHttps(String profileImageUrlHttps) {
		this.profileImageUrlHttps = profileImageUrlHttps;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public TwitterUser(int Id, String screenName, String name, String profileImageUrl){
		this.Id = Id;
		this.screenName = screenName;
		this.name = name;
		this.profileImageUrl = profileImageUrl;
	}

	public TwitterUser() {
		// TODO Auto-generated constructor stub
	}
}
