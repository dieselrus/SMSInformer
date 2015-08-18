package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by diesel on 30.07.2015.
 */
public class SMSQueuing extends AppCompatActivity {

    private ListView lvSimple = null;

    private ArrayList sms_id = null;
    private ArrayList sms_num = null;
    private ArrayList sms_text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smsqueuing);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

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
            sms_num.add(getString(R.string.sms_queuing_table_phone));
            sms_text.add(getString(R.string.sms_queuing_table_message));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsqueuing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clean_smsqueuing) {

            if(AlarmDb.clean_smsqueuing()){
                alert(getString(R.string.sms_queuing_clear_ok));
            }else{
                alert(getString(R.string.sms_queuing_not_clear));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        //Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }
}
