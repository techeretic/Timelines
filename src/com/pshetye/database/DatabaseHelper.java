package com.pshetye.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.pshetye.tweets.TwitterUser;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	 
    // Database Name
    private static final String DATABASE_NAME = "TwitterUsers";
 
    // Contacts table name
    private static final String TABLE_NAME = "TwitterUser";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SCREEN_NAME = "screenName";
    private static final String KEY_NAME = "name";
    private static final String KEY_PROFURL = "profurl";
    
    private static int notes;

	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
				+ KEY_SCREEN_NAME + " TEXT, " 
				+ KEY_NAME + " TEXT, " 
				+ KEY_PROFURL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	// Adding new TwitterUser
	public void addTwitterUser(TwitterUser twiU) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_ID, twiU.getId()); 
	    values.put(KEY_SCREEN_NAME, twiU.getScreenName()); 
	    values.put(KEY_NAME, twiU.getName()); 
	    values.put(KEY_PROFURL, twiU.getProfileImageUrl()); 
	 
	    // Inserting Row
	    db.insert(TABLE_NAME, null, values);
	    db.close(); // Closing database connection		
	}
	 
	// Getting single contact
	public TwitterUser getTwitterUser(long _id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
	    		KEY_SCREEN_NAME, KEY_NAME, KEY_PROFURL }, KEY_ID + "=?",
	            new String[] { String.valueOf(_id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    TwitterUser note = new TwitterUser(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
	    		cursor.getString(2), cursor.getString(3));
	    // return Note
	    return note;
	}
	 
	// Getting All Contacts
	public List<TwitterUser> getAllTwitterUsers() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<TwitterUser> TwitterUsers = new ArrayList<TwitterUser>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME;
		
		Cursor cursor = db.rawQuery(selectQuery, null);        
		
		// looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	TwitterUser twiu = new TwitterUser(Integer.parseInt(cursor.getString(0)), cursor.getString(1), 
	        			cursor.getString(2), cursor.getString(3));
	            // Adding contact to list
	        	TwitterUsers.add(twiu);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return TwitterUsers;
	}
	
	// Getting All Contacts
	public List<String> getAllStringNotes() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<String> noteList = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + TABLE_NAME; // + " ORDER BY " + KEY_ID + " DESC";
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		notes = cursor.getCount();
		
		// looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            // Adding contact to list
	            noteList.add(cursor.getString(1));
	        } while (cursor.moveToNext());
	    }
	    // return contact list
	    return noteList;
	}
	 
	// Getting contacts Count
	public int getTwitterUsersCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_NAME;
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		return cursor.getCount();
	}
	 
	// Deleting single contact
	public void deleteTwitterUser(TwitterUser twiU) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_NAME, KEY_ID + " = ?",
	            new String[] { String.valueOf(twiU.getId()) });
	    if (twiU.getId() != 0 && twiU.getId() < notes) {
	    	
	    }
	    db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
