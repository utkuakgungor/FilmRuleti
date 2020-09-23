package com.utkuakgungor.filmruleti.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.utkuakgungor.filmruleti.R;
import com.utkuakgungor.filmruleti.utils.Friends;
import com.utkuakgungor.filmruleti.utils.FriendsAdapter;
import com.utkuakgungor.filmruleti.utils.SwipeToDeleteCallback;
import com.utkuakgungor.filmruleti.utils.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendsFragment extends Fragment {

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    private DatabaseReference referenceFriends, referenceUsers, referenceUsersAll;
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private List<Friends> result;
    private List<String> userList;
    private ImageButton friendDelete;
    private int userNumber = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        referenceFriends = FirebaseDatabase.getInstance().getReference("Friends").child(Objects.requireNonNull(firebaseUser.getDisplayName()));
        recyclerView = v.findViewById(R.id.friendList);
        referenceFriends.keepSynced(true);
        friendDelete=v.findViewById(R.id.friendDelete);
        result = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new FriendsAdapter(result,firebaseUser.getDisplayName());
        recyclerView.setAdapter(adapter);
        referenceFriends.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Friends friends = snapshot.getValue(Friends.class);
                referenceUsersAll = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(friends).getUsername());
                referenceUsersAll.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(Objects.equals(snapshot.getKey(),"picture")){
                            friends.setImage(snapshot.getValue(String.class));
                            result.add(friends);
                            adapter.notifyDataSetChanged();
                        }
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
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        FloatingActionButton addButton = v.findViewById(R.id.btn_add);
        addButton.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Arkadaş Ekleme");
            View friendView = inflater.inflate(R.layout.friend_input, null);
            builder.setView(friendView);
            builder.setPositiveButton("Ekle", (dialog, which) -> {
                EditText editText = friendView.findViewById(R.id.friendUsernameEdit);
                String userName = Objects.requireNonNull(editText.getText()).toString().trim();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                    Snackbar.make(v1, "Lütfen kullanıcı adı giriniz.", Snackbar.LENGTH_LONG).show();
                } else {
                    referenceUsers = FirebaseDatabase.getInstance().getReference("Users").child(userName);
                    referenceUsers.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            userNumber++;
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
                    referenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (userNumber == 0) {
                                Snackbar.make(v1, "Kullanıcı bulunamadı.", Snackbar.LENGTH_LONG).show();
                            } else {
                                Friends friends = new Friends();
                                friends.setUsername(userName);
                                String id = referenceFriends.push().getKey();
                                referenceFriends.child(Objects.requireNonNull(id)).setValue(friends);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            builder.setNegativeButton("Vazgeç", (dialog, which) -> dialog.cancel());

            builder.show();
        });
        return v;
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Friends item = result.get(position);
                referenceFriends.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String deneme;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                adapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
