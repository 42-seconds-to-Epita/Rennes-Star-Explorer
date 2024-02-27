package uqac.dim.rse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.io.InputStream;

import uqac.dim.rse.objects.AMarker;
import uqac.dim.rse.objects.LineRoute;
import uqac.dim.rse.objects.Lines.MetroLine;
import uqac.dim.rse.objects.markers.MetroStationMarker;

class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mContentsMetro;

    private final MainActivity main;

    public CustomInfoWindowAdapter(MainActivity main) {
        this.main = main;

        this.mContentsMetro = View.inflate(main.getApplicationContext(), R.layout.metro_marker_info_view, null);
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(@NonNull Marker marker) {
        if (marker.getTag() instanceof MetroStationMarker) {
            renderMetro((MetroStationMarker) marker.getTag(), mContentsMetro);
            return mContentsMetro;
        }

        return null;
    }

    private void renderMetro(MetroStationMarker marker, View view) {
        ((TextView) view.findViewById(R.id.name)).setText(marker.name);
        ((TextView) view.findViewById(R.id.adress)).setText(String.format("%d %s, %s %s", marker.roadNumber, marker.roadName, marker.ZIPCode, marker.cityName));
        ((TextView) view.findViewById(R.id.PMR)).setText(marker.isPMR ? "Est accessible aux PMR" : "N'est pas accessible aux PMR");
        ((TextView) view.findViewById(R.id.techcode)).setText(marker.technicalNames.first == null ? marker.technicalNames.second : marker.technicalNames.first);

        ((LinearLayout) view.findViewById(R.id.metro_marker_info_view_images)).removeAllViews();
        for (MetroLine metroLine : DataManager.instance.metroLines.values()) {
            boolean isOk = false;
            for (LineRoute route : metroLine.routes.values()) {
                if (isOk) {
                    break;
                }

                for (AMarker marker1 : route.commercialsStops.values()) {
                    if (marker.id == marker1.id) {
                        if (metroLine.pictoImageView == null) {
                            Log.i("DIM", "Error image metro " + metroLine.shortName);
                        } else {
                            ((LinearLayout) view.findViewById(R.id.metro_marker_info_view_images)).addView(metroLine.pictoImageView);
                        }
                        isOk = true;
                        break;
                    }
                }
            }
        }
    }
}
