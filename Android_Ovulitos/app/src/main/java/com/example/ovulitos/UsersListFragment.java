package com.example.ovulitos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ovulitos.User;
import com.example.ovulitos.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersListFragment extends Fragment {

    private RecyclerView recyclerUsers;
    private ProgressBar progressBar;
    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        recyclerUsers = view.findViewById(R.id.recycler_users_list);
        progressBar = view.findViewById(R.id.progress_users);

        view.findViewById(R.id.btn_back_users).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, user -> {
            // Abrir el fragmento de chat con el usuario seleccionado
            ChatFragment chatFragment = new ChatFragment();
            Bundle args = new Bundle();
            args.putString("otherUserId", user.getUid());
            args.putString("otherUserName", user.getNombre());
            chatFragment.setArguments(args);

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).reemplazarFragmento(chatFragment);
            }
        });
        recyclerUsers.setAdapter(userAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadUsers();

        return view;
    }

    private void loadUsers() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        progressBar.setVisibility(View.VISIBLE);
        db.collection("usuarios").get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful() && task.getResult() != null) {
                userList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String uid = document.getString("uid");
                    if (uid != null && !uid.equals(currentUser.getUid())) {
                        User user = new User(
                                uid,
                                document.getString("email"),
                                document.getString("nombre"),
                                document.getString("avatar")
                        );
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Log.e("UsersListFragment", "Error getting documents: ", task.getException());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
