package ru.dsoft38.smsinformer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ListView lvSimple = null;
    private ArrayList log_time = null;
    private ArrayList log_text = null;
    private ImageButton btnReloadLog = null;
    private ImageButton btnBackLog = null;
    private ImageButton btnForwardLog = null;

    private int iLimit = 20;
    private int iOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        Pref.getPref(this);
        iLimit = Pref.prefLogRow;

        log_time = new ArrayList();
        log_text = new ArrayList();

        //AlarmReceiver.scheduleAlarms(this);
        Intent i = new Intent(this, ReceiveService.class);
        this.startService(i);

        // Использование собственного шаблона
        lvSimple = (ListView) findViewById(R.id.listView);

        getLog(String.valueOf(iLimit), String.valueOf(iOffset));

        //lvSimple.setAdapter(new CustomAdapter(this, log_time, log_text));

        btnReloadLog = (ImageButton) findViewById(R.id.btnReloadLog);
        btnBackLog = (ImageButton) findViewById(R.id.btnBack);
        btnForwardLog = (ImageButton) findViewById(R.id.btnForward);

        btnReloadLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                getLog(String.valueOf(iLimit), String.valueOf(iOffset));
            }
        });

        btnBackLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                iOffset -= iLimit;
                getLog(String.valueOf(iLimit), String.valueOf(iOffset));
            }
        });

        btnForwardLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_time = new ArrayList();
                log_text = new ArrayList();

                iOffset += iLimit;
                getLog(String.valueOf(iLimit), String.valueOf(iOffset));
            }
        });
    }

    private void getLog(String limit, String offset) {
        AlarmDb db = new AlarmDb(this);
        db.open();
        Cursor c = db.select_LOG(limit, offset);

        if (c != null) while (c.moveToNext()) {

            long l = Long.parseLong(c.getString(1));

            Format formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

            log_time.add(formatter.format(new Date(l)));
            log_text.add(c.getString(2));
        }

        db.close();

        lvSimple.setAdapter(new CustomAdapter(this, log_time, log_text));
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
    }
}
