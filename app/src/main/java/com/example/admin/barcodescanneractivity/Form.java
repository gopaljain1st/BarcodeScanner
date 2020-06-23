package com.example.admin.barcodescanneractivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Form extends AppCompatActivity
{
    EditText name,email,mobile,title,address;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form);
        initComponent();
        Intent in=getIntent();
        String result=in.getStringExtra("result");
        String []arr=result.split(":|\\;");
        for(int i=0;i<arr.length;i++)
        {
            Log.e("arr["+i+"]",arr[i]);
        }
        name.setText(arr[2]);
        mobile.setText(arr[6]);
        email.setText(arr[10]);
        address.setText(arr[12]);
        title.setText(arr[14]);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  saveToServer();
            }
        });
    }
    private void saveToLocalStorage(int syncStatus)
    {
        DatabaseHelper helper=new DatabaseHelper(this);
        SQLiteDatabase db=helper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("name",name.getText().toString());
        values.put("email",email.getText().toString());
        values.put("mobile",mobile.getText().toString());
        values.put("title",title.getText().toString());
        values.put("address",address.getText().toString());
        values.put("sync_status",syncStatus);
        db.insert("contact",null,values);
        db.close();
        helper.close();
    }
    private void initComponent()
    {
        name=findViewById(R.id.userName);
        email=findViewById(R.id.userEmail);
        mobile=findViewById(R.id.userMobile);
        title=findViewById(R.id.userOccupation);
        address=findViewById(R.id.userAddress);
        submit=findViewById(R.id.submit);
    }
    private void saveToServer()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving Data");
        progressDialog.show();
        if(checkNetworkConnection())
        {

            String url="https://inventivepartner.com/Inventive_fruits/addSyncContact.php";
            StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    progressDialog.dismiss();
                    if(response.equals("item Inserted"))
                    {
                        saveToLocalStorage(1);
                    }
                    else saveToLocalStorage(0);
                    startActivity(new Intent(Form.this,MainActivity.class));
                    Toast.makeText(Form.this, "Information Added", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(Form.this, ""+error, Toast.LENGTH_SHORT).show();
                    saveToLocalStorage(0);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String>map=new HashMap<>();
                    map.put("name",name.getText().toString());
                    map.put("email",email.getText().toString());
                    map.put("mobile",mobile.getText().toString());
                    map.put("title",title.getText().toString());
                    map.put("address",address.getText().toString());
                    return map;
                }
            };
            MySingleton.getInstance(this).addToRequestQue(stringRequest);
        }
        else
        {
            progressDialog.dismiss();
            AlertDialog alertDialog =new AlertDialog.Builder(this).setMessage("internet connection not avilable your data is saved localy when conncetion come it will transfer to server").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveToLocalStorage(0);
                    startActivity(new Intent(Form.this,MainActivity.class));
                }
            }).show();
        }
    }
    public boolean checkNetworkConnection()
    {
        ConnectivityManager connectivityManager =(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }
}
