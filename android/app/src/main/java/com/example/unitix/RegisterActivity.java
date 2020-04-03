package com.example.unitix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, first_name, last_name, phone;
    Button create_account;
    DataSource ds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone);
        create_account = (Button) findViewById(R.id.create_account_btn);
        ds = new DataSource();

    }

    public void onCreateAccountButtonClick(View v) {

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String firstNameText = first_name.getText().toString();
        String lastNameText = last_name.getText().toString();
        String phoneText = phone.getText().toString();

        User u = ds.getUser(emailText);

        if (u != null) {
            //account exists - please login
            finish();
        }

        if (emailText.length() > 0 && passwordText.length() > 0) {
            ds.createUser(emailText, passwordText, firstNameText, lastNameText, phoneText);
        }

        //else toast to enter the details and stay on the page and send an error message
    }

//    private class createUserTask extends AsyncTask<String, String, boolean> {
//        protected boolean doInBackground(String... message) {
//            return true;
//        }
//
//        }
//
//    }
}