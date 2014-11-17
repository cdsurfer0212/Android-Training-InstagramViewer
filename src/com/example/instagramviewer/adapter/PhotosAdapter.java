package com.example.instagramviewer.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.instagramviewer.R;
import com.example.instagramviewer.model.Photo;
import com.squareup.picasso.Picasso;

public class PhotosAdapter extends ArrayAdapter<Photo> {

    public PhotosAdapter(Context context, List<Photo> photos) {
        super(context, android.R.layout.simple_list_item_1, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Photo photo = getItem(position);
        
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);
        }

        ImageView imgPhoto = (ImageView) convertView.findViewById(R.id.imgPhoto); 
        imgPhoto.getLayoutParams().height = photo.getImageHeight();
        
        // Reset the image from the recycled view
        imgPhoto.setImageResource(0);
        
        // Background: Send a network request to the url, download the image bytes, convert into bitmap, resizing the image, insert bitmap into the imageview
        Picasso.with(getContext()).load(photo.getImageUrl()).into(imgPhoto);
        
        return convertView;
        
    }

    
}
