package com.example.tcc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NovaSenha extends AppCompatActivity {
  private EditText validatepassword, confirmpassword;
  private FirebaseAuth mAuth;
  private String id;
  private String password =  "";
  private String confirmpassword2 =  "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_senha);
        validatepassword = (EditText)findViewById(R.id.passsword);
        confirmpassword = (EditText)findViewById(R.id.passswordconfirm);
        id = getIntent().getStringExtra("id");
        password = validatepassword.getText().toString().trim();
        confirmpassword2 = confirmpassword.getText().toString().trim();

    }

    public void novasenha(View view) {
        ChecarInternet checarInternet = new ChecarInternet();
        if(!checarInternet.conectado(this)){
            Toast.makeText(this, "Conectado à internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals(confirmpassword2)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Usuários");
            reference.child(id).child("senha").setValue(password);
            startActivity(new Intent(getApplicationContext(), LembrarSenhaMensagemSucesso.class));
            finish();
        }else{
            validatepassword.setError("Senhas não conferem");
            confirmpassword.setError("Senhas não conferem");
        }
    }

}
