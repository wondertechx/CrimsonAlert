package com.android.crimsonalert.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.crimsonalert.LiveMapsActivity;
import com.android.crimsonalert.R;

import com.android.crimsonalert.models.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyCircleAdapter extends RecyclerView.Adapter<MyCircleAdapter.MembersViewHolder> {
    ArrayList<users> userList2;
    Context c;
    public MyCircleAdapter(ArrayList<users> userList2, Context c){
        this.userList2 = userList2;
        this.c = c;
    }
    @Override
    public int getItemCount() {
        return userList2.size();
    }

    @NonNull
    @Override
    public MyCircleAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.circle_content, viewGroup, false);
        MyCircleAdapter.MembersViewHolder membersViewHolder = new MyCircleAdapter.MembersViewHolder(v,c,userList2);


        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyCircleAdapter.MembersViewHolder membersViewHolder, int i) {
        users currentUserobj = userList2.get(i);

        membersViewHolder.name2.setText(currentUserobj.name);
        membersViewHolder.userid2.setText(currentUserobj.userId);




    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name2;
        TextView userid2;

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

            name2 = itemView.findViewById(R.id.circleName);

            userid2 = itemView.findViewById(R.id.circleUserid);



        }

        @Override
        public void onClick(View v) {
            //CircleJoin circleJoin = new CircleJoin();
            String username = name2.getText().toString();
            String memberid = userid2.getText().toString();
            Intent intent = new Intent(v.getContext(), LiveMapsActivity.class);
            intent.putExtra("name",username);
            intent.putExtra("userid",memberid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(intent);
        }
    }

}