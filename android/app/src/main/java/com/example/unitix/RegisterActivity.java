package com.example.unitix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unitix.server.DataSource;
import com.example.unitix.models.User;

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
        ds = DataSource.getInstance();

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
            Toast.makeText(getApplicationContext(), "User exists, please login instead", Toast.LENGTH_LONG).show();
            Log.d("Yash","User exists");
            finish();
        } else if (emailText.length() > 0 && passwordText.length() > 0) {
            ds.createUser(emailText, passwordText, firstNameText, lastNameText, phoneText);
            Toast.makeText(getApplicationContext(), "Account created", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, DashboardActivity.class);
            // pass along current user email
            i.putExtra("EMAIL", emailText);
            startActivityForResult(i, 1);

        } else {
            Toast.makeText(getApplicationContext(), "Please enter a username and a password", Toast.LENGTH_LONG).show();


            //else toast to enter the details and stay on the page and send an error message
        }


    }

}