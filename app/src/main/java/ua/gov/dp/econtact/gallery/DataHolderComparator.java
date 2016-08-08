package ua.gov.dp.econtact.gallery;


import ua.gov.dp.econtact.gallery.databinders.DataHolder;

import java.util.Comparator;

/**
 * Created by Yalantis
 * 7/15/2015.
 *
 * @author Pavel
 */
public class DataHolderComparator implements Comparator<DataHolder> {
    @Override
    public int compare(final DataHolder leftEntity, final DataHolder rightEntity) {
        if (rightEntity.getDataTaken() == null || leftEntity.getDataTaken() == null) {
            return 0;
        }
        return rightEntity.getDataTaken().compareTo(leftEntity.getDataTaken());
    }
}
