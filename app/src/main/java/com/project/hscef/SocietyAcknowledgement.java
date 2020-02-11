package com.project.hscef;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SocietyAcknowledgement extends AppCompatActivity {
    TextView txt_soc_id, txt_soc_name, getTxt_soc_id,valid;
    Button btn_joinSoc, btn_createSoc;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final String ID = getID();
    String uname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_acknowledgement);
        txt_soc_id = findViewById(R.id.txt_soc_id);
        getTxt_soc_id = findViewById(R.id.etxt_soc_id);
        valid = findViewById(R.id.validation);
        txt_soc_name = findViewById(R.id.txt_soc_name);
        btn_createSoc = findViewById(R.id.btn_CreateSoc);
        btn_joinSoc = findViewById(R.id.btn_socJoin);
        btn_joinSoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SocietyAcknowledgement.this);
                final String socId = txt_soc_id.getText().toString();
                dialog.setTitle("Confirmation").setMessage("Are you sure you want to join:" + socId).setPositiveButton("Yes Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addUsertoSoc(ID,socId);
                    }
                }).setNegativeButton("No Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SocietyAcknowledgement.this, "Cacelled", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();
            }
        });
        btn_createSoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SocietyAcknowledgement.this);
                final String socName = txt_soc_name.getText().toString();
                final String socId = getTxt_soc_id.getText().toString();
                dialog.setTitle("Confirmation").setMessage("Are you sure you want to Create Society Named:" + socName).setPositiveButton("Yes Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,String> map = new HashMap<>();
                        map.put("soc_name",socName);
                        map.put("unique_id",socId);
                        map.put("created_userid", ID);
                        map.put("created_username", uname);
                        
                        addNewSociety(map);
                    }
                }).setNegativeButton("No Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SocietyAcknowledgement.this, "Cancelled", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.show();
            }
        });
        getTxt_soc_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                db.collection("society_master").whereEqualTo("unique_id",getTxt_soc_id.getText().toString()).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty() || getTxt_soc_id.getText().toString().trim().length() == 0){
                                valid.setText("Not Valid");
                                btn_createSoc.setEnabled(false);
                            }else{
                                valid.setText("Valid");
                                btn_createSoc.setEnabled(true);
                            }
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void addUsertoSoc(final String ID, final String socID) {
        db.collection("society_master").whereEqualTo("unique_id",socID).limit(1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    StringBuilder socid1 = new StringBuilder();
                    if (task.getResult().isEmpty()){
                        Toast.makeText(getApplicationContext(), "ID is not valid Try Again!!",Toast.LENGTH_LONG).show();
                    }else{
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            socid1.append(document.getId());
                        }
                        db.collection("user_master").document(ID).update("SocietyId",socid1.toString());
                        db.collection("user_master").document(ID).update("isInSoc","True");
                        Toast.makeText(getApplicationContext(), "Society Joined", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SocietyAcknowledgement.this,TabbedActivity.class));
                    }
                }
            }
        });
    }

    public void addNewSociety(Map<String, String> map) {
        db.collection("society_master").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String socID = documentReference.getId();
                Log.d("tag",documentReference.getId());
                db.collection("user_master").document(ID).update("SocietyId",socID);
                db.collection("user_master").document(ID).update("isInSoc","True");
                Log.d("tag", "Society Added");
                Toast.makeText(getApplicationContext(),"Society Added", Toast.LENGTH_LONG).show();
                startActivity(new Intent(SocietyAcknowledgement.this,TabbedActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("tag", e.getMessage());
            }
        });
    }

    public String getID() {

        try {
            uname = user.getDisplayName();
            return user.getUid();
        } catch (NullPointerException e) {
            Log.d("tag",e.getMessage());
            return null;
        }
    }
}