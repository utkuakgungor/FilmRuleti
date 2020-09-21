package com.utkuakgungor.filmruleti.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.utkuakgungor.filmruleti.R;
import com.utkuakgungor.filmruleti.utils.User;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProfileFragment homeFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        TextInputEditText usernameEdit = v.findViewById(R.id.editUsername);
        TextInputEditText passwordEdit = v.findViewById(R.id.editPassword);
        homeFragment = new ProfileFragment();
        TextInputEditText passwordValidEdit = v.findViewById(R.id.editPasswordValid);
        TextInputEditText emailEdit = v.findViewById(R.id.editEmail);
        FloatingActionButton btnRegister = v.findViewById(R.id.btn_register);
        mAuth = FirebaseAuth.getInstance();
        btnRegister.setOnClickListener(v1 -> {
            if (Objects.requireNonNull(passwordEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(passwordValidEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(emailEdit.getText()).toString().equals("")
                    || Objects.requireNonNull(usernameEdit.getText()).toString().equals("")) {
                Snackbar.make(v1, "Lütfen alanları doldurunuz.", Snackbar.LENGTH_LONG).show();
            } else if (!passwordEdit.getText().toString().equals(passwordValidEdit.getText().toString())) {
                Snackbar.make(v1, "Şifreler aynı değil. Lütfen kontrol ediniz.", Snackbar.LENGTH_LONG).show();
            } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(usernameEdit.getText().toString()).matches()) {
                Snackbar.make(v1, "Lütfen kullanıcı adına E-Mail adresi girmeyiniz.", Snackbar.LENGTH_LONG).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches()) {
                Snackbar.make(v1, "Lütfen E-mail adresi giriniz.", Snackbar.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(emailEdit.getText().toString(), passwordEdit.getText().toString())
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(usernameEdit.getText().toString()).build();
                                Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(userProfileChangeRequest);
                                User user = new User();
                                user.setUsername(usernameEdit.getText().toString());
                                user.setEmail(emailEdit.getText().toString());
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(usernameEdit.getText().toString())
                                        .setValue(user);
                                mAuth.signOut();
                                Toast.makeText(getContext(), "Kayıt işlemi başarılı.", Toast.LENGTH_LONG).show();
                                requireActivity().getSupportFragmentManager().popBackStack();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, homeFragment).commit();
                            } else {
                                Snackbar.make(v1, "Kayıt işlemi başarısız. Lütfen yeniden deneyiniz.", Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });
        return v;
    }
}
