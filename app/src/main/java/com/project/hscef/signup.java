package com.project.hscef;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    EditText textEmail, textPass, textPhone, textName;
    Button profilePicSetter;
    ImageView pfpPreview;
    Button register;
    Uri filePath;
    FirebaseFirestore firebaseFirestore;
    Bitmap bitmap;
    String profilepic;
    static final int PICK_IMAGE = 1;
    private FirebaseAuth mAuth;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseUser authUser;
    Map<String, Object> data = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        textEmail = findViewById(R.id.txt_email);
        textName = findViewById(R.id.txt_username);
        textPass = findViewById(R.id.txt_pass);
        textPhone = findViewById(R.id.txt_phone);
        profilePicSetter = findViewById(R.id.profilePicSelector);
        pfpPreview = findViewById(R.id.profilePicImage);
        register = findViewById(R.id.btn_reg);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textEmail.getText().toString();
                String pass = textPass.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.d("MAUTH", "CREATED AUTH USER");
                            FirebaseUser user = mAuth.getCurrentUser();
                            UpdateData(user);
                            startActivity(new Intent(signup.this, LoginActivity.class));
                            finish();

                        }else{
                            Log.d("MAUTH", "CREATED AUTH USER FAILED", task.getException());
                        }
                    }
                });
            }
        });

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

    }

    void UpdateData(final FirebaseUser user){
        String uid = user.getUid();
        String duri;
        final StorageReference pfpref = storageReference.child("profilepics/"+uid);
        pfpref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               final String name = textName.getText().toString();
                 pfpref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("NAME", "User profile updated.");
                                        }
                                    }
                                });
                        profilepic = uri.toString();
                    }
                });
            }
        });
        data.put("name", textName.getText().toString());
        data.put("password", textPass.getText().toString());
        data.put("email", textEmail.getText().toString());
        data.put("phone", textPhone.getText().toString());
        data.put("profilepic", textPhone.getText().toString());
        data.put("isInSoc", "False");
        addDocument(data,uid);
    }
    void addDocument(Map<String, Object> data, String uid){
        firebaseFirestore.collection("user_master").document(uid).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d("doc","Data ADDED FINALLY");
                    Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_LONG);
                    startActivity(new Intent(signup.this, LoadingActivity.class));
                    finish();
                }else{
                    task.getException().printStackTrace();
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
