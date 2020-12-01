package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private FirebaseDatabase database;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<User> listuser = new ArrayList<User>();
    private ArrayAdapter<User> arrayAdapteruser;
    private EditText editpalavra;
    private ImageButton imgBtn;
    private ListView listVpesquisa;
    private FirebaseAuth firebaseAuth;
    private static final String USERS= "Usuários";
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        inicializarcomponentes();
        inicializafirebase();
        eventoedit();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(USERS);
        RecyclerView rc = findViewById(R.id.recycler);
        adapter = new GroupAdapter();

        imgBtn = findViewById(R.id.imgBtn4);
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);

                UserItem userItem = (UserItem) item;
                intent.putExtra("user", userItem.user);
                startActivity(intent);
            }
        });
        buscarusuarios();
    }

    private void inicializafirebase() {
        FirebaseApp.initializeApp(ContactsActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void inicializarcomponentes() {
        editpalavra = (EditText) findViewById(R.id.edtpalavra3);
        //* listVpesquisa = (ListView) findViewById(R.id.listVpesquisa);
    }

    private void eventoedit() {
        editpalavra.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String palavra = editpalavra.getText().toString().trim();
                pesquisarPalavra(palavra);
            }
        });
    }

    private void pesquisarPalavra(String palavra) {
        Query query;
        if(palavra.equals("")){
            query = databaseReference.child("Usuários").orderByChild("nome");

        }else{
            query = databaseReference.child("Usuários").orderByChild("nome").startAt(palavra).endAt(palavra+"\uf8ff");
        }
        adapter.clear();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot objsnapshot : dataSnapshot.getChildren()){
                    User u= objsnapshot.getValue(User.class);
                    adapter.add(new UserItem(u));
                }
                arrayAdapteruser = new ArrayAdapter<User>(ContactsActivity.this, android.R.layout.simple_list_item_1, listuser);
                //*  listVpesquisa.setAdapter(arrayAdapteruser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void buscarusuarios() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@Nullable DataSnapshot dataSnapshot) {

                for( DataSnapshot ds:dataSnapshot.getChildren()){
                    User user= ds.getValue(User.class);
                    adapter.add(new UserItem(user));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private class UserItem extends Item<ViewHolder> {
        private final User user;


        private UserItem(User user) {
            this.user = user;

        }



        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txt_Username = viewHolder.itemView.findViewById(R.id.textView18);
            ImageView imageView = viewHolder.itemView.findViewById(R.id.imageView3);

            txt_Username.setText(user.getNome());
            Picasso.get()
                    .load(user.getProfileurl())
                    .into(imageView);

        }

        @Override
        public int getLayout() {
            return R.layout.item_usuario;
        }
    }


}
