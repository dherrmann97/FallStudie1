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
    //Variablen definieren inkl. Firebase variablen

    private EditText email;
    private EditText passwort;
    private Button einlogenKnpf;
    private TextView einlogenfr;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einlogen);


        //Variablen initialisieren
        email = findViewById(R.id.emailEinfügen);
        passwort = findViewById(R.id.passwortEinfügen);
        einlogenKnpf = findViewById(R.id.einlogenknopf);
        einlogenfr = findViewById(R.id.keinAccount);
        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        //Auf die nächste Aktivität springen, hier von "haben sie kein Account?" zu Activity Registrieren springen
        einlogenfr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EinlogenActivity.this, RegistrierenActivity.class);
                startActivity(intent);
            }
        });
        // hier die Eingaben vom User überprüefen und Authentification von Firebase verwenden. Nach Authentifizierung auf MainActivity springen
        einlogenKnpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = email.getText().toString();
                String passwort1 = passwort.getText().toString();

                if(TextUtils.isEmpty(email1)){
                    email.setError("Feld darf nicht leer sein");
                }
                if(TextUtils.isEmpty(passwort1)){
                    passwort.setError("Feld darf nicht leer sein");
                } else {
                    progressDialog.setMessage("Einlogen");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    auth.signInWithEmailAndPassword(email1, passwort1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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