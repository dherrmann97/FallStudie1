package com.example.fallstudie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.Hold;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {

    //Def Variablen

    private FloatingActionButton faknpf;
    private DatabaseReference budgetRef;
    private FirebaseAuth auth;
    private ProgressDialog loader;
    private TextView gesamtBudget;
    private RecyclerView recview;
    private String post_schlüssel= "";
    private String ausgabe="";
    private int summe=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        auth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(auth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);
        gesamtBudget = findViewById(R.id.totalBudgetAmount);
        recview = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recview.setHasFixedSize(true);
        recview.setLayoutManager(linearLayoutManager);
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;

                for(DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount+= data.getAmount();
                    String sTotal = String.valueOf("Gesamtbudget: €" + totalAmount);
                    gesamtBudget.setText(sTotal);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        faknpf = findViewById(R.id.fab);
        faknpf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ausHinzufügen();
            }
        });
    }
    //Methode um Ausgaben in Firebase Database hinzufügen
    private void ausHinzufügen(){
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

        /*Den Knopf "Speichern" in clickListener setzen. Davor sicherstellen,
        dass der User eine valide Ausgabe und valide Summe ausgewählt/eingegeben hat*/
        speichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetSumme = summe1.getText().toString();
                String budgetAusgabe = spiner.getSelectedItem().toString();

                if(TextUtils.isEmpty(budgetSumme)){
                    summe1.setError("Feld darf nicht leer sein");
                    return;
                }
                if(budgetAusgabe.equals("Kategorien")){
                    Toast.makeText(BudgetActivity.this, "Wählen Sie eine Kategorie", Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setMessage("Kategorie wird hinzugefügt");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id= budgetRef.push().getKey();

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
                    Data data = new Data(budgetAusgabe, datum, id, null, Integer.parseInt(budgetSumme), monat.getMonths(), woche.getWeeks());
                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this, "Kategorie gespeichert", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
     //FirebaseRecyclerAdapter erstellen
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(budgetRef,Data.class).build();
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {

            //Text in holder einsetezen. Switch-Case um Ausgaben mit den Icons zu verbinden
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull  Data model) {
                holder.summeEinfügen("Summe: €" + model.getAmount());
                holder.datumEinfügen("Am: "+ model.getDate());
                holder.ausgaBebennen("Kategorie: "+model.getItem());
                holder.notiz.setVisibility(View.GONE);

                switch(model.getItem()){

                    case "Miete":
                        holder.imageView.setImageResource(R.drawable.miete1);
                        break;

                    case "Reisekosten":
                        holder.imageView.setImageResource(R.drawable.reisekosten1);
                        break;

                    case "Essen/Trinken":
                        holder.imageView.setImageResource(R.drawable.essentrinken);
                        break;

                    case"Büromaterial":
                        holder.imageView.setImageResource(R.drawable.buero);
                        break;

                    case"Weiterbildung":
                        holder.imageView.setImageResource(R.drawable.weiterbildung);
                        break;

                    case"Technikausstatung":
                        holder.imageView.setImageResource(R.drawable.technik);
                        break;

                    case"Fahrtkosten":
                        holder.imageView.setImageResource(R.drawable.fahrtkosten);
                        break;

                    case"Sonstig":
                        holder.imageView.setImageResource(R.drawable.sonstiges);
                        break;


                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_schlüssel=getRef(position).getKey();
                        ausgabe = model.getItem();
                        summe = model.getAmount();
                        updateData();
                    }
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent, false);

                return new MyViewHolder(view);
            }
        };
        recview.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ImageView imageView;
        public TextView notiz, datum;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            imageView = itemView.findViewById(R.id.imageview);
            notiz = itemView.findViewById(R.id.note);
            datum = itemView.findViewById(R.id.date);

        }

        public void ausgaBebennen (String ausgabeName){
            TextView item = mView.findViewById(R.id.item);
            item.setText(ausgabeName);
        }

        public void summeEinfügen(String summee){
            TextView amount = mView.findViewById(R.id.amount);
            amount.setText(summee);
        }

        public void datumEinfügen(String datum2){
            TextView date = mView.findViewById(R.id.date);
            date.setText(datum2);
        }
    }
    //Methode um Daten (Summe, Notizen) zu aktualisieren/löschen
    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mAusgabe = mView.findViewById(R.id.itemName);
        final EditText mSumme = mView.findViewById(R.id.amount);
        final EditText mNotizen = mView.findViewById(R.id.note);
        mNotizen.setVisibility(View.GONE);
        mAusgabe.setText(ausgabe);
        mSumme.setText(String.valueOf(summe));
        mSumme.setSelection(String.valueOf(summe).length());
        Button löschenBut = mView.findViewById(R.id.btnDelete);
        Button btnAktualisieren = mView.findViewById(R.id.btnUpdate);
        btnAktualisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                summe = Integer.parseInt(mSumme.getText().toString());

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar Kal = Calendar.getInstance();
                String date = dateFormat.format(Kal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime jetzt = new DateTime();
                Weeks woche = Weeks.weeksBetween(epoch, jetzt);
                Months monat = Months. monthsBetween(epoch, jetzt);
                Data data = new Data(ausgabe, date, post_schlüssel, null, summe, monat.getMonths(), woche.getWeeks());
                budgetRef.child(post_schlüssel).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Erfolgreich aktualisiert", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                dialog.dismiss();
            }
        });

        löschenBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.child(post_schlüssel).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                dialog.dismiss();

            }
        });
        dialog.show();
    }
}