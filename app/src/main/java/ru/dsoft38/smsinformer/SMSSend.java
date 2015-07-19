package ru.dsoft38.smsinformer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;

import java.util.ArrayList;

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
            while (c.moveToNext()) {
                currentID = c.getString(0);
                arrayNum = c.getString(1).split(";");
                smsText = c.getString(3);

                sendingSMS();
            }
        }

        db.close();
        // Запускаем отправку
        //sendingSMS();

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
        String phone = arrayNum[currentSMSNumberIndex].replace("-", "").replace(";", "").replace(" ", "").trim();

        // Проверяем длину номера 11 символов или 12, если с +
        //if (phone.length() != 0 && (phone.length() == 11 || (phone.substring(0, 1).equals("+") && phone.length() == 12))) {
            SmsManager smsManager = SmsManager.getDefault();

            //smsManager.sendTextMessage(phone, null, smsText, sentPIn, deliverPIn);


            ArrayList<String> al_message = new ArrayList<String>();
            al_message = smsManager.divideMessage(smsText);

            ArrayList<PendingIntent> al_piSent = new ArrayList<PendingIntent>();
            ArrayList<PendingIntent> al_piDelivered = new ArrayList<PendingIntent>();

            for (int i = 0; i < al_message.size(); i++)
            {
                Intent sentIntent = new Intent("SMS_SENT");
                //sentIntent.putExtra("PARTS", "Часть: "+i);
                //sentIntent.putExtra("MSG", "Сообщение: "+al_message.get(i));
                PendingIntent pi_sent = PendingIntent.getBroadcast(context, i, sentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                al_piSent.add(sentPIn);

                Intent deliveredIntent = new Intent("SMS_DELIVERED");
                //deliveredIntent.putExtra("PARTS", "Часть: "+i);
                //deliveredIntent.putExtra("MSG", "Сообщение: "+al_message.get(i));
                //PendingIntent pi_delivered = PendingIntent.getBroadcast(this, i, deliveredIntent,
                //        PendingIntent.FLAG_UPDATE_CURRENT);

                al_piDelivered.add(deliverPIn);
            }

            AlarmDb db = new AlarmDb(context);
            db.insertLog(context.toString(), "Отправляется СМС на номер: " + phone + " с текстом: " + al_message + " )");
            db = null;

            smsManager.sendMultipartTextMessage(phone, null, al_message, al_piSent, al_piDelivered);

            smsManager = null;
            phone = null;
        //} else {
            //AlarmDb db = new AlarmDb(context);
            //db.delete_SMS_DATA(SMSSend.currentID);
            //db = null;
        //    currentSMSNumberIndex++;
        //    SMSSend.sendingSMS();
        //}
    }
}
