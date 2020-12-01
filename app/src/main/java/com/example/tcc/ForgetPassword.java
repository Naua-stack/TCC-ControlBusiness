package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class ForgetPassword extends AppCompatActivity {
   CountryCodePicker countryCodePicker;
    private EditText medttelefone;
    private String telefone= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodePicker2);
        medttelefone = (EditText) findViewById(R.id.telefoneuser);
    }

    public void chamarverify(View view) {

        telefone= "+"+countryCodePicker.getFullNumber()+ medttelefone.getText().toString();



        Query checarusuário = FirebaseDatabase.getInstance().getReference("Usuários").orderByChild("telefone").equalTo(telefone);
        checarusuário.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    medttelefone.setError(null);
                    Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                    intent.putExtra("telefone", telefone);
                    intent.putExtra("whatToDo", "updateData");
                    startActivity(intent);
                    finish();
                } else{
                      medttelefone.setError("esse usuário não existe");
                      medttelefone.requestFocus();
                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
