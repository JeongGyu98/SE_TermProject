package com.example.se_termproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainHistory extends AppCompatActivity
{
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private ListViewBtnItem item;
    private ArrayList<ListViewBtnItem> items;
    private Context context;
    private ListViewHistoryAdapter adapter;
    private ListView listView;
    private TextView countHistoryText, posId;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_hide:
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    startActivity(intent);

                    finish();

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        context = getApplicationContext();
        items = new ArrayList<>();

        listView = (ListView)findViewById(R.id.listHistoryView);
        countHistoryText = (TextView)findViewById(R.id.countHistoryText);
        posId = (TextView)findViewById(R.id.posHistoryId);

        myRef.orderByChild("flag").equalTo("Y").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                items.clear();

                item = new ListViewBtnItem();

                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    item = new ListViewBtnItem();

                    item.setSubject(dataSnapshot.child(child.getKey()).child("subject").getValue(String.class));
                    item.setMemo(dataSnapshot.child(child.getKey()).child("memo").getValue(String.class));
                    item.setDate(dataSnapshot.child(child.getKey()).child("date").getValue(String.class));
                    item.setTime(dataSnapshot.child(child.getKey()).child("time").getValue(String.class));
                    item.setFlag(dataSnapshot.child(child.getKey()).child("flag").getValue(String.class));
                    item.setPosition(child.getKey());

                    items.add(item);
                }
                adapter = new ListViewHistoryAdapter(context, R.layout.before_layout, items);

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent intent = new Intent(getApplicationContext(), MainDetail.class);

                intent.putExtra("position", items.get(position).getPosition());
                intent.putExtra("checkFlag", "History");
                startActivity(intent);
            }
        });
    }
}
