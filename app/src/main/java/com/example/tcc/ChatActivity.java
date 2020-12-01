package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private  GroupAdapter adapter;
    private User user;
    private EditText editChat;
    private TextView nameuser;
    private ImageView img_chat;

    private Class<User> me;
    private DatabaseReference msgRef,msg2;
    private FirebaseDatabase database;
    private List<Message> lista = new ArrayList<>();
    private List<Long>  timestamps = new ArrayList<>();
    private DatabaseReference mdatabase;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        user = getIntent().getExtras().getParcelable("user");
        Log.e("teste", user.getId());
        nameuser = (TextView) findViewById(R.id.nome_user);
        img_chat = (ImageView) findViewById(R.id.img_chat);
        nameuser.setText(user.getNome());
        Picasso.get()
                .load(user.getFoto())
                .into(img_chat);

        RecyclerView rv = findViewById(R.id.recycler_chat);
        Button btn_chat = findViewById(R.id.btn_chat);
        editChat = findViewById(R.id.edit_chat);

        database = FirebaseDatabase.getInstance();
        msgRef = database.getReference("Conversas").child(FirebaseAuth.getInstance().getUid()
        ).child(user.getId());


        mdatabase = FirebaseDatabase.getInstance().getReference();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();



            }
        });
         adapter = new GroupAdapter();
         rv.setLayoutManager(new LinearLayoutManager(this));
         rv.setAdapter(adapter);
         FirebaseAuth.getInstance().getUid();


            fetchmessage();

    }

    private void fetchmessage() {

       msgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //value add then it will call

                Message message = dataSnapshot.getValue(Message.class);
                adapter.add(new Itemsmessages(message));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //value change
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // value remove
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    private void sendMessage() {
        Toast.makeText(ChatActivity.this, "Mensagem Enviada com sucesso", Toast.LENGTH_SHORT).show();
        String text = editChat.getText().toString();
        editChat.setText(null);
        String fromId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String toId = user.getId();
        long timestamp = System.currentTimeMillis();
        Message message = new Message();

        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);
        if (!message.getText().isEmpty()) {
            FirebaseAuth.getInstance();
            Toast.makeText(this, "Mensagem Enviada" ,Toast.LENGTH_SHORT).show();

            mdatabase.child("Conversas").child(fromId).child(toId).child(String.valueOf(timestamp)).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("oi");

                }
            });
            mdatabase.child("ultimasmensagens").child(fromId).child(toId).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("oi");

                }
            });

            mdatabase.child("Conversas").child(toId).child(fromId).child(String.valueOf(timestamp)).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("oi");
                }
            });
            mdatabase.child("ultimasmensagens").child(toId).child(fromId).setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("oi");
                }
            });


        }
    }

            private class Itemsmessages extends Item<ViewHolder> {
                private final Message message;

                private Itemsmessages(Message message) {
                    this.message = message;
                }


                @Override
                public void bind(@NonNull ViewHolder viewHolder, int position) {
                    TextView txtmsg = viewHolder.itemView.findViewById(R.id.txt_message);
                    ImageView  imgmsg = viewHolder.itemView.findViewById(R.id.img_message_usuario);
                    txtmsg.setText(message.getText());





                }

                @Override
                public int getLayout() {
                   return message.getFromId() .equals(FirebaseAuth.getInstance().getUid())


                           ? R.layout.item_to_message

                           : R.layout.item_message;


                }
            }
        }







