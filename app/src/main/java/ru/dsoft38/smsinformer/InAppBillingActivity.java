package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.dsoft38.smsinformer.util.IabHelper;
import ru.dsoft38.smsinformer.util.IabResult;
import ru.dsoft38.smsinformer.util.Inventory;
import ru.dsoft38.smsinformer.util.Purchase;

/**
 * Created by diesel on 22.07.2015.
 */
public class InAppBillingActivity extends Activity {
    // id вашей покупки из админки в Google Play
    private static String LICENSE_STRING = "license_for_one_month_trial";
    static final  String TAG = "InAppBillingActivity";

    static final String SKU_TEST = "com.example.buttonclick";
    static final String SKU_ONE_MONTH = "license_for_one_month";
    static final String SKU_ONE_MONTH_TRIAL = "license_for_one_month_trial";
    static final String SKU_ONE_YEAR = "license_for_year";
    static final String SKU_PURCHASE = "license_purchase_app";

    private RadioButton rbtnTrial = null;
    private RadioButton rbtnMonth = null;
    private RadioButton rbtnYear = null;
    private RadioButton rbtnPurchase = null;

    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QGiMiAHNPx59pir0bKmJeGB3DQ2BVL3emDFyUZAB9lwnZTMNdsxlmpRR3PhH+VL/zDL0x6bvsk1Ec8m+L26VxNASBF10yKcpbHpYPIqDSQplq46VZrijVVrxuRS/GT+q1WFCRMdth4hIoMIZ4CdoJvkWfhP5TmBGTLqjSrCmrEIuYfNaZKHhAQ5BamC8aiTMQ5kkv/PG6j6UPmb1c0A7SIAHje7Lc3LFy5bOoDhmRV4LfDyRMORyncs69YTL8P2EuJdnrXMWU+QAmiUumTqbfkEw3RaTK5RPDBHqs1gD99pKtVkmZ9Tj/HRfkYFrFJS8lWJP5fC6qt7alM+y2qwEwIDAQAB";

    IInAppBillingService mService;
    ServiceConnection mServiceConn;
    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_buy);

        mHelper = new IabHelper(this, BASE64_PUBLIC_KEY);

        billingInit();

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

        Button purchaseBtn = (Button) findViewById(R.id.btnBuy);
        purchaseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });

        rbtnTrial = (RadioButton) findViewById(R.id.rbtTrial);
        rbtnTrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnTrial.setChecked(true);
                rbtnMonth.setChecked(false);
                rbtnYear.setChecked(false);
                rbtnPurchase.setChecked(false);

                LICENSE_STRING = "license_for_one_month_trial";
            }
        });

        rbtnMonth = (RadioButton) findViewById(R.id.rbtMonth);
        rbtnMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnTrial.setChecked(false);
                rbtnMonth.setChecked(true);
                rbtnYear.setChecked(false);
                rbtnPurchase.setChecked(false);

                LICENSE_STRING = "license_for_one_month";
            }
        });

        rbtnYear = (RadioButton) findViewById(R.id.rbtYear);
        rbtnYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnTrial.setChecked(false);
                rbtnMonth.setChecked(false);
                rbtnYear.setChecked(true);
                rbtnPurchase.setChecked(false);

                LICENSE_STRING = "license_for_year";
            }
        });

        rbtnPurchase = (RadioButton) findViewById(R.id.rbtPurchase);
        rbtnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbtnTrial.setChecked(false);
                rbtnMonth.setChecked(false);
                rbtnYear.setChecked(false);
                rbtnPurchase.setChecked(true);

                LICENSE_STRING = "license_purchase_app";
            }
        });

        Button btnOk = (Button) findViewById(R.id.btnBuy);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy(LICENSE_STRING);
            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((About) getActivity()).cancelClicked();
                try {
                    this.finalize();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
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

                    String sku = jo.getString(LICENSE_STRING);
                    Toast.makeText(
                            InAppBillingActivity.this,
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

    private void billingInit() {
        mHelper = new IabHelper(this, BASE64_PUBLIC_KEY);

        // включаем дебагинг (в релизной версии ОБЯЗАТЕЛЬНО выставьте в false)
        mHelper.enableDebugLogging(true);

        // инициализируем; запрос асинхронен
        // будет вызван, когда инициализация завершится
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }

                // чекаем уже купленное
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    // Слушатель для востановителя покупок.
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Проверяются покупки.
             * Обратите внимание, что надо проверить каждую покупку, чтобы убедиться, что всё норм!
             * см. verifyDeveloperPayload().
             */

            Purchase purchase_app = inventory.getPurchase(SKU_PURCHASE);
            if(purchase_app != null){
                Pref.lic = Pref.License.PURCHASE;
                Pref.prefTimeEnd = -1;

                return;
            }

            Purchase purchase_one_month = inventory.getPurchase(SKU_ONE_MONTH);
            if(purchase_app != null){
                Pref.lic = Pref.License.ONE_MONTH;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 30 * 24 *60 * 60 * 1000;

                return;
            }

            Purchase purchase_one_month_trial = inventory.getPurchase(SKU_ONE_MONTH_TRIAL);
            if(purchase_app != null){
                Pref.lic = Pref.License.ONE_MONTH_TRIAL;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 30 * 24 *60 * 60 * 1000;

                return;
            }

            Purchase purchase_year = inventory.getPurchase(SKU_ONE_YEAR);
            if(purchase_app != null){
                Pref.lic = Pref.License.ONE_YEAR;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 1 * 365 * 24 *60 * 60 * 1000;

                return;
            }

            Purchase purchase = inventory.getPurchase(SKU_TEST);
                /*PreferencesHelper.savePurchase(
                        context,
                        PreferencesHelper.Purchase.DISABLE_ADS,
                        purchase != null && verifyDeveloperPayload(purchase));
                ads.show(!PreferencesHelper.isAdsDisabled());*/
            if(purchase != null){
                Pref.lic = Pref.License.PURCHASE;
                Pref.prefTimeEnd = -1;

                return;
            }
        }
    };

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
		/*
		 * здесь необходимо свою верификацию реализовать Хорошо бы ещё с
		 * использованием собственного стороннего сервера.
		 */

        return true;
    }

    // Прокает, когда покупка завершена
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);
            if (result.isFailure()) {
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(LICENSE_STRING)) {

                Log.d(TAG, "Purchase for disabling ads done. Congratulating user.");
                Toast.makeText(getApplicationContext(), "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
                // сохраняем в настройках, что отключили рекламу
                //PreferencesHelper.savePurchase(context, PreferencesHelper.Purchase.DISABLE_ADS, true);
                // отключаем рекламу
                //ads.show(!PreferencesHelper.isAdsDisabled());
            }

        }
    };

    private void buy(String LICENSE){
        ArrayList skuList = new ArrayList();
        skuList.add(LICENSE);
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

                    if (sku.equals(LICENSE)) {
                        System.out.println("price " + price);
                        Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku,
                                "inapp",
                                "");
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
