package ua.gov.dp.econtact.gallery.databinders;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ua.gov.dp.econtact.gallery.customcomponents.GalleryFrameLayout;
import ua.gov.dp.econtact.gallery.customcomponents.RecyclingImageView;

/**
 * GalleryHolder - view holder for gallery items
 *
 * @author Ed Baev
 */
class GalleryHolder {

    GalleryFrameLayout baseLayout;

    FrameLayout listBaseLayout;

    RecyclingImageView folderThumbnail;

    TextView folderName;

    TextView folderCount;

    ImageView videoIcon;
}
