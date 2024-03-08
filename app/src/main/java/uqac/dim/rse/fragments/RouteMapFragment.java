package uqac.dim.rse.fragments;

import static uqac.dim.rse.utils.ColorConverter.hexToArgb;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.rse.CustomInfoWindowAdapter;
import uqac.dim.rse.MainActivity;
import uqac.dim.rse.R;
import uqac.dim.rse.objects.AMarker;
import uqac.dim.rse.objects.LineRoute;

public class RouteMapFragment extends Fragment implements OnMapReadyCallback {

    public static RouteMapFragment instance;

    private List<Marker> markers = new ArrayList<>();
    private Polyline polyline;

    public LineRoute defaultRoute;

    private GoogleMap map;

    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            MainActivity.instance.hideAllFragments();

            FragmentManager fragmentManager = MainActivity.instance.getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag("routeListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("routeListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new RouteListFragments(new ArrayList<>()), "routeListFragTag").commit();
            }
        }
    };

    public RouteMapFragment(LineRoute route) {
        super();
        RouteMapFragment.instance = this;
        this.defaultRoute = route;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("DIM", "1");
        View result = inflater.inflate(R.layout.route_map, container, false);
        Log.i("DIM", "2");
        ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.route_map_map)).getMapAsync(this);
        return result;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("DIM", "ready");
        this.map = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.114700, -1.679400)));
        this.map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.map.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(MainActivity.instance, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.instance, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        }

        this.map.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.instance));
        this.updateMap();
    }

    public void updateMap() {
        if (this.map == null) {
            return;
        }

        for (Marker marker : this.markers) {
            marker.remove();
        }
        this.markers = new ArrayList<>();

        if (polyline != null) {
            polyline.remove();
        }

        for (AMarker marker : this.defaultRoute.commercialsStops.values()) {
            Marker temp = map.addMarker(new MarkerOptions().position(marker.coords).title(marker.name));
            temp.setTag(marker);
            this.markers.add(temp);
        }

        PolylineOptions polylineOptions = new PolylineOptions().width(10)
                .color(hexToArgb(this.defaultRoute.color));
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for (LatLng pos : this.defaultRoute.drawPoints.values()) {
            polylineOptions.add(pos);
            bounds.include(pos);
        }

        this.polyline = map.addPolyline(polylineOptions);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            callback.remove();
        } else {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        }
    }
}
