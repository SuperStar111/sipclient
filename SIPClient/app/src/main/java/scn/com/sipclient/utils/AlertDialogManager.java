package scn.com.sipclient.utils;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by star on 6/29/2016.
 */
public class AlertDialogManager {
    public void showAlertDialog(Context context, String title, String message,
                                 Boolean status) {
        MaterialDialog alertDialog = new MaterialDialog.Builder(context).build();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        alertDialog.setContent(message);

        // Showing Alert Message
        alertDialog.show();
    }


    public static void showAlertDialog(Context context, int titleId, int messageId,
                                int positiveId) {
        MaterialDialog.Builder alertDialogBuidler = new MaterialDialog.Builder(context);

        // Setting Dialog Title
        alertDialogBuidler.title(titleId);

        alertDialogBuidler.content(messageId);
        alertDialogBuidler.positiveText(positiveId);
        // Showing Alert Message
        alertDialogBuidler.show();
    }

    public static void showAlertDialog(Context context, int titleId, String message,
                                       int positiveId) {
        MaterialDialog.Builder alertDialogBuidler = new MaterialDialog.Builder(context);

        // Setting Dialog Title
        alertDialogBuidler.title(titleId);

        alertDialogBuidler.content(message);
        alertDialogBuidler.positiveText(positiveId);
        // Showing Alert Message
        alertDialogBuidler.show();
    }

    public static void showAlertDialog(Context context, String title, String message,
                                       int positiveId) {
        MaterialDialog.Builder alertDialogBuidler = new MaterialDialog.Builder(context);

        // Setting Dialog Title
        alertDialogBuidler.title(title);

        alertDialogBuidler.content(message);
        alertDialogBuidler.positiveText(positiveId);
        // Showing Alert Message
        alertDialogBuidler.show();
    }
}
