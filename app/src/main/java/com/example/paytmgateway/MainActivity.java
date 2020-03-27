package com.example.paytmgateway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText name,contactNo,vehicleNo;
    private Button add;
    private String userName,userVehicle,userContact,message,userId,uniqueId;
    private boolean smsSent= false;
    //private int uniqueId;
    //CountryCodePicker countryCodePicker;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =1 ;
    private int flag=0;
    FirebaseAuth mAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        //Intent intent = getIntent();
        //userId = intent.getStringExtra("uid");
        userId = user.getUid();

        name = findViewById(R.id.name);
        contactNo = findViewById(R.id.contact);
        vehicleNo = findViewById(R.id.vehicleNo);
        add = findViewById(R.id.addButton);
        //payment = findViewById(R.id.payment);
        //countryCodePicker=(CountryCodePicker)findViewById(R.id.ccp1);
        //countryCodePicker.registerCarrierNumberEditText(contactNo);

        uniqueId = String.valueOf(gen());
        checkSMSPermission();
        //if(mAuth.getCurrentUser()!=null){
          //  userId =  mAuth.getCurrentUser().getUid();
        //}



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag+=1;
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("flag",flag);
                editor.apply();
                if(TextUtils.isEmpty(contactNo.getText().toString()) || TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(vehicleNo.getText().toString())){
                    Toast.makeText(MainActivity.this, "Enter the details", Toast.LENGTH_SHORT).show();
                }
                else if(contactNo.getText().toString().replace(" ","").length()!=10){
                    Toast.makeText(MainActivity.this, "Enter Correct No ...", Toast.LENGTH_SHORT).show();
                }
                else {
                    userName = name.getText().toString().trim();
                    userContact = contactNo.getText().toString().trim();
                    userVehicle = vehicleNo.getText().toString().trim();

                    //ref.child("Users").child(userId).orderByChild("contact").equalTo(userContact).addListenerForSingleValueEvent(new ValueEventListener() {
                      //  @Override
                        //public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          //  if(dataSnapshot.getValue() !=null){
                            //    Toast.makeText(MainActivity.this,"User logged in",Toast.LENGTH_SHORT).show();
                            //}
                            //else{
                                Users users = new Users();
                                users.setName(userName);
                                users.setContact(userContact);
                                users.setVehicle_number(userVehicle);
                                users.setUniqueId(uniqueId);
                                String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                users.setTime(currentTime);

                                //Toast.makeText(MainActivity.this, "User id "+currentuser, Toast.LENGTH_SHORT).show();
                                DatabaseReference myRef = ref.child("Users").child(userId);
                                myRef.setValue(users);
                                smsSent=sendSmsMessage(userContact,uniqueId);
                                if(smsSent) {
                                    Toast.makeText(MainActivity.this, "Unique Code sent ", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                                }
                                //Intent intent = new Intent(MainActivity.this,VerificationActivity.class);
                                //intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                                //startActivity(intent);
                            //}
                        //}

                        /*@Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/

                }
            }
        });

       /* payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }
            }
        });*/

    }

    public boolean sendSmsMessage(String contact,String uniqueId){
        message = "Your unique parking code is "+ uniqueId;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contact,null,message,null,null);
        return true;
    }

    protected void checkSMSPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.SEND_SMS)){
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    public int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
    }

}
