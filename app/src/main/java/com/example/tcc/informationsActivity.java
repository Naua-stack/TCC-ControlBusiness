package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class informationsActivity extends AppCompatActivity {
    private TextView cpftextview, nometextview, emailtextview, cnpjtextview, razaosocialtextview, telefonetextview,  paistextview, ruatextview, bairrotextview, ceptextview, estadotextview, cidadetextview, suportetextview;
    private User user;
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference userref2;
    private static final String USERS = "Usuários";
    private static final String USERS2 = "Empresas";
    private FirebaseAuth firebaseAuth;
    private Integer count = 0;
    private String email, password;
    private ImageButton voltarinfo;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);
        buscarLocation();
        voltarinfo = findViewById(R.id.voltarinfo);
        paistextview = findViewById(R.id.país);
        cidadetextview = findViewById(R.id.cidade);
        suportetextview = findViewById(R.id.suportetextview);
        estadotextview = findViewById(R.id.estado);
        bairrotextview = findViewById(R.id.bairro);
        ceptextview = findViewById(R.id.cep);
        final Intent intent = getIntent();
        email = intent.getStringExtra("email");

        razaosocialtextview = findViewById(R.id.txtnome);

        nometextview = findViewById(R.id.txtnome);
        emailtextview = findViewById(R.id.email_text_view);
        telefonetextview = findViewById(R.id.telefone_textview);
        ruatextview = findViewById(R.id.rua);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(USERS);
        userref2 = database.getReference(USERS2);
        firebaseAuth = FirebaseAuth.getInstance();

        voltarinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(informationsActivity.this, PerfilActivity.class);
                        startActivity(intent);
            }
        });

        suportetextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent suporteintent = new Intent(informationsActivity.this, SuportActivity.class);
                startActivity(suporteintent);
            }
        });
    }


    private void buscarLocation() {
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference userRefLocation = database2.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Location");
        if(userRefLocation != null){
            userRefLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    LocationData locationData = snapshot.getValue(LocationData.class);
                    paistextview.setText(locationData.getPaís());
                    cidadetextview.setText(locationData.getCidade());
                    estadotextview.setText(locationData.getEstado());
                    bairrotextview.setText(locationData.getBairro());
                    ceptextview.setText(locationData.getCEP());
                    ruatextview.setText(locationData.getRua());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
    public void onStart() {
        super.onStart();
        // Chr is signed eck if usein (non-null) and update UI accordingly.
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(informationsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


}