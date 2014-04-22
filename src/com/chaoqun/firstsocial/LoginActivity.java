package com.chaoqun.firstsocial;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoqun.firstsocial.typo.ParseConstants;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class LoginActivity extends Activity {
	
	private static final String TAG = LoginActivity.class.getSimpleName();
	
	protected TextView mSignUpTextView;
	protected TextView mResetTextView;

	protected EditText mUsername;
	protected EditText mPassword;
	protected Button mLoginButton;
	
	protected Button mFbLoginButton;
	protected Dialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_login);
		
		mSignUpTextView = (TextView)findViewById(R.id.signUpText);
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});
		mResetTextView = (TextView)findViewById(R.id.passwordForgotText);
		mResetTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
				startActivity(intent);
			}
		});
		
		mUsername = (EditText)findViewById(R.id.usernameField);
		mPassword = (EditText)findViewById(R.id.passwordField);
		mLoginButton = (Button)findViewById(R.id.loginButton);
		
		mFbLoginButton = (Button) findViewById(R.id.fbButton);
		mFbLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFbLoginButtonClicked();
			}
		});
		
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString().trim();
				String password = mPassword.getText().toString().trim();
								
				if (username.isEmpty() || password.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message)
						.setTitle(R.string.login_error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					// Login
					setProgressBarIndeterminateVisibility(true);
					
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						@Override
						public void done(ParseUser user, ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							
							if (e == null) {
								// Success!
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else {
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setMessage(e.getMessage())
									.setTitle(R.string.login_error_title)
									.setPositiveButton(android.R.string.ok, null);
								AlertDialog dialog = builder.create();
								dialog.show();
							}
						}
					});
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onFbLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException err) {
				
				if (user == null) {
					LoginActivity.this.progressDialog.dismiss();
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else {
					if (user.isNew()) {
						Log.d(TAG, "User signed up and logged in through Facebook!");
						onFbSignUp(); // change username(first time, username is randomly assigned) to real username
									// inside onFbSignUp(), intent of MainActivity would start if successful
					}
					else {
						Log.d(TAG, "User logged in through Facebook!");
						LoginActivity.this.progressDialog.dismiss();
						Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
					}					
				}
			}
		});
	}
	
	private void onFbSignUp(){
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}
		else {
			LoginActivity.this.progressDialog.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
			builder.setMessage("Could not get Facebook user's information!")
				.setTitle(R.string.login_error_title)
				.setPositiveButton(android.R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
	}

	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							
							ParseUser newUser = ParseUser.getCurrentUser();
							newUser.setUsername(user.getName());
							
							newUser.setPassword(user.getId());
							newUser.put(ParseConstants.KEY_FIRSTNAME, user.getFirstName());
							newUser.put(ParseConstants.KEY_LASTNAME, user.getLastName());
							if (user.getLocation().getProperty("name") != null) {
								newUser.put(ParseConstants.KEY_HOMETOWN, 
										(String) user.getLocation().getProperty("name"));
							}
							newUser.saveInBackground(new SaveCallback() {
								
								@Override
								public void done(ParseException e) {
									LoginActivity.this.progressDialog.dismiss();
									if (e == null) {
										// Success!
										Intent intent = new Intent(LoginActivity.this, MainActivity.class);
										intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
										startActivity(intent);
									}
									else {
										AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
										builder.setMessage(e.getMessage())
										.setTitle(R.string.signup_error_title)
										.setPositiveButton(android.R.string.ok, null);
										AlertDialog dialog = builder.create();
										dialog.show();
									}
								}
							});							

						}
						else if (response.getError() != null) {
							LoginActivity.this.progressDialog.dismiss();
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.d(TAG, getString(R.string.fb_session_invalid));
							} else {
								Log.d(TAG, "Some other error: " + response.getError().getErrorMessage());
							}
							Toast.makeText(LoginActivity.this, getString(R.string.fb_session_invalid), Toast.LENGTH_LONG).show();
						}
					}
				});
		
		request.executeAsync();
		
	}
}
