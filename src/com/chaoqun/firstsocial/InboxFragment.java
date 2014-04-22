package com.chaoqun.firstsocial;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.chaoqun.firstsocial.typo.ParseConstants;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


//currently there is a bug need to be fixed:
//when message is send and return to inbox fragment,
//the new message would not be shown.
//Only after call onResume() again, new message would show again.

//stage: show message
public class InboxFragment extends ListFragment{

	public static final String TAG = InboxFragment.class.getSimpleName();
	protected List<ParseObject> mMessages;
	protected byte[] mBytes;
	private boolean refreshed = false;
	final Handler myHandler = new Handler();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container,
				false);
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		refreshInbox();
	}

	protected void refreshInbox() {
		getActivity().setProgressBarIndeterminateVisibility(true);
		
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				
				if (e == null) {
					// We found messages!
					mMessages = messages;

					String[] usernames = new String[mMessages.size()];
					int i = 0;
					for(ParseObject message : mMessages) {
						usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
						i++;
					}
					
					/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(), 
							android.R.layout.simple_list_item_1, 
							usernames);*/
					//Customed Adapter
					if(getListView().getAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(
								getListView().getContext(), mMessages);
						setListAdapter(adapter);
					}
					else {
						//to maintain your scroll position when you refresh
						((MessageAdapter)getListView().getAdapter()).refill(mMessages);
					}
				}
			}
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri fileUri;
		
		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			// view the image
			fileUri = Uri.parse(file.getUrl());
			Intent intent = new Intent(getActivity(), ViewImageActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		}
		else if (messageType.equals(ParseConstants.TYPE_VIDEO) ){
			// view the video
			fileUri = Uri.parse(file.getUrl());
			Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
			intent.setDataAndType(fileUri, "video/*");
			startActivity(intent);
		}
		else {
			// view the text		
			file.getDataInBackground(new GetDataCallback() {
				
				@Override
				public void done(byte[] data, ParseException e) {
					if(e == null) {
						mBytes = data;
						
						Log.d(TAG, new String(mBytes));
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage(new String(mBytes))
						.setTitle(R.string.text_title)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								refreshed = true;
								refreshInbox();
							}
						});
						final AlertDialog dialog = builder.create();
						dialog.show();
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								dialog.dismiss();
								if(refreshed == false) UpdateGUI();	
								refreshed = false;
							}
						}, 10*1000);
					}
					else {
						Toast.makeText(getActivity(), R.string.error_getting_message, Toast.LENGTH_LONG).show();
					}
				}
			});
			
			
		}
		
		//then delete the message
		List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
		if(ids.size() == 1) {
			//delete message in back-end
			message.deleteInBackground();
		}
		else {
			ids.remove(ParseUser.getCurrentUser().getObjectId());
			ArrayList<String> idsToRemove = new ArrayList<String>();
			idsToRemove.add(ParseUser.getCurrentUser().getObjectId());
			
			message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
			message.saveInBackground();	
			
		}
	}
	
	//fixed the problem: update listview from timer.
	//http://www.techsono.com/consult/update-android-gui-timer/
	private void UpdateGUI() {

		//tv.setText(String.valueOf(i));
		myHandler.post(myRunnable);
	}

	final Runnable myRunnable = new Runnable() {
		public void run() {
			refreshInbox();
		}
	};
	
}
