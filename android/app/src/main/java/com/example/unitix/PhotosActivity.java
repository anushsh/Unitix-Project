package com.example.unitix;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.server.DataSource;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PhotosActivity extends AppCompatActivity {

    protected DataSource ds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        this.ds = DataSource.getInstance();


        LinearLayout imageFeed = findViewById(R.id.photos_feed);

        String[] urls = new String[]{
                "https://unitixphotos.s3.amazonaws.com/legend.jpg",
                "https://unitixphotos.s3.amazonaws.com/concorde.jpg"
        };

        for (int i = 0; i < urls.length; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Picasso.with(this).load(urls[i]).into(imageView);
            imageFeed.addView(imageView);
        }
    }
}
