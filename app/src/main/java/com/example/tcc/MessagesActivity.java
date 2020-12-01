package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class MessagesActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        RecyclerView rv = findViewById(R.id.recycler_ultimas);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        verifyAuthentication();
        fetchlastmessage();

    }

    private void fetchlastmessage() {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference();
        DatabaseReference conversasRef = FirebaseDatabase.getInstance().getReference().child("Conversas");
        conversasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot productsSnapshot = requestSnapshot.child("Location");

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void verifyAuthentication(){
        if(FirebaseAuth.getInstance().getUid() == null){
             Intent intent= new Intent(MessagesActivity.this,  LoginActivity.class);
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
             startActivity(intent);
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.contatos:
               Intent intent =  new Intent(MessagesActivity.this, ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAuthentication();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
