package com.example.ovulitos;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ovulitos.global.GlobalVariables;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoticiasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoticiasFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NoticiasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoticiasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoticiasFragment newInstance(String param1, String param2) {
        NoticiasFragment fragment = new NoticiasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noticias, container, false);
        cargarNoticiasDesdeFirebase(view);
        return view;
    }

    private void cargarNoticiasDesdeFirebase(View view) {
        if (GlobalVariables.email == null) {
            fetchNewsFromFirebase(view, null);
            return;
        }

        FirebaseFirestore.getInstance().collection("usuarios").document(GlobalVariables.email)
            .get()
            .addOnSuccessListener(doc -> {
                String fase = null;
                if (doc.exists() && doc.contains("proximaReglaPrevista")) {
                    String proxima = doc.getString("proximaReglaPrevista");
                    long duracionCiclo = 28;
                    if (doc.contains("cicloMedio")) {
                        Long val = doc.getLong("cicloMedio");
                        if (val != null) duracionCiclo = val;
                    } else if (doc.contains("duracionCiclo")) {
                        Long val = doc.getLong("duracionCiclo");
                        if (val != null) duracionCiclo = val;
                    }

                    if (proxima != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            LocalDate date = LocalDate.parse(proxima);
                            LocalDate hoy = LocalDate.now();
                            long dias = ChronoUnit.DAYS.between(hoy, date);
                            long diaActualCiclo = duracionCiclo - dias + 1;

                            if (diaActualCiclo >= 1 && diaActualCiclo <= 5) {
                                fase = "menstrual";
                            } else if (diaActualCiclo >= 6 && diaActualCiclo <= 13) {
                                fase = "folicular";
                            } else if (diaActualCiclo == 14) {
                                fase = "ovulacion";
                            } else if (diaActualCiclo >= 15) {
                                fase = "lutea";
                            }
                        } catch (Exception e) {
                            Log.e("NOTICIAS", "Error al calcular la fase del ciclo", e);
                        }
                    }
                }
                fetchNewsFromFirebase(view, fase);
            })
            .addOnFailureListener(e -> {
                fetchNewsFromFirebase(view, null);
            });
    }

    private void fetchNewsFromFirebase(View view, String fase) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        if (fase != null) {
            db.collection("noticias_semanales")
                .whereEqualTo("fase", fase)
                .limit(4)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        displayNews(view, queryDocumentSnapshots.getDocuments());
                    } else {
                        fetchGenericNews(view);
                    }
                })
                .addOnFailureListener(e -> fetchGenericNews(view));
        } else {
            fetchGenericNews(view);
        }
    }

    private void fetchGenericNews(View view) {
        FirebaseFirestore.getInstance().collection("noticias_semanales")
            .limit(4)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                displayNews(view, queryDocumentSnapshots.getDocuments());
            });
    }

    private void displayNews(View view, List<com.google.firebase.firestore.DocumentSnapshot> documents) {
        int i = 1;
        for (com.google.firebase.firestore.DocumentSnapshot doc : documents) {
            String titulo = doc.getString("titulo");
            String url = doc.getString("url");
            String imagen = doc.getString("imagen");

            int cardId = getResources().getIdentifier("card_noticia_" + i, "id", requireContext().getPackageName());
            int tvId = getResources().getIdentifier("tv_noticia_" + i, "id", requireContext().getPackageName());
            int imgId = getResources().getIdentifier("img_noticia_" + i, "id", requireContext().getPackageName());

            if (cardId != 0 && tvId != 0) {
                com.google.android.material.card.MaterialCardView card = view.findViewById(cardId);
                android.widget.TextView tv = view.findViewById(tvId);

                if (card != null && tv != null) {
                    tv.setText(titulo);
                    card.setVisibility(View.VISIBLE);

                    if (imgId != 0) {
                        com.google.android.material.imageview.ShapeableImageView imgView = view.findViewById(imgId);
                        if (imgView != null) {
                            int imgResId = 0;
                            if (imagen != null && !imagen.isEmpty()) {
                                imgResId = getResources().getIdentifier(imagen, "drawable", requireContext().getPackageName());
                            }
                            if (imgResId != 0) {
                                imgView.setImageResource(imgResId);
                            } else {
                                if (i % 2 == 0) {
                                    imgView.setImageResource(R.drawable.foto_comida);
                                } else {
                                    imgView.setImageResource(R.drawable.foto_embarazada);
                                }
                            }
                        }
                    }

                    if (url != null && !url.isEmpty()) {
                        card.setOnClickListener(v -> {
                            try {
                                android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url));
                                startActivity(browserIntent);
                            } catch (Exception e) {
                                Log.e("NOTICIAS", "Error opening URL", e);
                            }
                        });
                    }
                }
            }
            i++;
            if (i > 4) break;
        }
    }
}