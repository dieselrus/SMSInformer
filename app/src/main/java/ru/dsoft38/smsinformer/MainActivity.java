package ru.dsoft38.smsinformer;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    public static final String SERVICE_START = "ru.dsoft38.smsinformer_SERVICE_START";

    private ListView lvSimple = null;
    private ArrayList log_time = null;
    private ArrayList log_text = null;
    private ImageButton btnReloadLog = null;
    private ImageButton btnBackLog = null;
    private ImageButton btnForwardLog = null;

    private TextView tvLogDate = null;
    String beginDay = "";
    String endDay = "";

    private int iLimit = 20;
    private int iOffset = 0;

    private int DIALOG_DATE = 1;
    private GregorianCalendar gc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        registerReceiver(reciverAlertDBOpen, new IntentFilter(SERVICE_START));

        Pref.getPref(this);
        iLimit = Pref.prefLogRow;

        log_time = new ArrayList();
        log_text = new ArrayList();

        //AlarmReceiver.scheduleAlarms(this);
        Intent i = new Intent(this, ReceiveService.class);
        this.startService(i);

        // Использование собственного шаблона
        lvSimple = (ListView) findViewById(R.id.listView);

        tvLogDate = (TextView) findViewById(R.id.tvLogDate);

        gc = new GregorianCalendar();
        gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
        gc.set(GregorianCalendar.MINUTE, 0);
        gc.set(GregorianCalendar.SECOND, 0);
        gc.set(GregorianCalendar.MILLISECOND, 0);

        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        tvLogDate.setText(formatter.format(new Date(gc.getTimeInMillis())));

        tvLogDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_DATE);
            }
        });

        beginDay = String.valueOf(gc.getTimeInMillis());
        endDay = String.valueOf(gc.getTimeInMillis() + 86399000);

        //getLog(String.valueOf(iLimit), String.valueOf(iOffset), beginDay, endDay);

        //lvSimple.setAdapter(new CustomAdapter(this, log_time, log_text));

        btnReloadLog = (ImageButton) findViewById(R.id.btnReloadLog);
        btnBackLog = (ImageButton) findViewById(R.id.btnBack);
        btnForwardLog = (ImageButton) findViewById(R.id.btnForward);

        btnReloadLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                getLog(String.valueOf(iLimit), String.valueOf(iOffset), beginDay, endDay);
            }
        });

        btnBackLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                iOffset -= iLimit;
                getLog(String.valueOf(iLimit), String.valueOf(iOffset), null, null);
            }
        });

        btnForwardLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                iOffset += iLimit;
                getLog(String.valueOf(iLimit), String.valueOf(iOffset), null, null);
            }
        });

        getLog(String.valueOf(iLimit), String.valueOf(iOffset), beginDay, endDay);
    }

    //==================== set log date dialog ===============================
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DAY_OF_MONTH));
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    OnDateSetListener myCallBack = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            gc.set(GregorianCalendar.YEAR, year);
            gc.set(GregorianCalendar.MONTH, monthOfYear);
            gc.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
            gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
            gc.set(GregorianCalendar.MINUTE, 0);
            gc.set(GregorianCalendar.SECOND, 0);
            gc.set(GregorianCalendar.MILLISECOND, 0);

            Format formatter = new SimpleDateFormat("dd.MM.yyyy");
            tvLogDate.setText(formatter.format(new Date(gc.getTimeInMillis())));

            beginDay = String.valueOf(gc.getTimeInMillis());
        }
    };

    /*

     */
    private void getLog(String limit, String offset, String dateStart, String dateEnd) {
        try {
            Cursor c;

            if(dateStart == null | dateEnd == null) {
                c = AlarmDb.select_LOG(limit, offset);
            } else {
                c = AlarmDb.select_LOG_DATE(limit, offset, dateStart, dateEnd);
            }

            if (c != null) while (c.moveToNext()) {

                long l = Long.parseLong(c.getString(1));

                Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

                log_time.add(formatter.format(new Date(l)));
                log_text.add(c.getString(2));
            }

            if(log_text.size() > 0 && log_time.size() > 0)
                lvSimple.setAdapter(new CustomAdapter(this, log_time, log_text));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.action_help){
            Intent i = new Intent(this, Help.class);
            startActivity(i);
        } else if(id == R.id.action_about){
            Intent i = new Intent(this, About.class);
            startActivity(i);
        } else if(id == R.id.action_activation){
            Intent i = new Intent(this, InAppBillingActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
        unregisterReceiver(reciverAlertDBOpen);
    }

    BroadcastReceiver reciverAlertDBOpen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(SERVICE_START)){
                getLog(String.valueOf(iLimit), String.valueOf(iOffset), beginDay, endDay);
            }
        }
    };
}
