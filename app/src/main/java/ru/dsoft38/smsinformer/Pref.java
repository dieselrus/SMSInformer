package ru.dsoft38.smsinformer;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by diesel on 12.07.15.
 */
public class Pref {
    public static String prefMailHost       = "";
    public static String prefMailAddress    = "";
    public static String prefMailPassword   = "";
    public static String prefMailProtocol   = "";

    public static int prefSyncFrequency         = -1;
    public static String prefSendSMSTimeFirst   = "09:00";
    public static String prefSendSMSTimeLast    = "20:00";

    public static int prefSendSMSHourFirst      = 9;
    public static int prefSendSMSMinuteFirst    = 0;
    public static int prefSendSMSHourLast       = 20;
    public static int prefSendSMSMinuteLast     = 0;

    //private Context context;

    public Pref(Context context){
        //this.context = context;
    }

    public static void getPref(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        prefMailHost        = pref.getString("mail_host", "");
        prefMailAddress     = pref.getString("mail_address", "");
        prefMailPassword    = pref.getString("mail_password", "");
        if(Integer.parseInt(pref.getString("mail_store_protocol", "0")) == 0) {
            prefMailProtocol = "imap";
        } else {
            prefMailProtocol = "pop3";
        }

        prefSyncFrequency = Integer.parseInt(pref.getString("sync_frequency", "-1"));
        prefSendSMSTimeFirst    = pref.getString("time_sms_send_first", "09:00");
        prefSendSMSTimeLast     = pref.getString("time_sms_send_last", "20:00");

        prefSendSMSHourFirst      = Integer.parseInt(prefSendSMSTimeFirst.split(":")[0]);
        prefSendSMSMinuteFirst    = Integer.parseInt(prefSendSMSTimeFirst.split(":")[1]);
        prefSendSMSHourLast       = Integer.parseInt(prefSendSMSTimeLast.split(":")[0]);
        prefSendSMSMinuteLast     = Integer.parseInt(prefSendSMSTimeLast.split(":")[1]);
    }

}
