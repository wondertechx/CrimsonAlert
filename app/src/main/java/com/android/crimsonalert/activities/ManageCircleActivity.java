package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.crimsonalert.Adapters.ManageCircleAdapter;
import com.android.crimsonalert.R;
import com.android.crimsonalert.Utils.RecyclerViewClickListener;
import com.android.crimsonalert.models.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageCircleActivity extends AppCompatActivity {

    private RecyclerView manageRecycler;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private ImageView back;

    FirebaseAuth auth;
    FirebaseUser user;
    users userList;
    ArrayList<users> namelist2;
    DatabaseReference reference, usersReference;
    String circlememberid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_circle);

        manageRecycler = findViewById(R.id.manageRecycler);
        back = findViewById(R.id.manageArrow);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        namelist2 = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        manageRecycler.setLayoutManager(layoutManager);
        manageRecycler.setHasFixedSize(true);

      manageRecycler.addOnItemTouchListener(new RecyclerViewClickListener(getApplicationContext(), (view, position) -> {
          namelist2.remove(position);
          adapter.notifyDataSetChanged();
      }));

      back.setOnClickListener(view -> {
          super.onBackPressed();
      });

        usersReference = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("JoinedMembers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namelist2.clear();

                if (dataSnapshot.exists()) {
                    for (DataSnapshot dss: dataSnapshot.getChildren()) {
                        circlememberid = dss.child("circlememberid").getValue(String.class);

                        usersReference.child(circlememberid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                userList = dataSnapshot.getValue(users.class);
                                namelist2.add(userList);
                                adapter.notifyDataSetChanged();

                                if(namelist2.size() == 0) {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManageCircleActivity.this);
                                    alertDialogBuilder.setTitle("Invite");
                                    alertDialogBuilder.setMessage("You're not sharing your location with anyone yet, Invite one?");
                                    alertDialogBuilder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(ManageCircleActivity.this, JoinActivity.class);
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

        adapter = new ManageCircleAdapter(namelist2, getApplicationContext());
        manageRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}