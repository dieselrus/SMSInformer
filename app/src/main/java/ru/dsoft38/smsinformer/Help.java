package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by diesel on 13.07.2015.
 */
public class Help extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.help);
        TextView tv = (TextView) findViewById(R.id.help);
        tv.setGravity(Gravity.CENTER);
        tv.setText(Html.fromHtml("<html><body><h1>&nbsp;</h1><p style=\"text-align: center;\"><strong>Пример содержания письма</strong></p>" +
                "<p><strong>Тема письма:</strong>SMSINFORMER(должна совпадать с темой в настройках)</p>" +
                "<p><strong>Содержание письма:</strong></p>"
                "<p>&nbsp;</p><p><strong>Автор:</strong>Гамза Денис.</p><p><strong>E-mail:</strong><a href=\"mailto:denis.gamza@gmail.com\">denis.gamza@gmail.com</a></p>" +
                "</body></html>"));

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
