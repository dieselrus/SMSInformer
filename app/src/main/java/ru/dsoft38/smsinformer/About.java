package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by diesel on 13.07.2015.
 */
public class About extends FragmentActivity {
    static final  String TAG = "About";

    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QGiMiAHNPx59pir0bKmJeGB3DQ2BVL3emDFyUZAB9lwnZTMNdsxlmpRR3PhH+VL/zDL0x6bvsk1Ec8m+L26VxNASBF10yKcpbHpYPIqDSQplq46VZrijVVrxuRS/GT+q1WFCRMdth4hIoMIZ4CdoJvkWfhP5TmBGTLqjSrCmrEIuYfNaZKHhAQ5BamC8aiTMQ5kkv/PG6j6UPmb1c0A7SIAHje7Lc3LFy5bOoDhmRV4LfDyRMORyncs69YTL8P2EuJdnrXMWU+QAmiUumTqbfkEw3RaTK5RPDBHqs1gD99pKtVkmZ9Tj/HRfkYFrFJS8lWJP5fC6qt7alM+y2qwEwIDAQAB";

    // id вашей покупки из админки в Google Play
    static final String SKU_TEST = "com.example.buttonclick";
    static final String SKU_ONE_MONTH = "license_for_one_month";
    static final String SKU_ONE_MONTH_TRIAL = "license_for_one_month_trial";
    static final String SKU_ONE_YEAR = "license_for_year";
    static final String SKU_PURCHASE = "license_purchase_app";

    IInAppBillingService mService;
    ServiceConnection mServiceConn;

    private Button btnPurchase;
    private PurchaseDialog myDialogFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.about);

        tv.setGravity(Gravity.CENTER);
        tv.setText(Html.fromHtml("<html><body><h1>&nbsp;</h1><p style=\"text-align: center;\"><strong>О программе</strong></p>" +
                "<p>Программа предназначена для трансляции определенных электронных писем на телефоны посредством СМС сообщений.</p>" +
                "<p>&nbsp;</p><p><strong>Автор: </strong>Гамза Денис.</p><p><strong>E-mail: </strong><a href=\"mailto:denis.gamza@gmail.com\">denis.gamza@gmail.com</a></p>" +
                "</body></html>"));

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        Button btnActivation = (Button) findViewById(R.id.btnActivation);
        btnActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                PurchaseDialog myDialogFragment = new PurchaseDialog();
                myDialogFragment.show(manager, "dialog");
            }
        });

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }
        };

        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        btnPurchase = (Button) findViewById(R.id.btnActivation);
        btnPurchase.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                myDialogFragment = new PurchaseDialog();
                myDialogFragment.show(manager, "dialog");

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);

                    String sku = jo.getString(SKU_TEST);
                    Toast.makeText(
                            About.this,
                            "You have bought the " + sku
                                    + ". Excellent choice,adventurer!",
                            Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    System.out.println("Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    private  void buy(){
        ArrayList skuList = new ArrayList();
        skuList.add(SKU_TEST);
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        Bundle skuDetails;
        try {
            Log.d(TAG, getPackageName());
            skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);

            int response = skuDetails.getInt("RESPONSE_CODE");
            Log.d(TAG, response + "");
            if (response == 0) {

                ArrayList<String> responseList = skuDetails
                        .getStringArrayList("DETAILS_LIST");

                for (String thisResponse : responseList) {
                    JSONObject object = new JSONObject(thisResponse);
                    String sku = object.getString("productId");
                    String price = object.getString("price");

                    if (sku.equals(SKU_TEST)) {
                        System.out.println("price " + price);
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku,
                                "inapp",
                                "NiNt36FV@FWk3IsONV~bXqLicoIdmfY$pLt~jah8Lno~");
                        PendingIntent pendingIntent = buyIntentBundle
                                .getParcelable("BUY_INTENT");
                        startIntentSenderForResult(
                                pendingIntent.getIntentSender(), 1001,
                                new Intent(), Integer.valueOf(0),
                                Integer.valueOf(0), Integer.valueOf(0));
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }

    }

    public void okClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку OK!",
                Toast.LENGTH_LONG).show();
    }

    public void cancelClicked() {
        Toast.makeText(getApplicationContext(), "Вы выбрали кнопку отмены!",
                Toast.LENGTH_LONG).show();
        myDialogFragment.dismiss();
    }
}
