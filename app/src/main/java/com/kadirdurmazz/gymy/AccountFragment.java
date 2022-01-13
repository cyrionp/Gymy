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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AccountFragment extends Fragment {
    private Activity activity;

    public AccountFragment(Activity activity) {
        this.activity = activity;
    }

    String userUID, userEmail;
    List<User> myUser;
    TextView txtAccount_Email, txtAccount_Name, txtAccount_Type, txtAccount_Gender, txtAccount_Age, txtAccount_Height, txtAccount_Weight;

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

        Button btnLogout = view.findViewById(R.id.btnLogout);
        txtAccount_Email.setText("Email: "+userEmail);

        getUserData();

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

                txtAccount_Name.setText("Name: "+myUser.get(0).getName());
                txtAccount_Type.setText("Type: "+myUser.get(0).getType());
                txtAccount_Gender.setText("Gender: "+myUser.get(0).getGender());
                txtAccount_Age.setText("Age: "+myUser.get(0).getAge());
                txtAccount_Height.setText("Height: "+myUser.get(0).getHeight());
                txtAccount_Weight.setText("Weight: "+myUser.get(0).getWeight());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity,"ERROR: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLogin(){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}