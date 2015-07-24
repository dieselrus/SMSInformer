package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SentReceiver extends BroadcastReceiver {
    private final String LOG_TAG = "SentReceiver";

    public SentReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //AlarmDb db = new AlarmDb(context);

        switch (getResultCode()) {

            case Activity.RESULT_OK:
                Log.d(LOG_TAG, "Сообщение отправлено!");

                AlarmDb.insertLog("Сообщение отправлено!");
                //db = null;

                //SMSSend.currentSMSNumberIndex++;
                SMSSend.sendingSMS();

                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF :
                Log.d(LOG_TAG, "Телефонный модуль выключен!");

                AlarmDb.insertLog("Телефонный модуль выключен!");
                //db = null;

                //sendingSMS();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU :
                Log.d(LOG_TAG, "Возникла проблема, связанная с форматом PDU (protocol description unit)!");

                AlarmDb.insertLog("Возникла проблема, связанная с форматом PDU (protocol description unit)!");
                //db = null;

                //sendingSMS();
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Log.d(LOG_TAG, "При отправке возникли неизвестные проблемы!");

                AlarmDb.insertLog("При отправке возникли неизвестные проблемы!");
                //db = null;

                //sendingSMS();
                break;
            default:
                // sent SMS message failed
                Log.d(LOG_TAG, "Сообщение не отправлено!");

                AlarmDb.insertLog("Сообщение не отправлено по непонятным причинам!");
                //db = null;

                //sendingSMS();
                break;
        }

        //db = null;
    }
}
