package com.example.tcc;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.OnItemLongClickListener;
import com.xwray.groupie.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TelaestActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private LinearLayout mycontent, overbox;
    private Produtos produtos;
    private TextView txtquantidade;
    boolean isScrooling = false;
    private FirebaseDatabase firebaseDatabase;
    private TextView txtNoProducts, nomeproduto;
    private ImageView imgNoProducts;
    private DatabaseReference databaseReference;
    private EditText editpalavra1;
    private ArrayAdapter<Produtos> arrayAdapteruser;
    private List<Produtos> listprodutos = new ArrayList<Produtos>();
    private ArrayAdapter<Produtos> arrayAdapterproduto;
    private TextView SemProdutoNoEstoque;
    private static final String USUÁRIOS = "Usuários";
    private static final String PRODUTOS = "Produtos";
    private GroupAdapter adapter;
    int currentItems, scrollOutItems, totalitems;
    LinearLayoutManager manager;
    private int countAdapter;
    private Animation fromsmall;
    ArrayList list;
    FirebaseAuth mAuth;
    Query query;
    private FirebaseDatabase database;
    private GestureOverlayView layoutManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.telaest);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        database = FirebaseDatabase.getInstance();
        RecyclerView rc = findViewById(R.id.recycler2);


        fromsmall = AnimationUtils.loadAnimation(this, R.anim.fromsmall);
        imgNoProducts = (ImageView) findViewById(R.id.btn_photoNoProducts);
        adapter = new GroupAdapter();

        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.setAdapter(adapter);


        buscarprodutos();


        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent = new Intent(TelaestActivity.this, HomeActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_estoque:

                        break;
                    case R.id.nav_account:
                        Intent intent1 = new Intent(TelaestActivity.this, PerfilActivity.class);
                        startActivity(intent1);
                        break;

                }
                return false;
            }
        });
    }







    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private void buscarprodutos() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Produtos");
        if (userRef != null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        final Produtos produtos = productSnapshot.getValue(Produtos.class);
                        adapter.add(new ProdutoItem(produtos));
                        countAdapter = adapter.getItemCount();

                        txtquantidade = (TextView) findViewById(R.id.txt_quantidade);
                        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(@NonNull Item item, @NonNull View view) {
                                final ProdutoItem produtoItem = (ProdutoItem) item;

                                AlertDialog.Builder builder = new AlertDialog.Builder(TelaestActivity.this);
                                String[] options = {"Update", "Delete"};
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            Intent intent = new Intent(TelaestActivity.this, TelaestAddActivity.class);
                                            intent.putExtra("nomeunico", produtoItem.produtos.getNome());
                                            intent.putExtra("datafab", produtoItem.produtos.getDatafab());
                                            intent.putExtra("foto", produtoItem.produtos.getFoto());
                                            intent.putExtra("valor", produtoItem.produtos.getValor());
                                            intent.putExtra("quantidade", produtoItem.produtos.getQuantidade());
                                            Log.e("produto", String.valueOf(produtoItem.produtos.getValor()));
                                            startActivity(intent);
                                        }
                                        if (which == 1) {
                                            showDeleteDatadialog(produtoItem.produtos.getNome());
                                        }
                                    }
                                });
                                builder.create().show();
                                return false;
                            }

                        });
                    }


                    if (countAdapter > 1) {
                        txtquantidade.setText("Você possui" + "  " + countAdapter + "  " + "Produtos");
                        imgNoProducts.setVisibility(View.INVISIBLE);


                    }
                    if (countAdapter == 1) {
                        txtquantidade.setText("Você possui" + "  " + countAdapter + "  " + "Produto");
                        imgNoProducts.setVisibility(View.INVISIBLE);


                    }


                    if (adapter.getItemCount() == 0) {
                        txtNoProducts = (TextView) findViewById(R.id.txtSemProdutos);

                        txtNoProducts.setText("Você não possui produtos no estoque");
                        imgNoProducts.setVisibility(View.VISIBLE);


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }


    private void showDeleteDatadialog(final String nome) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaestActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Deseja Realmente excluir o produto" + " " + nome);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference userRef = database.getInstance().getReference().child("Usuários").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Produtos");
                Query mquery = userRef.orderByChild("nome").equalTo(nome);
                mquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                          ds.getRef().removeValue();

                            finish();
                            startActivity(getIntent());
                        }
                        Toast.makeText(TelaestActivity.this, "Produto deletado com sucesso", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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


    public void abrirtelaest(View view) {
        Intent ir = new Intent(this, TelaestAddActivity.class);
        startActivity(ir);
    }

    private class ProdutoItem extends Item<ViewHolder> {
        private final Produtos produtos;


        ProdutoItem(Produtos produtos) {
            this.produtos = produtos;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txt_nomeproduto = viewHolder.itemView.findViewById(R.id.textView18);
            TextView txt_preco = viewHolder.itemView.findViewById(R.id.preco);
            TextView txt_fabricacao = viewHolder.itemView.findViewById(R.id.fabricacao);
            TextView txt_quantidade = viewHolder.itemView.findViewById(R.id.quantidade);

            ImageView imageViewProduto = viewHolder.itemView.findViewById(R.id.imageViewProduto);
            String preco = String.valueOf(produtos.getValor());
            String quantidade = String.valueOf(produtos.getQuantidade());


            txt_nomeproduto.setText(produtos.getNome());
            txt_preco.setText(preco);
            txt_quantidade.setText(quantidade);

            txt_fabricacao.setText(produtos.getDatafab());


            Picasso.get()
                    .load(produtos.getProfileurl())
                    .into(imageViewProduto);


        }


        @Override
        public int getLayout() {
            return R.layout.item_produtos;
        }


    }


}
