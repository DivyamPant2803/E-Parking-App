package com.example.paytmgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LogoutActivity extends AppCompatActivity {

    private Button signOut,verifyButton;
    private String upiId = "7355073348@paytm";
    private String paisa;
    private EditText verifyCode;
    private final int UPI_PAYMENT = 0;
    ConstraintLayout logoutLayout;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mauthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String uid,verifyId,logintime;
    int hours,min,days,rate=0,flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logoutLayout = findViewById(R.id.logoutLayout);
        signOut = findViewById(R.id.signOut);
        verifyButton = findViewById(R.id.verifyButton);
        verifyCode = findViewById(R.id.verifyCode);

        uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference ref = database.getReference().child("Users").child(uid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                logintime = users.getTime();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("login",Context.MODE_PRIVATE);
                flag = sharedPreferences.getInt("flag",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("flag",0);
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LogoutActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String logoutTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                verifyId = verifyCode.getText().toString();
                final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users");
                ref2.orderByChild("uniqueId").equalTo(verifyId).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            ValueEventListener postListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Toast.makeText(LogoutActivity.this, "ID Validated", Toast.LENGTH_SHORT).show();
                                    int rate2 = timeDifference(logintime,logoutTime);
                                    paisa = String.valueOf(rate2);
                                    startActivity(new Intent(LogoutActivity.this,Popup.class).putExtra("Paisa",paisa));
                                    //payusingUPI(upiId,paisa);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            };
                            mDatabase.addValueEventListener(postListener);

                        }
                        else {
                            Toast.makeText(LogoutActivity.this,"ID Not Validated",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void payusingUPI(String upi,String paisa){
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upi)
                .appendQueryParameter("am",paisa)
                .appendQueryParameter("cu","INR")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        Intent chooser = Intent.createChooser(intent,"Pay with");
        if(null != chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser, UPI_PAYMENT);
        }else{
            Toast.makeText(LogoutActivity.this,"No UPI app found",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {

                    if(data !=null){
                        String text =  data.getStringExtra("response");
                        Log.d("UPI","onActivityResult "+text);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(text);
                        upiPaymentDataOperation(dataList);
                    }else{
                        Log.d("UPI","onActivityResult "+"Returned Data is Null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                }else{
                    Log.d("UPI","onActivityResult "+"Returned Data is Null");
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }
    private void upiPaymentDataOperation(ArrayList<String> data){
        if(isConnectedToInternet(LogoutActivity.this)){
            String str = data.get(0);
            Log.d("UPI","upiPaymentDataOperation: "+str);
            String paymentCancel="";
            if(str==null)
                str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for(int i =0;i<response.length;i++){
                String equalStr[] = response[i].split("=");
                if(equalStr.length>=2){
                    if(equalStr[0].toLowerCase().equals("Status".toLowerCase())){
                        status = equalStr[1].toLowerCase();
                    }
                    else if(equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())){
                        approvalRefNo = equalStr[1];
                    }
                    else {
                        paymentCancel = "Payment Cancelled by the user";
                    }
                }
            }
            if(status.equals("success")){
                Toast.makeText(LogoutActivity.this,"Transaction Successful",Toast.LENGTH_SHORT).show();
                Log.d("UPI","responseStr: "+approvalRefNo);
            }
            else if("Payment Cancelled by the user".equals(paymentCancel)){
                Toast.makeText(LogoutActivity.this,"Payment Cancelled by the user",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(LogoutActivity.this,"Transaction Failed. Try Again",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(LogoutActivity.this,"Internet connection is not available",Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected() && networkInfo.isConnectedOrConnecting() && networkInfo.isAvailable()){
                return true;
            }
        }
        return false;
    }
    public int timeDifference(String stime,String etime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        try {
            Date date1 = simpleDateFormat.parse(stime);
            Date date2 = simpleDateFormat.parse(etime);
            long difference = date2.getTime() - date1.getTime();
            days = (int) (difference / (1000*60*60*24));
            hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
            min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
            hours = (hours < 0 ? -hours : hours);
            Toast.makeText(LogoutActivity.this,""+min,Toast.LENGTH_SHORT).show();
            if(min>=0 && min<=60){
                rate=10;
            }
            else if(min>60){
                rate=20;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rate;
    }
}

