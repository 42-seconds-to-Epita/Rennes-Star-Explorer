package uqac.dim.rse.fragments.recyclers;

import static uqac.dim.rse.utils.ColorConverter.hexToArgb;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public RouteListViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.route_list_card_title);
            this.textName = (TextView) itemView.findViewById(R.id.route_list_card_name);
            this.textStart = (TextView) itemView.findViewById(R.id.route_list_card_start);
            this.textEnd = (TextView) itemView.findViewById(R.id.route_list_card_end);
            this.textLength = itemView.findViewById(R.id.route_list_card_length);
            this.textPMR = itemView.findViewById(R.id.route_list_card_pmr);
            this.mapView = (MapView) itemView.findViewById(R.id.route_list_card_map);
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

            map.addPolyline(polylineOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 15));
        }
    }

    public List<LineRoute> routes = new ArrayList<>();

    @Override
    public RouteListViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_list_card_view, parent, false);

        RouteListViewHolder metroListViewHolder = new RouteListViewHolder(view);
        return metroListViewHolder;
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
    }

    @Override
    public int getItemCount() {
        return this.routes.size();
    }
}
