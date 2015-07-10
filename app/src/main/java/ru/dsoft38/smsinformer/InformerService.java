package ru.dsoft38.smsinformer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;


public class InformerService extends IntentService {

    static boolean DEBUG_LOG = true;
    private static final String TAG_LOG = "SMSInformer";

    public static final String SET_ALARM = "ru.dsoft38.smsinformer_SET_ALARM";
    public static final String RUN_ALARM = "ru.dsoft38.smsinformer_RUN_ALARM";
    // Флаги для отправки и доставки SMS
    private static final String SENT_SMS_FLAG = "ru.dsoft38.smsinformer_SENT_SMS";
    private static final String DELIVER_SMS_FLAG = "ru.dsoft38.smsinformer_DELIVER_SMS";

    private PendingIntent sentPIn = null;
    private PendingIntent deliverPIn = null;
    private SentReceiver sentReceiver;

    public InformerService() {
        super("InformerService");
    }

    public InformerService(String name) {
        super(name);
    }

    public String getDateStr(Calendar c) {
        return new StringBuilder().append(c.get(Calendar.DAY_OF_MONTH)).append(".")
                .append(c.get(Calendar.MONTH) + 1).append(".")
                .append(c.get(Calendar.YEAR)).append(" ")
                .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(c.get(Calendar.MINUTE))
                .toString();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent sentIn = new Intent(SENT_SMS_FLAG);
        sentPIn = PendingIntent.getBroadcast(this, 0, sentIn, 0);

        Intent deliverIn = new Intent(DELIVER_SMS_FLAG);
        deliverPIn = PendingIntent.getBroadcast(this, 0, deliverIn, 0);

        // Регистрация на оповещения об отправке и доставке СМС
        if(sentReceiver == null) {
            sentReceiver = new SentReceiver();
            registerReceiver(sentReceiver, new IntentFilter(SENT_SMS_FLAG));
        }

        Bundle extras = intent.getExtras();
        long TIME = extras.getLong("utime");

        if(intent.getAction().equalsIgnoreCase(SET_ALARM)){
            AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, InformerService.class);
            long UTIME = TIME + 2 * 60 * 1000;
            i.putExtra("utime", UTIME);
            i.setAction(InformerService.RUN_ALARM);

            PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            mgr.cancel(pi);
            mgr.set(AlarmManager.RTC_WAKEUP, TIME, pi);

            if (DEBUG_LOG) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(UTIME);
                Log.d(TAG_LOG, "Сигнализация установлена: время = " + getDateStr(cal) + "( " + UTIME + " )");
            }
        } else if (intent.getAction().equalsIgnoreCase(RUN_ALARM)) { // Сигнализация сработала!
            if (DEBUG_LOG) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(TIME);
                Log.d(TAG_LOG, "Аларм! время = " + getDateStr(cal) + "( " + TIME + " )");
            }

            try {
                // Проверяем почту
                MailReader reader = new MailReader(this, "gamza@cbs38.ru", "Jnrhjqcz");

                // Если получили письма, отправляем СМС
                if(reader.readMail()){
                    SMSSend sms = new SMSSend(this, sentPIn, deliverPIn);
                    sms.send();
                }

                //Log.d("SendMail", "read ok");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Ставим новую задачу
            AlarmReceiver.scheduleAlarms(this, TIME);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
       if(sentReceiver != null) {
           unregisterReceiver(sentReceiver);
           sentReceiver = null;
       }
    }
}
