package ua.gov.dp.econtact.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import ua.gov.dp.econtact.App;

/**
 * Created by Yalantis
 * 2015/08/22.
 *
 * @author Artem Kholodnyi
 */
public final class BlurImageUtil {

    private static final float DEFAULT_BLUR_RADIUS = 12.5f;

    private BlurImageUtil() {

    }

    public static Bitmap blur(final Bitmap source, final int width, final int height) {
        return blur(source, width, height, DEFAULT_BLUR_RADIUS);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(final Bitmap source, final int width,
                              final int height, final float blurRadius) {
        Bitmap inBitmap = Bitmap.createScaledBitmap(source, width, height, false);
        final Bitmap outputBitmap = Bitmap.createBitmap(inBitmap);

        RenderScript rs = RenderScript.create(App.getContext());
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        inBitmap.recycle();

        return outputBitmap;
    }
}
