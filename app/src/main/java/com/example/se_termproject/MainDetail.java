package com.example.se_termproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainDetail extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private ArrayList<ListViewBtnItem> items;
    private ListViewBtnItem item;
    private EditText subjectEdit;
    private TextView dateText, timeText, memo;
    private Calendar cal;
    private String position;
    private String checkFlag;
    private Intent intent;
    private Button Delete, Done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);

        intent = getIntent();

        position = intent.getExtras().getString("position");
        checkFlag = intent.getExtras().getString("checkFlag");
        cal = Calendar.getInstance();

        Done = (Button) findViewById(R.id.Done);
        Delete = (Button) findViewById(R.id.Delete);
        subjectEdit = (EditText) findViewById(R.id.subjectEdit);
        dateText = (TextView) findViewById(R.id.date);
        timeText = (TextView) findViewById(R.id.time);
        memo = (TextView) findViewById(R.id.memo);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String tmp = dataSnapshot.child(position).child("subject").getValue(String.class);
                String date = dataSnapshot.child(position).child("date").getValue(String.class);
                String time = dataSnapshot.child(position).child("time").getValue(String.class);
                String memoStr = dataSnapshot.child(position).child("memo").getValue(String.class);

                dateText.setText(date);
                timeText.setText(time);
                subjectEdit.setText(tmp);
                memo.setText(memoStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> taskMap = new HashMap<>();

                taskMap.put("subject", subjectEdit.getText().toString());
                taskMap.put("date", dateText.getText().toString());
                taskMap.put("time", timeText.getText().toString());
                taskMap.put("memo", memo.getText().toString());

                myRef.child(position).updateChildren(taskMap);

                switch (checkFlag) {
                    case "Main":
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case "History":
                        intent = new Intent(getApplicationContext(), MainHistory.class);
                        break;
                    default:
                        break;
                }

                startActivity(intent);

                finish();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef.child(position).setValue(null);
                switch (checkFlag) {
                    case "Main":
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case "History":
                        intent = new Intent(getApplicationContext(), MainHistory.class);
                        break;
                    default:
                        break;
                }
                startActivity(intent);

                finish();
            }
        });
    }

    public void mOnDateClick(View v) {
        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style
                .Theme_Holo_Light_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                String msg = "";
                if (month < 10) {
                    msg = String.format("%d-0%d-%d", year, month + 1, date);
                } else if (date < 10) {
                    msg = String.format("%d-%d-0%d", year, month + 1, date);
                } else if (date < 10 && month < 10) {
                    msg = String.format("%d-0%d-0%d", year, month + 1, date);
                } else {
                    msg = String.format("%d-%d-%d", year, month + 1, date);
                }
                dateText.setText(msg);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        dialog.show();
    }

    public void mOnTimeClick(View v) {
        TimePickerDialog dialog = new TimePickerDialog(this, android.R.style
                .Theme_Holo_Light_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {

                int time = (min * 60 + hour * 60 * 60) * 1000;
                System.out.println(time);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                String formatted = format.format(time);

                System.out.println("Formatted" + formatted);
                timeText.setText(formatted);
            }
        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true);

        dialog.show();
    }
}
