package com.example.fallstudie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AdptAusgbHeute extends RecyclerView.Adapter<AdptAusgbHeute.Viewhld>{

    private Context cont;
    private List<Data> meinDatenList;
    private String post_schlüssel= "";
    private String ausgabe="";
    private int summe=0;
    private String notiz = "";

    public AdptAusgbHeute(Context cont, List<Data> meinDatenList) {
        this.cont = cont;
        this.meinDatenList = meinDatenList;
    }

    @NonNull
    @Override
    public Viewhld onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cont).inflate(R.layout.retrieve_layout, parent, false);
        return new AdptAusgbHeute.Viewhld(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewhld holder, int position) {
        final Data daten = meinDatenList.get(position);
        holder.kategorie.setText("Kategorie: "+ daten.getItem()) ;
        holder.summe.setText("Summe: " + daten.getAmount());
        holder.datum.setText("Am: " + daten.getDate());
        holder.anmerkung.setText("Anmerkung: " +daten.getNotes());

        switch(daten.getItem()){

            case "Miete":
                holder.imgview.setImageResource(R.drawable.miete1);
                break;

            case "Reisekosten":
                holder.imgview.setImageResource(R.drawable.reisekosten1);
                break;

            case "Essen/Trinken":
                holder.imgview.setImageResource(R.drawable.essentrinken);
                break;

            case"Büromaterial":
                holder.imgview.setImageResource(R.drawable.buero);
                break;

            case"Weiterbildung":
                holder.imgview.setImageResource(R.drawable.weiterbildung);
                break;

            case"Technikausstatung":
                holder.imgview.setImageResource(R.drawable.technik);
                break;

            case"Fahrtkosten":
                holder.imgview.setImageResource(R.drawable.fahrtkosten);
                break;

            case"Sonstig":
                holder.imgview.setImageResource(R.drawable.sonstiges);
                break;


        } //Daten aktualisieren
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_schlüssel=daten.getId();
                ausgabe = daten.getItem();
                notiz = daten.getDate();
                summe = daten.getAmount();
                datenAktualisieren();
            }
        });


    }

    private void datenAktualisieren() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(cont);
        LayoutInflater inflater = LayoutInflater.from(cont);
        View mView = inflater.inflate(R.layout.update_layout, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();
        final TextView mAusgabe = mView.findViewById(R.id.itemName);
        final EditText mSumme = mView.findViewById(R.id.amount);
        final EditText mNotizen = mView.findViewById(R.id.note);

        mAusgabe.setText(ausgabe);
        mSumme.setText(String.valueOf(summe));
        mSumme.setSelection(String.valueOf(summe).length());
        mNotizen.setText(notiz);
        mNotizen.setSelection(notiz.length());
        Button löschenBut = mView.findViewById(R.id.btnDelete);
        Button btnAktualisieren = mView.findViewById(R.id.btnUpdate);
        btnAktualisieren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                summe = Integer.parseInt(mSumme.getText().toString());
                notiz = mNotizen.getText().toString();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar Kal = Calendar.getInstance();
                String date = dateFormat.format(Kal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime jetzt = new DateTime();
                Weeks woche = Weeks.weeksBetween(epoch, jetzt);
                Months monat = Months. monthsBetween(epoch, jetzt);
                Data data = new Data(ausgabe, date, post_schlüssel, notiz, summe, monat.getMonths(), woche.getWeeks());

                //Ausgb aktualisieren. Wenn erfolgreich, msg wird gezeigt
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ausgaben").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.child(post_schlüssel).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(cont, "Erfolgreich aktualisiert", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(cont, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                dialog.dismiss();
            }
        });
           //löschen von Ausgabe. Wenn erfolgreich gelöscht, wird bekannt gegeben. Wenn nicht gelöscht, dann Exception geschmiessen.
        löschenBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ausgaben").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                ref.child(post_schlüssel).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(cont, "Erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(cont, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
                dialog.dismiss();

            }
        });
        dialog.show();
    }



    @Override
    public int getItemCount() {
       return meinDatenList.size();
    }

    public class Viewhld extends RecyclerView.ViewHolder{
        //Variablen von retrieveLayout initialisieren

        public TextView kategorie;
        public TextView summe;
        public TextView anmerkung;
        public TextView datum;
        public ImageView imgview;

        public Viewhld(@NonNull View itemView) {
            super(itemView);

            kategorie = itemView.findViewById(R.id.item);
            summe = itemView.findViewById(R.id.amount);
            anmerkung = itemView.findViewById(R.id.note);
            datum = itemView.findViewById(R.id.date);
            imgview = itemView.findViewById(R.id.imageview);
        }
    }
}
