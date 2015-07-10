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
        scheduleAlarms(context);
    }

    static void scheduleAlarms(Context context) {
        long NOW = Calendar.getInstance().getTimeInMillis();
        startMailService(context, NOW);
    }

    static void scheduleAlarms(Context ctxt, long TIME) {
        startMailService(ctxt, TIME);
    }

    static void startMailService(Context context, long UTIME) {
        Intent i = new Intent(context, InformerService.class);
        i.setAction(InformerService.SET_ALARM);
        i.putExtra("utime", UTIME);
        context.startService(i);
    }
}
