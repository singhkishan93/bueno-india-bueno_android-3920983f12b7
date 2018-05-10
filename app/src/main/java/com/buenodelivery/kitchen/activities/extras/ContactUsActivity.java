package com.buenodelivery.kitchen.activities.extras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;
import com.buenodelivery.kitchen.models.response.location.ConfigResponseModel;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUsActivity extends BaseActivity {

    @Bind(R.id.call_text)
    public TextView callTextView;
    @Bind(R.id.email_text)
    public TextView emailTextView;
    private ConfigResponseModel configResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        enableBackButton();
        ButterKnife.bind(this);
        configResponseModel = preferenceManager.getConfiguration();
        if (configResponseModel == null) {
            finish();
            return;
        }
        bindUI();
    }

    private void bindUI() {
        callTextView.setText(configResponseModel.contactUsPhone);
        emailTextView.setText(configResponseModel.contactUsEmail);
    }

    @OnClick({R.id.call_container, R.id.email_container})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_container:
                Uri call = Uri.parse("tel:" + configResponseModel.contactUsPhone);
                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                startActivity(surf);
                break;
            case R.id.email_container:
                String[] TO = {configResponseModel.contactUsEmail};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);

                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Bueno");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
