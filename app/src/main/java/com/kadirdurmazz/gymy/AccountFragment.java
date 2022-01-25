package com.kadirdurmazz.gymy;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccountFragment extends Fragment {
    private Activity activity;

    public AccountFragment(Activity activity) {
        this.activity = activity;
    }

    String userUID, userEmail;
    List<User> myUser;
    TextView txtAccount_Email, txtAccount_Type;
    EditText txtAccount_Name, txtAccount_Gender, txtAccount_Age, txtAccount_Height, txtAccount_Weight;

    DatabaseReference mRef;
    FirebaseDatabase db;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userUID = currentUser.getUid();
        userEmail = currentUser.getEmail();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        txtAccount_Email = view.findViewById(R.id.txtAccount_Email);
        txtAccount_Name = view.findViewById(R.id.txtAccount_Name);
        txtAccount_Type = view.findViewById(R.id.txtAccount_Type);
        txtAccount_Gender = view.findViewById(R.id.txtAccount_Gender);
        txtAccount_Age = view.findViewById(R.id.txtAccount_Age);
        txtAccount_Height = view.findViewById(R.id.txtAccount_Height);
        txtAccount_Weight = view.findViewById(R.id.txtAccount_Weight);

        Button btnUpdateUser = view.findViewById(R.id.btnUpdateUser);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        txtAccount_Email.setText("Email: "+userEmail);

        getUserData();

        btnUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                goToLogin();
            }
        });
    }

    private void getUserData() {
        myUser = new ArrayList<>();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Users/"+userUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myUser.add(user);

                txtAccount_Name.setText(myUser.get(0).getName());
                txtAccount_Type.setText("Type: "+myUser.get(0).getType());
                txtAccount_Gender.setText(myUser.get(0).getGender());
                txtAccount_Age.setText(myUser.get(0).getAge());
                txtAccount_Height.setText(myUser.get(0).getHeight());
                txtAccount_Weight.setText(myUser.get(0).getWeight());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity,"ERROR: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(){
        String name = txtAccount_Name.getText().toString().trim();
        String gender = txtAccount_Gender.getText().toString().trim();
        String age = txtAccount_Age.getText().toString().trim();
        String height = txtAccount_Height.getText().toString().trim();
        String weight = txtAccount_Weight.getText().toString().trim();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Users/"+userUID);

        Map map = new HashMap();
        map.put("uid",userUID);
        map.put("type","basic");
        map.put("email",userEmail);
        map.put("name",name);
        map.put("gender",gender);
        map.put("age",age);
        map.put("height",height);
        map.put("weight",weight);

        mRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(activity, "Your information is updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLogin(){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}