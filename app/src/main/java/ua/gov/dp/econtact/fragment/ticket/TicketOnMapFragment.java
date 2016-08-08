package ua.gov.dp.econtact.fragment.ticket;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.Bind;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.TicketActivity;
import ua.gov.dp.econtact.fragment.BaseFragment;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;

public class TicketOnMapFragment extends BaseFragment implements OnMapReadyCallback {

    private static final float MAP_ZOOM_RATE = 8.0f;

    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.view_map)
    MapView mMapView;

    private GoogleMap mMap;
    private Ticket mTicket;


    public static TicketOnMapFragment newInstance() {
        return new TicketOnMapFragment();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mTicket = App.dataManager.getTicketById(activity.getIntent().getExtras().getLong(TicketActivity.ID));
        mChangeTitleListener.changeTitle(mTicket.getTicketId());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ticket_on_map;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData(savedInstanceState);
    }

    private void setData(final Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        locationDisable();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        if (mTicket != null) {
            addItem();
        }
        if (isLocationPermissionGranted()) {
            locationEnable();
            mMap.setMyLocationEnabled(true);

        } else {
            showLocationPermissionSnackbar(mCoordinatorLayout);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BaseFragment.LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationEnable();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void addItem() {
        if (mTicket.getGeoAddress() != null) {
            LatLng ticketPosition = new LatLng(Double.parseDouble(mTicket.getGeoAddress().getLatitude()),
                    Double.parseDouble(mTicket.getGeoAddress().getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions().position(ticketPosition);
            switch (TicketStates.getTicketStateById(mTicket.getState().getId())) {
                case DONE:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_2));
                    break;
                case IN_PROGRESS:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_1));
                    break;
                case PENDING:
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_3));
                    break;
                default:
                    break;
            }
            mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ticketPosition, MAP_ZOOM_RATE));
        }
    }

    private void locationDisable() {
        if (mMap != null) {
            mMap.setMyLocationEnabled(false);
        }
    }

    private void locationEnable() {
        if (mMap != null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setMyLocationEnabled(true);
        }
    }
}
