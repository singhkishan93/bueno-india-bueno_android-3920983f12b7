package com.buenodelivery.kitchen.utils;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.Toast;

import com.buenodelivery.kitchen.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by navjot on 30/10/15.
 */
public class UtilitySingleton {

    private static int displayWidth;
    private Context appContext;

    @Inject
    public UtilitySingleton(Context appContext) {
        this.appContext = appContext;
    }

    public static int ordinalIndexOf(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos + 1);
        return pos;
    }

    public boolean isEditTextEmpty(EditText editText, String field) {
        if (editText == null || TextUtils.isEmpty(editText.getText())) {
            ShowToast(field + " cannot be empty.");
            return true;
        } else {
            return false;
        }
    }

    public int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public String generateRandomOTP() {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        String result = "";
        for (int i = 0; i < 4; i++) {
            result += numbers.get(i).toString();
        }
        return result;
    }

    public final boolean isValidEmail(EditText target) {
        boolean isValidEmail;
        isValidEmail = !(target == null || target.getText() == null) && android.util.Patterns.EMAIL_ADDRESS.matcher(target.getText()).matches();
        if (!isValidEmail) {
            ShowToast("Please type a valid email address");
        }
        return isValidEmail;
    }

    public void shareText(Context context, String body, String subject) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Using"));
    }

    public AlertDialog showAlertDialog(Context context, String title, String message) {
        return showAlertDialog(context, title, message, null);
    }

    public AlertDialog showAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        return builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", onClickListener)
                .setNegativeButton("Dismiss", null)
                .show();
    }

    public static float calculateDistance(Location myLocation,
                                          String latitude,
                                          String longitude) {
        float distance;
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            float[] distFloats = new float[1];
            Location.distanceBetween(myLocation.getLatitude(),
                    myLocation.getLongitude(),
                    Double.valueOf(latitude),
                    Double.valueOf(longitude),
                    distFloats);
            distance = distFloats[0];
        } else {
            distance = 10000000;
        }
        return distance;
    }

    public void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Offer", text);
        clipboard.setPrimaryClip(clip);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        ShowToast(R.string.toast_network_not_available);
        return false;
    }

    public static int getDisplayWidth(Context context) {
        if (displayWidth == 0) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            displayWidth = metrics.widthPixels;
        }
        return displayWidth;
    }

    public ProgressDialog getDialog(Context context, boolean show) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        if (show) {
            progressDialog.show();
        }
        return progressDialog;
    }

    /**
     * Description : Message String
     */
    public void ShowToast(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Description : Message Res ID
     */
    public void ShowToast(int msgID) {
        ShowToast(appContext.getString(msgID));
    }

    public void openUrl(Context context, String url) {
        context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }
}
