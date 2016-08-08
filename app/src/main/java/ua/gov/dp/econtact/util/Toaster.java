package ua.gov.dp.econtact.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;

public final class Toaster {

    private Toaster() {
    }

    public static void showShort(final String value) {
        Toast.makeText(App.getContext(), value, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(final int resId) {
        Toast.makeText(App.getContext(), App.getContext().getString(resId),
                Toast.LENGTH_SHORT).show();
    }

    public static void showShort(final Activity activity, final String value) {
        if (activity != null) {
            Toast.makeText(activity, value, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showShort(final Activity activity, final int resId) {
        if (activity != null) {
            Toast.makeText(activity, activity.getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

    public static void share(final View view, final int string) {
        share(view, view.getContext().getString(string));
    }

    public static void share(final View view, final String string) {
        share(view, string, view.getContext().getResources().getColor(R.color.white_30_transparent), Color.BLACK);
    }

    public static void share(final View view, final int string, final int colorBg, final int colorTxt) {
        shareLong(view, view.getContext().getString(string), colorBg, colorTxt);
    }

    public static void share(final View view, final String string, final int colorBg, final int colorTxt) {
        Snackbar bar = Snackbar.make(view, string, Snackbar.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) bar.getView();
        group.setBackgroundColor(colorBg);
        View viewBar = bar.getView();
        TextView tv = (TextView) viewBar.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(colorTxt);
        bar.show();
    }

    public static void shareLong(final View view, final String string, final int colorBg, final int colorTxt) {
        Snackbar bar = Snackbar.make(view, string, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) bar.getView();
        group.setBackgroundColor(colorBg);
        View viewBar = bar.getView();
        TextView tv = (TextView) viewBar.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(colorTxt);
        bar.show();
    }
}
