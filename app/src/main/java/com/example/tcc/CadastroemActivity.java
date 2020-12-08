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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class CadastroemActivity extends AppCompatActivity {

    private EditText medtrazao;
    private EditText medtEmail2;
    private EditText medtCnpj;
    private EditText medtSenha2;
    private EditText medtTelefone;
    private Button mbtnCadastrar2;
    private Button mbtnselectimg2;
    private ImageView mImagephoto2;
    private Uri mSelectUri;
    private CountryCodePicker countryCodePicker;
    //Variaveis dos dados que vamos registrar
    private String nome = "";
    private String email = "";
    private String cnpj = "";
    private String senha = "";
    private String telefone = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
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
        setContentView(R.layout.cadastroempresa);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        countryCodePicker = (CountryCodePicker) findViewById(R.id.countryCodePicker2);
        medtrazao = (EditText) findViewById(R.id.edtRazão);
        medtEmail2 = (EditText) findViewById(R.id.edtEmail2);
        medtCnpj = (EditText) findViewById(R.id.edtCnpj);
        medtSenha2 = (EditText) findViewById(R.id.edtSenhaEm);
        medtTelefone = (EditText) findViewById(R.id.edttelefone2);
        mbtnCadastrar2 = (Button) findViewById(R.id.btnCadastrar);
        mbtnselectimg2 = (Button) findViewById(R.id.btn_photo2);
        mImagephoto2 = (ImageView) findViewById(R.id.img_photo2);
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN.NNN.NNN/NNNN-NN");
        SimpleMaskFormatter tel = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher telefone2 = new SimpleMaskTextWatcher(medtTelefone, tel);
        MaskTextWatcher mtw = new MaskTextWatcher(medtCnpj, smf);
        medtTelefone.addTextChangedListener(telefone2);
        medtCnpj.addTextChangedListener(mtw);
        mbtnselectimg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimg();
            }
        });
        mbtnCadastrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = medtrazao.getText().toString();
                email = medtEmail2.getText().toString();
                cnpj = medtCnpj.getText().toString();
                senha = medtSenha2.getText().toString();
                telefone = "+" + countryCodePicker.getFullNumber() + medtTelefone.getText().toString();


                if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty() && !cnpj.isEmpty() && !telefone.isEmpty()) {
                    if (senha.length() >= 6) {
                        for (char c : senha.toCharArray())
                            if (c >= 'A' && c <= 'Z') {
                                medtSenha2.setError(null);

                                Query checarusuário = FirebaseDatabase.getInstance().getReference("Usuários").orderByChild("telefone").equalTo(telefone);
                                checarusuário.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            medtTelefone.setError("Esse número de telefone existe");


                                        } else {
                                            if(mSelectUri == null) {
                                                Toast.makeText(CadastroemActivity.this, "Você precisa informar uma foto", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CadastroemActivity.this, "A senha deve conter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();

                    }


                } else {

                    Toast.makeText(CadastroemActivity.this, "Complete todos os campos", Toast.LENGTH_SHORT).show();
                }


            }

            private void registrarUsuario() {
                mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveuserinfirebase();








                        } else {
                            Toast.makeText(CadastroemActivity.this,task.getException().getMessage().toString() , Toast.LENGTH_SHORT).show();
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
        ref.putFile(mSelectUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String profileurl = uri.toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("nome", nome);
                        map.put("email", email);
                        map.put("cnpj", cnpj);
                        map.put("telefone", telefone);
                        map.put("foto",profileurl);
                        map.put("id" , id);
                        String id = mAuth.getCurrentUser().getUid();
                        mDatabase.child("Usuários").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task2.isSuccessful()) {
                                    startActivity(new Intent(CadastroemActivity.this, HomeActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(CadastroemActivity.this, "Não pode criar os dados corretamente", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0){
            mSelectUri= data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectUri);
                mImagephoto2.setImageDrawable(new BitmapDrawable(bitmap));
                mbtnselectimg2.setAlpha(0);

            } catch (IOException e) {

            }
        }
    }

    private void selectimg() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);

    }

    public void voltarTelaEscolha1(View view) {
        Intent ir = new Intent(this,OpcaoActivity.class);
        startActivity(ir);
    }
}