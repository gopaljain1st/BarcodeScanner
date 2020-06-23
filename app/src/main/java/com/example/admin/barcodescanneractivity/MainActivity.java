package com.example.admin.barcodescanneractivity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;
    LinearLayoutManager manager;
    RecyclerView.Adapter<MyAdapter.MyAdapterViewHolder>adapter;
    ArrayList<contact>al;
    BroadcastReceiver broadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_list);
        rv=findViewById(R.id.rv);
        manager=new LinearLayoutManager(this);
        rv.setLayoutManager(manager);
        al=new ArrayList<>();
        adapter=new MyAdapter(this,al);
        rv.setAdapter(adapter);
        readFromLocalStorage();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                readFromLocalStorage();
            }
        };
        registerReceiver(new NetworkMonitor(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private void readFromLocalStorage()
    {
        al.clear();
        DatabaseHelper helper=new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
        Cursor c=sqLiteDatabase.rawQuery("select * from contact",null);
        while(c.moveToNext())
        {
            String name = c.getString(1);
            int syncStatus = c.getInt(6);
            al.add(new contact(name,syncStatus));
        }
        adapter.notifyDataSetChanged();
        c.close();
        helper.close();
    }
    public boolean checkNetworkConnection()
    {
        ConnectivityManager connectivityManager =(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!= null && networkInfo.isConnected());
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver,new IntentFilter(NetworkMonitor.UI_UPDATE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
