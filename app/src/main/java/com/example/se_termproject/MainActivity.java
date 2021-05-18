package com.example.se_termproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private ListViewBtnAdapter adapter;
    private ListViewBtnItem item;
    private ArrayList<ListViewBtnItem> items;
    private ListView listview;
    private String subject;
    private Context context;
    private TextView countText;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    Intent intent = new Intent(getApplicationContext(), MainHistory.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        context = getApplicationContext();
        listview = (ListView) findViewById(R.id.listView);
        countText = (TextView)findViewById(R.id.countText);

        if (items == null)
        {
            items = new ArrayList<>();
        }

        Date date = new Date();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();

        myRef.orderByChild("flag").equalTo("N").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                items.clear();

                if (dataSnapshot.getChildrenCount() == 0)
                {
                    item = new ListViewBtnItem();
                    item.setSubject("");
                    items.add(item);

                    adapter = new ListViewBtnAdapter(context, R.layout.before_layout, items);

                    listview.setAdapter(adapter);
                }
                else
                {
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

                    adapter = new ListViewBtnAdapter(context, R.layout.before_layout, items);

                    listview.setAdapter(adapter);

                    countText.setText(dataSnapshot.getChildrenCount() + " items due today");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Intent intent = new Intent(getApplicationContext(), MainDetail.class);
                intent.putExtra("position", items.get(position).getPosition());
                intent.putExtra("checkFlag", "Main");

                startActivity(intent);
            }
        });
    }
}
