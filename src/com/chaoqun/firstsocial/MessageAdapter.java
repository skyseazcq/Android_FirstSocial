package com.chaoqun.firstsocial;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaoqun.firstsocial.typo.ParseConstants;
import com.parse.ParseObject;


//customed adapter useful
public class MessageAdapter extends ArrayAdapter<ParseObject> {
	
	protected static final String TAG = MessageAdapter.class.getSimpleName();
	protected Context mContext;
	protected List<ParseObject> mMessages;
	
	public MessageAdapter(Context context, List<ParseObject> messages) {
		super(context, R.layout.message_item, messages);
		mContext = context;
		mMessages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, null);
			holder = new ViewHolder();
			holder.iconImageView = (ImageView)convertView.findViewById(R.id.messageIcon);
			holder.nameLabel = (TextView)convertView.findViewById(R.id.senderLabel);
			holder.timeLabel = (TextView)convertView.findViewById(R.id.sendTime);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		ParseObject message = mMessages.get(position);
		
		if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
			holder.iconImageView.setImageResource(R.drawable.ic_action_picture);
		}
		else if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_VIDEO)){
			holder.iconImageView.setImageResource(R.drawable.ic_action_play_over_video);
		}
		else { //text
			holder.iconImageView.setImageResource(R.drawable.ic_action_unread);
		}
		holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));
		Date date = new Date();
		long after = date.getTime() - message.getCreatedAt().getTime();
		holder.timeLabel.setText(getTimeDiffString(after));
		//Log.d(TAG, date.toString());
		//Log.d(TAG, message.getCreatedAt().toString());
		//Log.d(TAG, getTimeDiffString(after));
		
		return convertView;
	}
	
	
	private String getTimeDiffString(long after) { //after >= 0 in millisecond
		if(after < 0) after = 0;
		after = after/1000/60;
		if(after < 60) return after + (after <= 1? " minute ago":" minutes ago");
		after = after/60;
		if(after < 24) return "about " + after + (after == 1? " hour ago": " hours ago");
		after = after/24;
		if(after < 30) return "about " + after + (after == 1? " day ago": " days agao"); //estimated
		after = after/30;
		if(after < 12) return "about " + after + (after == 1? " month ago": " months ago");
		after = after/12;
		return "about " + after + (after == 1? " year ago": " years ago");
	}


	//about static class in java.  http://www.geeksforgeeks.org/static-class-in-java/
	private static class ViewHolder {
		ImageView iconImageView;
		TextView nameLabel;
		TextView timeLabel;
	}
	
	public void refill(List<ParseObject> messages) {
		mMessages.clear();
		mMessages.addAll(messages);
		notifyDataSetChanged();// tells the ListView that the data has been modified; 
								//and to show the new data, the ListView must be redrawn
	}
}






