package com.project.hscef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    EditText textEmail, textPass, textPhone, textName;
    String pfpUrl;
    Button profilePicSetter;
    ImageView pfpPreview;
    Button register;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseUser authUser;
    Uri filePath;
    FirebaseFirestore firebaseFirestore;
    Bitmap bitmap;
    static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firebaseFirestore = FirebaseFirestore.getInstance();
        textEmail = findViewById(R.id.txt_email);
        textEmail = findViewById(R.id.txt_username);
        textPass = findViewById(R.id.txt_pass);
        textPhone = findViewById(R.id.txt_phone);
        profilePicSetter = findViewById(R.id.profilePicSelector);
        pfpPreview = findViewById(R.id.profilePicImage);
        register = findViewById(R.id.btn_reg);
        mAuth = FirebaseAuth.getInstance();
        profilePicSetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setEnabled(false);
                if (filePath!=null){
                mAuth.createUserWithEmailAndPassword(textEmail.getText().toString(), textPass.getText().toString()).
                        addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT);
                                authUser = mAuth.getCurrentUser();
                                firebaseFirestore = FirebaseFirestore.getInstance();
                                firebaseFirestore.collection("user_master").document();
                                final String userID = authUser.getUid();
                                final String s = UUID.randomUUID().toString();
                                storageReference = storageReference.child("profilepic" + s);
                                Map<String, Object> map = new HashMap<>();
                                map.put("email", textEmail.getText().toString());
                                map.put("name", textName.getText().toString());
                                firebaseFirestore.collection("user_master").document(userID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    DocumentReference df = firebaseFirestore.collection("user_master").document(userID);
                                                    df.update("profile_pic", s);
                                                }
                                            });
                                            Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUpActivity.this, LoadingActivity.class));
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
            }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null ){
            filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView i = findViewById(R.id.profilePicImage);
                i.setImageBitmap(bitmap);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
