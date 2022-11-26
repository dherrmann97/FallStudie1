package com.example.fallstudie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdptAusgbWoche extends RecyclerView.Adapter<AdptAusgbWoche.Viewhld>{

    private Context cont;
    private List<Data> meinDatenList;

    public AdptAusgbWoche(Context cont, List<Data> meinDatenList) {
        this.cont = cont;
        this.meinDatenList = meinDatenList;
    }

    @NonNull
    @Override
    public Viewhld onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(cont).inflate(R.layout.retrieve_layout, parent, false);
        return new AdptAusgbWoche.Viewhld(view);
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


        } 

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
