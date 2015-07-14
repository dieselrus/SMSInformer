package ru.dsoft38.smsinformer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;

/**
 * Created by diesel on 10.07.2015.
 */
public class SMSSend {
    private final String LOG_TAG = "Send SMS";

    private static Context context;

    private static PendingIntent sentPIn = null;
    private static PendingIntent deliverPIn = null;

    public static int currentSMSNumberIndex = 0;
    private static String[] arrayNum = null;
    private static String smsText = null;
    public static String currentID = null;

    public SMSSend(Context context, PendingIntent sentPIn, PendingIntent deliverPIn) {

        this.context = context;
        this.sentPIn = sentPIn;
        this.deliverPIn = deliverPIn;
    }

    public void send(){
        AlarmDb db = new AlarmDb(context);
        db.open();
        Cursor c = db.select_SMS_DATA();


        if (c != null) {
            if (c.moveToFirst()) {
                currentID = c.getString(0);
                arrayNum = c.getString(1).split(";");
                smsText = c.getString(3);
            }
        }

        db.close();
        // Запускаем отправку
        sendingSMS();

    }

    public static void sendingSMS(){
        if ( arrayNum == null || currentSMSNumberIndex >= arrayNum.length ) {
            currentSMSNumberIndex = 0;
            arrayNum = null;

            AlarmDb db = new AlarmDb(context);
            db.delete_SMS_DATA(SMSSend.currentID);

            context = null;
            sentPIn = null;
            deliverPIn = null;

            return;
        }

        // Удаляем не нужные символы
        String num = arrayNum[currentSMSNumberIndex].replace("-", "").replace(";", "").replace(" ", "").trim();

        // Проверяем длину номера 11 символов или 12, если с +
        if (num.length() == 11 || (num.substring(0, 1).equals("+") && num.length() == 12)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(num, null, smsText, sentPIn, deliverPIn);
            smsManager = null;
            num = null;
        }
    }
}
