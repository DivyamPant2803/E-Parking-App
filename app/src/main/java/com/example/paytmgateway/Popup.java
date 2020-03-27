package com.example.paytmgateway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Popup extends AppCompatActivity {

    EditText upiId,note,name,amount;
    Button payButton;
    String loginTime,logoutTime,paisa;
    private final int UPI_PAYMENT = 0;
    int hours,min,days,rate=0,flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);

        upiId = findViewById(R.id.upiID);
        //note = findViewById(R.id.note);
        //name = findViewById(R.id.name);
        //amount = findViewById(R.id.amount);
        payButton = findViewById(R.id.payButton);

        Intent intent = getIntent();
        //loginTime = intent.getStringExtra("loginTime");
        //logoutTime = intent.getStringExtra("logoutTime");
        paisa = intent.getStringExtra("Paisa");
        //amount.setText(paisa);
        Toast.makeText(Popup.this, " "+paisa, Toast.LENGTH_SHORT).show();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width),(int)(height*.4));

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upiId.getText().toString().isEmpty()){
                    Toast.makeText(Popup.this,"Enter UPI ID",Toast.LENGTH_SHORT).show();
                }
                else{
                    payusingUPI(upiId.getText().toString(),paisa,"Sachin","Hello");
                }
            }
        });

    }

    public void payusingUPI(String upi,String paisa,String name,String note){
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa",upi)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",paisa)
                .appendQueryParameter("cu","INR")
                .build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        Intent chooser = Intent.createChooser(intent,"Pay with");
        if(null != chooser.resolveActivity(getPackageManager())){
            startActivityForResult(chooser, UPI_PAYMENT);
        }else{
            Toast.makeText(Popup.this,"No UPI app found",Toast.LENGTH_SHORT).show();
        }
    }

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
        if(isConnectedToInternet(Popup.this)){
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
                Toast.makeText(Popup.this,"Transaction Successful",Toast.LENGTH_SHORT).show();
                Log.d("UPI","responseStr: "+approvalRefNo);
            }
            else if("Payment Cancelled by the user".equals(paymentCancel)){
                Toast.makeText(Popup.this,"Payment Cancelled by the user",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Popup.this,"Transaction Failed. Try Again",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(Popup.this,"Internet connection is not available",Toast.LENGTH_SHORT).show();
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

}



