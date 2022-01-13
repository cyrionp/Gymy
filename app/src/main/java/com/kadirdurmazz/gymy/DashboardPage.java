package com.kadirdurmazz.gymy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DashboardPage extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    DatabaseReference mRef;
    FirebaseDatabase db;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    BottomNavigationView bottomNavigationView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_page);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.home);

        //This line will be deleted
        Toast.makeText(this, "Signed in email: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
    }


    DashboardFragment dashboardFragment = new DashboardFragment(this);
    AddFragment addFragment = new AddFragment(this);
    AccountFragment accountFragment = new AccountFragment(this);



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.home:
                selectedFragment = new DashboardFragment(this);
                break;

            case R.id.add:
                selectedFragment = new AddFragment(this);
                break;

            case R.id.account:
                selectedFragment = new AccountFragment(this);
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
        return true;
    }
}