package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.app.AlertDialog;
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
    static final  String TAG = "InAppBillingActivity";

    static final String SKU_ONE_MONTH = "license_for_one_month";
    static final String SKU_ONE_MONTH_TRIAL = "license_for_one_month_trial";
    static final String SKU_ONE_YEAR = "license_for_year";
    static final String SKU_PURCHASE = "license_purchase_app";

    private Button btnMonth = null;
    private Button btnYear = null;
    private Button btnPurchase = null;

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
        //mHelper.enableDebugLogging(true);

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







        btnMonth = (Button) findViewById(R.id.btnMonth);
        btnYear = (Button) findViewById(R.id.btnYear);
        btnPurchase = (Button) findViewById(R.id.btnPurchase);

    }


    // User clicked the "Month" button
    public void onBuyMonthButtonClicked(View arg0) {
        Log.d(TAG, "Buy gas button clicked.");

        if (Pref.mSubscribedToMonth) {
            //complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
            return;
        }

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        //setWaitScreen(true);
        Log.d(TAG, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(this, SKU_ONE_MONTH, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // User clicked the "Year" button
    public void onBuyYearButtonClicked(View arg0) {
        Log.d(TAG, "Buy gas button clicked.");

        if (Pref.mSubscribedToYear) {
            //complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
            return;
        }

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener
        //setWaitScreen(true);
        Log.d(TAG, "Launching purchase flow for gas.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(this, SKU_ONE_YEAR, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    // User clicked the "Upgrade to Premium" button.
    public void onPurchaseAppButtonClicked(View arg0) {
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");
        setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(this, SKU_PURCHASE, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == 1001) {
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
        }*/

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
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
            }

            Purchase purchase_one_month = inventory.getPurchase(SKU_ONE_MONTH);
            if(purchase_one_month != null && verifyDeveloperPayload(purchase_one_month)){
                Pref.mSubscribedToMonth = true;
                Pref.lic = Pref.License.ONE_MONTH;
            }

            Purchase purchase_one_month_trial = inventory.getPurchase(SKU_ONE_MONTH_TRIAL);
            if(purchase_one_month_trial != null && verifyDeveloperPayload(purchase_one_month_trial)){
                Pref.lic = Pref.License.ONE_MONTH_TRIAL;
            }

            Purchase purchase_year = inventory.getPurchase(SKU_ONE_YEAR);
            if(purchase_year != null && verifyDeveloperPayload(purchase_year)){
                Pref.mSubscribedToYear = true;
                Pref.lic = Pref.License.ONE_YEAR;
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
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }

            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            /*if (purchase.getSku().equals(SKU_GAS)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }
            else*/ if (purchase.getSku().equals(SKU_PURCHASE)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                Pref.mIsPurchase = true;
                Pref.lic = Pref.License.PURCHASE;
                updateUi();
                setWaitScreen(false);
            }
            else if (purchase.getSku().equals(SKU_ONE_MONTH)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Infinite month subscription purchased.");
                alert("Thank you for subscribing to month!");
                Pref.mSubscribedToMonth = true;
                Pref.lic = Pref.License.ONE_MONTH;
                //mTank = TANK_MAX;
                updateUi();
                setWaitScreen(false);
            }
            else if (purchase.getSku().equals(SKU_ONE_YEAR)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Infinite year subscription purchased.");
                alert("Thank you for subscribing to year!");
                Pref.mSubscribedToYear = true;
                Pref.lic = Pref.License.ONE_YEAR;
                //mTank = TANK_MAX;
                updateUi();
                setWaitScreen(false);
            }

            Pref.checkLicense = true;

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                //mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
                //saveData();
                //alert("You filled 1/4 tank. Your tank is now " + String.valueOf(mTank) + "/4 full!");
            }
            else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        //findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        //findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        Log.e(TAG, "**** SMS Informer Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

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
        //btnPurchase.setVisibility(mIsPurchase ? View.GONE : View.VISIBLE);
        //btnMonth.setVisibility(mSubscribedToMonth ? View.GONE : View.VISIBLE);
        //btnYear.setVisibility(mSubscribedToYear ? View.GONE : View.VISIBLE);

        btnPurchase.setEnabled(!Pref.mIsPurchase);
        btnMonth.setEnabled(!(Pref.mSubscribedToMonth | Pref.mSubscribedToYear));
        btnYear.setEnabled(!(Pref.mSubscribedToMonth | Pref.mSubscribedToYear));

        // "Upgrade" button is only visible if the user is not premium
        //findViewById(R.id.upgrade_button).setVisibility(mIsPremium ? View.GONE : View.VISIBLE);

        // "Get infinite gas" button is only visible if the user is not subscribed yet
        //rbtnMonth.setVisibility(mSubscribedToMonth ? View.GONE : View.VISIBLE);

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
