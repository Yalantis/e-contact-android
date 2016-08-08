package ua.gov.dp.econtact.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public final class ShowDialogUtil {

    private ShowDialogUtil() {
    }

    public static void showOkDialog(final Context ctx, final int title, final int message) {
        showOkDialog(ctx, title, message, null);
    }

    public static void showOkDialog(final Context ctx, final int title, final int message,
                                    final DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }
}
