package com.braintreepayments.demo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Window;

import com.braintreepayments.api.Braintree;
import com.braintreepayments.api.BraintreeBroadcastManager;
import com.braintreepayments.api.BraintreeBrowserSwitchActivity;
import com.braintreepayments.api.Braintree.BraintreeSetupFinishedListener;
import com.braintreepayments.api.Braintree.PaymentMethodCreatedListener;
import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.view.PaymentButton;
import com.braintreepayments.api.models.PaymentMethod;
import com.google.android.gms.wallet.Cart;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;

import java.util.Collections;

public class PaymentButtonActivity extends Activity implements PaymentMethodCreatedListener,
        BraintreeSetupFinishedListener {

    private PaymentButton mPaymentButton;

    private BroadcastReceiver mBrowserSwitchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action
                    .equalsIgnoreCase(BraintreeBrowserSwitchActivity.LOCAL_BROADCAST_BROWSER_SWITCH_COMPLETED)) {
                mPaymentButton.onActivityResult(PaymentButton.REQUEST_CODE, intent.getIntExtra(
                        BraintreeBrowserSwitchActivity.BROADCAST_BROWSER_EXTRA_RESULT,
                        Activity.RESULT_OK),
                        intent);
            }
        }
    };

    protected void onCreate(Bundle onSaveInstanceState) {
        super.onCreate(onSaveInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.payment_button);

        // Register the BroadcastReceiver for Coinbase browser switch
        BraintreeBroadcastManager.getInstance(this).registerReceiver(mBrowserSwitchReceiver,
                new IntentFilter(
                        BraintreeBrowserSwitchActivity.LOCAL_BROADCAST_BROWSER_SWITCH_COMPLETED));

        mPaymentButton = (PaymentButton) findViewById(R.id.payment_button);

        Braintree.setup(this, getIntent().getStringExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN),
                this);
    }

    @Override
    public void onBraintreeSetupFinished(boolean setupSuccessful, Braintree braintree,
            String errorMessage, Exception exception) {
        if (setupSuccessful) {
            braintree.addListener(this);

            Cart cart = getIntent().getParcelableExtra(BraintreePaymentActivity.EXTRA_ANDROID_PAY_CART);
            boolean isBillingAgreement = getIntent().getBooleanExtra(BraintreePaymentActivity.EXTRA_ANDROID_PAY_IS_BILLING_AGREEMENT, false);
            boolean shippingAddressRequired = getIntent().getBooleanExtra("shippingAddressRequired", false);
            boolean phoneNumberRequired = getIntent().getBooleanExtra("phoneNumberRequired", false);
            mPaymentButton.setAndroidPayOptions(cart, isBillingAgreement, shippingAddressRequired,
                    phoneNumberRequired);
            boolean payPalAddressScopeRequested = getIntent().getBooleanExtra("payPalAddressScopeRequested", false);
            if (payPalAddressScopeRequested) {
                mPaymentButton.setAdditionalPayPalScopes(
                        Collections.singletonList(PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
            }
            mPaymentButton.initialize(this, braintree);
        } else {
            Intent intent = new Intent()
                    .putExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE, errorMessage);
            setResult(RESULT_FIRST_USER, intent);
            finish();
        }
    }

    protected void onDestroy() {
        BraintreeBroadcastManager.getInstance(this).unregisterReceiver(mBrowserSwitchReceiver);
        super.onDestroy();
    }

    @Override
    public void onPaymentMethodCreated(PaymentMethod paymentMethod) {
        setResult(RESULT_OK, new Intent()
                .putExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD, (Parcelable) paymentMethod));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        if (responseCode == RESULT_OK && requestCode == PaymentButton.REQUEST_CODE) {
            setProgressBarIndeterminateVisibility(true);
            mPaymentButton.onActivityResult(requestCode, responseCode, data);
        } else {
            setProgressBarIndeterminateVisibility(false);
        }
    }
}
