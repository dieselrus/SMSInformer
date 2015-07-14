package ru.dsoft38.smsinformer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ReceiveService extends Service {

    private final String LOG_TAG = "ReceiveService";

    // Флаги для отправки и доставки SMS
    private static final String SENT_SMS_FLAG = "ru.dsoft38.smsinformer_SENT_SMS";
    private static final String DELIVER_SMS_FLAG = "ru.dsoft38.smsinformer_DELIVER_SMS";
    public static PendingIntent sentPIn = null;
    public static PendingIntent deliverPIn = null;
    private static SentReceiver sentReceiver;

    public ReceiveService() {
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate");

        // Поставил задачу
        AlarmReceiver.scheduleAlarms(this);

        Intent sentIn = new Intent(SENT_SMS_FLAG);
        sentPIn = PendingIntent.getBroadcast(this, 0, sentIn, 0);

        Intent deliverIn = new Intent(DELIVER_SMS_FLAG);
        deliverPIn = PendingIntent.getBroadcast(this, 0, deliverIn, 0);

        sentReceiver = new SentReceiver();

        // Регистрация на оповещения об отправке и доставке СМС
        registerReceiver(sentReceiver, new IntentFilter(SENT_SMS_FLAG));

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(LOG_TAG, "onDestroy");
        // отмена регистрации на оповещение отправки и доставка СМС
        unregisterReceiver(sentReceiver);
        sentReceiver = null;
        //unregisterReceiver(deliverReceiver);
    }
}
