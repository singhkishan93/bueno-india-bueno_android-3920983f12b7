package com.bueno.kitchen.views.font;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bueno.kitchen.utils.Config;

/**
 * Created by navjot on 11/1/16.
 */
public class CustomFontTextView extends TextView {

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFont.parseAttributes(this, context, attrs, Config.Fonts.FONT_ROBOTO_REGULAR);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        CustomFont.parseAttributes(this, context, attrs, Config.Fonts.FONT_ROBOTO_REGULAR);
    }

    public CustomFontTextView(Context context) {
        super(context);
        CustomFont.setCustomTypeface(this, Config.Fonts.FONT_ROBOTO_REGULAR, context);
    }

}
