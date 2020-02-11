package com.project.hscef;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class HomePage_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Intent intent = getIntent();
        HashMap<String, String> map =(HashMap<String, String>) intent.getSerializableExtra("hashmap");
        TextView data = findViewById(R.id.data);
        String s = map.get("dispname")+"--"+map.get("email")+"--"+map.get("UID");
        data.setText(s);
        ImageView v = findViewById(R.id.img);
        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(Uri.parse(map.get("profilepic"))).into(v);
    }
}
