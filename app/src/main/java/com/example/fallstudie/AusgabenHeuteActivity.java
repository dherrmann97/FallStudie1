package com.example.fallstudie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class AusgabenHeuteActivity extends AppCompatActivity {

    private TextView ausgabeheute;
    private RecyclerView recycler;
    private Toolbar toolbr;
    private ProgressBar progressbar;
    private FloatingActionButton floatknpf;
    private ProgressDialog loader;
    private FirebaseAuth auth;
    private String userid="";
    private DatabaseReference ausgabenRef;
    private AdptAusgbHeute ausgabenHeute;
    private List<Data> meinDatenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ausgaben_heute);

        ausgabeheute = findViewById(R.id.gesammtAusgabe);
        recycler = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(linearLayoutManager);
        meinDatenList= new ArrayList<>();
        ausgabenHeute = new AdptAusgbHeute(this, meinDatenList);
        recycler.setAdapter(ausgabenHeute);
        toolbr = findViewById(R.id.toolbar);
        progressbar = findViewById(R.id.progressBar);
        floatknpf = findViewById(R.id.fab);
        setSupportActionBar(toolbr);
        getSupportActionBar().setTitle("Ausgaben heute");
        loader = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        userid = auth.getCurrentUser().getUid();
        ausgabenRef = FirebaseDatabase.getInstance().getReference("Ausgaben").child(userid);

        ausgbLesen();

        floatknpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ausgabehinfügen();
            }
        });



    }

    private void ausgbLesen() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar kalender = Calendar.getInstance();
        String datum = dateFormat.format(kalender.getTime());

        DatabaseReference referenz = FirebaseDatabase.getInstance().getReference("ausgaben").child(userid);
        Query qr = referenz.orderByChild("datum").equalTo(datum);
        qr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meinDatenList.clear();
                for(DataSnapshot dtsnap : snapshot.getChildren()){
                    Data data = dtsnap.getValue(Data.class);
                    meinDatenList.add(data);
                }
                ausgabenHeute.notifyDataSetChanged();
                progressbar.setVisibility(View.GONE);

                //Summe für heute aktualisieren
                int gsmtSumme = 0;
                for(DataSnapshot datasnp: snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)datasnp.getValue();
                    Object gsmt = map.get("summe");
                    int pgsmt = Integer.parseInt(String.valueOf(gsmt));
                    gsmtSumme= gsmtSumme +pgsmt;

                    ausgabeheute.setText("Gesamt Ausgaben heute: "+ gsmtSumme);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void ausgabehinfügen() {
        AlertDialog.Builder myDialog= new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        //Die Views von inputLayout initialisieren
        final Spinner spiner = myView.findViewById(R.id.itemsSpinner);
        final EditText summe1 = myView.findViewById(R.id.amount);
        final Button löschen = myView.findViewById(R.id.cancel);
        final Button speichern = myView.findViewById(R.id.save);
        final EditText notizen = myView.findViewById(R.id.note);
        notizen.setVisibility(View.VISIBLE);

        /*Den Knopf "Speichern" in clickListener setzen. Davor sicherstellen,
        dass der User eine valide Ausgabe und valide Summe ausgewählt/eingegeben hat*/
        speichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String summe = summe1.getText().toString();
                String ausgabe  = spiner.getSelectedItem().toString();
                String notiz = notizen.getText().toString();

                if(TextUtils.isEmpty(summe)){
                    summe1.setError("Feld darf nicht leer sein");
                    return;
                }
                if(ausgabe.equals("Kategorien")){
                    Toast.makeText(AusgabenHeuteActivity.this, "Wählen Sie eine Kategorie", Toast.LENGTH_SHORT).show();
                }

                if(TextUtils.isEmpty(notiz)){
                    notizen.setError("Geben sie eine Annmerkung ein ");
                    return;
                }
                else{
                    loader.setMessage("Kategorie wird hinzugefügt");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id= ausgabenRef.push().getKey();

                    //Datum wird hinzugefügt mit epochTime
                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar kalender = Calendar.getInstance();
                    String datum = dateFormat.format(kalender.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime jetzt = new DateTime();
                    Weeks woche = Weeks.weeksBetween(epoch, jetzt);
                    Months monat = Months. monthsBetween(epoch, jetzt);



                    //In Firebase hinzufügen mithilfe der Modell-Klasse Data. Es wird überprüft ob das task erfolgreich durchgeführt wurde
                    //wenn nicht ergolreich dann ein Exeption wird geworfen
                    Data data = new Data(ausgabe, datum, id, notiz, Integer.parseInt(summe), monat.getMonths(), woche.getWeeks());
                    ausgabenRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(AusgabenHeuteActivity.this, "Kategorie gespeichert", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AusgabenHeuteActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();

                        }
                    });
                }
                dialog.dismiss();


            }
        });
        löschen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
}