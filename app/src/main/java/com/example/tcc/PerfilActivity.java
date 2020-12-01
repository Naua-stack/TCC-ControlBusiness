package com.example.tcc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class PerfilActivity extends AppCompatActivity {
    private TextView cpftextview, nometextview, emailtextview, contatosTextview, cnpjtextview, irparaInformacoes, razaosocialtextview, telefonetextview, paistextview, ruatextview, bairrotextview, ceptextview, estadotextview, cidadetextview;
    private ImageView perfilfoto;
    private Button logout, excluir;
    private ImageView userimage;
    private String email, password;

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private DatabaseReference userref2;
    private FirebaseAuth firebaseAuth;
    private User user;
    private static final String USERS = "Usuários";
    private static final String USERS2 = "Empresas";

    private Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        final Intent intent = getIntent();
        email = intent.getStringExtra("email");

        firebaseAuth = FirebaseAuth.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        buscarfoto();
       irparaInformacoes = findViewById(R.id.textView31);
       contatosTextview = findViewById(R.id.textView);
        perfilfoto = findViewById(R.id.imageViewUser);
        paistextview = findViewById(R.id.país);
        cidadetextview = findViewById(R.id.cidade);
        estadotextview = findViewById(R.id.estado);
        bairrotextview = findViewById(R.id.bairro);
        ceptextview = findViewById(R.id.cep);
        ruatextview = findViewById(R.id.rua);
        excluir= findViewById(R.id.excluir);


        razaosocialtextview = findViewById(R.id.txtnome);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference userRef3 = database.getInstance().getReference().child("Usuários").child(id);

        nometextview = findViewById(R.id.txtnome);
        emailtextview = findViewById(R.id.email_text_view);
        telefonetextview = findViewById(R.id.telefone_textview);

        logout = (Button) findViewById(R.id.btnLogout);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(USERS);
        userref2 = database.getReference(USERS);
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PerfilActivity.this);
                builder.setTitle("Deletar Conta");
                builder.setMessage("Deseja Realmente excluir sua conta?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        userRef3.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                    Intent intent1 = new Intent(PerfilActivity.this, MainActivity.class);
                                    finish();
                                    Toast.makeText(PerfilActivity.this, "Conta Deletada com sucesso", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(PerfilActivity.this, "Conta Deletada com sucesso", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(PerfilActivity.this, MainActivity.class);
                                    startActivity(intent1);
                                }else {
                                    Toast.makeText(PerfilActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.create().show();

            }

        });
        irparaInformacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentlocal = new Intent(PerfilActivity.this, informationsActivity.class);
                startActivity(intentlocal);
            }
        });
        contatosTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intencontato = new Intent(PerfilActivity.this, ContactsActivity.class);
                startActivity(intencontato);
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (firebaseAuth.getCurrentUser().getEmail().toLowerCase().equals(ds.child("email").getValue(String.class).trim().toLowerCase())) {
                        count++;

                        razaosocialtextview.setText(ds.child("razão").getValue(String.class));
                        emailtextview.setText(ds.child("email").getValue(String.class));
                        telefonetextview.setText(ds.child("telefone").getValue(String.class));


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        userref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    if (firebaseAuth.getCurrentUser().getEmail().toLowerCase().equals(ds.child("email").getValue(String.class).trim().toLowerCase())) {

                        count++;

                        nometextview.setText(ds.child("nome").getValue(String.class));
                        emailtextview.setText(ds.child("email").getValue(String.class));
                        telefonetextview.setText(ds.child("telefone").getValue(String.class));


                    }


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    public void onStart() {
        super.onStart();
        // Chr is signed eck if usein (non-null) and update UI accordingly.
        final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(PerfilActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_estoque:
                        Intent intent1 = new Intent(PerfilActivity.this, TelaestActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_account:

                        break;

                }
                return false;
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                gomainscreen();


            }
        });

    }
    private void buscarfoto() {
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference userRefLocation = database2.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(userRefLocation != null){
            userRefLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    final User user = snapshot.getValue(User.class);

                    Picasso.get().load(user.getProfileurl()).into(perfilfoto);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void gomainscreen() {
        Intent logoutintent = new Intent(this, LoginActivity.class);
        startActivity(logoutintent);
    }

}



