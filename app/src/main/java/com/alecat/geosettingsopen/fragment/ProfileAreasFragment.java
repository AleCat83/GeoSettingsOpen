package com.alecat.geosettingsopen.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.dialog.DialogArea;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


public class ProfileAreasFragment extends Fragment implements OnMapReadyCallback {
    // Store instance variables

    private Long mProfileID;
    private GoogleMap mMap;
    private View mView;
    private LongSparseArray<Marker> mAreaMarkerMap = new LongSparseArray<>();
    private LongSparseArray<Circle> mAreaCircle = new LongSparseArray<>();
    private LongSparseArray<List<Long>> mGhostAreaChild = new LongSparseArray<>();


    public static final int REQUESTCODE_PLACE = 1;

    private BroadcastReceiver mNewGhostArea = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("area")) {
                Bundle bundle = intent.getExtras();

                AreaModel area = AreaHelper.getArea(getContext(), (Long) bundle.get("area"));
                addGhostAreaOnMap(area);
            }
        }
    };


    private BroadcastReceiver mDeletedArea = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("area_id")) {

                Bundle bundle = intent.getExtras();
                deleteAreaMarker((Long) bundle.get("area_id"));
            }
        }
    };



    private BroadcastReceiver mAreaModified = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("area_id")) {

                Bundle bundle = intent.getExtras();

                AreaModel areaModel = AreaHelper.getArea(context, (Long) bundle.get("area_id"));

                if(areaModel != null){ //not sure how areaModel can be null at this point mut it happened, investigating

                    deleteAreaMarker(areaModel.id);

                    addAreaOnMap(areaModel);
                }
            }
        }
    };

    // newInstance constructor for creating fragment with arguments
    public static ProfileAreasFragment newInstance(Long profileId) {
        ProfileAreasFragment profileAreasFragment = new ProfileAreasFragment();
        Bundle args = new Bundle();
        args.putLong("ProfileId", profileId);
        profileAreasFragment.setArguments(args);
        return profileAreasFragment;
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        mProfileID = getArguments().getLong("ProfileId");

        //ProfileModel profile = ProfileManager.getProfile(getContext(), mProfileID);

        if(mView == null){
            mView = inflater.inflate(R.layout.fragment_profile_areas, container, false);
            initFragment();
        }

        /*mAreaContainer = (LinearLayout) mView.findViewById(R.id.areaContainer);
        ScrollViewSupportMapFragment mapFragment = (ScrollViewSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.setListener(new ScrollViewSupportMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                mAreaContainer.requestDisallowInterceptTouchEvent(true);
            }
        });
        mapFragment.getMapAsync(this);*/



        /*SwitchCompat profileActive = (SwitchCompat) mView.findViewById(R.id.profile_active);

        profileActive.setChecked(profile.active);

        profileActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveActive(isChecked);

            }
        });*/

        return mView;
    }


    private void initFragment(){

        if(mMap == null){
            final LinearLayout areaContainer = (LinearLayout) mView.findViewById(R.id.areaContainer);
            ScrollViewSupportMapFragment mapFragment = (ScrollViewSupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.setListener(new ScrollViewSupportMapFragment.OnTouchListener() {
                @Override
                public void onTouch() {
                    areaContainer.requestDisallowInterceptTouchEvent(true);
                }
            });

            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Place place = PlaceAutocomplete.getPlace(getActivity(), data);

        if (place != null) {
            addNewArea(place.getLatLng());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefMapsType = sharedPref.getString("pref_maps_type", "normal");

        switch (prefMapsType){
            case "normal":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "hybrid":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case "satellite":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }






        //carico le aree parent

        List<AreaModel> parentAreas = AreaHelper.getAllParentArea(getContext());





        for (AreaModel area:parentAreas){
            addAreaOnMap(area);
        }


        //carico le aree fantasma

        // TODO: 15/01/17 per ora le aree ghost restano spente

        /*List<AreaModel> ghostAreas = areaManager.getAllGhostAreaByProfile(mProfileID);

        for(AreaModel area:ghostAreas){
            addGhostAreaOnMap(area);
        }*/



        //imposto azione di click su mappa

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                AreaModel area = AreaHelper.getAreaByLatLng(getContext(), latLng.latitude, latLng.longitude, null);
                if(area == null){//se clicco in un area già occupata non permetto la creazione di un altra area
                    addNewArea(latLng);
                }
            }
        });



        //imposto azione di click su marker

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                class ProfileNameIWAdapter implements GoogleMap.InfoWindowAdapter {

                    private final View mContentView;

                    ProfileNameIWAdapter(Long profileId) {

                        final ProfileModel profileModel = ProfileHelper.getProfile(getContext(), profileId);
                        mContentView = getActivity().getLayoutInflater().inflate(R.layout.infowindow_area, null);

                        TextView profileNameView = (TextView) mContentView.findViewById(R.id.profile_name);
                        profileNameView.setText(profileModel.name);
                    }

                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        return mContentView;
                    }
                }



                AreaModel clickedArea = AreaHelper.getArea(getContext(), getAreaFromMarker(marker));

                if (clickedArea.profile_id.equals(mProfileID)) {
                    mMap.setInfoWindowAdapter(null);

                    //AreaModel areaModel = mMarkerAreaMap.get(marker);
                    openAreaPopup(clickedArea);



                } else {

                    mMap.setInfoWindowAdapter(new ProfileNameIWAdapter(clickedArea.profile_id));
                }


                return false;
            }
        });



        //imposto azione di drag and drop

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}
            @Override
            public void onMarkerDrag(Marker marker) {}
            @Override
            public void onMarkerDragEnd(Marker marker) {

                AreaModel area = AreaHelper.getArea(getContext(), getAreaFromMarker(marker));
                LatLng newPosition = marker.getPosition();
                area.latitude = newPosition.latitude;
                area.longitude = newPosition.longitude;
                AreaHelper.saveArea(getContext(), area);
                Circle oldCircle = mAreaCircle.get(area.id);
                oldCircle.setCenter(newPosition);
            }
        });


        //regolo la visuale im modo da contenente tutte le aree del profilo

        int boundsCounter = 0;
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (AreaModel area : parentAreas) {
            if (!area.ghost && area.profile_id.equals(mProfileID) ||
                    (mProfileID == 1 && !area.ghost)) {
                latLngBuilder.include(new LatLng(area.latitude, area.longitude));
                boundsCounter++;
            }
        }

        if(boundsCounter == 1){
            final LatLngBounds bounds = latLngBuilder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 16);
            mMap.moveCamera(cu);
        }
        else if(boundsCounter > 1){
            final LatLngBounds bounds = latLngBuilder.build();
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);//todo may cause crashes
                    mMap.moveCamera(cu);

                }
            });
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNewGhostArea,
                new IntentFilter("new-ghost-area"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDeletedArea,
                new IntentFilter("area-deleted"));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mAreaModified,
                new IntentFilter("area-modified"));


    }



    private void addAreaOnMap(AreaModel area){

        if(area.all_world){
            return;
        }

        int circleStrokeColor = 0x3000ff00;
        int circleColor = 0x3000ff00;
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);

        if(!area.profile_id.equals(mProfileID)){
            circleStrokeColor = 0x30CFCDD3;
            circleColor = 0x30CFCDD3;
            markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_gray);
        }

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(area.latitude, area.longitude))
                .icon(markerIcon)
        );
        if(area.profile_id.equals(mProfileID)){
            marker.setDraggable(true);
        }

        LatLng latLng = new LatLng(area.latitude, area.longitude);

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(area.radius + area.threshold)
                .strokeColor(circleStrokeColor)
                .fillColor(circleColor));

        mAreaCircle.put(area.id, circle);
        mAreaMarkerMap.put(area.id, marker);
    }


    private void addGhostAreaOnMap(AreaModel area){

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(area.latitude, area.longitude))
                .radius(area.radius)
                .strokeColor(0x30ffffff)
                .fillColor(0x30ffffff));

        mAreaCircle.put(area.id, circle);

        List<Long> ghostList = new ArrayList<>();
        if(mGhostAreaChild.get(area.parent_area_id) != null){
            ghostList = mGhostAreaChild.get(area.parent_area_id);
        }
        ghostList.add(area.id);


        mGhostAreaChild.put(area.id, ghostList);
    }


    private void addNewArea(LatLng latLng){

        AreaModel areaModel = new AreaModel(
                null,
                getResources().getString(R.string.area_label_area),
                "",
                latLng.latitude,
                latLng.longitude,
                50,
                0,
                mProfileID,
                false,
                null,
                false,
                0,
                false, 1L);

        AreaHelper.saveArea(getContext(), areaModel);

        if(areaModel.id != null){
            addAreaOnMap(areaModel);
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mMap.moveCamera(cu);
            openAreaPopup(areaModel);
        }
    }

    private void saveActive(Boolean state) {

        ProfileModel profile = ProfileHelper.getProfile(getContext(), mProfileID);
        profile.active = state;
        ProfileHelper.saveProfile(getContext(), profile);

    }

    private Long getAreaFromMarker(Marker marker){
        for(int i = 0; i < mAreaMarkerMap.size(); i++) {
            Long key = mAreaMarkerMap.keyAt(i);
            Marker tempMarker = mAreaMarkerMap.get(key);
            if(tempMarker.equals(marker)){
                return key;
            }
        }
        return null;
    }


    private void openAreaPopup(final AreaModel area){

        final DialogArea dialogFragment = new DialogArea();

        Bundle arg = new Bundle();
        arg.putLong("area_id", area.id);
        dialogFragment.setArguments(arg);

        dialogFragment.show(getFragmentManager(), "area-" + String.valueOf(area.id));

    }



    private void deleteAreaMarker(final Long areaId){

        List<Long> areasToRemove = new ArrayList<>();

        List<Long> ghostAreaToRemove = mGhostAreaChild.get(areaId);

        if(ghostAreaToRemove != null){
            areasToRemove.addAll(ghostAreaToRemove);
        }

        areasToRemove.add(areaId);

        for(long area_id:areasToRemove){

            Marker areaMarker = mAreaMarkerMap.get(area_id);


            Circle circleToRemove = mAreaCircle.get(area_id);

            if(circleToRemove != null){
                circleToRemove.remove();
                mAreaCircle.remove(area_id);
            }

            if(areaMarker != null){
                areaMarker.remove();
            }
        }
    }


}














