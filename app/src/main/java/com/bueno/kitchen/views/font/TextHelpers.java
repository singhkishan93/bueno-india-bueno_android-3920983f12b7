package com.bueno.kitchen.views.font;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

/**
 * Created by navjot on 11/1/15.
 */
public class TextHelpers {

    public static SpannableString textAsCustomFont(String fontName, String text, Context context) {
        SpannableString span = new SpannableString(text);
        span.setSpan(new TypefaceSpan(context, fontName), 0, text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}
