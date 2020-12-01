package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;


public class OpcaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opcaocadastro);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void voltarTelaLogin(View view) {
        Intent ir = new Intent(this, MainActivity.class);
        startActivity(ir);
    }

    public void irParaCm(View view) {
        Intent ir2 = new Intent(this, CadastroemActivity.class);
        startActivity(ir2);
    }
    public void irParaCa(View view) {
        Intent ir2 = new Intent(this, CadastroauActivity.class);
        startActivity(ir2);
    }
}