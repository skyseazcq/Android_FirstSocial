package com.chaoqun.firstsocial;

import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chaoqun.firstsocial.typo.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class FriendsFragment extends ListFragment{

	public static final String TAG = FriendsFragment.class.getSimpleName();
	
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected List<ParseUser> mFriends;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends,
				container, false);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshFriends();
	}

	protected void refreshFriends() {
		getActivity().setProgressBarIndeterminateVisibility(true);
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if (e == null) {
					getActivity().setProgressBarIndeterminateVisibility(false);
					mFriends = friends;
					String[] friendnames = new String[mFriends.size()];
					int i = 0;
					for(ParseUser friend : mFriends) {
						friendnames[i++] = friend.getUsername();
					}
					
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(),
							android.R.layout.simple_list_item_1,
							friendnames);
					setListAdapter(adapter);
					
					/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							getListView().getContext(),
							android.R.layout.simple_list_item_1,
							friendnames);
					setListAdapter(adapter);*/
				}
				else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
					builder.setMessage(e.getMessage())
						.setTitle(R.string.error_title)
						.setPositiveButton(android.R.string.ok, null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d(TAG, mFriends.get(position).getUsername());		
		Intent intent = new Intent(getActivity(), FriendDetailActivity.class);
		intent.putExtra(ParseConstants.KEY_USERNAME, mFriends.get(position).getUsername());
		intent.putExtra(ParseConstants.KEY_FIRSTNAME, mFriends.get(position).getString(ParseConstants.KEY_FIRSTNAME));
		intent.putExtra(ParseConstants.KEY_LASTNAME, mFriends.get(position).getString(ParseConstants.KEY_LASTNAME));
		intent.putExtra(ParseConstants.KEY_HOMETOWN, mFriends.get(position).getString(ParseConstants.KEY_HOMETOWN));
		intent.putExtra(ParseConstants.KEY_INTEREST, mFriends.get(position).getString(ParseConstants.KEY_INTEREST));
		startActivity(intent);
	}
	
}
