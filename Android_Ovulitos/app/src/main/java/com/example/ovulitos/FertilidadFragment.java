package com.example.ovulitos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class FertilidadFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflamos el layout fragment_informacion.xml
        return inflater.inflate(R.layout.fragment_fertilidad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView cardHinchazon = view.findViewById(R.id.cardHinchazon);
        MaterialCardView cardParanoia = view.findViewById(R.id.cardParanoia);
        android.widget.TextView tvArticulo1 = view.findViewById(R.id.tv_articulo_1);
        android.widget.TextView tvArticulo2 = view.findViewById(R.id.tv_articulo_2);

        // Fetch de fertilidad_semanal
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("fertilidad_semanal")
                .limit(2)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int i = 0;
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String titulo = doc.getString("titulo");
                        String url = doc.getString("url");
                        
                        if (i == 0 && tvArticulo1 != null && cardHinchazon != null) {
                            tvArticulo1.setText(titulo);
                            if (url != null && !url.isEmpty()) {
                                cardHinchazon.setOnClickListener(v -> {
                                    android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                                    startActivity(browserIntent);
                                });
                            }
                        } else if (i == 1 && tvArticulo2 != null && cardParanoia != null) {
                            tvArticulo2.setText(titulo);
                            if (url != null && !url.isEmpty()) {
                                cardParanoia.setOnClickListener(v -> {
                                    android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                                    startActivity(browserIntent);
                                });
                            }
                        }
                        i++;
                    }
                });
    }
}