package com.kadirdurmazz.gymy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter {
    List<Item> items;
    private Context context;
    private int selectedItem;

    public ItemRecyclerAdapter(Context context, List<Item> items){
        this.context = context;
        this.items = items;
        selectedItem = 999999;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyHolder myHolder = (MyHolder)holder;
        Item item = items.get(position);

        myHolder.itemView.setTag(items.get(position));
        myHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.primaryDarkColor));

        myHolder.fieldDate.setText(item.getFieldDate());
        myHolder.field1.setText(item.getField1());
        myHolder.field2.setText(item.getField2());
        myHolder.field3.setText(item.getField3());
        myHolder.field4.setText(item.getField4());
        myHolder.field5.setText(item.getField5());

        if (selectedItem == position) {
            myHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.redColor));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousItem = selectedItem;
                selectedItem = position;

                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return items.size();
    }

    public void filterList(ArrayList<Item> filteredList){
        items = filteredList;
        notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView fieldDate, field1, field2, field3, field4, field5;
        CardView cardView;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            fieldDate = itemView.findViewById(R.id.fieldDate);
            field1 = itemView.findViewById(R.id.field1);
            field2 = itemView.findViewById(R.id.field2);
            field3 = itemView.findViewById(R.id.field3);
            field4 = itemView.findViewById(R.id.field4);
            field5 = itemView.findViewById(R.id.field5);
        }
    }
}
