package uqac.dim.rse.objects.markers;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.rse.objects.AMarker;
import uqac.dim.rse.objects.IStation;

public class MetroStationMarker extends AMarker implements IStation {

    public List<Integer> subStationsId = new ArrayList<>();
    public String code;
    public Pair<String, String> technicalNames;
    public Pair<Integer, Integer> elevation;
    public Boolean isPMR;
}
