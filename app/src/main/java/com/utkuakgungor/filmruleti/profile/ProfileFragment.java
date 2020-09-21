package com.utkuakgungor.filmruleti.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utkuakgungor.filmruleti.R;
import com.utkuakgungor.filmruleti.utils.User;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FriendsFragment friendsFragment;
    private RegisterFragment registerFragment;
    private ProfileFragment homeFragment;
    private DatabaseReference reference;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        registerFragment=new RegisterFragment();
        homeFragment=new ProfileFragment();
        friendsFragment=new FriendsFragment();
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
                    if(android.util.Patterns.EMAIL_ADDRESS.matcher(usernameEdit.getText().toString()).matches()){
                        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(usernameEdit.getText()).toString(), Objects.requireNonNull(passwordEdit.getText()).toString())
                                .addOnCompleteListener(requireActivity(), task -> {
                                    if (task.isSuccessful()) {
                                        requireActivity().getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.main_frame, homeFragment).commit();
                                    } else {
                                        Snackbar.make(v12, "Kullanıcı bulunamadı veya şifre yanlış.", Snackbar.LENGTH_LONG).show();
                                    }
                                });
                    }
                    else{
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(usernameEdit.getText().toString());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User.class);
                                if(user==null){
                                    Snackbar.make(v12,"Kullanıcı bulunamadı veya şifre yanlış.",Snackbar.LENGTH_LONG).show();
                                }
                                else{
                                    mAuth.signInWithEmailAndPassword(user.getEmail(), Objects.requireNonNull(passwordEdit.getText()).toString())
                                            .addOnCompleteListener(requireActivity(), task -> {
                                                if (task.isSuccessful()) {
                                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                                            .replace(R.id.main_frame, homeFragment).commit();
                                                } else {
                                                    Snackbar.make(v12, "Kullanıcı bulunamadı veya şifre yanlış.", Snackbar.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
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
            View v = inflater.inflate(R.layout.fragment_profile, container, false);
            MaterialTextView usernameText = v.findViewById(R.id.profileName);
            MaterialButton logout = v.findViewById(R.id.profileLogout);
            MaterialButton friendsButton = v.findViewById(R.id.profileFriends);
            friendsButton.setOnClickListener(v1 -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, friendsFragment)
                        .addToBackStack(null).commit();
            });
            logout.setOnClickListener(v1 -> {
                mAuth.signOut();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, homeFragment).commit();
            });
            SharedPreferences sharedPreferences= requireActivity().getSharedPreferences("Ayarlar",MODE_PRIVATE);
            AutoCompleteTextView autoCompleteTextView=v.findViewById(R.id.filled_exposed_dropdown);
            String[] options = {getResources().getString(R.string.text_system),getResources().getString(R.string.text_dark),getResources().getString(R.string.text_light)};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.option_item, options);
            autoCompleteTextView.setAdapter(adapter);
            if(sharedPreferences.contains("Dark")){
                autoCompleteTextView.setText(getResources().getString(R.string.text_dark),false);
            }
            else if(sharedPreferences.contains("Light")){
                autoCompleteTextView.setText(getResources().getString(R.string.text_light),false);
            }
            else{
                autoCompleteTextView.setText(getResources().getString(R.string.text_system),false);
            }
            SharedPreferences.Editor editor=sharedPreferences.edit();
            autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
                if(parent.getItemAtPosition(position).toString().equals(requireActivity().getResources().getString(R.string.text_dark))){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.remove("Light");
                    editor.putString("Dark","Dark");
                    editor.commit();
                }
                else if(parent.getItemAtPosition(position).toString().equals(requireActivity().getResources().getString(R.string.text_light))){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.remove("Dark");
                    editor.putString("Light","Light");
                    editor.commit();
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.remove("Dark");
                    editor.remove("Light");
                    editor.commit();
                }
            });
            usernameText.setText(firebaseUser.getDisplayName());
            return v;
        }
    }
}