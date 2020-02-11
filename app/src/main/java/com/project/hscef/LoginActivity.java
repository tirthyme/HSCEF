package com.project.hscef;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.xml.sax.helpers.LocatorImpl;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText txt_email, txt_password;
    TextView txt_error;
    Button btn_login;
    ProgressBar progressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email = findViewById(R.id.username);
        txt_error = findViewById(R.id.error);
        txt_password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login);
        progressBar = findViewById(R.id.loading);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String email = txt_email.getText().toString();
                final String password = txt_password.getText().toString();
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged In Successfully",Toast.LENGTH_LONG);
                            user = mAuth.getCurrentUser();
                            map = new HashMap<>();
                            map.put("dispname", user.getDisplayName());
                            map.put("profilepic",user.getPhotoUrl().toString());
                            map.put("email", user.getEmail());
                            map.put("UID", user.getUid());
                            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                            firebaseFirestore.collection("user_master").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.d("Tag", "data get");
                                        DocumentSnapshot data = task.getResult();
                                        if (data.get("isInSoc").toString().equals("False")){
                                            Intent intent = new Intent(LoginActivity.this, SocietyAcknowledgement.class);
                                            intent.putExtra("hashmap", map);
                                            startActivity(intent);
                                        }
                                        else{
                                            Intent intent = new Intent(LoginActivity.this, TabbedActivity.class);
                                            intent.putExtra("hashmap", map);
                                            startActivity(intent);
                                        }

                                    }
                                    else{
                                        Log.d("Tag", task.getException().toString());
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                            txt_error.setText("Invalid Credentials.. Please LogIn Again");
                            Log.d("Login",task.getException().toString());
                        }
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
