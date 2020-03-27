package com.example.paytmgateway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private Button login,logout;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        login = findViewById(R.id.loginButton);
        logout = findViewById(R.id.logoutButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
                flag = sharedPreferences.getInt("flag",0);
                if(flag==0) {
                    Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(DashboardActivity.this,"User already logged in",Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                Intent intent = new Intent(DashboardActivity.this,LogoutActivity.class);
                intent.putExtra("time",time);

                startActivity(intent);
            }
        });

    }
}
