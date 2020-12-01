package com.example.tcc;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TelaestAddActivity extends AppCompatActivity {
    private EditText medtNome;
    private EditText medtquant;
    private EditText medtvalor;
    private Button btnAdd;
    private EditText edtDate;
    private Uri mSelectUri;
    private ImageButton imgBtn;
    private ImageView mImagephoto;
    private TextView title;
    private Button mbtnselectimg;
    DatePickerDialog.OnDateSetListener setListener;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
  ProgressDialog progressDialog;
    private String nome = "";
    private String nomeunico ;
    private Integer quantidade ;
    private Float valor;
    private String datafab ="";
    private String foto ="";
   StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.telaestadd);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        medtNome = (EditText) findViewById(R.id.edtnome);
        medtquant = (EditText) findViewById(R.id.edtquanti);
        medtvalor = (EditText) findViewById(R.id.edtvalor);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        edtDate = (EditText) findViewById(R.id.edtDate);
        mbtnselectimg = (Button) findViewById(R.id.btn_photo3);
        imgBtn = findViewById(R.id.imgBtn5);
        title = findViewById(R.id.textView13);
        mImagephoto = (ImageView) findViewById(R.id.img_photo3);
        edtDate.setFocusable(false);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaestAddActivity.this, TelaestActivity.class);
                startActivity(intent);
            }
        });
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TelaestAddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                          month = month+1;
                          String date = day+"/"+month+"/"+year;
                          edtDate.setText(date);
                    }
                }, year, month,day);
                datePickerDialog.show();

            }
        });
        mbtnselectimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectimg();
            }
        });
        Bundle intent = getIntent().getExtras();
        if(intent != null){
            nomeunico = intent.getString("nomeunico");
            quantidade = intent.getInt("quantidade");
            valor = intent.getFloat("valor");
            datafab = intent.getString("datafab");
            foto = intent.getString("foto");

            medtNome.setText(nomeunico);
            medtquant.setText(String.valueOf(quantidade));
            medtvalor.setText(String.valueOf(valor));
            edtDate.setText(datafab);
            title.setText("Atualizar Produto");
            Picasso.get().load(foto).into(mImagephoto);
            mbtnselectimg.setAlpha(0);
            btnAdd.setText("Atualizar");

        }


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nome = medtNome.getText().toString();
                datafab = edtDate.getText().toString();
                if(btnAdd.getText().equals("Adicionar")){
                    if(medtquant.getText().toString().isEmpty() || medtvalor.getText().toString().isEmpty() || nome.isEmpty() || datafab.isEmpty()){
                        Toast.makeText(TelaestAddActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    } else {
                        quantidade = Integer.valueOf(medtquant.getText().toString());
                        valor = Float.valueOf(medtvalor.getText().toString());
                        registrarproduto();
                    }
                }else{
                   updateDatabase();
                }





            }

        });
    }





    private void updateDatabase() {

        nome = medtNome.getText().toString();
        datafab = edtDate.getText().toString();
        quantidade = Integer.valueOf(medtquant.getText().toString());
        valor = Float.valueOf(medtvalor.getText().toString());

        final String id = mAuth.getCurrentUser().getUid();
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);

        if(mSelectUri == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Produtos");
            Query query = userRef.orderByChild("nome").equalTo(nomeunico);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        ds.getRef().child("nome").setValue(nome);
                        ds.getRef().child("valor").setValue(valor);
                        ds.getRef().child("quantidade").setValue(quantidade);
                        ds.getRef().child("datafab").setValue(datafab);
                    }
                    startActivity(new Intent(TelaestAddActivity.this, TelaestActivity.class));
                    finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            ref.putFile(mSelectUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String profileUrl = uri.toString();
                            if(profileUrl == ""){
                                Toast.makeText(TelaestAddActivity.this,"Selecione uma imagem",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Produtos");
                                Query query = userRef.orderByChild("nome").equalTo(nomeunico);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot ds: snapshot.getChildren()){
                                            ds.getRef().child("nome").setValue(nome);
                                            ds.getRef().child("valor").setValue(valor);
                                            ds.getRef().child("quantidade").setValue(quantidade);
                                            ds.getRef().child("datafab").setValue(datafab);
                                            ds.getRef().child("foto").setValue(profileUrl);

                                        }
                                        startActivity(new Intent(TelaestAddActivity.this, TelaestActivity.class));
                                        finish();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }
                    });

                }
            });
        }




    }

    private void registrarproduto() {
          final String id = mAuth.getCurrentUser().getUid();
          String filename = UUID.randomUUID().toString();
          final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);

          if(mSelectUri == null) {
              Toast.makeText(this, "Selecione uma imagem para o produto", Toast.LENGTH_SHORT).show();
          } else {
              ref.putFile(mSelectUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                          @Override
                          public void onSuccess(Uri uri) {

                              String profileurl = uri.toString();
                              if(profileurl ==  ""){
                                  Toast.makeText(TelaestAddActivity.this,"Selecione uma imagem",
                                          Toast.LENGTH_SHORT).show();
                              }
                              Map<String, Object> map = new HashMap<>();
                              map.put("nome", nome);
                              map.put("idusuario", id);
                              map.put("quantidade", quantidade);
                              map.put("valor", valor);
                              map.put("datafab", datafab);
                              map.put("foto",profileurl);
                              FirebaseDatabase database = FirebaseDatabase.getInstance();
                              String id = mAuth.getCurrentUser().getUid();
                              mDatabase.child("Usuários").child(id).child("Produtos").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      Intent intent = new Intent(getApplicationContext(), TelaestActivity.class);
                                      startActivity(intent);
                                  }
                              });


                          }


                      });
                  }
              });

          }
          }








    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            if (data != null) {
                mSelectUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectUri);
                    mImagephoto.setImageDrawable(new BitmapDrawable(bitmap));
                    mbtnselectimg.setAlpha(0);
                    if (mSelectUri == null) {

                    }

                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Não foi possível selecionar uma imagem", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
        private void selectimg () {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);

        }

}
