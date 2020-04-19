package com.example.unitix;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.models.Event;
import com.example.unitix.models.Show;
import com.example.unitix.models.User;
import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.Group;

import org.w3c.dom.Text;

public class GroupPageActivity extends AppCompatActivity {

    DataSource ds = new DataSource();
    Event event;
    Group group;
    User user;
    private String email;
    String groupID;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();
        name = intent.getStringExtra("groupName");
        String bio = intent.getStringExtra("bio");
        groupID = intent.getStringExtra("groupID");
        TextView groupName = findViewById(R.id.group_name);
        TextView groupBio = findViewById(R.id.group_description);
        groupName.setText(name);
        groupBio.setText(bio);

        email = intent.getStringExtra("EMAIL");
        this.user = this.ds.getUser(email);

        TextView followingStatus = findViewById(R.id.following_status);
        //Log.e("KARA", "Before checking following status");
        if (checkFollowingStatus()) {
            //Log.e("KARA", "following is true");
            followingStatus.setText("Following " + groupID);
            Button followButton = findViewById(R.id.followbtn);
            followButton.setVisibility(View.INVISIBLE);
        } else {
            //Log.e("KARA", "following is false");
            followingStatus.setText("Not Following " + groupID);
            Button followButton = findViewById(R.id.followbtn);
            followButton.setVisibility(View.VISIBLE);
        }
    }


//    LinearLayout feed = findViewById(R.id.group_feed);
//    TextView followingStatusText = new TextView(getApplicationContext());
//            followingStatusText.setText("Following " + name);
//            feed.addView(followingStatusText);
    public void onFollowButtonClick(View v) {
//        Log.e("KARA", "before ds.follow group groupID: " + groupID);
//        if (ds.followGroup(email, groupID)) {
//            showFollowSuccessToast();
//        } else {
//            showFollowFailureToast();
//        }
        Button followButton = findViewById(R.id.followbtn);
        followButton.setVisibility(View.GONE);
        TextView followingStatus = findViewById(R.id.following_status);
        followingStatus.setText("Following " + name);
    }

//    void addFollowButton() {
//        LinearLayout feed = findViewById(R.id.group_feed);
//        final Button followButton = new Button(getApplicationContext());
//        followButton.setText("Follow");
//        //followButton.setTag(groupID);
//        Log.e("KARA", "in addFollowButton groupID: " + groupID);
//        feed.addView(followButton);
//        followButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO: DATABASE STUFF TO ADD GROUP TO FOLLOWING
//                Log.e("KARA", "before ds.follow group groupID: " + groupID);
//                if (ds.followGroup(email, groupID)) {
//                    showFollowSuccessToast();
//                } else {
//                    showFollowFailureToast();
//                }
//                followButton.setVisibility(View.GONE);
//                TextView followingStatus = findViewById(R.id.following_status);
//                followingStatus.setText("Following " + name);
//            }
//        });
//    }


    boolean checkFollowingStatus() {
       String[] following = user.followers;
        //Log.e("KARA", "entered checkFollowingStatus");
        //Log.e("KARA", "followers length: " + following.length);
       for (int i = 0; i < following.length; i++) {

           if (following[i].equals(groupID)) {
               return true;
           }
       }
       return false;
    }

    void showFollowSuccessToast() {
        Toast.makeText(getApplicationContext(), "group followed!", Toast.LENGTH_LONG).show();
    }

    void showFollowFailureToast() {
        Toast.makeText(getApplicationContext(), "an error has occurred...", Toast.LENGTH_LONG).show();
    }

}
