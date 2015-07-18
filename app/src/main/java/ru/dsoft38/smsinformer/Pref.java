package ru.dsoft38.smsinformer;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by diesel on 12.07.15.
 */
public class Pref {
    public static String prefMailHost       = "";
    public static String prefMailUser       = "";
    public static String prefMailPassword   = "";
    public static String prefMailProtocol   = "";
    public static String prefMailSubject    = "";
    public static int prefMailPort          = 993;

    public static int prefSyncFrequency             = -1;
    public static String prefSendSMSTimeFirst       = "09:00";
    public static String prefSendSMSTimeLast        = "20:00";
    public static boolean prefEnableReadMailTime    = true;
    public static boolean prefMailEnableSSL         = true;

    public static boolean prefSMSTextCut    = true;
    public static int prefMaxSMSCount       = 3;

    //private Context context;

    public Pref(Context context){
        //this.context = context;
    }

    public static void getPref(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        prefMailHost        = pref.getString("mail_host", "");
        prefMailUser        = pref.getString("mail_user", "");
        prefMailPassword    = pref.getString("mail_password", "");
        prefMailSubject     = pref.getString("mail_subject", "SMSINFORMER");
        prefMailPort        = Integer.parseInt(pref.getString("mail_port", "993"));
        prefMailEnableSSL   = pref.getBoolean("mail_enable_ssl", true);

        if(Integer.parseInt(pref.getString("mail_store_protocol", "0")) == 0) {
            prefMailProtocol = "imaps";
        } else {
            prefMailProtocol = "pop3";
        }


        prefSyncFrequency       = Integer.parseInt(pref.getString("sync_frequency", "-1"));
        prefSendSMSTimeFirst    = pref.getString("time_sms_send_first", "09:00");
        prefSendSMSTimeLast     = pref.getString("time_sms_send_last", "20:00");
        prefEnableReadMailTime  = pref.getBoolean("enable_reader_time", true);

        prefSMSTextCut  = pref.getBoolean("enable_cut_sms_text", true);
        prefMaxSMSCount = Integer.parseInt(pref.getString("max_sms_count_in_one_mail", "993"));

    }

}
