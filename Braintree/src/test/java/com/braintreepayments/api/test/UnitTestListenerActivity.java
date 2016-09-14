package com.braintreepayments.api.test;

import android.app.Activity;

import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.ConfigurationListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.interfaces.PaymentMethodNoncesUpdatedListener;
import com.braintreepayments.api.interfaces.UnionPayListener;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.UnionPayCapabilities;

import org.apache.tools.ant.types.resources.Union;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that implements all listeners used by {@link com.braintreepayments.api.BraintreeFragment}
 * for testing.
 */
public class UnitTestListenerActivity extends Activity implements PaymentMethodNonceCreatedListener,
        PaymentMethodNoncesUpdatedListener, BraintreeErrorListener, ConfigurationListener, BraintreeCancelListener,
        UnionPayListener {

    public final List<Configuration> configurations = new ArrayList<>();

    @Override
    public void onError(Exception error) {}

    @Override
    public void onConfigurationFetched(Configuration configuration) {
        configurations.add(configuration);
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {}

    @Override
    public void onPaymentMethodNoncesUpdated(List<PaymentMethodNonce> paymentMethodNonces) {}

    @Override
    public void onCancel(int requestCode) {}

    @Override
    public void onCapabilitiesFetched(UnionPayCapabilities capabilities) {}

    @Override
    public void onSmsCodeSent(String enrollmentId, boolean smsCodeRequired) {}
}
