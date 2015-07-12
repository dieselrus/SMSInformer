package ru.dsoft38.smsinformer;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Pref.getPref(context);
        scheduleAlarms(context);
    }

    static void scheduleAlarms(Context context) {
        long NOW = Calendar.getInstance().getTimeInMillis();
        startInformerService(context, NOW);
    }

    static void scheduleAlarms(Context ctxt, long TIME) {
        startInformerService(ctxt, TIME);
    }

    static void startInformerService(Context context, long UTIME) {
        Pref.getPref(context);

        Intent i = new Intent(context, InformerService.class);
        i.setAction(InformerService.SET_ALARM);
        i.putExtra("utime", UTIME);
        context.startService(i);
    }
}
