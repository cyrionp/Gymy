package com.kadirdurmazz.gymy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddFragment extends Fragment {
    private Activity activity;
    View view;

    public AddFragment(Activity activity){
        this.activity = activity;
    }

    FirebaseDatabase db;
    DatabaseReference mRef;
    FirebaseUser currentUser;
    private Map map;
    private String userUID, selectedDate;
    private DatePickerDialog datePickerDialog;
    Button btnDatePicker;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add, container, false);
        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        EditText mField1 = view.findViewById(R.id.txtAdd_Field1);
        EditText mField2 = view.findViewById(R.id.txtAdd_Field2);
        EditText mField3 = view.findViewById(R.id.txtAdd_Field3);
        EditText mField4 = view.findViewById(R.id.txtAdd_Field4);
        EditText mField5 = view.findViewById(R.id.txtAdd_Field5);
        Button btnAddItem = view.findViewById(R.id.btnAddItem);
        Button btnChangeFields = view.findViewById(R.id.btnChangeFields);

        initDatePicker();
        selectedDate = getTodaysDate();
        btnDatePicker = view.findViewById(R.id.btnDatePicker);
        btnDatePicker.setText(getTodaysDate());

        getFields(mField1, mField2, mField3, mField4, mField5);

        btnDatePicker.setOnClickListener(v->{
            openDatePicker();
        });

        btnChangeFields.setOnClickListener(v -> {
            String sField1 = mField1.getText().toString().trim();
            String sField2 = mField2.getText().toString().trim();
            String sField3 = mField3.getText().toString().trim();
            String sField4 = mField4.getText().toString().trim();
            String sField5 = mField5.getText().toString().trim();

            if(!sField1.isEmpty() && !sField2.isEmpty() && !sField3.isEmpty() && !sField4.isEmpty() && !sField5.isEmpty()){
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                assert currentUser != null;
                userUID = currentUser.getUid();

                db = FirebaseDatabase.getInstance();
                mRef = db.getReference("Fields/"+userUID);

                map = new HashMap();
                map.put("fieldName1",sField1);
                map.put("fieldName2",sField2);
                map.put("fieldName3",sField3);
                map.put("fieldName4",sField4);
                map.put("fieldName5",sField5);

                ChangeFields(mRef, map);

                mField1.setText("");
                mField2.setText("");
                mField3.setText("");
                mField4.setText("");
                mField5.setText("");

                getFields(mField1, mField2, mField3, mField4, mField5);
            }
            else{
                Toast.makeText(activity,"Fill all the fields",Toast.LENGTH_SHORT).show();
            }
        });

        btnAddItem.setOnClickListener(v->{
            String sField1 = mField1.getText().toString().trim();
            String sField2 = mField2.getText().toString().trim();
            String sField3 = mField3.getText().toString().trim();
            String sField4 = mField4.getText().toString().trim();
            String sField5 = mField5.getText().toString().trim();

            if(!sField1.isEmpty() && !sField2.isEmpty() && !sField3.isEmpty() && !sField4.isEmpty() && !sField5.isEmpty()){
                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                assert currentUser != null;
                userUID = currentUser.getUid();

                db = FirebaseDatabase.getInstance();
                mRef = db.getReference("Items/"+userUID+"/"+selectedDate);

                map = new HashMap();
                map.put("fieldDate",selectedDate);
                map.put("field1",sField1);
                map.put("field2",sField2);
                map.put("field3",sField3);
                map.put("field4",sField4);
                map.put("field5",sField5);

                AddItem(mRef, map);

                Toast.makeText(activity,"Item added", Toast.LENGTH_SHORT).show();

                mField1.setText("");
                mField2.setText("");
                mField3.setText("");
                mField4.setText("");
                mField5.setText("");
            }
            else{
                Toast.makeText(activity,"Fill all the fields",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFields(EditText field1, EditText field2, EditText field3, EditText field4, EditText field5) {
        List<Field> fields;
        fields = new ArrayList<>();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        userUID = currentUser.getUid();

        db = FirebaseDatabase.getInstance();
        mRef = db.getReference("Fields/" + userUID);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Field field = dataSnapshot.getValue(Field.class);
                fields.add(field);

                field1.setHint(fields.get(0).getFieldName1());
                field2.setHint(fields.get(0).getFieldName2());
                field3.setHint(fields.get(0).getFieldName3());
                field4.setHint(fields.get(0).getFieldName4());
                field5.setHint(fields.get(0).getFieldName5());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(activity, "ERROR: " + error.getDetails(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day+"-"+month+"-"+year;
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = day+"-"+month+"-"+year;
                String formattedDate = day+"-"+month+"-"+year;
                btnDatePicker.setText(date);
                selectedDate = formattedDate;
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(activity, style, dateSetListener, year, month, day);
    }

    private void openDatePicker(){
        datePickerDialog.show();
    }

    private void ChangeFields(DatabaseReference mRef, Map map){
        try {
            mRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(activity,"Fields' names are changed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void AddItem(DatabaseReference mRef, Map map){
        try {
            mRef.setValue(map);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}