package ua.gov.dp.econtact.util;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.ViewGroup;

public final class BackgroundUtil {

    private BackgroundUtil() {

    }

    public static void setLayoutGradient(final ViewGroup viewGroup, final int[] colors,
                                         final float[] distribution) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(final int width, final int height) {
                return new LinearGradient(0, 0, 0, height,
                        colors, //substitute the correct colors for these
                        distribution,
                        Shader.TileMode.REPEAT);
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        viewGroup.setBackground(paint);
    }
}
