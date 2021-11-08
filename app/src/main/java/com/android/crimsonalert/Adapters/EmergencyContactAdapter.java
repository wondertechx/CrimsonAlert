package com.android.crimsonalert.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.crimsonalert.R;
import com.android.crimsonalert.models.users;

import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<users> contactArrayList;

    public EmergencyContactAdapter(Context context, ArrayList<users> contactArrayList){
    this.context = context;
    this.contactArrayList = contactArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_content, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final users contact = contactArrayList.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhone());
       // holder.delete.setChecked(true);


    }
    @Override
    public int getItemCount() {
        return contactArrayList == null ? 0 : contactArrayList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView contactName, contactPhone;
        public ImageView Emergencydelete;

        MyViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.emergencyName);
            contactPhone =  itemView.findViewById(R.id.emergencyPhone);
            Emergencydelete = itemView.findViewById(R.id.emergencyDelete);
        }

    }
}