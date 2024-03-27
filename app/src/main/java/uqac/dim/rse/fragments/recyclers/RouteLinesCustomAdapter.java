package uqac.dim.rse.fragments.recyclers;

import static uqac.dim.rse.utils.ColorConverter.hexToArgb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.rse.DataManager;
import uqac.dim.rse.MainActivity;
import uqac.dim.rse.R;
import uqac.dim.rse.fragments.RouteListListFragments;
import uqac.dim.rse.fragments.RouteMapFragment;
import uqac.dim.rse.objects.LineRoute;

public class RouteLinesCustomAdapter extends RecyclerView.Adapter<RouteLinesCustomAdapter.RouteListViewHolder> {

    public static RouteLinesCustomAdapter instance;

    public RouteLinesCustomAdapter(List<LineRoute> defaultRoutes) {
        super();
        RouteLinesCustomAdapter.instance = this;
        this.routes = defaultRoutes;
    }

    public static class RouteListViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

        LineRoute data;

        TextView textTitle;
        TextView textName;
        TextView textStart;
        TextView textEnd;
        TextView textLength;
        TextView textPMR;
        MapView mapView;
        GoogleMap map;
        Button mapButton;
        Button listButton;
        Polyline currentPolyline;

        public RouteListViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.route_list_card_title);
            this.textName = (TextView) itemView.findViewById(R.id.route_list_card_name);
            this.textStart = (TextView) itemView.findViewById(R.id.route_list_card_start);
            this.textEnd = (TextView) itemView.findViewById(R.id.route_list_card_end);
            this.textLength = itemView.findViewById(R.id.route_list_card_length);
            this.textPMR = itemView.findViewById(R.id.route_list_card_pmr);
            this.mapView = (MapView) itemView.findViewById(R.id.route_list_card_map);
            this.mapButton = itemView.findViewById(R.id.list_route_card_map_button);
            this.listButton = itemView.findViewById(R.id.list_route_card_list_button);
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(this.textTitle.getContext());
            map = googleMap;

            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

            PolylineOptions polylineOptions = new PolylineOptions().width(10)
                    .color(hexToArgb(data.color));
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

            for (LatLng pos : data.drawPoints.values()) {
                polylineOptions.add(pos);
                bounds.include(pos);
            }

            currentPolyline = map.addPolyline(polylineOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
        }
    }

    public List<LineRoute> routes = new ArrayList<>();

    @Override
    public RouteListViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_list_card_view, parent, false);

        return new RouteListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RouteListViewHolder holder, final int listPosition) {
        LineRoute route = this.routes.get(listPosition);

        if (route == null) {
            return;
        }

        holder.data = route;

        holder.textTitle.setText(route.name);
        if (DataManager.instance.allLines.get(route.userId) != null) {
            holder.textName.setText("Ligne " + DataManager.instance.allLines.get(route.userId).shortName + ", " + route.direction);
        } else {
            holder.textName.setText("Ligne " + route.userId + ", " + route.direction);
        }

        if (DataManager.instance.allMarkers.get(route.startStation) != null) {
            holder.textStart.setText("Départ : " + DataManager.instance.allMarkers.get(route.startStation).name);
        } else {
            holder.textStart.setText("Départ : " + route.startStation);
        }

        if (DataManager.instance.allMarkers.get(route.endStation) != null) {
            holder.textEnd.setText("Arrivée : " + DataManager.instance.allMarkers.get(route.endStation).name);
        } else {
            holder.textEnd.setText("Arrivée : " + route.endStation);
        }

        holder.textLength.setText("Longueur : " + route.length);
        holder.textPMR.setText(route.isPMR ? "Est accessible aux PMR" : "N'est pas accessible aux PMR");

        holder.mapButton.setOnClickListener(v -> this.makeRouteMap(route));
        holder.listButton.setOnClickListener(v -> this.makeRouteList(route));

        if (holder.map == null) {
            return;
        }

        if (holder.currentPolyline != null) {
            holder.currentPolyline.remove();
        }

        PolylineOptions polylineOptions = new PolylineOptions().width(10)
                .color(hexToArgb(holder.data.color));
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();

        for (LatLng pos : holder.data.drawPoints.values()) {
            polylineOptions.add(pos);
            bounds.include(pos);
        }

        holder.currentPolyline = holder.map.addPolyline(polylineOptions);
        holder.map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
    }

    @Override
    public int getItemCount() {
        return this.routes.size();
    }

    private void makeRouteMap(LineRoute route) {
        MainActivity.instance.hideAllFragments();
        FragmentManager fragmentManager = MainActivity.instance.getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag("routeMapFrag") != null) {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("routeMapFrag")).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container_view, new RouteMapFragment(route), "routeMapFrag").commit();
        }

        if (RouteMapFragment.instance != null) {
            RouteMapFragment.instance.defaultRoute = route;
            RouteMapFragment.instance.updateMap();
        }
    }

    private void makeRouteList(LineRoute route) {
        MainActivity.instance.hideAllFragments();
        FragmentManager fragmentManager = MainActivity.instance.getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag("routeListListFrag") != null) {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("routeListListFrag")).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_container_view, new RouteListListFragments(route), "routeListListFrag").commit();
        }

        if (RouteLinesListCustomAdapter.instance != null) {
            RouteLinesListCustomAdapter.instance.currentRoute = route;
        }
    }
}
