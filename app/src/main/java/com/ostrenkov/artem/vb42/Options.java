package com.ostrenkov.artem.vb42;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ostrenkov.artem.vb42.R;

public class Options extends AppCompatActivity {


    public void getparam(String name, String res){

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        String mySetting = sharedPref.getString(name, null);
        res = mySetting;

    }
    public String getparam(String name){

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        String mySetting = sharedPref.getString(name, null);
        return mySetting;

    }

    public void setparam(String name,  String res) {

        SharedPreferences sharedPref = getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name, res);
        editor.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        ((TextView) findViewById(R.id.edURL2)).setText(getparam("URL2"));
        ((TextView) findViewById(R.id.edURl1)).setText(getparam("URL1"));
        ((TextView) findViewById(R.id.edURL3)).setText(getparam("URL3"));
        ((TextView) findViewById(R.id.edURL4)).setText(getparam("URL4"));

        String ph = "UserID = " + getparam("userID");
        ((TextView) findViewById(R.id.twUserID)).setText(ph);



    }

    public void onClickApplyURL2(View view) {


        setparam("URL",((TextView) findViewById(R.id.edURL2)).getText().toString());
        setparam("URL2",((TextView) findViewById(R.id.edURL2)).getText().toString());
        onBackPressed();
    }

    public void onClickApplyURL1(View view) {

        setparam("URL",((TextView) findViewById(R.id.edURl1)).getText().toString());
        setparam("URL1",((TextView) findViewById(R.id.edURl1)).getText().toString());
        onBackPressed();
    }

    public void onClickApplyURL3(View view) {

        setparam("URL",((TextView) findViewById(R.id.edURL3)).getText().toString());
        setparam("URL3",((TextView) findViewById(R.id.edURL3)).getText().toString());
        onBackPressed();
    }

    public void onClickApplyURL4(View view) {

        setparam("URL",((TextView) findViewById(R.id.edURL4)).getText().toString());
        setparam("URL4",((TextView) findViewById(R.id.edURL4)).getText().toString());
        onBackPressed();
    }

}
