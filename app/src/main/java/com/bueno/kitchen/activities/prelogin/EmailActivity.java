package com.bueno.kitchen.activities.prelogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.bueno.kitchen.R;
import com.bueno.kitchen.core.BaseActivity;
import com.bueno.kitchen.utils.Config;

import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EmailActivity extends BaseActivity {

    @Bind(R.id.email_text_input)
    TextInputLayout emailTextInputLayout;
    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        enableBackButton();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.submit_button)
    public void onClick(View v) {
        if (isValidEmail()) {
            Intent intent = new Intent();
            intent.putExtra(Config.Intents.INTENT_EMAIL, emailEditText.getText().toString().trim());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private boolean isValidEmail() {
        emailTextInputLayout.setErrorEnabled(false);
        String email = emailEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if (pattern.matcher(email).matches()) {
                return true;
            } else {
                emailTextInputLayout.setError("Invalid email address.");
            }
        } else {
            emailTextInputLayout.setError("Email address cannot be blank..");
        }
        emailTextInputLayout.setErrorEnabled(true);
        return false;
    }
}
