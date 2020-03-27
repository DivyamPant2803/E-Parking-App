package com.example.paytmgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 3;
    FirebaseAuth mAuth;
    EditText contactNumber;
    Button loginPhone;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FirebaseApp.initializeApp(this);

        loginPhone = findViewById(R.id.loginPhone);
        contactNumber = findViewById(R.id.contactNumber);
        mAuth = FirebaseAuth.getInstance();
        countryCodePicker=findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(contactNumber);

        loginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(contactNumber.getText().toString())){
                    Toast.makeText(LoginActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                }
                else if(contactNumber.getText().toString().replace(" ","").length()!=10){
                    Toast.makeText(LoginActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(LoginActivity.this,VerificationActivity.class);
                    intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                    startActivity(intent);
                }
            }
        });

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                }
            }
        };


    }
}
