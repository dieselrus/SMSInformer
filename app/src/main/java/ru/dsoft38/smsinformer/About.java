package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by diesel on 13.07.2015.
 */
public class About extends AppCompatActivity {
    static final  String TAG = "About";

    Button btnActivation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.about);

        String License = "";

        if(Pref.lic == Pref.License.ONE_MONTH){
            License = "Ежемесячная подписка.";
        } else if(Pref.lic == Pref.License.ONE_YEAR){
            License = "Ежегодная подписка.";
        } else if(Pref.lic == Pref.License.PURCHASE){
            License = "Программа куплена.";
        } else if(Pref.lic == Pref.License.NONE){
            License = "Не активирована.";
        }

        tv.setGravity(Gravity.CENTER);
        tv.setText(Html.fromHtml("<html><body><h1>&nbsp;</h1><p style=\"text-align: center;\"><strong>О программе</strong></p>" +
                "<p>Программа предназначена для трансляции определенных электронных писем на телефоны посредством СМС сообщений.</p>" +
                "<p>&nbsp;</p><p><strong>Автор: </strong>Гамза Денис.</p><p><strong>E-mail: </strong><a href=\"mailto:denis.gamza@gmail.com\">denis.gamza@gmail.com</a></p>" +
                "</body></html>") + String.format("Версия программы: %s \n\r", getVersion()) + String.format("Лицензия: %s", License));

        /*tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });*/

        btnActivation = (Button) findViewById(R.id.btnActivation);

        if(Pref.lic == Pref.License.PURCHASE){
            btnActivation.setVisibility(View.INVISIBLE);
        }

    }

    public void onClick(View arg0){
        Intent i = new Intent(this, InAppBillingActivity.class);
        startActivity(i);
    }

    private String getVersion() {
        //int version = -1;
        String version = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            //version = pInfo.versionCode;
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e(this.getClass().getSimpleName(), "Name not found", e1);
        }
        return version;
    }
}
