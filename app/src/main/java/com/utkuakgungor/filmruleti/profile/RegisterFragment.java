package com.utkuakgungor.filmruleti.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.utkuakgungor.filmruleti.R;
import com.utkuakgungor.filmruleti.utils.User;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private User user;
    private DatabaseReference reference;
    private ProfileFragment homeFragment;
    private Integer usernameNumber = 0;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        TextInputEditText usernameEdit = v.findViewById(R.id.editUsername);
        TextInputEditText passwordEdit = v.findViewById(R.id.editPassword);
        progressBar = v.findViewById(R.id.registerBar);
        homeFragment = new ProfileFragment();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        TextInputEditText passwordValidEdit = v.findViewById(R.id.editPasswordValid);
        TextInputEditText emailEdit = v.findViewById(R.id.editEmail);
        FloatingActionButton btnRegister = v.findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(v1 -> {
            progressBar.setVisibility(View.VISIBLE);
            if (Objects.requireNonNull(passwordEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(passwordValidEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(emailEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(usernameEdit.getText()).toString().equals("")) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(v1, getResources().getString(R.string.text_enter_fields), Snackbar.LENGTH_LONG).show();
            } else if (!passwordEdit.getText().toString().equals(passwordValidEdit.getText().toString())) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(v1, getResources().getString(R.string.text_passwords_same), Snackbar.LENGTH_LONG).show();
            } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(usernameEdit.getText().toString()).matches()) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(v1, getResources().getString(R.string.text_enter_username), Snackbar.LENGTH_LONG).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches()) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(v1, getResources().getString(R.string.text_enter_email), Snackbar.LENGTH_LONG).show();
            } else {
                reference.child(usernameEdit.getText().toString().trim()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        usernameNumber++;
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if (usernameNumber == 0) {
                    mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString().trim(), passwordEdit.getText().toString())
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(usernameEdit.getText().toString()).build();
                                    Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(userProfileChangeRequest);
                                    user = new User();
                                    user.setUsername(usernameEdit.getText().toString().trim());
                                    user.setEmail(emailEdit.getText().toString().trim());
                                    user.setPicture("Boş");
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(usernameEdit.getText().toString())
                                            .setValue(user);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), getResources().getString(R.string.text_register_success), Toast.LENGTH_LONG).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.main_frame, homeFragment).commit();
                                } else {
                                    Snackbar.make(v1, getResources().getString(R.string.text_register_error), Snackbar.LENGTH_LONG).show();
                                }
                            });
                } else {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(v1, getResources().getString(R.string.username_taken), Snackbar.LENGTH_LONG).show();
                }

            }
        });
        return v;
    }
}
