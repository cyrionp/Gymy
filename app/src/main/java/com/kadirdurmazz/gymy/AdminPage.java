package com.kadirdurmazz.gymy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {

    List<User> users;
    UserRecyclerAdapter userRecyclerAdapter;
    FirebaseDatabase db;
    DatabaseReference mRef;
    TextView txtNoUser;
    EditText txtSearchAdmin;
    Button btnLogoutAdmin;
    ProgressBar progressBarAdmin;
    RecyclerView recyclerViewAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        txtNoUser = findViewById(R.id.txtNoUser);
        txtSearchAdmin = findViewById(R.id.txtSearchAdmin);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);
        progressBarAdmin = findViewById(R.id.progress_bar_admin);
        recyclerViewAdmin = findViewById(R.id.recyclerviewAdmin);
        recyclerViewAdmin.setLayoutManager(new LinearLayoutManager(this));
        txtNoUser.setVisibility(View.GONE);

        users = new ArrayList<>();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Users");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    users.add(user);
                }

                userRecyclerAdapter = new UserRecyclerAdapter(users);
                recyclerViewAdmin.setAdapter(userRecyclerAdapter);
                setVisibilities();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                String selectedItem = users.remove(position).getUid();

                db = FirebaseDatabase.getInstance();
                mRef = db.getReference("Users");
                mRef.child(selectedItem).removeValue();

                mRef = db.getReference("Fields");
                mRef.child(selectedItem).removeValue();

                mRef = db.getReference("Items");
                mRef.child(selectedItem).removeValue();

                userRecyclerAdapter.notifyItemRemoved(position);
                userRecyclerAdapter.notifyDataSetChanged();
                setVisibilities();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewAdmin);

        txtSearchAdmin.addTextChangedListener(new TextWatcher() {
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

        btnLogoutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                goToLogin();
            }
        });
    }

    private void setVisibilities(){
        int itemCount = userRecyclerAdapter.getItemCount();

        if(itemCount>0){
            txtNoUser.setVisibility(View.GONE);
            recyclerViewAdmin.setVisibility(View.VISIBLE);
            progressBarAdmin.setVisibility(View.GONE);
        }else{
            txtNoUser.setVisibility(View.VISIBLE);
            recyclerViewAdmin.setVisibility(View.GONE);
            progressBarAdmin.setVisibility(View.GONE);
        }
    }

    private void filter(String txt){
        ArrayList<User> filteredList = new ArrayList<>();
        for(User user:users){
            if(user.getEmail().toLowerCase().contains(txt.toLowerCase())){
                filteredList.add(user);
            }
        }

        userRecyclerAdapter.filterList(filteredList);
    }

    private void goToLogin(){
        Intent intent = new Intent(this,MainActivity.class);
        this.startActivity(intent);
        this.finish();
    }
}