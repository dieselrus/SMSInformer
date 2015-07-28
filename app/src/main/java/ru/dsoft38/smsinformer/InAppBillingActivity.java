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

    // Does the user have the premium upgrade?
    boolean mIsPurchase = false;

    // Does the user have an active subscription to the infinite gas plan?
    boolean mSubscribedToMonth = false;
    boolean mSubscribedToYear = false;

    static final String SKU_ONE_MONTH = "license_for_one_month";
    static final String SKU_ONE_MONTH_TRIAL = "license_for_one_month_trial";
    static final String SKU_ONE_YEAR = "license_for_year";
    static final String SKU_PURCHASE = "license_purchase_app";

    private RadioButton rbtnTrial = null;
    private RadioButton rbtnMonth = null;
    private RadioButton rbtnYear = null;
    private RadioButton rbtnPurchase = null;

    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QGiMiAHNPx59pir0bKmJeGB3DQ2BVL3emDFyUZAB9lwnZTMNdsxlmpRR3PhH+VL/zDL0x6bvsk1Ec8m+L26VxNASBF10yKcpbHpYPIqDSQplq46VZrijVVrxuRS/GT+q1WFCRMdth4hIoMIZ4CdoJvkWfhP5TmBGTLqjSrCmrEIuYfNaZKHhAQ5BamC8aiTMQ5kkv/PG6j6UPmb1c0A7SIAHje7Lc3LFy5bOoDhmRV4LfDyRMORyncs69YTL8P2EuJdnrXMWU+QAmiUumTqbfkEw3RaTK5RPDBHqs1gD99pKtVkmZ9Tj/HRfkYFrFJS8lWJP5fC6qt7alM+y2qwEwIDAQAB";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // The helper object
    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.license_buy);

        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, BASE64_PUBLIC_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    //complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
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

    // Слушатель для востановителя покупок.
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            if (mHelper == null) return;

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
            if(purchase_app != null && verifyDeveloperPayload(purchase_app)){
                Pref.lic = Pref.License.PURCHASE;
                Pref.prefTimeEnd = -1;

                return;
            }

            Purchase purchase_one_month = inventory.getPurchase(SKU_ONE_MONTH);
            if(purchase_one_month != null && verifyDeveloperPayload(purchase_one_month)){
                mSubscribedToMonth = true;
                Pref.lic = Pref.License.ONE_MONTH;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 30 * 24 *60 * 60 * 1000;

                return;
            }

            Purchase purchase_one_month_trial = inventory.getPurchase(SKU_ONE_MONTH_TRIAL);
            if(purchase_one_month_trial != null && verifyDeveloperPayload(purchase_one_month_trial)){
                Pref.lic = Pref.License.ONE_MONTH_TRIAL;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 30 * 24 *60 * 60 * 1000;

                return;
            }

            Purchase purchase_year = inventory.getPurchase(SKU_ONE_YEAR);
            if(purchase_year != null && verifyDeveloperPayload(purchase_year)){
                mSubscribedToYear = true;
                Pref.lic = Pref.License.ONE_YEAR;
                Pref.prefTimeEnd = purchase_app.getPurchaseTime() + 1 * 365 * 24 *60 * 60 * 1000;

                return;
            }

            updateUi();

            //Purchase purchase = inventory.getPurchase(SKU_TEST);
                /*PreferencesHelper.savePurchase(
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



    @Override
    public void onDestroy() {
        super.onDestroy();

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

    // updates UI to reflect model
    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
        rbtnMonth.setVisibility(mIsPurchase ? View.GONE : View.VISIBLE);

        // "Upgrade" button is only visible if the user is not premium
        //findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        // "Get infinite gas" button is only visible if the user is not subscribed yet
        rbtnMonth.setVisibility(mSubscribedToMonth ? View.GONE : View.VISIBLE);

        // update gas gauge to reflect tank status
       /* if (mSubscribedToInfiniteGas) {
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(R.drawable.gas_inf);
        }
        else {
            int index = mTank >= TANK_RES_IDS.length ? TANK_RES_IDS.length - 1 : mTank;
            ((ImageView)findViewById(R.id.gas_gauge)).setImageResource(TANK_RES_IDS[index]);
        }*/
    }
}
