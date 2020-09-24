package com.utkuakgungor.filmruleti.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.utkuakgungor.filmruleti.R;
import com.utkuakgungor.filmruleti.settings.SettingsActivity;
import com.utkuakgungor.filmruleti.utils.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private int userNumber = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private FriendsFragment friendsFragment;
    private RegisterFragment registerFragment;
    private ProfileFragment profileFragment;
    private CircleImageView circleImageView;
    private User user;
    private StorageReference storageReference;
    private GoogleSignInClient googleSignInClient;
    private ProgressBar picturebar, progressBar;
    private Bitmap bitmap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        registerFragment = new RegisterFragment();
        profileFragment = new ProfileFragment();
        friendsFragment = new FriendsFragment();
        if (firebaseUser == null) {
            View v = inflater.inflate(R.layout.fragment_login, container, false);
            MaterialButton loginButton = v.findViewById(R.id.btn_mail);
            MaterialButton registerButton = v.findViewById(R.id.btn_register);
            ImageButton googleButton = v.findViewById(R.id.googleLogin);
            ImageButton githubButton = v.findViewById(R.id.githubLogin);
            progressBar = v.findViewById(R.id.loginBar);
            ImageButton twitterButton = v.findViewById(R.id.twitterLogin);
            TextInputEditText usernameEdit = v.findViewById(R.id.loginUsername);
            TextInputEditText passwordEdit = v.findViewById(R.id.loginPassword);
            loginButton.setOnClickListener(v12 -> {
                progressBar.setVisibility(View.VISIBLE);
                if (Objects.requireNonNull(usernameEdit.getText()).toString().equals("") || Objects.requireNonNull(passwordEdit.getText()).toString().equals("")) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(v12, getResources().getString(R.string.text_enter_username_password), Snackbar.LENGTH_LONG).show();
                } else {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(usernameEdit.getText().toString()).matches()) {
                        mAuth.signInWithEmailAndPassword(Objects.requireNonNull(usernameEdit.getText()).toString(), Objects.requireNonNull(passwordEdit.getText()).toString())
                                .addOnSuccessListener(authResult -> {
                                    progressBar.setVisibility(View.GONE);
                                    requireActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.main_frame, profileFragment).commit();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(v12, getResources().getString(R.string.text_username_password), Snackbar.LENGTH_LONG).show();
                                });
                    } else {
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(usernameEdit.getText().toString().trim());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                user = snapshot.getValue(User.class);
                                if (user == null) {
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(v12, getResources().getString(R.string.text_username_password), Snackbar.LENGTH_LONG).show();
                                } else {
                                    mAuth.signInWithEmailAndPassword(user.getEmail(), Objects.requireNonNull(passwordEdit.getText()).toString())
                                            .addOnSuccessListener(authResult -> {
                                                progressBar.setVisibility(View.GONE);
                                                requireActivity().getSupportFragmentManager().beginTransaction()
                                                        .replace(R.id.main_frame, profileFragment).commit();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                Snackbar.make(v12, getResources().getString(R.string.text_username_password), Snackbar.LENGTH_LONG).show();
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
                progressBar.setVisibility(View.VISIBLE);
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
                signInWithProvider(provider);
            });
            googleButton.setOnClickListener(v1 -> {
                progressBar.setVisibility(View.VISIBLE);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                googleSignIn();
            });
            githubButton.setOnClickListener(v1 -> {
                progressBar.setVisibility(View.VISIBLE);
                OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                signInWithProvider(provider);
            });
            return v;
        } else {
            View v = inflater.inflate(R.layout.fragment_profile, container, false);
            circleImageView = v.findViewById(R.id.profilePicture);
            picturebar = v.findViewById(R.id.profileBar);
            MaterialTextView usernameText = v.findViewById(R.id.profileName);
            MaterialButton logout = v.findViewById(R.id.profileLogout);
            MaterialButton friendsButton = v.findViewById(R.id.profileFriends);
            MaterialButton settingButton=v.findViewById(R.id.settignButton);
            settingButton.setOnClickListener(v1 -> {
                Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
                startActivity(settingsIntent);
            });
            friendsButton.setOnClickListener(v1 -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, friendsFragment)
                        .addToBackStack(null).commit();
            });
            circleImageView.setOnClickListener(v1 -> {
                openFileChooser();
            });
            logout.setOnClickListener(v1 -> {
                mAuth.signOut();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, profileFragment).commit();
            });
            if (firebaseUser.getPhotoUrl() != null) {
                storageReference = FirebaseStorage.getInstance().getReference("profileimages")
                        .child(firebaseUser.getUid() + ".jpeg");
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).into(circleImageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).into(circleImageView);
                                }
                            });
                        });
            }
            usernameText.setText(firebaseUser.getDisplayName());
            return v;
        }
    }

    private void signInWithProvider(OAuthProvider.Builder provider) {
        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener(
                    authResult -> {
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName()));
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                userNumber++;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (userNumber == 0) {
                                    user = new User();
                                    user.setUsername(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                                    user.setEmail(mAuth.getCurrentUser().getEmail());
                                    user.setPicture("Boş");
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child((Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName())))
                                            .setValue(user);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame, profileFragment).commit();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        int index = 0;
                        if (e.getClass().equals(FirebaseAuthUserCollisionException.class)) {
                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                            if (exception.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                                Snackbar.make(requireView(), getResources().getString(R.string.text_user_already_registered), Snackbar.LENGTH_LONG).show();
                                index++;
                            }
                        }
                        if (index == 0) {
                            Snackbar.make(requireView(), getResources().getString(R.string.text_register_error), Snackbar.LENGTH_LONG).show();
                        }
                    });
        } else {
            mAuth.startActivityForSignInWithProvider(requireActivity(), provider.build())
                    .addOnSuccessListener(
                            authResult -> {
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName()));
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        userNumber++;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (userNumber == 0) {
                                            user = new User();
                                            user.setUsername(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                                            user.setEmail(mAuth.getCurrentUser().getEmail());
                                            user.setPicture("Boş");
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child((Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName())))
                                                    .setValue(user);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                progressBar.setVisibility(View.GONE);
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_frame, profileFragment).commit();
                            })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        int index = 0;
                        if (e.getClass().equals(FirebaseAuthUserCollisionException.class)) {
                            FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                            if (exception.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                                Snackbar.make(requireView(), getResources().getString(R.string.text_user_already_registered), Snackbar.LENGTH_LONG).show();
                                index++;
                            }
                        }
                        if (index == 0) {
                            Snackbar.make(requireView(), getResources().getString(R.string.text_register_error), Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void googleSignIn() {
        Intent googleSignInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, 1);
    }

    private void uploadPicture() {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            storageReference = FirebaseStorage.getInstance().getReference("profileimages")
                    .child(firebaseUser.getUid() + ".jpeg");
            storageReference.delete();
            storageReference.putBytes(byteArrayOutputStream.toByteArray());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri).build();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(firebaseUser.getDisplayName()).trim()).child("picture");
                    reference.setValue(uri.toString());
                    firebaseUser.updateProfile(userProfileChangeRequest);
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picturebar.setVisibility(View.VISIBLE);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            circleImageView.setImageBitmap(bitmap);
            uploadPicture();
            picturebar.setVisibility(View.GONE);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(requireContext(), getResources().getString(R.string.text_register_success), Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(Objects.requireNonNull(account));
        } catch (ApiException e) {
            Snackbar.make(requireView(), getResources().getString(R.string.text_register_error), Snackbar.LENGTH_LONG).show();
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = new User();
                user.setUsername(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                user.setEmail(mAuth.getCurrentUser().getEmail());
                user.setPicture("Boş");
                FirebaseDatabase.getInstance().getReference("Users")
                        .child((Objects.requireNonNull(mAuth.getCurrentUser().getDisplayName())))
                        .setValue(user);
                progressBar.setVisibility(View.GONE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, profileFragment).commit();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            int index = 0;
            if (e.getClass().equals(FirebaseAuthUserCollisionException.class)) {
                FirebaseAuthUserCollisionException exception = (FirebaseAuthUserCollisionException) e;
                if (exception.getErrorCode().equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                    Snackbar.make(requireView(), getResources().getString(R.string.text_user_already_registered), Snackbar.LENGTH_LONG).show();
                    index++;
                }
            }
            if (index == 0) {
                Snackbar.make(requireView(), getResources().getString(R.string.text_register_error), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}