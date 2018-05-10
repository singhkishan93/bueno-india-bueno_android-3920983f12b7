package com.buenodelivery.kitchen.views.font;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.buenodelivery.kitchen.utils.Config;

/**
 * Created by navjot on 11/1/16.
 */
public class CustomFontButton extends Button {

    public CustomFontButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFont.parseAttributes(this, context, attrs, Config.Fonts.FONT_ROBOTO_REGULAR);
    }

    public CustomFontButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CustomFont.parseAttributes(this, context, attrs, Config.Fonts.FONT_ROBOTO_REGULAR);
    }

    public CustomFontButton(Context context) {
        super(context);
    }
}
