package ru.dsoft38.smsinformer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class InformerService extends IntentService {

    static boolean DEBUG_LOG = false;
    private static final String TAG_LOG = "SMSInformer";

    public static final String SET_ALARM = "ru.dsoft38.smsinformer_SET_ALARM";
    public static final String RUN_ALARM = "ru.dsoft38.smsinformer_RUN_ALARM";

    public InformerService() {
        super("InformerService");
    }

    public InformerService(String name) {
        super(name);
    }

    // Определяем подключены ли к интернету
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            if (DEBUG_LOG) {
                Log.d(TAG_LOG, "ONLINE");
            }
            return true;
        }
        else {
            if (DEBUG_LOG) {
                Log.d(TAG_LOG, "OFFLINE");
            }
            return false;
        }
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

        // Если интервал проверки почны меньше или равен нулю (никогда)
        if(Pref.prefSyncFrequency <= 0)
            return;

        Bundle extras = intent.getExtras();
        long TIME = extras.getLong("utime");

        if(intent.getAction().equalsIgnoreCase(SET_ALARM)){

            AlarmManager mgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(this, InformerService.class);
            long UTIME = TIME + Pref.prefSyncFrequency * 60 * 1000;
            i.putExtra("utime", UTIME);
            i.setAction(InformerService.RUN_ALARM);

            PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            mgr.cancel(pi);
            mgr.set(AlarmManager.RTC_WAKEUP, TIME, pi);

            if (DEBUG_LOG) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(UTIME);
                Log.d(TAG_LOG, "Задача установлена на " + getDateStr(cal) + " ( " + UTIME + " )");
            }

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(UTIME);
            //AlarmDb db = new AlarmDb(this);
            AlarmDb.insertLog("Задача установлена на " + getDateStr(cal));

        } else if (intent.getAction().equalsIgnoreCase(RUN_ALARM)) { // Сигнализация сработала!

            if (DEBUG_LOG) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(TIME);
                Log.d(TAG_LOG, "Старт задачи " + getDateStr(cal) + "( " + TIME + " )");
            }

            // Проверяем лицензию, если истек срок или не куплена, выходи
            if(Pref.lic != Pref.License.PURCHASE && Calendar.getInstance().getTimeInMillis() > Pref.prefTimeEnd){
                AlarmDb.insertLog("Необходимо преобрести лицензию!");
                // Ставим новую задачу
                AlarmReceiver.scheduleAlarms(this, TIME);

                return;
            }


            Calendar cal = Calendar.getInstance();
            //AlarmDb db = new AlarmDb(this);
            AlarmDb.insertLog("Старт задачи " + getDateStr(cal));

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                boolean isSplit = false;
                boolean isWithin = false;

                Date dt1 = null, dt2 = null, dt3 = null;

                dt1 = sdf.parse(Pref.prefSendSMSTimeFirst);
                dt2 = sdf.parse(Pref.prefSendSMSTimeLast);
                dt3 = sdf.parse(sdf.format(new Date()));

                isSplit = (dt2.compareTo(dt1) < 0);

                if (DEBUG_LOG) {
                    Log.d(TAG_LOG, "[split]: " + isSplit);
                }

                if (isSplit) {
                    isWithin = (dt3.after(dt1) || dt3.before(dt2));
                } else {
                    isWithin = (dt3.after(dt1) && dt3.before(dt2));
                }

                if (DEBUG_LOG) {
                    Log.d(TAG_LOG, "Is time within interval? " + isWithin);
                }

                MailReader reader;
                boolean bMailReed = false;

                // Если не ограничивать получение почты по времени
                if(isOnline() && !Pref.prefEnableReadMailTime){
                    reader = new MailReader(this, Pref.prefMailUser, Pref.prefMailPassword);
                    bMailReed = reader.readMail();
                }

                if (isWithin) {
                    // Проверяем почту
                    if(isOnline() && Pref.prefEnableReadMailTime && isWithin){
                        reader = new MailReader(this, Pref.prefMailUser, Pref.prefMailPassword);
                        bMailReed = reader.readMail();
                    }

                    // Если получили письма, отправляем СМС
                    if (isOnline() && bMailReed) {

                        SMSSend sms = new SMSSend(this, ReceiveService.sentPIn, ReceiveService.deliverPIn);
                        sms.send();
                        sms = null;

                    } else if (!isOnline()) {
                        if (DEBUG_LOG) {
                            Log.d(TAG_LOG, "Нет подключения к интернет.");
                        }

                        //db = new AlarmDb(this);
                        AlarmDb.insertLog("Нет подключения к интернет.");
                        //db = null;
                    } else if (!bMailReed) {
                        if (DEBUG_LOG) {
                            Log.d(TAG_LOG, "Проблемы с чтением почты.");
                        }
                        //db = new AlarmDb(this);
                        AlarmDb.insertLog("Проблемы с чтением почты.");
                        //db = null;
                    }

                }

                reader = null;

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
    }
}
