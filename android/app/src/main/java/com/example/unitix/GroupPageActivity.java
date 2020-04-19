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
import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.Group;

public class GroupPageActivity extends AppCompatActivity {

    DataSource ds;
    Event event;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        this.ds = new DataSource();

        Intent intent = getIntent();
        String name = intent.getStringExtra("groupName");
        String bio = intent.getStringExtra("bio");
//        this.user = this.ds.getUser(getIntent().getStringExtra("EMAIL"));
        TextView groupName = findViewById(R.id.group_name);
        TextView groupBio = findViewById(R.id.group_description);
        groupName.setText(name);
        groupBio.setText(bio);

    }

}
