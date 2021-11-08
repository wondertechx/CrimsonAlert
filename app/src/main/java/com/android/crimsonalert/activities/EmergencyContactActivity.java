package com.android.crimsonalert.activities;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crimsonalert.Adapters.EmergencyContactAdapter;
import com.android.crimsonalert.R;
import com.android.crimsonalert.Utils.RecyclerViewClickListener;
import com.android.crimsonalert.models.users;
import com.google.android.material.snackbar.Snackbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class EmergencyContactActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    private EmergencyContactAdapter contactListAdapter;
    private ArrayList<users> contactArrayList;
    private SharedPreferences sp;
    //private FirebaseAuth firebaseAuth;
    private ImageView addButton, emergencyText, arrow;
    private TextView tapHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        contactArrayList = new ArrayList<>();
        final RecyclerView emergencyContactsList = (RecyclerView) findViewById(R.id.emergencyRecycler);

       // firebaseAuth = FirebaseAuth.getInstance();

        addButton = findViewById(R.id.emergencyButton);

        emergencyText = findViewById(R.id.emergencyDesc);
        arrow = findViewById(R.id.arrow);
        tapHere = findViewById(R.id.taphere);

        arrow.setOnClickListener(view -> {
            super.onBackPressed();
        });



        // retrieve saved contact list to initialise recycler view
        sp = getSharedPreferences("LOGGED_USER", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonArrayList = sp.getString("contact_list", null);
        Type type = new TypeToken<ArrayList<users>>() {}.getType();
        if (jsonArrayList != null) {
            contactArrayList = gson.fromJson(jsonArrayList, type);
        }

        if (contactArrayList.size()==0){
            emergencyText.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
            tapHere.setVisibility(View.VISIBLE);
        }



        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        contactListAdapter = new EmergencyContactAdapter(getApplicationContext(), contactArrayList);
        emergencyContactsList.setLayoutManager(layoutManager);
        emergencyContactsList.setAdapter(contactListAdapter);


        emergencyContactsList.addOnItemTouchListener(new RecyclerViewClickListener(getApplicationContext(),
                new RecyclerViewClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, final int position) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
                        alertDialogBuilder.setTitle("Delete Contact");
                        alertDialogBuilder.setMessage("Delete this contact from emergency list?");
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        contactArrayList.remove(position);

                                        contactListAdapter.notifyDataSetChanged();

                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // do nothing

                                        contactListAdapter.notifyDataSetChanged();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactArrayList.size() < 5) {
                    doLaunchContactPicker(view);
                } else {
                    Snackbar.make(view, "Max emergency contact limit is 5", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String phone, name;
                    try {
                        Uri result = data.getData();
                        // get the contact id from the Uri
                        String id = result.getLastPathSegment();
                        // query for contact
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id + ""}, null);

                        if (cursor != null) {
                            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

                            // let's just get the first number
                            if (cursor.moveToFirst()) {
                                phone = cursor.getString(phoneIndex);
                                phone = phone.replaceAll("\\s", "");    // remove all spaces
                                name = cursor.getString(nameIndex);
                                users contact = new users(name, phone);
                                int flag = 0;
                                for (users c : contactArrayList) {
                                    if (c.getPhone().equals(contact.getPhone())) {  // check if contact already exists in list
                                        Toast.makeText(getApplicationContext(),
                                                "This number already exists in Emergency Contacts", Toast.LENGTH_SHORT).show();
                                        flag = 1;
                                        break;
                                    }
                                }
                                if (flag == 0) {
                                    emergencyText.setVisibility(View.INVISIBLE);
                                    arrow.setVisibility(View.INVISIBLE);
                                    tapHere.setVisibility(View.INVISIBLE);

                                    contactArrayList.add(contact);
                                    contactListAdapter.notifyDataSetChanged();
                                }

                                Log.d(TAG, "Retrieved Contact: Phone: " + phone + " Name: " + name);

                            } else {
                                Log.d(TAG, "No results");
                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Failed to get phone data: " + e.getMessage());
                        Toast.makeText(getApplicationContext(),
                                "Sorry, couldn't get phone data", Toast.LENGTH_SHORT).show();
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "sorry, couldn't get phone data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        } else {
            Log.d(TAG, "Activity Result Not OK");
        }
    }
    @Override
    protected void onPause() {
        addContactListToDB(contactArrayList);
        super.onPause();
    }
    private void addContactListToDB(ArrayList<users> contactArrayList) {
        // Firebase DB
       // DatabaseReference databaseRef;
       // databaseRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
      //  databaseRef.child("emergencyList").removeValue();    // delete existing contact list
      //  for (users contact : contactArrayList)    // add contact list
        //    databaseRef.child("emergencyList").push().setValue(contact);

        // shared preference
        Gson gson = new Gson();
        String jsonArrayList = gson.toJson(contactArrayList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("contact_list", jsonArrayList);
        editor.apply();
    }






}