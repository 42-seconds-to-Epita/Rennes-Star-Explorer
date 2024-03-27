package uqac.dim.rse;

import static uqac.dim.rse.utils.ColorConverter.hexToArgb;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.rse.fragments.AlertListFragments;
import uqac.dim.rse.fragments.MetroListFragments;
import uqac.dim.rse.fragments.TrainListFragment;
import uqac.dim.rse.objects.LineRoute;
import uqac.dim.rse.objects.markers.MetroStationMarker;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    public static MainActivity instance;

    private final List<Polyline> displayPoly = new ArrayList<>();

    private RequestQueue requestQueue = null;

    private GoogleMap map;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.instance = this;

        requestQueue = Volley.newRequestQueue(this);
        DataManager dataManager = new DataManager(this);

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_map_fragment);
        mapFragment.getMapAsync(this);

        // Nagivation Menu
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Datas used for map display
        dataManager.loadMapData();
        dataManager.LoadAlerts();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(48.114700, -1.679400)));
        this.map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        this.map.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.map.setMyLocationEnabled(true);
        }

        this.map.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));
        this.map.setOnMarkerClickListener(marker -> {
            for (Polyline polyline : displayPoly) {
                polyline.remove();
            }

            if (marker.getTag() instanceof MetroStationMarker) {
                MetroStationMarker data = (MetroStationMarker) marker.getTag();

                for (Integer subStationId : data.subStationsId) {
                    for (LineRoute lineRoute : DataManager.instance.allLinesRoutes.values()) {
                        if (!lineRoute.commercialsStopsIds.containsValue(subStationId)) {
                            continue;
                        }

                        PolylineOptions polylineOptions = new PolylineOptions().width(10)
                                .color(hexToArgb(lineRoute.color));
                        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

                        for (LatLng pos : lineRoute.drawPoints.values()) {
                            polylineOptions.add(pos);
                            bounds.include(pos);
                        }
                        displayPoly.add(map.addPolyline(polylineOptions));
                    }
                }
            }
            return false;
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        this.hideAllFragments();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.metro_list_menu) {
            if (fragmentManager.findFragmentByTag("metroListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("metroListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new MetroListFragments(), "metroListFragTag").commit();
            }
        } else if (id == R.id.map_menu) {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(R.id.main_map_fragment)).commit();
        } else if (id == R.id.train_list_menu) {
            if (fragmentManager.findFragmentByTag("trainListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("trainListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new TrainListFragment(), "trainListFragTag").commit();
            }
        } else if (id == R.id.alert_list_menu) {
            if (fragmentManager.findFragmentByTag("alertListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("alertListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new AlertListFragments(), "alertListFragTag").commit();
            }
        }

        DrawerLayout drawer = findViewById(R.id.my_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void hideAllFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().hide(fragmentManager.findFragmentById(R.id.main_map_fragment)).commit();

        if (fragmentManager.findFragmentByTag("metroListFragTag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("metroListFragTag")).commit();
        }

        if (fragmentManager.findFragmentByTag("trainListFragTag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("trainListFragTag")).commit();
        }

        if (fragmentManager.findFragmentByTag("routeListFragTag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("routeListFragTag")).commit();
        }

        if (fragmentManager.findFragmentByTag("alertListFragTag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("alertListFragTag")).commit();
        }

        if (fragmentManager.findFragmentByTag("routeMapFrag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("routeMapFrag")).commit();
        }

        if (fragmentManager.findFragmentByTag("routeListListFrag") != null) {
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag("routeListListFrag")).commit();
        }
    }

    public void requestStarAPI(String url, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, onSuccess, onError);
        requestQueue.add(stringRequest);
    }

    public GoogleMap getMap() {
        return map;
    }
}
