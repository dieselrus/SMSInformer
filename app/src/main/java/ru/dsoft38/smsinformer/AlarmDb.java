package ru.dsoft38.smsinformer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.Calendar;

public class AlarmDb {

    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase database;
    private static DatabaseOpenHelper databaseOpenHelper;

    private static final String TB_SEND_SMS = "tbSendSms";
    private static final String TB_LOG = "tbLog";
    private static final String SQL_CREATE_TB_SEND_SMS = "CREATE TABLE " + TB_SEND_SMS +
            " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, phones TEXT, groupid TEXT, msg TEXT );";
    private static final String SQL_CREATE_TB_LOG = "CREATE TABLE " + TB_LOG +
            " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, datetime TEXT, text TEXT );";

    public static void insertAlarm(String NUMBER, String GROUPID, String MSG) {
        ContentValues data = new ContentValues();
        data.put("phones", NUMBER);
        data.put("groupid", GROUPID);
        data.put("msg", MSG);

        if(!database.isOpen())
            open();
        database.insert(TB_SEND_SMS, null, data);
        //close();
    }

    public static Cursor select_SMS_DATA() {
        //return database.rawQuery("SELECT _id, phones, groupid, msg FROM tbSendSMS LIMIT 1", null);
        return database.rawQuery("SELECT _id, phones, groupid, msg FROM tbSendSMS", null);
    }

    public static boolean delete_SMS_DATA(String _id){

        if(!database.isOpen())
            open();
        database.delete(TB_SEND_SMS, "_id = " + _id, null);
        //close();

        return false;
    }

    public static void insertLog(String text) {
        long NOW = Calendar.getInstance().getTimeInMillis();

        ContentValues data = new ContentValues();
        data.put("datetime", String.valueOf(NOW));
        data.put("text", text);

        if(!database.isOpen())
            open();
        database.insert(TB_LOG, null, data);
        //if(database.isOpen())
        //    close();
    }

    public static Cursor select_LOG(String limit, String offset) {
        return database.rawQuery("SELECT _id, datetime, text FROM tbLog LIMIT " + limit + " OFFSET " + offset, null);
        //return database.rawQuery("SELECT _id, datetime, text FROM tbLog ORDER BY datetime DESC LIMIT 100", null);
        //return database.rawQuery("SELECT _id, datetime, module, text FROM tbLog LIMIT 200", null);
    }

    public static Cursor select_LOG_DATE(String limit, String offset,  String dateStart, String dateEnd) {
        return database.rawQuery("SELECT _id, datetime, text FROM tbLog " + " WHERE datetime >= " + dateStart + " and datetime <= " + dateEnd + " LIMIT " + limit + " OFFSET " + offset, null);
        //return database.rawQuery("SELECT _id, datetime, text FROM tbLog ORDER BY datetime DESC LIMIT 100", null);
        //return database.rawQuery("SELECT _id, datetime, module, text FROM tbLog LIMIT 200", null);
    }

    public static Cursor select_smsqueuing() {
        return database.rawQuery("SELECT _id, phones, msg FROM tbSendSms", null);
    }

    public boolean delete_old_log(String _id){

        if(!database.isOpen())
            open();
        database.delete(TB_LOG, "_id = " + _id, null);
        //close();

        return false;
    }

    public static boolean isOpen(){
        return database.isOpen();
    }

    public AlarmDb(Context context) {
        String DATABASE_NAME = "smsinformer";
        String FILE_DIR = "Android"; //"Android/data/ru.dsoft38.smsinformer";
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);

        /*
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + FILE_DIR
                + File.separator + DATABASE_NAME;

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Мы можем читать и писать на неё
            mExternalStorageAvailable = mExternalStorageWriteable = true;
            new File(path).mkdirs();
            databaseOpenHelper = new DatabaseOpenHelper(context, path, null, DATABASE_VERSION);
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Только читать
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        } else {
            // Не можем ничего делать. Возможно карты нет
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        */
    }

    public static void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public static void close() {
        if (database != null) database.close();
        //SQLiteOpenHelper.close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TB_SEND_SMS);
            db.execSQL(SQL_CREATE_TB_LOG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}

