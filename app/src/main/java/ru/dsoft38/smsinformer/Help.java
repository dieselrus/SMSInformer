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
        tv.setText(Html.fromHtml("<p><strong>Пример содержания письма</strong></p>" +
                        "<p><strong>Тема письма:</strong></p>" +
                        "<p>SMSINFORMER <em>(должна совпадать с темой в настройках программы)</em></p>" +
                        "<p><strong>Содержание письма:</strong></p>" +
                        "<p>&lt;PhoneList&gt;8ХХХХХХХХХХ;+7ХХХХХХХХХХ;8ХХХХХХХХХХ&lt;/PhoneList&gt;<br/>&lt;MessageText&gt;Текст сообщения для СМС. Ограничение 60 символов.&lt;/MessageText&gt;</p>" +
                        "<p><strong>Расшифровка:</strong></p>" +
                        "<ul><li><p>В теге <strong><em>&lt;</em></strong><strong><em>PhoneList&gt;&lt;/</em></strong><strong><em>PhoneList&gt;</em></strong> должен содержаться список\n номеров телефонов получателей, разделенный символом «<strong>;</strong>».</p></li>" +
                        "<li><p>В теге <strong><em>&lt;</em></strong><strong><em>MessageText&gt;&lt;/</em></strong><strong><em>MessageText&gt; </em></strong>должен содержаться\n текст сообщения.</p></li>" +
                        "<ul>"
        ));

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
