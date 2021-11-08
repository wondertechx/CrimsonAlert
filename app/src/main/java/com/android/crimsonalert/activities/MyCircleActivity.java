package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.crimsonalert.Adapters.MyCircleAdapter;
import com.android.crimsonalert.R;
import com.android.crimsonalert.models.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCircleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ImageView back;

    FirebaseAuth auth;
    FirebaseUser user;
    users usersList;
    ArrayList<users> namelist;
    DatabaseReference reference, usersReference;
    String circlememberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_circle);

        recyclerView = findViewById(R.id.circleRecycler);
        back = findViewById(R.id.circleArrow);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        namelist = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        usersReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("CircleMembers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namelist.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss: dataSnapshot.getChildren()) {
                        circlememberid = dss.child("circlememberid").getValue(String.class);

                        usersReference.child(circlememberid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                usersList = dataSnapshot.getValue(users.class);
                                namelist.add(usersList);
                                adapter.notifyDataSetChanged();

                                if(namelist.size() == 0) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyCircleActivity.this);
                                    alertDialogBuilder.setTitle("Add a person");
                                    alertDialogBuilder.setMessage("You don't have any person to see their live location, add one?");
                                    alertDialogBuilder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(MyCircleActivity.this, JoinActivity.class);
                                                    startActivity(intent);

                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        adapter = new MyCircleAdapter(namelist, getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}