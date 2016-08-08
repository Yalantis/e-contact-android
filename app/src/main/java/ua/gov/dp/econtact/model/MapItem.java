package ua.gov.dp.econtact.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapItem implements ClusterItem {

    private final LatLng mPosition;
    private SmallTicket mTicket;

    public MapItem(final double lat, final double lng, final SmallTicket ticket) {
        mPosition = new LatLng(lat, lng);
        mTicket = ticket;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public SmallTicket getTicket() {
        return mTicket;
    }
}
