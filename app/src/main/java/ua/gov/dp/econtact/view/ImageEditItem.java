package ua.gov.dp.econtact.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

import ua.gov.dp.econtact.R;

/**
 * Created by Yalantis
 *
 * @author Andrew Khristyan
 */
public class ImageEditItem extends RelativeLayout {

    private SquaredImageView mSquaredImageView;
    private ImageView mImageViewDelete;

    public ImageEditItem(final Context context) {
        this(context, null, 0);
    }

    public ImageEditItem(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageEditItem(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.view_edit_image, this, true);
        mSquaredImageView = (SquaredImageView) findViewById(R.id.image_View_ticket);
        mImageViewDelete = (ImageView) findViewById(R.id.image_view_delete);
    }

    public void bindData(final Uri uri, final OnItemDeleteListener onItemDeleteListener) {
        final int imageSize = 190;
        Picasso.with(getContext())
                .load(new File(uri.getPath()))
                .resize(imageSize, imageSize)
                .config(Bitmap.Config.RGB_565)
                .centerCrop()
                .into(mSquaredImageView);
        mImageViewDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                onItemDeleteListener.onItemDelete(ImageEditItem.this, uri.getPath());
            }
        });
    }

    public interface OnItemDeleteListener {

        void onItemDelete(final ImageEditItem imageEditItem, final String path);

    }
}
