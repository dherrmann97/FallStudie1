package com.example.fallstudie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EinlogenActivity extends AppCompatActivity {

    private EditText email;
    private EditText passwort;
    private Button einlogenBtn;
    private TextView einlogenfr;
    private FirebaseAuth mauth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einlogen);


        email = findViewById(R.id.emailEinfügen);
        passwort = findViewById(R.id.passwortEinfügen);
        einlogenBtn = findViewById(R.id.einlogenknopf);
        einlogenfr = findViewById(R.id.keinAccount);
        mauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        einlogenfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EinlogenActivity.this, RegistrierenActivity.class);
                startActivity(intent);
            }
        });

        einlogenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = email.getText().toString();
                String passwortString = passwort.getText().toString();

                if(TextUtils.isEmpty(emailString)){
                    email.setError("Feld darf nicht leer sein");
                }
                if(TextUtils.isEmpty(passwortString)){
                    passwort.setError("Feld darf nicht leer sein");
                } else {
                    progressDialog.setMessage("Einlogen");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mauth.signInWithEmailAndPassword(emailString, passwortString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(EinlogenActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            }else {
                                Toast.makeText(EinlogenActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                                progressDialog.dismiss();
                            }

                        }
                    });
                }
            }
        });







    }
}