package com.utkuakgungor.filmruleti.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.utkuakgungor.filmruleti.R;

import java.util.Objects;
import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private HomeFragment homeFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        TextInputEditText usernameEdit = v.findViewById(R.id.editUsername);
        TextInputEditText passwordEdit = v.findViewById(R.id.editPassword);
        homeFragment=new HomeFragment();
        TextInputEditText passwordValidEdit = v.findViewById(R.id.editPasswordValid);
        TextInputEditText emailEdit = v.findViewById(R.id.editEmail);
        FloatingActionButton btnRegister = v.findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(v1 -> {
            if (Objects.requireNonNull(passwordEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(passwordValidEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(emailEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(usernameEdit.getText()).toString().equals("")) {
                Snackbar.make(v1, "lütfen alanları doldurunuz.", Snackbar.LENGTH_LONG).show();
            } else if (!passwordEdit.getText().toString().equals(passwordValidEdit.getText().toString())) {
                Snackbar.make(v1, "Şifreler aynı değil. Lütfen kontrol ediniz.", Snackbar.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString(), passwordEdit.getText().toString())
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                Snackbar.make(v1, "Kayıt işlemi başarılı.", Snackbar.LENGTH_LONG).show();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, homeFragment)
                                        .addToBackStack(null).commit();
                            } else {
                                Snackbar.make(v1, "Kayıt işlemi başarısız. Lütfen yeniden deneyiniz.", Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });
        return v;
    }
}
