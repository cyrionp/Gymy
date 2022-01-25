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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage extends AppCompatActivity {

    EditText txtEmailR, txtPasswordR, txtPasswordAgainR, txtName, txtGender, txtAge, txtHeight, txtWeight;
    Button btnRegister;
    TextView tvGoLogin;
    String validEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase db;
    DatabaseReference mRef;

    private User myUser;
    private Field myField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        txtEmailR = findViewById(R.id.txtEmailR);
        txtPasswordR = findViewById(R.id.txtPasswordR);
        txtPasswordAgainR = findViewById(R.id.txtPasswordAgainR);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtAge = findViewById(R.id.txtAge);
        txtHeight = findViewById(R.id.txtHeight);
        txtWeight = findViewById(R.id.txtWeight);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin = findViewById(R.id.tvGoLogin);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        tvGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPage.this,MainActivity.class);
                RegisterPage.this.startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performRegistration();
            }
        });
    }

    private void performRegistration(){
        String email = txtEmailR.getText().toString().trim();
        String password = txtPasswordR.getText().toString().trim();
        String passwordAgain = txtPasswordAgainR.getText().toString().trim();
        String name = txtName.getText().toString().trim();
        String gender = txtGender.getText().toString().trim();
        String age = txtAge.getText().toString().trim();
        String height = txtHeight.getText().toString().trim();
        String weight = txtWeight.getText().toString().trim();

        int _age = Integer.parseInt(txtAge.getText().toString().trim());
        int _height = Integer.parseInt(txtHeight.getText().toString().trim());
        int _weight = Integer.parseInt(txtWeight.getText().toString().trim());

        if(!email.matches(validEmail)){
            giveError(txtEmailR,"Email is not valid");
        }else if(!password.equals(passwordAgain)){
            giveError(txtPasswordAgainR,"Password are not same");
        }else if(password.isEmpty() || password.length()<6){
            giveError(txtPasswordR,"Password must have 6 chars at least");
        }else if(name.isEmpty() || name.length()<3){
            giveError(txtName,"Name must have 3 chars at least");
        }else if(!(gender.equals("Male") || gender.equals("Female"))){
            giveError(txtGender,"Gender must be Male or Female");
        }else if(_age<1 || _age>200){
            giveError(txtAge,"Given age must be between 1-200");
        }else if(_height<1){
            giveError(txtHeight,"Given height must be higher than 0");
        }else if(_weight<1){
            giveError(txtWeight,"Given weight must be higher than 0");
        }else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert currentUser != null;
                        String uid = currentUser.getUid();
                        db = FirebaseDatabase.getInstance();

                        mRef = db.getReference("Users");
                        myUser = new User(uid,"basic",email,name,gender,age,height,weight);
                        mRef.child(uid).setValue(myUser);

                        mRef = db.getReference("Fields");
                        myField = new Field("field1", "field2", "field3", "field4", "field5");
                        mRef.child(uid).setValue(myField);

                        goToLogin();
                        Toast.makeText(RegisterPage.this, "You are registered", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RegisterPage.this, "ERROR: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void goToLogin(){
        startActivity(new Intent(RegisterPage.this, DashboardPage.class));
    }

    private void giveError(EditText txt, String message){
        txt.setError(message);
        txt.requestFocus();
    }
}