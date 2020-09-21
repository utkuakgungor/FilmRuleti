package com.utkuakgungor.filmruleti.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.utkuakgungor.filmruleti.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.UserViewHolder> {
    private List<Friends> list;
    private int sira;
    private Snackbar snackbar;
    private View view;
    private TextView snackbar_text;
    private Context context;
    private String filmadi,filmyili,filmpuani,filmozeti,filmfoto,filmyoutube,filmsure,filmozeteng,filmsureeng,textcolor,filmoyuncular,filmtur,filmtureng,filmyonetmen,filmsinif,filmresimler;

    public FriendsAdapter(Context context,List<Friends> list){
        this.list=list;
        this.context=context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new UserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_friend,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, final int position ){
        final Friends user=list.get(position);
        holder.textView.setText(user.getUsername());

    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public int getSira(){
        return sira;
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        MaterialTextView textView;

        private UserViewHolder(View itemView){
            super(itemView);

            imageView=itemView.findViewById(R.id.friendImage);
            textView=itemView.findViewById(R.id.friendUsername);
        }
    }
}
