package com.example.instagramviewer.fragment;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.instagramviewer.R;
import com.example.instagramviewer.activity.PhotosActivity;
import com.example.instagramviewer.adapter.CommentsAdapter;
import com.example.instagramviewer.model.Comment;
import com.example.instagramviewer.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class CommentsDialog extends DialogFragment {
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> comments;
    
    private ListView lvComments;

    public CommentsDialog() {
    }

    public static CommentsDialog newInstance(String mediaId) {
        CommentsDialog frag = new CommentsDialog();
        Bundle args = new Bundle();
        args.putString("mediaId", mediaId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        comments = new ArrayList<Comment>();
        commentsAdapter = new CommentsAdapter(getActivity(), comments);
        
        View view = inflater.inflate(R.layout.fragment_comments, container);
        lvComments = (ListView) view.findViewById(R.id.lvComments);
        lvComments.setAdapter(commentsAdapter); 
        
        String mediaId = getArguments().getString("mediaId");     
        fetchComments(mediaId);
        
        getDialog().requestWindowFeature(DialogFragment.STYLE_NO_TITLE | DialogFragment.STYLE_NORMAL);
        //getDialog().setCancelable(true);
        //getDialog().setCanceledOnTouchOutside(true);
        //getDialog().setTitle("title");
        
        return view;
    }
    
    private void fetchComments(String mediaId) {
        // Setup the popular url endpoint
        String popularUrl = "https://api.instagram.com/v1/media/" + mediaId + "/comments?client_id=" + PhotosActivity.CLIENT_ID;
        
        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();
        
        // Trigger the network request
        client.get(popularUrl, new JsonHttpResponseHandler() {
            // Define success and failure callbacks
            
            // Handle the successful response 
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Fired once the successful response back
                //Log.i("INFO", response.toString());
                
                JSONArray commentsJson = null;
                try {
                    commentsJson = response.getJSONArray("data");
                    populateCommentList(commentsJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
            
            private void populateCommentList(JSONArray commentsJson) {
                try {
                    comments.clear();
                    for (int i = 0; i < commentsJson.length(); i++) {
                        JSONObject commentJson = commentsJson.getJSONObject(i);
                        
                        Comment comment = new Comment();
                        comment.setText(commentJson.getString("text"));
                        comment.setTimestamp(commentJson.getString("created_time"));
                        
                        // set user
                        User user = new User();
                        user.setFullName(commentJson.getJSONObject("from").getString("full_name"));
                        user.setProfilePicture(commentJson.getJSONObject("from").getString("profile_picture"));
                        comment.setUser(user);
                        
                        comments.add(comment);
                    }
                    
                    commentsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
