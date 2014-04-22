package com.chaoqun.firstsocial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ResetPasswordActivity extends Activity {

	protected EditText mUsername;
	protected EditText mEmail;
	protected Button mResetButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_resetpassword);
		
		mUsername = (EditText)findViewById(R.id.usernameField);
		mEmail = (EditText)findViewById(R.id.emailField);
		mResetButton = (Button)findViewById(R.id.resetButton);
		
		mResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString().trim();
				String email = mEmail.getText().toString().trim();
								
				if (username.isEmpty() || email.isEmpty()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
					builder.setMessage(R.string.reset_error_message)
						.setTitle(R.string.reset_error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
				else {
					// reset the password!
					setProgressBarIndeterminateVisibility(true);
					
					ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
						  @Override						
						  public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);
							
							if (e == null) {
								// Success! Email is send.
								Toast.makeText(ResetPasswordActivity.this, R.string.email_send_for_reset_password, Toast.LENGTH_LONG).show();
								
								Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
							}
							else {
								AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
								builder.setMessage(e.getMessage())
									.setTitle(R.string.reset_error_title)
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

	
}
