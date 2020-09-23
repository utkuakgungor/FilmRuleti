package com.utkuakgungor.filmruleti.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.utkuakgungor.filmruleti.R;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.UserViewHolder> {
    private List<Friends> list;
    private DatabaseReference referenceFriends;

    public FriendsAdapter(List<Friends> list,String userName) {
        this.list = list;
        this.referenceFriends= FirebaseDatabase.getInstance().getReference("Friends").child(userName);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position) {
        final Friends user = list.get(position);
        holder.textView.setText(user.getUsername());
        if (!user.getImage().equals("Boş")) {
            Picasso.get().load(user.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(user.getImage()).into(holder.imageView);
                }
            });
        }
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                referenceFriends.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Friends friends=snapshot.getValue(Friends.class);
                        if(Objects.requireNonNull(friends).getUsername().equals(user.getUsername())){
                            list.remove(holder.getAdapterPosition());
                            notifyDataSetChanged();
                            referenceFriends.child(Objects.requireNonNull(snapshot.getKey())).removeValue();
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
        });
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        MaterialTextView textView;
        ImageButton imageButton;

        private UserViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.friendImage);
            textView = itemView.findViewById(R.id.friendUsername);
            imageButton=itemView.findViewById(R.id.friendDelete);
        }
    }
}
