package com.android.crimsonalert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.crimsonalert.R;

public class EmergencyCallActivity extends AppCompatActivity {
    private TextView phone;
    private Button add;
    private ImageView back;

    static final int PICK_CONTACT = 1;

    public static final String CALL_PREFS = "callPrefs";
    public static final String phoneNumber = "phone";

    private String callNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);

        phone = findViewById(R.id.callPhoneTv);
        add = findViewById(R.id.addCallBtn);
        back = findViewById(R.id.callArrow);

        back.setOnClickListener(view -> EmergencyCallActivity.super.onBackPressed());

        SharedPreferences sharedPreferences = getSharedPreferences(CALL_PREFS, MODE_PRIVATE);
        callNumber = sharedPreferences.getString(phoneNumber, "");
        phone.setText(callNumber);

        add.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);

        });


    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        try {
                            if (hasPhone.equalsIgnoreCase("1")) {
                                Cursor phones = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                        null, null);
                                phones.moveToFirst();
                                String cNumber = phones.getString(phones.getColumnIndex("data1"));
                                System.out.println("number is:" + cNumber);
                                phone.setText(cNumber);
                                saveData();
                                phone.setVisibility(View.VISIBLE);
                            }

                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(CALL_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(phoneNumber, phone.getText().toString());
        editor.apply();
    }
}