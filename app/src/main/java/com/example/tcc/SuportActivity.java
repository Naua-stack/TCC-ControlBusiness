package com.example.tcc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SuportActivity extends AppCompatActivity {

    EditText assunto, mensagem;
    Button enviar;
    ImageButton voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telasuporte);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        voltar = findViewById(R.id.voltarsuporte);
        assunto = findViewById(R.id.assunto);
        mensagem = findViewById(R.id.problema);
        enviar = findViewById(R.id.btnenviarproblema);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuportActivity.this, informationsActivity.class);
                startActivity(intent);
            }
        });
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringassunto = assunto.getText().toString();
                String stringmensagem =  mensagem.getText().toString();
                String email = "suportcontrolbusiness@gmail.com";
                sendEmail(stringassunto, stringmensagem, email);

            }
        });
    }

    private void sendEmail(String stringassunto, String stringmensagem, String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, stringassunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, stringmensagem);
        try {
           startActivity(Intent.createChooser(emailIntent, "Choose an email client"));
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
