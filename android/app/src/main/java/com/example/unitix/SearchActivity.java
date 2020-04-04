package com.example.unitix;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    DataSource ds = new DataSource();
    User user;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable);
        Intent intent = getIntent();
        this.user = this.ds.getUser(intent.getStringExtra("EMAIL"));

    }
}
