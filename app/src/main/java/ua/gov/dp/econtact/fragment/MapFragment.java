package ua.gov.dp.econtact.fragment;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import timber.log.Timber;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.MainActivity;
import ua.gov.dp.econtact.activity.NewTicketActivity;
import ua.gov.dp.econtact.activity.TicketActivity;
import ua.gov.dp.econtact.adapter.MapSpinnerAdapter;
import ua.gov.dp.econtact.adapter.TicketsAdapter;
import ua.gov.dp.econtact.event.TicketsByIdsEvent;
import ua.gov.dp.econtact.event.tickets.SmallTicketsEvent;
import ua.gov.dp.econtact.interfaces.ListListener;
import ua.gov.dp.econtact.listeners.MapFragmentListener;
import ua.gov.dp.econtact.listeners.SimpleAnimatorListener;
import ua.gov.dp.econtact.model.MapItem;
import ua.gov.dp.econtact.model.SmallTicket;
import ua.gov.dp.econtact.model.StatusSpinnerItem;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;

/**
 * Created by Yalantis
 */
public class MapFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, ListListener {

    public static final float MAP_ZOOM_RATE = 8.0f;
    public static final float ALPHA_INVISIBLE = 0.0f;
    public static final float ALPHA_VISIBLE = 1.0f;
    public static final float HOLE_RADIUS_PERCENT = 50f;
    public static final float TRANSPARENT_CIRCLE_RADIUS_PERCENT = 5f;
    public static final int MAX_CLUSTER_SIZE_LIMIT = 100;

    @Bind(R.id.spinner_ticket_status)
    Spinner mStatusSpinner;
    @Bind(R.id.view_map)
    MapView mMapView;
    private GoogleMap mMap;
    @Bind(R.id.wrapper_map_results)
    LinearLayout mResultsWrapper;
    @Bind(R.id.recycler_view_tickets)
    RecyclerView mTicketsRecyclerView;
    @Bind(R.id.progress)
    ProgressBar mProgressBar;

    private LocationManager mLocationManager;
    private ClusterManager<MapItem> mClusterManager;

    private List<SmallTicket> mTicketsList;

    private TicketsAdapter mTicketsAdapter;
    private StatusSpinnerItem mCurrentStatus;
    private boolean mIsClusterSet;
    private boolean isAnimating;
    private boolean isResultsVisible;
    private MapFragmentListener mMapFragmentListener;
    private final static int TICKET_PAGE_LIMIT = 10;
    private boolean isLoadingNow;
    private LinearLayoutManager mLayoutManager;
    private int mTicketsOffset;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mMapFragmentListener = (MapFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Context should implement " + MapFragmentListener.class.getSimpleName());
        }
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
        setListeners();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.apiManager.getTicketsForMap();
    }

    private void setData() {
        if (isAdded()) {
            mMapView.onCreate(null);
            mMapView.getMapAsync(this);
            setStatusSpinner();
            setResultsViewAppearance(false);
            setTicketsRecycler();
        }
    }

    private void setTicketsRecycler() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTicketsRecyclerView.setLayoutManager(mLayoutManager);
        mTicketsRecyclerView.setNestedScrollingEnabled(false);
        mTicketsAdapter = new TicketsAdapter(getActivity(), new ArrayList<Ticket>(), this);
        mTicketsRecyclerView.setAdapter(mTicketsAdapter);
    }

    private void setListeners() {
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (isResultsVisible) {
                            animateResultsViewAppearance(false);
                        } else {
                            mActivity.onBackPressed();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    private void setStatusSpinner() {
        mCurrentStatus = new StatusSpinnerItem(getString(R.string.task_all), null);
        List<StatusSpinnerItem> statuses = new ArrayList<>();
        statuses.add(new StatusSpinnerItem(getString(R.string.task_all), null));
        statuses.add(new StatusSpinnerItem(getString(R.string.task_done), TicketStates.DONE));
        statuses.add(new StatusSpinnerItem(getString(R.string.task_in_process), TicketStates.IN_PROGRESS));
        statuses.add(new StatusSpinnerItem(getString(R.string.task_pending), TicketStates.PENDING));

        MapSpinnerAdapter mapSpinnerAdapter = new MapSpinnerAdapter(statuses);
        mStatusSpinner.setAdapter(mapSpinnerAdapter);
        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                mCurrentStatus = (StatusSpinnerItem) mStatusSpinner.getSelectedItem();
                if (mClusterManager != null) {
                    mClusterManager.clearItems();
                    addItems();
                    mClusterManager.cluster();
                    mTicketsAdapter.clear();
                    if (isResultsVisible) {
                        animateResultsViewAppearance(false);
                    }
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        enableUserLocation();
    }


    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        disableUserLocation();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
        super.onLowMemory();

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        centerMapOnMyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && mMap != null) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    enableUserLocation();
                    animateCameraToUserLocation();
                } else {
                    animateCameraToDefaultLocation();
                }
            }
        }
    }

    private void disableUserLocation() {
        if (mMap != null && isLocationPermissionGranted()) {
            //noinspection MissingPermission
            mMap.setMyLocationEnabled(false);
        }
    }

    private void enableUserLocation() {
        if (mMap != null && isLocationPermissionGranted()) {
            //noinspection MissingPermission
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    private void centerMapOnMyLocation() {
        if (isLocationPermissionGranted()) {
            enableUserLocation();
            animateCameraToUserLocation();
        } else {
            animateCameraToDefaultLocation();
            showLocationPermissionSnackbar(mMapFragmentListener.getCoordinatorLayout());
        }
    }

    private void animateCameraToUserLocation() {
        if (mMap != null) {
            mLocationManager = (LocationManager) mActivity.getSystemService(Activity.LOCATION_SERVICE);
            String provider = mLocationManager.getBestProvider(new Criteria(), true);
            try {
                if (!TextUtils.isEmpty(provider)) {
                    Location location = mLocationManager.getLastKnownLocation(provider);
                    if (location == null) {
                        animateCameraToDefaultLocation();
                        mLocationManager.requestSingleUpdate(provider, this, null);
                    } else {
                        animateCameraToLocation(location);
                    }
                }
            } catch (SecurityException e) {
                Timber.e(e, "Can't get location. NO PERMISSIONS");
            }
            setUpCluster();
        }
    }

    private void animateCameraToDefaultLocation() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Const.DefaultLocation.DEFAULT_AREA_LATITUDE,
                        Const.DefaultLocation.DEFAULT_AREA_LONGITUDE),
                MAP_ZOOM_RATE));
        setUpCluster();
    }

    private void animateCameraToLocation(final Location location) {
        LatLng coordinates = new LatLng(location.getLatitude(),
                location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates,
                MAP_ZOOM_RATE));
    }

    private void setUpCluster() {
        if (!mIsClusterSet) {
            mIsClusterSet = true;

            mClusterManager = new ClusterManager<>(mActivity, mMap);
            mClusterManager.setRenderer(new ClusterRenderer());
            mClusterManager.setOnClusterClickListener(mClusterClickListener);
            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MapItem>() {
                @Override
                public boolean onClusterItemClick(final MapItem mapItem) {
                    if (!isAnimating) {
                        navigateToTicketActivity(mapItem.getTicket().getId(), null, false);
                    }
                    return true;
                }
            });

            mMap.setOnCameraChangeListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    if (isResultsVisible) {
                        animateResultsViewAppearance(false);
                    }
                }
            });
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (isResultsVisible) {
                        animateResultsViewAppearance(false);
                    }
                    return false;
                }
            });

            addItems();
        }
    }

    private RecyclerView.OnScrollListener mOnScrollListener;
    private ClusterManager.OnClusterClickListener<MapItem> mClusterClickListener = new ClusterManager.OnClusterClickListener<MapItem>() {
        @Override
        public boolean onClusterClick(final Cluster<MapItem> cluster) {
            if (!isAnimating) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTicketsAdapter.clear();
                mTicketsOffset = getFirstTicketsLimit(cluster);
                App.apiManager.getTicketsByIds(buildIdString(0, cluster));
                resetOnScrollListener(cluster);
            }
            return true;
        }
    };

    private void resetOnScrollListener(Cluster<MapItem> cluster) {
        if (mOnScrollListener != null) {
            mTicketsRecyclerView.removeOnScrollListener(mOnScrollListener);
        }
        mOnScrollListener = createOnScrollListenerForCluster(cluster);
        mTicketsRecyclerView.addOnScrollListener(mOnScrollListener);
    }

    @NonNull
    private RecyclerView.OnScrollListener createOnScrollListenerForCluster(final Cluster<MapItem> cluster) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                paging(cluster);
            }
        };
    }

    private void paging(Cluster<MapItem> cluster) {
        int visibleItemCount = mLayoutManager.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();
        int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
        if (!isLoadingNow && ((visibleItemCount + pastVisibleItems) >= totalItemCount) && (totalItemCount < cluster.getItems().size())) {
            mTicketsOffset = mTicketsOffset + TICKET_PAGE_LIMIT;
            isLoadingNow = true;
            App.apiManager.getTicketsByIds(buildIdString(totalItemCount, cluster));
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private int getFirstTicketsLimit(Cluster<MapItem> cluster) {
        final int clusterItemsCount = cluster.getItems().size();
        if (clusterItemsCount >= TICKET_PAGE_LIMIT) {
            return TICKET_PAGE_LIMIT;
        } else {
            return clusterItemsCount;
        }
    }

    private String buildIdString(int totalItemCount, Cluster<MapItem> cluster) {
        StringBuilder ids = new StringBuilder();
        for (int i = totalItemCount; (i < mTicketsOffset) && (i < cluster.getItems().size()); i++) {
            Object[] mapItems = cluster.getItems().toArray();
            MapItem mapItem = (MapItem) mapItems[i];
            ids.append(mapItem.getTicket().getId());
            ids.append(",");
        }

        if (ids.length() > 1) {
            ids.deleteCharAt(ids.length() - 1);
        }
        return ids.toString();
    }

    private void addItems() {
        if (mTicketsList != null) {
            MapItem offsetItem;
            for (SmallTicket ticket : mTicketsList) {
                if ((mCurrentStatus.getState() == null || mCurrentStatus.getState().containsStatus(ticket.getState()))
                        && TicketStates.getTicketStateById(ticket.getState()) != TicketStates.REJECTED) {

                    if (TextUtils.isEmpty(ticket.getLatitude()) || TextUtils.isEmpty(ticket.getLongitude())) {
                        continue;
                    }

                    offsetItem = new MapItem(Double.valueOf(ticket.getLatitude()), Double.valueOf(ticket.getLongitude()), ticket);
                    mClusterManager.addItem(offsetItem);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        disableUserLocation();
    }

    protected void stopLocationUpdates() {
        if (mLocationManager != null) {
            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onLocationChanged(final Location location) {
        animateCameraToLocation(location);
    }

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {

    }

    @Override
    public void onProviderEnabled(final String provider) {

    }

    @Override
    public void onProviderDisabled(final String provider) {

    }

    private void setResultsViewAppearance(final boolean isVisible) {
        mResultsWrapper.setTranslationY(
                isVisible ? 0 : getResources().getDimension(R.dimen.map_results_view_hiding_height));
        mTicketsRecyclerView.setAlpha(isVisible ? ALPHA_VISIBLE : ALPHA_INVISIBLE);
    }

    protected void animateResultsViewAppearance(final boolean isVisible) {
        mResultsWrapper
                .animate()
                .setListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(final Animator animation) {
                        isAnimating = true;
                    }

                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        isAnimating = false;
                        isResultsVisible = isVisible;
                        mResultsWrapper.setBackground(isVisible ? getActivity().getResources().getDrawable(R.drawable.blue_gradient) : null);
                    }
                })
                .translationY(
                        isVisible ? 0 : getResources().getDimension(R.dimen.map_results_view_hiding_height))
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
        mTicketsRecyclerView
                .animate()
                .alpha(!isVisible ? ALPHA_INVISIBLE : ALPHA_VISIBLE)
                .setInterpolator(new AccelerateInterpolator(2))
                .start();
    }

    @Override
    public void onListItemClick(final int position) {
        List<Ticket> tickets = mTicketsAdapter.getTickets();
        if (tickets != null && !tickets.isEmpty()) {
            navigateToTicketActivity(tickets.get(position).getId(), null, false);
        }
    }

    private void navigateToTicketActivity(final long id, final TicketStates state, final boolean isFromMap) {
        Intent intent;
        if (state != null && state.containsStatus(Const.TICKET_STATUS_DRAFT)) {
            intent = new Intent(mActivity, NewTicketActivity.class);
            intent.putExtra(TicketActivity.ID, id);
            mActivity.startActivityForResult(intent, MainActivity.REQUEST_NEW_TICKET);
        } else {
            intent = TicketActivity.newIntent(getContext(), id, false, isFromMap);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public boolean onListItemLongClick(final int position) {
        return false;
    }

    protected PieData generatePieData(final Cluster<MapItem> cluster) {

        int doneItemsCounter = 0;
        int inProgressItemsCounter = 0;
        int notDoneItemsCounter = 0;


        for (MapItem mapItem : cluster.getItems()) {
            switch (TicketStates.getTicketStateById(mapItem.getTicket().getState())) {
                case DONE:
                    doneItemsCounter++;
                    break;
                case IN_PROGRESS:
                    inProgressItemsCounter++;
                    break;
                case PENDING:
                    notDoneItemsCounter++;
                    break;
                default:
                    break;
            }
        }

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        values.add("");
        colors.add(getResources().getColor(R.color.task_done));
        entries.add(new Entry((float) doneItemsCounter, 0));

        values.add("");
        colors.add(getResources().getColor(R.color.task_pending));
        entries.add(new Entry((float) notDoneItemsCounter, 1));

        values.add("");
        colors.add(getResources().getColor(R.color.task_in_process));
        entries.add(new Entry((float) inProgressItemsCounter, 2));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueFormatter(new LargeValueFormatter());
        dataSet.setValueTextColor(Color.TRANSPARENT);

        return new PieData(values, dataSet);
    }

    public void onEvent(final SmallTicketsEvent event) {
        removeStickyEvent(event);
        mTicketsList = event.getTickets();
        if (mClusterManager != null) {
            mClusterManager.clearItems();
            addItems();
            mClusterManager.cluster();
            if (isResultsVisible) {
                animateResultsViewAppearance(false);
            }
        }
    }

    public void onEvent(final TicketsByIdsEvent event) {
        removeStickyEvent(event);
        mTicketsAdapter.addTickets(event.getTickets());

        animateResultsViewAppearance(true);
        isLoadingNow = false;
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Draws chart instead of cluster icon.
     */
    private class ClusterRenderer extends DefaultClusterRenderer<MapItem> {

        private final IconGenerator mClusterIconGenerator = new IconGenerator(mActivity);
        private final PieChart mPieChart;
        private View mClusterView;
        private BitmapDescriptor mMarkerDone = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_2));
        private BitmapDescriptor mMarkerInProgress = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_1));
        private BitmapDescriptor mMarkerPending = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_pin_3));

        public ClusterRenderer() {
            super(mActivity, mMap, mClusterManager);
            mClusterView = mActivity.getLayoutInflater().inflate(R.layout.item_pie_chart, null);
            mClusterIconGenerator.setContentView(mClusterView);
            mPieChart = (PieChart) mClusterView.findViewById(R.id.pie_chart);
            mClusterIconGenerator.setBackground(getResources().getDrawable(R.drawable.transparent));
        }

        @Override
        protected void onBeforeClusterItemRendered(final MapItem mapItem, final MarkerOptions markerOptions) {

            switch (TicketStates.getTicketStateById(mapItem.getTicket().getState())) {
                case DONE:
                    markerOptions.icon(mMarkerDone);
                    break;
                case IN_PROGRESS:
                    markerOptions.icon(mMarkerInProgress);
                    break;
                case PENDING:
                    markerOptions.icon(mMarkerPending);
                    break;
                default:
                    break;
            }
            super.onBeforeClusterItemRendered(mapItem, markerOptions);
        }

        @Override
        protected void onBeforeClusterRendered(final Cluster<MapItem> cluster,
                                               final MarkerOptions markerOptions) {

            if (isAdded()) {
                int size = (int) getResources().getDimension(R.dimen.cluster_max_size);
                if (cluster.getItems().size() <= MAX_CLUSTER_SIZE_LIMIT) {
                    size = (int) (((getResources().getDimension(R.dimen.cluster_max_size)
                            - getResources().getDimension(R.dimen.cluster_min_size)) / 100)
                            * cluster.getItems().size()
                            + getResources().getDimension(R.dimen.cluster_min_size));
                }
                mClusterView.getLayoutParams().height = size;
                mClusterView.getLayoutParams().width = size;

                // TODO: improve chart displaying according to design
                mPieChart.setDrawHoleEnabled(true);
                mPieChart.setHoleColorTransparent(true);

                //set empty description
                mPieChart.setDescription("");

                mPieChart.setCenterText(String.valueOf(cluster.getSize()));
                mPieChart.setCenterTextColor(getResources().getColor(android.R.color.black));
                // radius of the center hole in percent of maximum radius
                mPieChart.setHoleRadius(HOLE_RADIUS_PERCENT);
                mPieChart.setHoleColor(getResources().getColor(R.color.white_80_transparent));
                mPieChart.setTransparentCircleRadius(TRANSPARENT_CIRCLE_RADIUS_PERCENT);

                mPieChart.getLegend().setEnabled(false);
                mPieChart.highlightValues(null);
                mPieChart.setData(generatePieData(cluster));

                Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        }

        @Override
        protected boolean shouldRenderAsCluster(final Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
