package uqac.dim.rse.objects;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class LineRoute {
    public String id;
    public String shortName;
    public String name;
    public int userId;
    public String direction;
    public String type;

    public int startStation;
    public int endStation;
    public int stopCount;
    public boolean isPMR;
    public double length;
    public String color;

    public HashMap<Integer, LatLng> drawPoints = new HashMap<>();
    public HashMap<Integer, AMarker> commercialsStops = new HashMap<>();
}
