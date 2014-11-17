package com.example.instagramviewer.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagramviewer.R;
import com.example.instagramviewer.adapter.CommentsAdapter;
import com.example.instagramviewer.fragment.CommentsDialog;
import com.example.instagramviewer.model.Comment;
import com.example.instagramviewer.model.Photo;
import com.example.instagramviewer.model.User;
import com.example.instagramviewer.widget.CustomLinearLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends FragmentActivity {
 
    private String mediaId;
    private Photo photo;
    private CommentsAdapter commentsAdapter;
    private ArrayList<Comment> comments;
    
    private CircularImageView civProfilePicture;
    private CustomLinearLayout cllComments;
    //private CustomListView clvComments;
    private ImageView ivPhoto;
    private TextView tvCaption;
    private TextView tvFullName;
    private TextView tvCommentsCount;
    private TextView tvLikesCount;
    private TextView tvRelativeTimestamp;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        
        // Extract the extras from the bundle
        mediaId = getIntent().getStringExtra("mediaId");
        fetchPhoto();
    }

    private void fetchPhoto() {
        // https://api.instagram.com/v1/media/<id>?client_id=12bf70aa7b504e9a84ff9ce3c16ac75b

        // Setup the popular url endpoint
        String popularUrl = "https://api.instagram.com/v1/media/" + mediaId + "?client_id=" + PhotosActivity.CLIENT_ID;
        
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
                
                JSONObject photoJson = null;
                try {
                    photoJson = response.getJSONObject("data");

                    photo = new Photo();
                    photo.setId(photoJson.getString("id"));
                    photo.setImageUrl(photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                    photo.setImageHeight(photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
                    photo.setCommentsCount(photoJson.getJSONObject("comments").getInt("count"));
                    photo.setLikesCount(photoJson.getJSONObject("likes").getInt("count"));
                    photo.setTimestamp(photoJson.getString("created_time"));
                    
                    if (photoJson.getJSONObject("caption") != null) {
                        photo.setCaption(photoJson.getJSONObject("caption").getString("text"));
                    }

                    // set user
                    User user = new User();
                    user.setFullName(photoJson.getJSONObject("user").getString("full_name"));
                    user.setProfilePicture(photoJson.getJSONObject("user").getString("profile_picture"));
                    photo.setUser(user);
                    
                    civProfilePicture = (CircularImageView) findViewById(R.id.civProfilePicture);
                    if (photo.getUser().getProfilePicture() != null)
                        Picasso.with(getBaseContext()).load(photo.getUser().getProfilePicture()).into(civProfilePicture);
                    
                    ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
                    if (photo.getImageUrl() != null)
                        Picasso.with(getBaseContext()).load(photo.getImageUrl()).into(ivPhoto);
                    
                    tvCaption = (TextView) findViewById(R.id.tvCaption);
                    tvCaption.setText(photo.getCaption());
                    
                    tvFullName = (TextView) findViewById(R.id.tvFullName);
                    tvFullName.setText(photo.getUser().getFullName());
                    
                    tvCommentsCount = (TextView) findViewById(R.id.tvCommentsCount);
                    tvCommentsCount.setText(String.valueOf(photo.getCommentsCount()));
                    
                    tvLikesCount = (TextView) findViewById(R.id.tvLikesCount);
                    tvLikesCount.setText(String.valueOf(photo.getLikesCount()));
                    
                    tvRelativeTimestamp = (TextView) findViewById(R.id.tvRelativeTimestamp);
                    tvRelativeTimestamp.setText(DateUtils.getRelativeTimeSpanString(Long.valueOf(photo.getTimestamp()) * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
                    
                    populateCommentList(photoJson);
                    
                    Log.i("DEBUG", photo.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
            
            private void populateCommentList(JSONObject photoJson) {
                comments = new ArrayList<Comment>();
                commentsAdapter = new CommentsAdapter(getBaseContext(), comments);
                
                cllComments = (CustomLinearLayout) findViewById(R.id.cllComments);
                cllComments.setAdapter(commentsAdapter);
                
                //clvComments = (CustomListView) findViewById(R.id.clvComments);
                //clvComments.setAdapter(commentsAdapter);
                
                JSONArray commentsJson = null;
                try {
                    comments.clear();
                    commentsJson = photoJson.getJSONObject("comments").getJSONArray("data");
                    for (int i = 0; i < 2; i++) {
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
                    cllComments.bindLinearLayout();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
  
    }
    
    private void showCommentsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        CommentsDialog commentsDialog = CommentsDialog.newInstance(mediaId);
        commentsDialog.show(fm, "fragment_comments");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onShowCommentsDialog(View v) {
        showCommentsDialog();
    }
}
