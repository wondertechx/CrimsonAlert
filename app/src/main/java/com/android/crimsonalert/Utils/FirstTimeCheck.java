package com.android.crimsonalert.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeCheck {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    public FirstTimeCheck(Context context){
        this.context = context;
        pref = context.getSharedPreferences("first",0);
        editor = pref.edit();
    }

    public void setFirst(Boolean isFirst)
    {
        editor.putBoolean("check",isFirst);
        editor.commit();

    }
    public boolean Check(){
        return pref.getBoolean("check", true);
    }
}
