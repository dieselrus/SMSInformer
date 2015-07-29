package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by diesel on 13.07.2015.
 */
public class About extends Activity {
    static final  String TAG = "About";

    Button btnActivation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.about);

        tv.setGravity(Gravity.CENTER);
        tv.setText(Html.fromHtml("<html><body><h1>&nbsp;</h1><p style=\"text-align: center;\"><strong>О программе</strong></p>" +
                "<p>Программа предназначена для трансляции определенных электронных писем на телефоны посредством СМС сообщений.</p>" +
                "<p>&nbsp;</p><p><strong>Автор: </strong>Гамза Денис.</p><p><strong>E-mail: </strong><a href=\"mailto:denis.gamza@gmail.com\">denis.gamza@gmail.com</a></p>" +
                "</body></html>"));

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btnActivation = (Button) findViewById(R.id.btnActivation);

    }

    public void onClick(View arg0){
        Intent i = new Intent(this, InAppBillingActivity.class);
        startActivity(i);
    }
}
