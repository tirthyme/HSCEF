package com.project.hscef;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event_Register extends AppCompatActivity {

    TextView txt_comp_title, txt_comp_desc;
    Button setDate, reg_comp, img_add,set_time_from,set_time_to;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Map<String, Object> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_register);
        map = new HashMap<>();
        setDate = findViewById(R.id.btn_datepicker);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        map.put("submit_date", df.format(c));
        map.put("submitter_name", user.getDisplayName());
        map.put("submitter_email", user.getEmail());
        setDate.setOnClickListener(new View.OnClickListener() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Event_Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String s = dayOfMonth+"/"+(month+1)+"/"+year;
                        setDate.setText(s);
                        setDate.setTextColor(Color.RED);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });
        set_time_from = findViewById(R.id.btn_timepicker_from);
        set_time_to = findViewById(R.id.btn_timepicker_to);
        set_time_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(Event_Register.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        set_time_from.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                dialog.setTitle("Select Time");
                dialog.show();
            }
        });
        set_time_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(Event_Register.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        set_time_to.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                dialog.setTitle("Select Time");
                dialog.show();
            }
        });
        TextView t = findViewById(R.id.sub_date);
        t.setText("Submit on"+df.format(c));
        txt_comp_title = findViewById(R.id.com_title);
        txt_comp_desc = findViewById(R.id.com_description);
        reg_comp = findViewById(R.id.reg_comp);
        reg_comp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txt_comp_title.getText().toString();
                String desc = txt_comp_desc.getText().toString();
                String date_from = set_time_from.getText().toString();
                String date_to = set_time_to.getText().toString();
                map.put("title", title);
                map.put("event_date", setDate.getText().toString());
                map.put("desc", desc);
                map.put("date_from", date_from);
                map.put("date_to", date_to);
                firebaseFirestore.collection("event_master").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("tag","EVENT ADDEDD=---" + documentReference.getId());
                        addSocId(documentReference.getId());
                        Toast.makeText(Event_Register.this,"Added", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Event_Register.this,TabbedActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("tag",e.getMessage());
                    }
                });
            }
        });
    }
    public void addSocId(final String socid){
        DocumentReference doc = firebaseFirestore.collection("user_master").document(user.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot;
                    snapshot = task.getResult();
                    firebaseFirestore.collection("event_master").document(socid).update("SocietyId",snapshot.getString("SocietyId"));
                    Log.d("tag","Soc ID Added");
                }else{
                    Log.d("tag",task.getException().toString());
                }
            }
        });
    }
}
