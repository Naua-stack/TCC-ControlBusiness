package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class telalogininfo extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView textView40;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telalogininfo);
        textView40 = findViewById(R.id.textView40);
        textView40.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail())  ;

    }
}