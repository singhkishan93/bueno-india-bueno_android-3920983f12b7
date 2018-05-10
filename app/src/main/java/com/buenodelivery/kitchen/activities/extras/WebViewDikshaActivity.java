package com.buenodelivery.kitchen.activities.extras;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.core.BaseActivity;

import butterknife.ButterKnife;

public class WebViewDikshaActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_diksha);
        ButterKnife.bind(this);
        enableBackButton();

      TextView myClickableUrl = (TextView) findViewById(R.id.TextView1);

        myClickableUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dikshaschoolindia.org/"));
                startActivity(browserIntent);
            }
        });
//        myClickableUrl.setText("view more");
//        Linkify.addLinks(myClickableUrl, Linkify.WEB_URLS);
    }

}


