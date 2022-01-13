package com.kadirdurmazz.gymy;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter {
    List<User> users;

    public UserRecyclerAdapter(List<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_user, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position){
        MyHolder myHolder = (MyHolder)holder;
        User user = users.get(position);

        myHolder.fieldEmail.setText(user.getEmail());
        myHolder.fieldName.setText(user.getName());
        myHolder.fieldType.setText(user.getType());
    }

    public int getItemCount(){
        return users.size();
    }

    public void filterList(ArrayList<User> filteredList){
        users = filteredList;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView fieldEmail, fieldName, fieldType;
        CardView cardView2;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            cardView2 = itemView.findViewById(R.id.cardView2);
            fieldEmail = itemView.findViewById(R.id.fieldEmail);
            fieldName = itemView.findViewById(R.id.fieldName);
            fieldType = itemView.findViewById(R.id.fieldType);
        }
    }
}
