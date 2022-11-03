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

public class RegistrierenActivity extends AppCompatActivity {

    private EditText emailET;
    private EditText passwortET;
    private Button registrierenBtn;
    private TextView registrierenfr;
    private FirebaseAuth mauth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrieren);


        registrierenBtn = findViewById(R.id.registrierenknopf);
        registrierenfr = findViewById(R.id.keinAccount);
        emailET = findViewById(R.id.emaileingeben);
        passwortET = findViewById(R.id.passworteingeben);
        mauth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        registrierenfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrierenActivity.this, EinlogenActivity.class);
                startActivity(intent);
            }
        });

        registrierenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailString = emailET.getText().toString();
                String passwortString = passwortET.getText().toString();

                if (TextUtils.isEmpty(emailString)) {
                    emailET.setError("Feld darf nicht leer sein");
                }
                if (TextUtils.isEmpty(passwortString)) {
                    passwortET.setError("Feld darf nicht leer sein");
                } else {
                    progressDialog.setMessage("Registrieren");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mauth.createUserWithEmailAndPassword(emailString, passwortString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrierenActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(RegistrierenActivity.this, task.getException().toString(), Toast.LENGTH_SHORT);
                                progressDialog.dismiss();
                            }

                        }
                    });
                }
            }
        });
    }
}