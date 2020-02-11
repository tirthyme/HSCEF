package com.project.hscef;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class LoadingActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Button b1 = findViewById(R.id.btn_logout);
        if(user != null){
            //Signed In Process
            TextView t = findViewById(R.id.txt);
            //Log.d("a",user.getUid()+ " ---- "+ user.getEmail() + " ---- "+ user.getDisplayName() + " ---- "+user.getPhotoUrl());
            ImageView i = findViewById(R.id.img);
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().load(user.getPhotoUrl()).into(i);
            Log.d("SN", "Sign in time");
            b1.setVisibility(View.VISIBLE);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("user_master").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot data= task.getResult();
                    try {
                        if (data.get("isInSoc").equals("False")) {
                            startActivity(new Intent(LoadingActivity.this, SocietyAcknowledgement.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoadingActivity.this,TabbedActivity.class));
                            finish();
                        }
                    }catch (NullPointerException e){
                        Log.d("tag", e.getMessage());
                        Toast.makeText(getApplicationContext(), "Fatal Error Occurred Please Reinstall.",Toast.LENGTH_LONG).show();
                        //startActivity(new Intent(LoadingActivity.this, SocietyAcknowledgement.class));
                    }
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAuth.signOut();
                    startActivity(new Intent(LoadingActivity.this, LoadingActivity.class));
                }
            });
        }
        else{
            //Sign Up
            Button b = findViewById(R.id.btn_login);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
                }
            });
            b = findViewById(R.id.btn_signup);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoadingActivity.this, signup.class));
                }
            });
        }
    }
}
