package ru.dsoft38.smsinformer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public AlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Pref.getPref(context);
        startReceiveService(context);
        scheduleAlarms(context);
    }

    static void scheduleAlarms(Context context) {
        /*InAppBilling inapp = new InAppBilling();
        inapp.billingInit(context);
        inapp = null;*/

        Pref.getPref(context);
        Pref.getLicense(context);
        startReceiveService(context);
        long NOW = Calendar.getInstance().getTimeInMillis();
        startInformerService(context, NOW);
    }

    static void scheduleAlarms(Context ctxt, long TIME) {
        Pref.getPref(ctxt);
        Pref.getLicense(ctxt);
        startReceiveService(ctxt);

        // Проверяем вхождение времени в разрешенный интерва
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        boolean isSplit = false;
        boolean isWithin = false;

        Date dt1 = null, dt2 = null, dt3 = null;

        try {
            dt1 = sdf.parse(Pref.prefSendSMSTimeFirst);
            dt2 = sdf.parse(Pref.prefSendSMSTimeLast);
            dt3 = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        isSplit = (dt2.compareTo(dt1) < 0);

        if (isSplit) {
            isWithin = (dt3.after(dt1) || dt3.before(dt2));
        } else {
            isWithin = (dt3.after(dt1) && dt3.before(dt2));
        }

        if(isWithin) {
            startInformerService(ctxt, TIME);
        } else {
            startInformerService(ctxt, TIME);
        }
    }

    static void startInformerService(Context context, long UTIME) {
        //Pref.getPref(context);

        Intent i = new Intent(context, InformerService.class);
        i.setAction(InformerService.SET_ALARM);
        i.putExtra("utime", UTIME);
        context.startService(i);
    }

    static void startReceiveService(Context context) {
        Intent i = new Intent(context, ReceiveService.class);
        context.startService(i);
    }
}
