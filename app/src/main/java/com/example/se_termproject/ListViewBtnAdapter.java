package com.example.se_termproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ListViewBtnAdapter extends ArrayAdapter {
    private ArrayList<ListViewBtnItem> items;
    private boolean buttonFlag = false;
    private Button button;
    private EditText editText;
    private ImageView imageView;
    private CheckBox checkBox;
    private ListView listView;
    private int flagInt = 1;
    private boolean flag = false;
    private TextView posId;
    private TextView timeDateText, subjectText;

    int resourceId;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    ListViewBtnAdapter(Context context, int resource, ArrayList<ListViewBtnItem> list) {
        super(context, resource, list);

        this.resourceId = resource;
        this.items = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.before_layout, parent, false);
        }

        editText = (EditText) convertView.findViewById(R.id.editText);
        button = (Button) convertView.findViewById(R.id.plus_btn);
        imageView = (ImageView) convertView.findViewById(R.id.infoBtn);
        listView = (ListView) convertView.findViewById(R.id.listView);
        posId = (TextView) convertView.findViewById(R.id.posId);
        checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
        subjectText = (TextView) convertView.findViewById(R.id.subjectText);
        timeDateText = (TextView) convertView.findViewById(R.id.timeDateText);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked() == false) {
                    Map<String, Object> taskMap = new HashMap<>();
                    taskMap.put("flag", "Y");
                    myRef.child(items.get(position).getPosition()).updateChildren(taskMap);
                }
            }
        });

        if (flag == false) {
            if (items != null && flagInt != 0) {
                editText.setClickable(false);
                editText.setEnabled(false);
                editText.setFocusable(false);
                button.setFocusable(false);
                checkBox.setFocusable(false);
                imageView.setFocusable(false);

                editText.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                subjectText.setVisibility(View.VISIBLE);
                timeDateText.setVisibility(View.VISIBLE);

                posId.setText(items.get(position).getPosition());
                subjectText.setText(items.get(position).getSubject());

                long now = System.currentTimeMillis();

                Date date = new Date(now);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                String getTime = sdf.format(date);

                if (getTime.equals(items.get(position).getDate())) {
                    timeDateText.setText(items.get(position).getTime());
                } else {
                    timeDateText.setText(items.get(position).getDate());
                }

                flagInt++;
            }
            if (flagInt == items.size() + 1) {
                ListViewBtnItem item = new ListViewBtnItem();
                item.setSubject("");
                items.add(item);
                notifyDataSetChanged();
                editText.setVisibility(View.VISIBLE);

                flagInt = 0;
            }

            if (items.get(position).getSubject().equals("")) {
                button.setVisibility(View.VISIBLE);
                checkBox.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.VISIBLE);

                flag = true;
            }
        } else {
            flag = false;
        }

        final Button button1 = (Button) convertView.findViewById(R.id.plus_btn);

        final View finalConvertView = convertView;
        button1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (buttonFlag == false) {
                    InputMethodManager imm = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                            InputMethodManager.HIDE_IMPLICIT_ONLY);

                    editText.setVisibility(View.VISIBLE);
                    editText.setClickable(true);
                    editText.setEnabled(true);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();

                    button.setText("OK");

                    buttonFlag = true;
                } else if (buttonFlag == true) {
                    InputMethodManager immhide = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                    editText.setVisibility(View.INVISIBLE);
                    editText.setClickable(false);
                    editText.setEnabled(false);
                    editText.setFocusable(false);
                    button.setFocusable(false);
                    checkBox.setFocusable(false);
                    imageView.setFocusable(false);

                    button.setVisibility(View.INVISIBLE);
                    checkBox.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.VISIBLE);

                    ListViewBtnItem item = new ListViewBtnItem();

                    item.setPosition(String.valueOf(position));
                    item.setSubject(editText.getText().toString());

                    Date date = new Date();

                    SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

                    Calendar cal = Calendar.getInstance();

                    item.setDate(sdformat.format(cal.getTime()));

                    sdformat = new SimpleDateFormat("HH:mm");
                    cal.add(Calendar.MINUTE, -date.getMinutes());
                    cal.add(Calendar.HOUR, 2);

                    item.setTime(sdformat.format(cal.getTime()));

                    item.setFlag("N");
                    item.setMemo("");

                    items.set(items.size() - 1, item);

                    myRef.push().setValue(item);

                    Toast.makeText(context, items.get(items.size() - 1).getSubject(),
                            Toast.LENGTH_LONG).show();

                    item.setSubject("a");

                    items.add(item);

                    notifyDataSetChanged();
                    buttonFlag = false;
                }
            }
        });

        return convertView;
    }
}