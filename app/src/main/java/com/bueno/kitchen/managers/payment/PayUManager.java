package com.bueno.kitchen.managers.payment;

import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;

import com.bueno.kitchen.managers.payment.core.PaymentManager;
import com.bueno.kitchen.models.core.PaymentDetailModel;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Model.PostData;
import com.payu.india.Payu.PayuConstants;

/**
 * Created by bedi on 20/03/16.
 * <p/>
 * TODO OnHold
 */
public class PayUManager extends PaymentManager {

    private Context context;


    int merchantIndex = 0;
    //    int env = PayuConstants.MOBILE_STAGING_ENV;
    // in case of production make sure that merchantIndex is fixed as 0 (0MQaQP) for other key's payu server cant generate hash
    int env = PayuConstants.PRODUCTION_ENV;

    String merchantTestKeys[] = {"gtKFFx", "gtKFFx"};
    String merchantTestSalts[] = {"eCwWELxi", "eCwWELxi" };

    String merchantProductionKeys[] = {"0MQaQP", "smsplus"};
    String merchantProductionSalts[] = {"13p0PXZk", "1b1b0",};

    String offerKeys[] = {"test123@6622", "offer_test@ffer_t5172", "offerfranklin@6636"};

    String merchantKey = env == PayuConstants.PRODUCTION_ENV ? merchantProductionKeys[merchantIndex]:merchantTestKeys[merchantIndex];
    //    String merchantSalt = env == PayuConstants.PRODUCTION_ENV ? merchantProductionSalts[merchantIndex] : merchantTestSalts[merchantIndex];
    String mandatoryKeys[] = { PayuConstants.KEY, PayuConstants.AMOUNT, PayuConstants.PRODUCT_INFO, PayuConstants.FIRST_NAME, PayuConstants.EMAIL, PayuConstants.TXNID, PayuConstants.SURL, PayuConstants.FURL, PayuConstants.USER_CREDENTIALS, PayuConstants.UDF1, PayuConstants.UDF2, PayuConstants.UDF3, PayuConstants.UDF4, PayuConstants.UDF5, PayuConstants.ENV};
    String mandatoryValues[] = { merchantKey, "10.0", "myproduct", "firstname", "me@itsmeonly.com", ""+System.currentTimeMillis(), "https://payu.herokuapp.com/success", "https://payu.herokuapp.com/failure", merchantKey+":payutest@payu.in", "udf1", "udf2", "udf3", "udf4", "udf5", ""+env};

    String inputData = "";


    private LinearLayout rowContainerLinearLayout;

    private PayUChecksum checksum;
    private PostData postData;
    private String key;
    private String salt;
    private String var1;
    private Intent intent;
    //    private mPaymentParams mPaymentParams;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private String cardBin;


    public PayUManager(Context context) {
        this.context = context;
    }

    @Override
    public void makePayment(PaymentDetailModel paymentDetailModel) {








        PaymentParams mPaymentParams = new PaymentParams();
        mPaymentParams.setKey("gtKFFx");
    //    mPaymentParams.setAmount(paymentDetailModel.amount);
        mPaymentParams.setAmount("15.0");
        mPaymentParams.setProductInfo(null);
        mPaymentParams.setFirstName("apporio_testing");
        mPaymentParams.setEmail("keshav@apporio.com");
        mPaymentParams.setTxnId("0123479543689");
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        mPaymentParams.setUdf1("udf1l");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");
        mPaymentParams.setHash("your payment hash");

        PayuHashes payuHashes = new PayuHashes();
        Intent intent = new Intent();
        intent.putExtra(PayuConstants.ENV, PayuConstants.PRODUCTION_ENV);
        intent.putExtra(PayuConstants.PAYMENT_DEFAULT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        context.startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
