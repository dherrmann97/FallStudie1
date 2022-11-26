package com.example.fallstudie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AusgabenWocheActivity extends AppCompatActivity {


    private Toolbar tlbr;
    private RecyclerView rclview;
    private ProgressBar progBar;
    private TextView gsmtAusgabeWoche;
    private FirebaseAuth auth;
    private String userid="";
    private DatabaseReference ausgabenRef;
    private AdptAusgbWoche wochenadpt;
    private List<Data> meineDatenList;
    private String sort="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ausgaben_woche);
        //Intitial. Var
        tlbr = findViewById(R.id.toolbar);
        setSupportActionBar(tlbr);
        getSupportActionBar().setTitle("Wochen Ausgabe");
        rclview = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rclview.setHasFixedSize(true);
        rclview.setLayoutManager(linearLayoutManager);
        progBar = findViewById(R.id.progressBar);
        gsmtAusgabeWoche = findViewById(R.id.gesamtBudgetWoche);
        meineDatenList= new ArrayList<>();
        wochenadpt = new AdptAusgbWoche(AusgabenWocheActivity.this, meineDatenList);
        rclview.setAdapter(wochenadpt);
        //Initialisierung auth (Firebase)
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();

        if(getIntent().getExtras()!=null){
            sort = getIntent().getStringExtra(sort);
            if(sort.equals("woche")){
                wochenAusgbLesen();

            }else if (sort.equals(("monat"))){
                monatAusgbLesesn();
            }
        }



    }

    private void monatAusgbLesesn() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime jetzt = new DateTime();
        Months monat = Months.monthsBetween(epoch, jetzt);

        ausgabenRef = FirebaseDatabase.getInstance().getReference("Ausgaben").child(userid);
        Query qr = ausgabenRef.orderByChild("month").equalTo(monat.getMonths());
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                meineDatenList.clear();
                for(DataSnapshot snp : snapshot.getChildren()){
                    Data data = snp.getValue(Data.class);
                    meineDatenList.add(data);
                }

                wochenadpt.notifyDataSetChanged();
                progBar.setVisibility(View.GONE);

                //Ausgaben der Woche zeigen
                int gsmtSumme = 0;
                for(DataSnapshot datasnp: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) datasnp.getValue();
                    Object gsmt = map.get("summe");
                    int pgsmt = Integer.parseInt(String.valueOf(gsmt));
                    gsmtSumme = gsmtSumme + pgsmt;

                    gsmtAusgabeWoche.setText("Gesamt Monatsausgaben: " + gsmtSumme);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Methode um Ausgaben der Woche abzurufen
    private void wochenAusgbLesen() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime jetzt = new DateTime();
        Weeks woche = Weeks.weeksBetween(epoch, jetzt);

        ausgabenRef = FirebaseDatabase.getInstance().getReference("Ausgaben").child(userid);
        Query qr = ausgabenRef.orderByChild("woche").equalTo(woche.getWeeks());
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                meineDatenList.clear();
                for(DataSnapshot snp : snapshot.getChildren()){
                    Data data = snp.getValue(Data.class);
                    meineDatenList.add(data);
                }

                wochenadpt.notifyDataSetChanged();
                progBar.setVisibility(View.GONE);

                //Ausgaben der Woche zeigen
                int gsmtSumme = 0;
                for(DataSnapshot datasnp: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) datasnp.getValue();
                    Object gsmt = map.get("summe");
                    int pgsmt = Integer.parseInt(String.valueOf(gsmt));
                    gsmtSumme = gsmtSumme + pgsmt;

                    gsmtAusgabeWoche.setText("Gesamt Wochenausgaben: " + gsmtSumme);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}