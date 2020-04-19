package com.example.unitix;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.unitix.server.DataSource;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class PaymentActivity extends AppCompatActivity {
    private Stripe stripe;
    DataSource ds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_gYtpByWDqLF0Y9ERW2dE5D0d"
        );
        Intent intent = getIntent();
        String pricePrelim = intent.getStringExtra("price");
        pricePrelim = pricePrelim.substring(1, pricePrelim.length());
        String[] arr = pricePrelim.split(".");
        Log.e("ANUSH", "Arr length: " + pricePrelim);
//        pricePrelim = arr[0] + arr[1];

//        pricePrelim = (Integer.parseInt(pricePrelim) * 100) + "";
        final String price = pricePrelim;
        ds = new DataSource();

        // Hook up the pay button to the card widget and Stripe instance
        Button payButton = findViewById(R.id.payButton);
        WeakReference<PaymentActivity> weakActivity = new WeakReference<>(this);

        payButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                Card card = cardInputWidget.getCard();
                if (card != null) {
                    // Create a Stripe token from the card details
                    stripe = new Stripe(getApplicationContext(), PaymentConfiguration.getInstance(getApplicationContext()).getPublishableKey());

                }
                stripe.createToken(card, new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(@NonNull Token result) {
                        String tokenID = result.getId();
                        // Send the token identifier to the server...
                        ds.createCharge(tokenID);
                        Log.e("ANUSH", "Token: " + tokenID);
                        Log.e("ANUSH", "Price: " + price);

                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // Handle error
                        Log.e("ANUSH", "Error: " + e);
                    }
                });
            }

        });

    }

}
