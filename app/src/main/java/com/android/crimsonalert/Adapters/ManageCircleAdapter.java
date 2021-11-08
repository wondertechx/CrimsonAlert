package com.android.crimsonalert.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.crimsonalert.R;
import com.android.crimsonalert.models.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ManageCircleAdapter extends RecyclerView.Adapter<ManageCircleAdapter.MembersViewHolder> {

    ArrayList<users> userList;
    Context c;
    public ManageCircleAdapter(ArrayList<users> userList, Context c){
        this.userList = userList;
        this.c = c;
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.manage_content, viewGroup, false);
        MembersViewHolder membersViewHolder = new MembersViewHolder(v,c,userList);


        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MembersViewHolder membersViewHolder, int i) {
        users currentUserobj = userList.get(i);

        membersViewHolder.name.setText(currentUserobj.name);
        membersViewHolder.userid.setText(currentUserobj.userId);




    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView userid;

        Context c;
        ArrayList<users> nameArrayList;
        //ArrayList<LocationModel> locationArraylist;
        FirebaseAuth auth;
        FirebaseUser user;


        public MembersViewHolder(@NonNull View itemView, Context c, ArrayList<users> nameArrayList) {
            super(itemView);

            this.c = c;
            this.nameArrayList = nameArrayList;

            itemView.setOnClickListener(this);
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();

            name = itemView.findViewById(R.id.manageName);

            userid = itemView.findViewById(R.id.manageUserid);



        }

        @Override
        public void onClick(View v) {
            //CircleJoin circleJoin = new CircleJoin();
            String memberid = userid.getText().toString();
            DatabaseReference removeReference = FirebaseDatabase.getInstance().getReference(memberid).child("CircleMembers");

            DatabaseReference removeRef2 = FirebaseDatabase.getInstance().getReference(user.getUid()).child("JoinedMembers");
            removeReference.child(user.getUid()).removeValue();
            removeRef2.child(memberid).removeValue();


        }
    }

}