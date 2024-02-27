package uqac.dim.rse.objects;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class AMarker {

    public LatLng coords;
    public int id;
    public String name;

    public long roadNumber;
    public String roadName;
    public String ZIPCode;
    public String cityName;

    public Marker marker;

    public void addMarker(GoogleMap map) {
        if (this.marker != null) {
            this.marker.remove();
        }

        this.marker = map.addMarker(new MarkerOptions().position(this.coords).title(this.name));

        if (this.marker == null) {
            return;
        }

        this.marker.setTag(this);
    }
}
