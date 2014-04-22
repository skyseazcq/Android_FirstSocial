package com.chaoqun.firstsocial;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;



public class FirstSocialApplication extends Application {
	
	@Override
	public void onCreate() { 
		super.onCreate();
	    Parse.initialize(this, "HQ25awz7kETzxHy3gHJduOAAtimtMvGhq6vBNeEI", "DvoUdPv3yokHa2N936ubN7Jtkm3yqqNFCB4DEyzE");
	    
	 // Set your Facebook App Id in strings.xml
	 	ParseFacebookUtils.initialize(getString(R.string.app_id));
	}
}
