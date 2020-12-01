package com.example.tcc;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.github.rtoshiro.util.format.text.SimpleMaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CadastroauActivity extends AppCompatActivity {

    private EditText medtNome;
    private EditText medtEmail2;
    private EditText medtCpf;
    private EditText medtSenha2;
    private EditText medttelefone;
    private Button mbtnCadastrar2;
    private Button mbtnselectimg;
    private Uri mSelectUri;
    private ImageView mImagephoto;
    private CountryCodePicker countryCodePicker;


    //Variaveis dos dados que vamos registrar
    private String nome = "";
    private String email = "";
    private String cpf = "";
    private String telefone = "";
    private String senha = "";
    private String phoneno = "";
    private String foto = "";
    private String profileUrl2 = "";
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private StorageReference mstorageferef;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[a-z A-Z])" +
                    "(?=.*[@#_&])" +
                    "$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastroautonomo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodePicker);
        medtNome = (EditText) findViewById(R.id.edtnome2);
        medtEmail2 = (EditText) findViewById(R.id.edtEmail3);
        medtCpf = (EditText) findViewById(R.id.edtCpf);
        medtSenha2 = (EditText) findViewById(R.id.edtSenhaAu);
        medttelefone = (EditText) findViewById(R.id.edttelefone);
        mbtnCadastrar2 = (Button) findViewById(R.id.btnCadastrar2);
        mbtnselectimg = (Button) findViewById(R.id.btn_photo);
        mImagephoto = (ImageView) findViewById(R.id.img_photo);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(medtCpf, smf);
        medtCpf.addTextChangedListener(mtw);
        SimpleMaskFormatter tel = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher telefone1 = new SimpleMaskTextWatcher(medttelefone, tel);
        medttelefone.addTextChangedListener(telefone1);
        mbtnselectimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimg();
            }
        });
        Bundle intent = getIntent().getExtras();
        if(intent != null){
         email =  intent.getString("email");
         telefone = intent.getString("telefone");
         nome = intent.getString("nome");
         foto = intent.getString("foto");
         medtEmail2.setText(email);
         medtNome.setText(nome);
         medttelefone.setText(telefone);
         Picasso.get().load(foto).into(mImagephoto);
         medtSenha2.setVisibility(View.INVISIBLE);
         medtCpf.setVisibility(View.INVISIBLE);
         mbtnselectimg.setAlpha(0);
         mbtnCadastrar2.setText("Editar");


        }
        mbtnCadastrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = medtNome.getText().toString();
                email = medtEmail2.getText().toString();
                cpf = medtCpf.getText().toString();
                telefone = "+" + countryCodePicker.getFullNumber() + medttelefone.getText().toString();

                senha = medtSenha2.getText().toString();
                if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !cpf.isEmpty() && !telefone.isEmpty()) {
                        if (senha.length() >= 6) {
                            for (char c : senha.toCharArray())
                                if (c >= 'A' && c <= 'Z') {
                                    medtSenha2.setError(null);

                                    Query checarusuário = FirebaseDatabase.getInstance().getReference("Usuários").orderByChild("telefone").equalTo(telefone);
                                    checarusuário.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                medttelefone.setError("Esse número de telefone existe");


                                            } else {
                                                if(mSelectUri == null) {
                                                    Toast.makeText(CadastroauActivity.this, "Você precisa informar uma foto", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    registrarUsuario();
                                                }

                                            }
                                        }


                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    medtSenha2.setError("Maiuscúla");
                                }
                        } else {
                            Toast.makeText(CadastroauActivity.this, "A senha deve conter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();

                        }


                    } else {

                        Toast.makeText(CadastroauActivity.this, "Complete todos os campos", Toast.LENGTH_SHORT).show();
                    }






            }

            private void registrarUsuario() {
                mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            saveuserinfirebase();


                        } else {
                            Toast.makeText(CadastroauActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }


        });

    }



    private void saveuserinfirebase() {
        final String id = mAuth.getCurrentUser().getUid();
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        Query checarusuário = FirebaseDatabase.getInstance().getReference("Usuários").orderByChild("telefone").equalTo(telefone);
        checarusuário.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    medttelefone.setError("Este Número de Telefone ja existe");
                } else {

                        ref.putFile(mSelectUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.i("Teste", uri.toString());
                                                final String profileurl = uri.toString();

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("nome", nome);
                                                map.put("email", email);
                                                map.put("cpf", cpf);
                                                map.put("telefone", telefone);
                                                map.put("foto", profileurl);
                                                map.put("id", id);
                                                final String id = mAuth.getCurrentUser().getUid();
                                                mDatabase.child("Usuários").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task2) {
                                                        if (task2.isSuccessful()) {

                                                            Intent intent = new Intent(CadastroauActivity.this, HomeActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            intent.putExtra("nome", nome);
                                                            intent.putExtra("cpf", cpf);
                                                            intent.putExtra("id", id);
                                                            intent.putExtra("foto", profileurl);
                                                            intent.putExtra("email", email);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(CadastroauActivity.this, "Não pode criar os dados corretamente", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });
                                            }



                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("Teste", e.getMessage(), e);
                                    }
                                });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
           if(data != null){
               mSelectUri = data.getData();
               Bitmap bitmap = null;
               try {
                   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectUri);
                   mImagephoto.setImageDrawable(new BitmapDrawable(bitmap));
                   mbtnselectimg.setAlpha(0);

               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           }

    }

    private void selectimg() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }


    public void voltarTelaEscolha(View view) {
        Intent ir = new Intent(this, OpcaoActivity.class);
        startActivity(ir);
    }
}


