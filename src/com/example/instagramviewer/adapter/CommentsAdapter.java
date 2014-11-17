package com.example.instagramviewer.adapter;

import java.util.List;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.instagramviewer.R;
import com.example.instagramviewer.model.Comment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class CommentsAdapter extends ArrayAdapter<Comment> {

    public CommentsAdapter(Context context, List<Comment> comments) {
        super(context, android.R.layout.simple_list_item_1, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);
        
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);
        }
        
        CircularImageView civProfilePicture = (CircularImageView) convertView.findViewById(R.id.civProfilePicture);
        if (comment.getUser().getProfilePicture() != null) {
            // Reset the image from the recycled view
            civProfilePicture.setImageResource(0);
            
            Picasso.with(getContext()).load(comment.getUser().getProfilePicture()).into(civProfilePicture);
        }
        
        TextView tvFullName = (TextView) convertView.findViewById(R.id.tvFullName);
        tvFullName.setText(comment.getUser().getFullName());
        
        TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
        tvComment.setText(comment.getText());
        
        TextView tvRelativeTimestamp = (TextView) convertView.findViewById(R.id.tvRelativeTimestamp);
        tvRelativeTimestamp.setText(DateUtils.getRelativeTimeSpanString(Long.valueOf(comment.getTimestamp()) * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        
        return convertView;
        
    }

    
}
