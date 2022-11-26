package com.example.fallstudie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //Cardview von ActivityLayout def. und init.
    private ImageView meinBudget;
    private ImageView tagesAusgabe;
    private ImageView wocheAusgaben;
    private ImageView monatAusgabe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meinBudget= findViewById(R.id.meinBudget);
        tagesAusgabe = findViewById(R.id.ausgabenHeute);
        monatAusgabe = findViewById(R.id.monatAusgabe);
        wocheAusgaben = findViewById(R.id.wocheasgb);

        //Von Main- auf BudgetActivity springen


        //von MainActivity auf MeinBudget springen
        meinBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        //von Mainactivity auf AusgabenHeute springen
        tagesAusgabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AusgabenHeuteActivity.class);
                startActivity(intent);
            }
        });

        //von MainActivity auf AusgabenWocheActivity springen
        wocheAusgaben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AusgabenWocheActivity.class);
                intent.putExtra("sort","woche");
                startActivity(intent);
            }
        });
        //Von MainActivity Monatsausgaben in der Klasse AusgabenwochenActivity gezeigt. Siehe Methode in AusgabenWochenActivity-Klasse
        monatAusgabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AusgabenWocheActivity.class);
                intent.putExtra("sort", "monat");
            }
        });
    }
}