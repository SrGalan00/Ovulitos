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
    private List<User> fullUserList;
    private List<User> filteredUserList;
    private android.widget.EditText etSearchUser;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        recyclerUsers = view.findViewById(R.id.recycler_users_list);
        progressBar = view.findViewById(R.id.progress_users);
        etSearchUser = view.findViewById(R.id.et_search_user);

        etSearchUser.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        view.findViewById(R.id.btn_back_users).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        fullUserList = new ArrayList<>();
        filteredUserList = new ArrayList<>();
        userAdapter = new UserAdapter(filteredUserList, user -> {
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
            if (!isAdded() || getView() == null) return;
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful() && task.getResult() != null) {
                fullUserList.clear();
                filteredUserList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String uid = document.getString("uid");
                    if (uid != null && !uid.equals(currentUser.getUid())) {
                        User user = new User(
                                uid,
                                document.getString("email"),
                                document.getString("nombre"),
                                document.getString("avatar")
                        );
                        fullUserList.add(user);
                        filteredUserList.add(user);
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

    private void filterUsers(String text) {
        filteredUserList.clear();
        if (text.isEmpty()) {
            filteredUserList.addAll(fullUserList);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (User user : fullUserList) {
                if (user.getNombre() != null && user.getNombre().toLowerCase().contains(filterPattern)) {
                    filteredUserList.add(user);
                }
            }
        }
        userAdapter.notifyDataSetChanged();
    }
}
