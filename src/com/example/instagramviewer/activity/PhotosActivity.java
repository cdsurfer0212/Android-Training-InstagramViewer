package com.example.instagramviewer.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.instagramviewer.R;
import com.example.instagramviewer.adapter.PhotosAdapter;
import com.example.instagramviewer.model.Photo;
import com.example.instagramviewer.model.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class PhotosActivity extends Activity {
    
    private PhotosAdapter photosAdapter;
    private ArrayList<Photo> photos;
    
    private ListView lvPhotos;
    private SwipeRefreshLayout swipeContainer;
    
    public static final String CLIENT_ID = "12bf70aa7b504e9a84ff9ce3c16ac75b";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        
        fetchPopularPhotos();
        setupPhotoListListner();
        
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos();
            } 
        });
        
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright, 
            android.R.color.holo_green_light, 
            android.R.color.holo_orange_light, 
            android.R.color.holo_red_light
        );
        
        lvPhotos.setOnScrollListener(new AbsListView.OnScrollListener() {  
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              int topRowVerticalPosition = (lvPhotos == null || lvPhotos.getChildCount() == 0) ? 0 : lvPhotos.getChildAt(0).getTop();
              swipeContainer.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }

    private void fetchPopularPhotos() {
        // https://api.instagram.com/v1/media/popular?client_id=12bf70aa7b504e9a84ff9ce3c16ac75b
        
        photos = new ArrayList<Photo>();
        
        // Create adapter bind it to the data in arraylist
        photosAdapter = new PhotosAdapter(this, photos);
        
        // Populate the data into the listview
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(photosAdapter);
        
        // Setup the popular url endpoint
        String popularUrl = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;
        
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
                
                JSONArray photosJson = null;
                try {
                    photos.clear();
                    photosJson = response.getJSONArray("data");
                    for (int i = 0; i < photosJson.length(); i++) {
                        JSONObject photoJson = photosJson.getJSONObject(i); 
                        
                        Photo photo = new Photo();
                        photo.setId(photoJson.getString("id"));
                        photo.setImageUrl(photoJson.getJSONObject("images").getJSONObject("standard_resolution").getString("url"));
                        photo.setImageHeight(photoJson.getJSONObject("images").getJSONObject("standard_resolution").getInt("height"));
                        photo.setLikesCount(photoJson.getJSONObject("likes").getInt("count"));
                        
                        if (!photoJson.isNull("caption") && photoJson.getJSONObject("caption") != null) {
                            photo.setCaption(photoJson.getJSONObject("caption").getString("text"));
                        }
                        
                        // set user
                        User user = new User(); 
                        user.setFullName(photoJson.getJSONObject("user").getString("full_name"));
                        user.setProfilePicture(photoJson.getJSONObject("user").getString("profile_picture"));                   
                        photo.setUser(user);
                        
                        photos.add(photo);
                        Log.i("DEBUG", photo.toString());
                    }
                    photosAdapter.notifyDataSetChanged();
                    
                    // ...the data has come back, finish populating listview...
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
  
    }

    private void setupPhotoListListner() {
        lvPhotos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                // Create the intent
                Intent intent = new Intent(getBaseContext(), PhotoActivity.class);
                
                // Define the parameters
                intent.putExtra("mediaId", photos.get(pos).getId());
                
                // Excute the intent
                startActivity(intent);
            }
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photos, menu);
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
}
