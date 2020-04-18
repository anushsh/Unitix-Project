package com.example.unitix.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.User;

public class MainActivity extends AppCompatActivity {

    EditText email,password;
    Button login, register;
    DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginbtn);
        register = (Button)findViewById(R.id.registerbtn);
        ds = new DataSource();

    }


    public void onLoginButtonClick(View v) {

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();

        if (emailText.length() > 0 && passwordText.length() > 0) {
            User u = ds.getUser(emailText);
            if (u != null) {
                String currPassword = u.password;

                if (currPassword.equals(passwordText)) {
                    Intent i = new Intent(this, DashboardActivity.class);

                    // pass along current user email
                    i.putExtra("EMAIL", emailText);
                    startActivityForResult(i, 1);
                } else {
                    Toast.makeText(getApplicationContext(), "Password is incorrect", Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(getApplicationContext(), "User and email does not exist", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please enter a username and a password", Toast.LENGTH_LONG).show();

        }

        //show toast that info is incorrect is user exists
        //if user doesn't exist, ask them to register

    }

    public void onRegisterButtonClick(View v) {

        Intent i = new Intent(this, RegisterActivity.class);

        startActivityForResult(i, 1);
    }
//
//    // this is simply a temporary button to get to skip login
//    public void onEventsListButtonClick(View v) {
//        Intent i = new Intent(this, DashboardActivity.class);
//
//        startActivityForResult(i, 1);
//    }
//
//    // this is simply a temporary button to get to skip login
//    public void onProfileButtonClick(View v) {
//        Intent i = new Intent(this, ProfileActivity.class);
//
//        startActivityForResult(i, 1);
//    }
}
