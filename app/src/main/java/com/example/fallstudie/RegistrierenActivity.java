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
    //Variablen definieren

    private EditText emailAD;
    private EditText passwort;
    private Button registrierenBtn;
    private TextView registrierenfr;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrieren);

        //variablen initialisieren
        registrierenBtn = findViewById(R.id.registrierenknopf);
        registrierenfr = findViewById(R.id.keinAccount);
        emailAD = findViewById(R.id.emaileingeben);
        passwort = findViewById(R.id.passworteingeben);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //Springen von Registrieren zurück zur Einloggen
        registrierenfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrierenActivity.this, EinlogenActivity.class);
                startActivity(intent);
            }
        });
        //Eingaben überprüfen, authentifizierung via Firebase und dann springen auf Mainactivity
        registrierenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email2 = emailAD.getText().toString();
                String passwort2 = passwort.getText().toString();

                if (TextUtils.isEmpty(email2)) {
                    emailAD.setError("Feld darf nicht leer sein");
                }
                if (TextUtils.isEmpty(passwort2)) {
                    passwort.setError("Feld darf nicht leer sein");
                } else {
                    progressDialog.setMessage("Registrieren");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    auth.createUserWithEmailAndPassword(email2, passwort2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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