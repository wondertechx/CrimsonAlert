package com.android.crimsonalert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.crimsonalert.R;
import com.android.crimsonalert.models.CircleJoin;
import com.android.crimsonalert.models.users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinActivity extends AppCompatActivity {
    private TextView code;
    private Button share;
    private EditText enterCode;

    private Button join;
    private ImageView back;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    public FirebaseUser user;

    DatabaseReference reference;

    String current_user_id, join_user_id, join_user_email;
    DatabaseReference currentReference, circleReference, joinedReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        code = findViewById(R.id.joinCode);
        share = findViewById(R.id.shareButton);
        enterCode = findViewById(R.id.joinEditext);
        join = findViewById(R.id.joinButton);
        back = findViewById(R.id.joinArrow);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference();
        currentReference = FirebaseDatabase.getInstance().getReference().child(user.getUid());

        current_user_id = user.getUid();

        back.setOnClickListener(view -> {
            super.onBackPressed();
        });

        share.setOnClickListener(view -> {
            String Code = code.getText().toString();
            String message = "Use this code to see my live location in an emergency using the Crimson Alert app " +Code;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            //intent.putExtra(Intent.EXTRA_SUBJECT, message);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, "Share"));

        });

        join.setOnClickListener(view -> {

            joinFunction();
        });

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users user =snapshot.getValue(users.class);
                code.setText(user.getCode());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(JoinActivity.this, error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinFunction() {
        Query query = reference.orderByChild("code").equalTo(enterCode.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    users users;
                    for (DataSnapshot childDss : dataSnapshot.getChildren()) {
                        users = childDss.getValue(users.class);
                        join_user_id = users.userId;


                        circleReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("CircleMembers");

                        CircleJoin circleJoin = new CircleJoin(current_user_id);
                        CircleJoin circleJoin1 = new CircleJoin(join_user_id);

                        circleReference.child(join_user_id).setValue(circleJoin1).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getApplicationContext(), "User joined sucessfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        joinedReference = FirebaseDatabase.getInstance().getReference(join_user_id).child("JoinedMembers");
                        joinedReference.child(user.getUid()).setValue(circleJoin);
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Code invalid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}