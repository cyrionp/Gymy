package com.kadirdurmazz.gymy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnLogin;
    TextView tvGoRegister, tvForgotPassword;
    List<User> myUser;
    String validEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String userUID, userType, userEmail;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase db;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        tvGoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = txtEmail.getText().toString().trim();

                if(!userEmail.matches(validEmail)){
                    txtEmail.setError("Email is not valid");
                    txtEmail.requestFocus();
                }else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Password reset link sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "We couldn't send reset link", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        if(!email.matches(validEmail)){
            txtEmail.setError("Email is not valid");
            txtEmail.requestFocus();
        }else if(password.isEmpty() || password.length()<6){
            txtPassword.setError("Password must have 6 chars at least");
            txtPassword.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        checkUserType();
                    }else{
                        Toast.makeText(MainActivity.this, "ERROR: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void checkUserType(){
        myUser = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userUID = currentUser.getUid();
        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Users/"+userUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                myUser.add(user);
                userType = myUser.get(0).getType();

                if(userType.equals("basic")){
                    goToDashboard();
                }else if(userType.equals("admin")){
                    goToAdmin();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"ERROR: "+error.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToRegister(){
        startActivity(new Intent(MainActivity.this, RegisterPage.class));
    }

    private void goToDashboard(){
        Intent intent = new Intent(MainActivity.this, DashboardPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goToAdmin(){
        Intent intent = new Intent(MainActivity.this, AdminPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}