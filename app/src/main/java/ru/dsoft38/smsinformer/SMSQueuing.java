package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by diesel on 30.07.2015.
 */
public class SMSQueuing extends Activity {

    private ListView lvSimple = null;

    private ArrayList sms_id = null;
    private ArrayList sms_num = null;
    private ArrayList sms_text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsqueuing);

        sms_id = new ArrayList();
        sms_num = new ArrayList();
        sms_text = new ArrayList();

        // Использование собственного шаблона
        lvSimple = (ListView) findViewById(R.id.lvSMSQueuing);

        getSMSQueuing();

        lvSimple.setAdapter(new CustomAdapterSMSQueuing(this, sms_id, sms_num, sms_text));
    }

    private void getSMSQueuing() {
        try {
            Cursor c;

            c = AlarmDb.select_smsqueuing();

            sms_id.add("id");
            sms_num.add("Получатель");
            sms_text.add("Текст");

            if (c != null) while (c.moveToNext()) {
                sms_id.add(c.getString(0));
                sms_num.add(c.getString(1));
                sms_text.add(c.getString(2));
            }

            lvSimple.setAdapter(new CustomAdapterSMSQueuing(this, sms_id, sms_num, sms_text));

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
