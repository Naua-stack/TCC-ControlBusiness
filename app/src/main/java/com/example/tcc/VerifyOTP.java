package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class VerifyOTP extends AppCompatActivity {
  private PinView pinView;
  private  String codeBySystem;
  private TextView txtmensagem;
  String whatToDo, nome, email, cpf, profileurl, id, telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_verify_o_t_p);
        pinView = findViewById(R.id.pinview);

         telefone = getIntent().getStringExtra("telefone");
        nome = getIntent().getStringExtra("nome");
        email= getIntent().getStringExtra("email");
        cpf = getIntent().getStringExtra("cpf");
        profileurl = getIntent().getStringExtra("foto");
        id = getIntent().getStringExtra("id");
        whatToDo = getIntent().getStringExtra("whatToDo");
         txtmensagem = (TextView) findViewById(R.id.txt_msg);
         txtmensagem.setText("digite uma senha única enviada em\n" +telefone);


        enviarCódigoparausuário(telefone);
    }

    private void enviarCódigoparausuário(String telefone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                telefone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;

                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if(code!=null){
                        pinView.setText(code);
                       verifycode(code);

                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(VerifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    private void verifycode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(whatToDo.equals("updateData")){
                                updateOldUsersData();
                            }
                            else{

                                Toast.makeText(VerifyOTP.this, "Verificação não completada", Toast.LENGTH_SHORT).show();
                            }


                        } else {


                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(VerifyOTP.this, "Verificação não completada", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void updateOldUsersData() {
        Intent intent = new Intent(getApplicationContext(), NovaSenha.class);
        intent.putExtra("telefone", telefone);
        startActivity(intent);
        finish();
    }



    public void chamarproximatela(View view){
         String code = pinView.getText().toString();
         if(!code.isEmpty()){
             verifycode(code);
         }
    }
}
