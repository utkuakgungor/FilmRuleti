package com.utkuakgungor.filmruleti.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.utkuakgungor.filmruleti.R;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private RegisterFragment registerFragment;
    private HomeFragment homeFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        registerFragment=new RegisterFragment();
        homeFragment=new HomeFragment();
        if (firebaseUser == null) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);
            MaterialButton loginButton = v.findViewById(R.id.btn_mail);
            MaterialButton registerButton = v.findViewById(R.id.btn_register);
            ImageButton googleButton = v.findViewById(R.id.googleLogin);
            ImageButton githubButton = v.findViewById(R.id.githubLogin);
            ImageButton facebookButton = v.findViewById(R.id.facebookLogin);
            ImageButton twitterButton = v.findViewById(R.id.twitterLogin);
            TextInputEditText usernameEdit = v.findViewById(R.id.loginUsername);
            TextInputEditText passwordEdit = v.findViewById(R.id.loginPassword);
            loginButton.setOnClickListener(v12 -> {
                if(Objects.requireNonNull(usernameEdit.getText()).toString().equals("") || Objects.requireNonNull(passwordEdit.getText()).toString().equals("")){
                    Snackbar.make(v12, "Lütfen kullanıcı adı ve şifre giriniz.", Snackbar.LENGTH_LONG).show();
                }
                else{
                mAuth.signInWithEmailAndPassword(Objects.requireNonNull(usernameEdit.getText()).toString(), Objects.requireNonNull(passwordEdit.getText()).toString())
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, homeFragment)
                                        .addToBackStack(null).commit();
                            } else {
                                Snackbar.make(v12, "Kullanıcı bulunamadı veya şifre yanlış.", Snackbar.LENGTH_LONG).show();
                            }
                        });
                }
            });
            registerButton.setOnClickListener(v13 -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, registerFragment)
                        .addToBackStack(null).commit();
            });
            twitterButton.setOnClickListener(v1 -> {

            });
            googleButton.setOnClickListener(v1 -> {

            });
            facebookButton.setOnClickListener(v1 -> {

            });
            githubButton.setOnClickListener(v1 -> {

            });
            return v;
        } else {
            View v = inflater.inflate(R.layout.fragment_home, container, false);
            MaterialTextView usernameText = v.findViewById(R.id.profileName);
            MaterialButton logout = v.findViewById(R.id.profileLogout);
            logout.setOnClickListener(v1 -> {
                mAuth.signOut();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, homeFragment)
                        .addToBackStack(null).commit();
            });
            usernameText.setText(firebaseUser.getDisplayName());
            return v;
        }
    }
}