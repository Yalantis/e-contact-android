package ua.gov.dp.econtact.interfaces;

import android.content.pm.ResolveInfo;

/**
 * Created by Aleksandr on 27.11.2014.
 */
public interface IShareDialog {

    void onShare(final ResolveInfo shareWith);
}
