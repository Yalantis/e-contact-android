package ua.gov.dp.econtact.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Alexey on 21.07.2016.
 */
public class ClickableUrlSpan extends ClickableSpan {

    private final String mUrl;

    public ClickableUrlSpan(@NonNull String url) {
        mUrl = url;
    }

    @Override
    public void onClick(View widget) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(mUrl));
        widget.getContext().startActivity(i);
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }

}
