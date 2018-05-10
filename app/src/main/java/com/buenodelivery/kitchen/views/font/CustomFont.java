package com.buenodelivery.kitchen.views.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.buenodelivery.kitchen.R;
import com.buenodelivery.kitchen.utils.Config;

/**
 * Created by navjot on 11/1/16.
 */
public class CustomFont {

    private static final int ROBOTO_BOLD = 1;
    private static final int ROBOTO_REGULAR = 2;

    private static CustomFont ourInstance = new CustomFont();

    public static CustomFont getInstance() {
        return ourInstance;
    }

    public static void setCustomTypeface(TextView textView, String fontString, Context context) {
        textView.setTypeface(FontCache.get(fontString, context));
    }

    public static void parseAttributes(TextView textView, Context context, AttributeSet attrs, String defaultFont) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomFontView);
        int typeface = values.getInt(R.styleable.CustomFontView_typeface, -1);
        switch (typeface) {
            case ROBOTO_BOLD:
                setCustomTypeface(textView, Config.Fonts.FONT_ROBOTO_BOLD, context);
                break;
            case ROBOTO_REGULAR:
                setCustomTypeface(textView, Config.Fonts.FONT_ROBOTO_REGULAR, context);
                break;
            default:
                setCustomTypeface(textView, defaultFont, context);
                break;
        }
        values.recycle();
    }
}
