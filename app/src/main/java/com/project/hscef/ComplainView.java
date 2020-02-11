package com.project.hscef;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.Map;

public class ComplainView extends AppCompatActivity {
    TextView sub_name, title, desc, sub_email,prob_date,sub_date;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_complain_view);
        sub_name = findViewById(R.id.comp_sub_name);
        sub_email = findViewById(R.id.comp_sub_em);
        sub_date = findViewById(R.id.comp_sub_dated);
        title = findViewById(R.id.comp_title);
        desc = findViewById(R.id.comp_desc);
        prob_date = findViewById(R.id.comp_prob_dated);
        Intent intent = getIntent();
        if(intent.getStringExtra("type").equals("complain")) {
            Log.d("tag1", intent.getStringExtra("complainid") + intent.getStringExtra("socid"));
            db.collection("complain_master").document(intent.getStringExtra("complainid")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot data = task.getResult();
                    Map<String, Object> map = data.getData();
                    prob_date.setText("Problem Dated: " + map.get("problem_date"));
                    sub_date.setText("Submitted On: " + map.get("submit_date").toString());
                    sub_name.setText("Submitted By: " + map.get("submitter_name").toString());
                    desc.setText("Description: " + map.get("desc"));
                    title.setText("Title: " + map.get("title").toString());
                    sub_email.setText("Submitter Email: " + map.get("submitter_email").toString());
                    Log.d("tag1", data.getData().toString());

                /*desc.setText("Description: "+ data.get("desc"));
                prob_date.setText("Problem  Dated: "+data.get("problem_date"));*/
                }
            });
        }else{

            Log.d("tag1", intent.getStringExtra("EventId") + intent.getStringExtra("socid"));
            db.collection("event_master").document(intent.getStringExtra("EventId")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot data = task.getResult();
                    Map<String, Object> map = data.getData();
                    prob_date.setVisibility(View.GONE);
                    sub_date.setText("Submitted On: " + map.get("submit_date").toString() +
                            "\n\nEvent Date: " +  map.get("event_date").toString() +
                            "\n\nEvent From: " +  map.get("date_from").toString()+
                            "\n\nEvent Ends: " +  map.get("date_to").toString());
                    sub_name.setText("Submitted By: " + map.get("submitter_name").toString());
                    desc.setText("Description: " + map.get("desc"));
                    title.setText("Title: " + map.get("title").toString());
                    sub_email.setText("Submitter Email: " + map.get("submitter_email").toString());
                    Log.d("tag1", data.getData().toString());

                /*desc.setText("Description: "+ data.get("desc"));
                prob_date.setText("Problem  Dated: "+data.get("problem_date"));*/
                }
            });
        }
    }
}
