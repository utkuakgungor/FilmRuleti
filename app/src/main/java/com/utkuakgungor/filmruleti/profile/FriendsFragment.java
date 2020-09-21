package com.utkuakgungor.filmruleti.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private Friends friends;
    private User user;
    private DatabaseReference referenceFriends,referenceUsers;
    private RecyclerView recyclerView;
    private FriendsAdapter adapter;
    private List<Friends> result;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        referenceFriends = FirebaseDatabase.getInstance().getReference("Friends").child(Objects.requireNonNull(firebaseUser.getDisplayName()));
        recyclerView = v.findViewById(R.id.friendList);
        referenceFriends.keepSynced(true);
        result = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        adapter = new FriendsAdapter(v.getContext(), result);
        recyclerView.setAdapter(adapter);
        FloatingActionButton addButton = v.findViewById(R.id.btn_add);
        addButton.setOnClickListener(v1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Arkadaş Ekleme");
            View friendView = inflater.inflate(R.layout.friend_input,null);
            builder.setView(friendView);
            builder.setPositiveButton("Ekle", (dialog, which) -> {
                EditText editText= friendView.findViewById(R.id.friendUsernameEdit);
                String userName = Objects.requireNonNull(editText.getText()).toString();
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                    Snackbar.make(v1, "Lütfen kullanıcı adı giriniz.", Snackbar.LENGTH_LONG).show();
                }
                else{
                    referenceUsers=FirebaseDatabase.getInstance().getReference("Users").child(userName);
                    referenceUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user=snapshot.getValue(User.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(user==null){
                        Snackbar.make(v1,"Kullanıcı bulunamadı.",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        friends=new Friends();
                        friends.setImage(user.getPicture());
                        friends.setUsername(userName);
                        referenceFriends.setValue(friends);
                    }
                }
            });
            builder.setNegativeButton("Vazgeç", (dialog, which) -> {
                dialog.cancel();
            });

            builder.show();
        });
        referenceFriends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                result.add(snapshot.getValue(Friends.class));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        enableSwipeToDelete();
        return v;
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Friends item = result.get(position);
                referenceFriends.child(item.getUsername()).removeValue();
                adapter.removeItem(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
