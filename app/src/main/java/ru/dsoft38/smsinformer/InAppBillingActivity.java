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
    static final String SKU_TEST = "com.example.buttonclick";
    static final  String TAG = "InAppBillingActivity";

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

            Purchase purchase = inventory.getPurchase(SKU_TEST);
/*                PreferencesHelper.savePurchase(
                        context,
                        PreferencesHelper.Purchase.DISABLE_ADS,
                        purchase != null && verifyDeveloperPayload(purchase));
                ads.show(!PreferencesHelper.isAdsDisabled());*/

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

            if (purchase.getSku().equals(SKU_TEST)) {

                Log.d(TAG, "Purchase for disabling ads done. Congratulating user.");
                Toast.makeText(getApplicationContext(), "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
                // сохраняем в настройках, что отключили рекламу
                //PreferencesHelper.savePurchase(context, PreferencesHelper.Purchase.DISABLE_ADS, true);
                // отключаем рекламу
                //ads.show(!PreferencesHelper.isAdsDisabled());
            }

        }
    };

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
