package com.chaoqun.firstsocial;

import com.chaoqun.firstsocial.typo.ParseConstants;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FriendDetailActivity extends Activity {

	protected TextView mUsernameTextView;
	protected TextView mFirstnameTextView;
	protected TextView mLastnameTextView;
	protected TextView mHometownTextView;
	protected TextView mInterestTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frienddetail);
		
		mUsernameTextView = (TextView)findViewById(R.id.friendnameField);
		mFirstnameTextView = (TextView)findViewById(R.id.firstnameField);
		mLastnameTextView = (TextView)findViewById(R.id.lastnameField);
		mHometownTextView = (TextView)findViewById(R.id.hometownField);
		mInterestTextView = (TextView)findViewById(R.id.interestField);
		
		String username = getIntent().getExtras().getString(ParseConstants.KEY_USERNAME);
		String firstname = getIntent().getExtras().getString(ParseConstants.KEY_FIRSTNAME);
		String lastname = getIntent().getExtras().getString(ParseConstants.KEY_LASTNAME);
		String hometown = getIntent().getExtras().getString(ParseConstants.KEY_HOMETOWN);
		String interest =getIntent().getExtras().getString(ParseConstants.KEY_INTEREST);
		
		mUsernameTextView.setText(ParseConstants.KEY_USERNAME + ": " + username);
		/*if(firstname != null) mFirstnameTextView.setText(ParseConstants.KEY_FIRSTNAME + ": " + firstname);
		else mFirstnameTextView.setText(ParseConstants.KEY_FIRSTNAME + ": ");
		if(lastname != null) mLastnameTextView.setText(ParseConstants.KEY_LASTNAME + ": " + lastname);
		else mFirstnameTextView.setText(ParseConstants.KEY_LASTNAME + ": ");
		if(hometown != null) mHometownTextView.setText(ParseConstants.KEY_HOMETOWN + ": " + hometown);
		else mFirstnameTextView.setText(ParseConstants.KEY_HOMETOWN + ": ");
		if(interest != null) mInterestTextView.setText(ParseConstants.KEY_INTEREST + ": " + interest);
		else mFirstnameTextView.setText(ParseConstants.KEY_INTEREST + ": ");*/
		mFirstnameTextView.setText(ParseConstants.KEY_FIRSTNAME + ": ");
		if(firstname != null) mFirstnameTextView.append(firstname);
		mLastnameTextView.setText(ParseConstants.KEY_LASTNAME + ": ");
		if(lastname != null) mLastnameTextView.append(lastname);
		mHometownTextView.setText(ParseConstants.KEY_HOMETOWN + ": ");
		if(hometown != null) mHometownTextView.append(hometown);
		mInterestTextView.setText(ParseConstants.KEY_INTEREST + ": ");
		if(interest != null) mInterestTextView.append(interest);
	}

}
