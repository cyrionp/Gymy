package com.kadirdurmazz.gymy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DashboardFragment extends Fragment{
    private Activity activity;
    public DashboardFragment(Activity activity){
        this.activity = activity;
    }

    View view;
    List<Item> items;
    ItemRecyclerAdapter itemRecyclerAdapter;
    FirebaseDatabase db;
    DatabaseReference mRef;
    FirebaseUser currentUser;
    String userUID;
    TextView txtNoItem, dashboard_Field1, dashboard_Field2, dashboard_Field3, dashboard_Field4, dashboard_Field5;
    EditText txtSearch;
    ProgressBar progressBar;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        dashboard_Field1 = view.findViewById(R.id.dashboard_Field1);
        dashboard_Field2 = view.findViewById(R.id.dashboard_Field2);
        dashboard_Field3 = view.findViewById(R.id.dashboard_Field3);
        dashboard_Field4 = view.findViewById(R.id.dashboard_Field4);
        dashboard_Field5 = view.findViewById(R.id.dashboard_Field5);
        txtNoItem = view.findViewById(R.id.txtNoItem);
        txtSearch = view.findViewById(R.id.txtSearch);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        txtNoItem.setVisibility(View.GONE);

        items = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userUID = currentUser.getUid();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Items/"+userUID);
        mRef.keepSynced(true);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Item item = ds.getValue(Item.class);
                    items.add(item);
                }

                getFields(dashboard_Field1, dashboard_Field2, dashboard_Field3, dashboard_Field4, dashboard_Field5);

                itemRecyclerAdapter = new ItemRecyclerAdapter(activity, items);
                recyclerView.setAdapter(itemRecyclerAdapter);
                setVisibilities();
                mRef.keepSynced(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity,"ERROR: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                viewHolder.itemView.setBackgroundColor(Color.parseColor("ffffff"));
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getLayoutPosition();
                String selectedItem = items.remove(position).getFieldDate();

                db = FirebaseDatabase.getInstance();
                mRef = db.getReference("Items/"+userUID);
                mRef.child(selectedItem).removeValue();
                mRef.keepSynced(true);

                itemRecyclerAdapter.notifyItemRemoved(position);
                itemRecyclerAdapter.notifyDataSetChanged();
                setVisibilities();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void getFields(TextView field1, TextView field2, TextView field3, TextView field4, TextView field5){
        List<Field> fields;
        fields = new ArrayList<>();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Fields/"+userUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Field field = dataSnapshot.getValue(Field.class);
                fields.add(field);

                field1.setText(fields.get(0).getFieldName1());
                field2.setText(fields.get(0).getFieldName2());
                field3.setText(fields.get(0).getFieldName3());
                field4.setText(fields.get(0).getFieldName4());
                field5.setText(fields.get(0).getFieldName5());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity,"ERROR: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisibilities(){
        int itemCount = itemRecyclerAdapter.getItemCount();

        if(itemCount>0){
            txtNoItem.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else{
            txtNoItem.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filter(String txt){
        ArrayList<Item> filteredList = new ArrayList<>();
        for(Item item:items){
            if(item.getFieldDate().toLowerCase().contains(txt.toLowerCase())){
                filteredList.add(item);
            }
        }

        itemRecyclerAdapter.filterList(filteredList);
    }
}