package ru.dsoft38.smsinformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import ru.dsoft38.smsinformer.util.IabHelper;
import ru.dsoft38.smsinformer.util.IabResult;
import ru.dsoft38.smsinformer.util.Inventory;
import ru.dsoft38.smsinformer.util.Purchase;

/**
 * Created by diesel on 22.07.2015.
 */
public class InAppBillingActivity extends Activity {
    // id вашей покупки из админки в Google Play
    static final String SKU_ADS_DISABLE = "license_for_one_month";

    // public key из админки Google Play
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QGiMiAHNPx59pir0bKmJeGB3DQ2BVL3emDFyUZAB9lwnZTMNdsxlmpRR3PhH+VL/zDL0x6bvsk1Ec8m+L26VxNASBF10yKcpbHpYPIqDSQplq46VZrijVVrxuRS/GT+q1WFCRMdth4hIoMIZ4CdoJvkWfhP5TmBGTLqjSrCmrEIuYfNaZKHhAQ5BamC8aiTMQ5kkv/PG6j6UPmb1c0A7SIAHje7Lc3LFy5bOoDhmRV4LfDyRMORyncs69YTL8P2EuJdnrXMWU+QAmiUumTqbfkEw3RaTK5RPDBHqs1gD99pKtVkmZ9Tj/HRfkYFrFJS8lWJP5fC6qt7alM+y2qwEwIDAQAB";
    private static final String TAG = "purchasefordisablingadvertising";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    IabHelper mHelper;

    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // грузим настройки
        //PreferencesHelper.loadSettings(this);

        layout = new RelativeLayout(this);
        Button btn = new Button(this);
        //btn.setText(getResources().getString(R.string.disable_ads));
        btn.setText("Buy");

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //if (!PreferencesHelper.isAdsDisabled())
                buy();
            }
        });
        layout.addView(btn);
        //context = this;

        // инициализация билинга
        billingInit();
        //ads = new AdMobController(this, layout);
        setContentView(layout);

        // если отключили рекламу, то не будем показывать
        //ads.show(!PreferencesHelper.isAdsDisabled());
    }

    private void buy() {
        //if (!PreferencesHelper.isAdsDisabled()) {
			/*
			 * для безопасности сгенерьте payload для верификации. В данном
			 * примере просто пустая строка юзается. Но в реальном приложение
			 * подходить к этому шагу с умом.
			 */
            String payload = "";
            mHelper.launchPurchaseFlow(this, SKU_ADS_DISABLE, RC_REQUEST,
                    mPurchaseFinishedListener, payload);
        //}
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
        private static final String TAG = "QueryInventoryFinishedListener";

        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

			/*
			 * Проверяются покупки. Обратите внимание, что надо проверить каждую
			 * покупку, чтобы убедиться, что всё норм! см.
			 * verifyDeveloperPayload().
			 */

            /*
            Purchase purchase = inventory.getPurchase(SKU_ADS_DISABLE);
            PreferencesHelper.savePurchase(context,
                    PreferencesHelper.Purchase.DISABLE_ADS, purchase != null
                            && verifyDeveloperPayload(purchase));
            ads.show(!PreferencesHelper.isAdsDisabled());
            */
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
		/*
		 * TODO: здесь необходимо свою верификацию реализовать Хорошо бы ещё с
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

            if (purchase.getSku().equals(SKU_ADS_DISABLE)) {

                /*
                LOG.d(TAG, "Purchase for disabling ads done. Congratulating user.");
                Toast.makeText(getApplicationContext(), "Purchase for disabling ads done.", Toast.LENGTH_SHORT);
                // сохраняем в настройках, что отключили рекламу
                PreferencesHelper.savePurchase(context, PreferencesHelper.Purchase.DISABLE_ADS, true);
                // отключаем рекламу
                ads.show(!PreferencesHelper.isAdsDisabled());
                */
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //if (ads != null)
        //    ads.onDestroy();

        if (mHelper != null)
            mHelper.dispose();
        mHelper = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (ads != null)
        //    ads.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //if (ads != null)
        //    ads.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (ads != null)
        //    ads.onStop();

    }
}
