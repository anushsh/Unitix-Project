package com.example.unitix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.unitix.server.DataSource;
import com.example.unitix.R;
import com.example.unitix.models.User;

public class EditProfileActivity extends AppCompatActivity {

    TextView email;
    EditText password, first_name, last_name, phone;
    Button save, cancel;
    DataSource ds;
    String emailString;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        email = (TextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone);
        save = (Button) findViewById(R.id.savebtn);
        cancel = (Button) findViewById(R.id.cancelbtn);
        ds = new DataSource();

        Intent intent = getIntent();
        emailString = intent.getStringExtra("EMAIL");
        this.user = this.ds.getUser(emailString);

        initializeFields();



    }

    private void initializeFields() {
        email.setText(user.getId());
        password.setText(user.password);
        first_name.setText(user.firstName);
        last_name.setText(user.lastName);
        phone.setText(user.phone);

    }

    public void onSaveButtonClick(View v) {

        String passwordText = password.getText().toString();
        String firstNameText = first_name.getText().toString();
        String lastNameText = last_name.getText().toString();
        String phoneText = phone.getText().toString();

        if (passwordText.length() > 0) {
            ds.updateUser(emailString, passwordText, firstNameText, lastNameText, phoneText);
            Toast.makeText(getApplicationContext(), "Profile successfully updated", Toast.LENGTH_LONG).show();

            finish();
            Log.d("Yash", "User Updated");


        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid password", Toast.LENGTH_LONG).show();
        }

    }

    public void onCancelButtonClick(View v) {

        finish();
    }
}
