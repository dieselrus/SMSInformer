package ru.dsoft38.smsinformer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ListView lvSimple = null;
    private SimpleAdapter sAdapter = null;
    private ArrayList<Map<String, String>> data = null;
    private Map<String, String> m = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AlarmReceiver.scheduleAlarms(this);
        Intent i = new Intent(this, ReceiveService.class);
        this.startService(i);

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2" };
        // Использование собственного шаблона
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.loglistviewe, R.id.label, values);
        // определяем список и присваиваем ему адаптер
        lvSimple = (ListView) findViewById(R.id.listView);
        lvSimple.setAdapter(sAdapter);
        registerForContextMenu(lvSimple);
        //setListAdapter(adapter);
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
