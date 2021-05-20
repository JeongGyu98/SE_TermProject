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
import com.google.firebase.auth.FirebaseAuth;
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
    private FirebaseAuth mAuth;
    Button btnLogout;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent intent = new Intent(getApplicationContext(), MainHistory.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mAuth = FirebaseAuth.getInstance();
        context = getApplicationContext();
        listview = (ListView) findViewById(R.id.listView);
        countText = (TextView)findViewById(R.id.countText);
        btnLogout = (Button)findViewById(R.id.btn_logout);

        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                signOut();
                finishAffinity();
            }
        });

        if (items == null) {
            items = new ArrayList<>();
        }

        Date date = new Date();

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();

        // N즉 아직 끝나지 않은 Task들을 띄워라
        myRef.orderByChild("flag").equalTo("N").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                
                // 새로운 Task를 입력할 경우 Subject는 빈칸으로 하고 추가
                if (dataSnapshot.getChildrenCount() == 0) {
                    item = new ListViewBtnItem();
                    item.setSubject("");
                    items.add(item);

                    adapter = new ListViewBtnAdapter(context, R.layout.before_layout, items);

                    listview.setAdapter(adapter);
                }
                else {
                    //item은 각각의 Task를 의미하고 items는 모든 item들의 집합
                    item = new ListViewBtnItem();
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        item = new ListViewBtnItem();

                        //파이어베이스에 저장된 Subject 읽어오기
                        item.setSubject(dataSnapshot.child(child.getKey()).child("subject")
                                .getValue(String.class));
                        //파이어베이스에 저장된 memo 읽어오기
                        item.setMemo(dataSnapshot.child(child.getKey()).child("memo")
                                .getValue(String.class));
                        //파이어베이스에 저장된 data 즉 날짜(년도, 월, 일) 읽어오기
                        item.setDate(dataSnapshot.child(child.getKey()).child("date")
                                .getValue(String.class));
                        //파이어베이스에 저장된 time 즉 시간(시, 분) 읽어오기
                        item.setTime(dataSnapshot.child(child.getKey()).child("time")
                                .getValue(String.class));
                        //파이어베이스에 저장된 flag 즉 N이나 Y를 읽어오기
                        item.setFlag(dataSnapshot.child(child.getKey()).child("flag")
                                .getValue(String.class));
                        item.setPosition(child.getKey());

                        items.add(item);
                    }

                    adapter = new ListViewBtnAdapter(context, R.layout.before_layout, items);

                    listview.setAdapter(adapter);

                    //item들의 개수를 새서 총 몇개 의 Task가 있다고 출력
                    countText.setText(dataSnapshot.getChildrenCount() + " Tasks to Do");
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

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
