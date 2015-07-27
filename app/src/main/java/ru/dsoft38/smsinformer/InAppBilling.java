package ru.dsoft38.smsinformer;

import android.content.Context;
import android.util.Log;

import ru.dsoft38.smsinformer.util.IabHelper;
import ru.dsoft38.smsinformer.util.IabResult;
import ru.dsoft38.smsinformer.util.Inventory;
import ru.dsoft38.smsinformer.util.Purchase;

/**
 * Created by diesel on 24.07.2015.
 */
public class InAppBilling {

    // id вашей покупки из админки в Google Play
    static final String SKU_TEST = "com.example.buttonclick";
    static final String SKU_ONE_MONTH = "license_for_one_month";
    static final String SKU_ONE_MONTH_TRIAL = "license_for_one_month_trial";
    static final String SKU_ONE_YEAR = "license_for_year";
    static final String SKU_PURCHASE = "license_purchase_app";

    static final  String TAG = "InAppBillingActivity";

    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7QGiMiAHNPx59pir0bKmJeGB3DQ2BVL3emDFyUZAB9lwnZTMNdsxlmpRR3PhH+VL/zDL0x6bvsk1Ec8m+L26VxNASBF10yKcpbHpYPIqDSQplq46VZrijVVrxuRS/GT+q1WFCRMdth4hIoMIZ4CdoJvkWfhP5TmBGTLqjSrCmrEIuYfNaZKHhAQ5BamC8aiTMQ5kkv/PG6j6UPmb1c0A7SIAHje7Lc3LFy5bOoDhmRV4LfDyRMORyncs69YTL8P2EuJdnrXMWU+QAmiUumTqbfkEw3RaTK5RPDBHqs1gD99pKtVkmZ9Tj/HRfkYFrFJS8lWJP5fC6qt7alM+y2qwEwIDAQAB";

    IabHelper mHelper;

    public void billingInit(Context context) {
        mHelper = new IabHelper(context, BASE64_PUBLIC_KEY);

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
}
