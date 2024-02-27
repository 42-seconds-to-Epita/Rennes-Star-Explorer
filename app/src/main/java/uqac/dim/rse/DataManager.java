package uqac.dim.rse;

import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import uqac.dim.rse.objects.ALine;
import uqac.dim.rse.objects.AMarker;
import uqac.dim.rse.objects.LineRoute;
import uqac.dim.rse.objects.Lines.BusLine;
import uqac.dim.rse.objects.Lines.MetroLine;
import uqac.dim.rse.objects.Picto;
import uqac.dim.rse.objects.markers.MetroStationMarker;

public class DataManager {
    public static DataManager instance;

    private MainActivity main;
    private JSONParser parser = new JSONParser();

    public List<AMarker> allMarkers = new ArrayList<>();
    public HashMap<Integer, MetroStationMarker> metroStationMarkers = new HashMap<>();

    public HashMap<Integer, ALine> allLines = new HashMap<>();
    public HashMap<Integer, BusLine> busLines = new HashMap<>();
    public HashMap<Integer, MetroLine> metroLines = new HashMap<>();
    public HashMap<String, LineRoute> allLinesRoutes = new HashMap<>();

    public DataManager(MainActivity main) {
        this.main = main;

        if (instance == null) {
            DataManager.instance = this;
        }
    }

    public void loadMapData() {
        // Map data for usage (add markers when loaded)

        // LOAD METRO
        // Arrets main : https://data.explore.star.fr/explore/dataset/tco-metro-topologie-stations-td/table/?disjunctive.id&disjunctive.codetechniquestationlignea&location=13,48.12233,-1.66563&basemap=jawg.streets
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-topologie-stations-td/records?limit=100",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;

                            MetroStationMarker temp = new MetroStationMarker();
                            temp.coords = new LatLng((double) ((JSONObject) tempJson.get("coordonnees")).get("lat"),
                                    (double) ((JSONObject) tempJson.get("coordonnees")).get("lon"));

                            temp.id = Integer.parseInt((String) tempJson.get("id"));
                            temp.name = (String) tempJson.get("nom");

                            if (tempJson.get("adressenumero") instanceof Long) {
                                temp.roadNumber = (long) tempJson.get("adressenumero");
                            }
                            temp.roadName = (String) tempJson.get("adressevoie");
                            temp.ZIPCode = (String) tempJson.get("codeinseecommune");
                            temp.cityName = (String) tempJson.get("nomcommune");

                            temp.code = (String) tempJson.get("code");
                            temp.isPMR = Objects.equals(tempJson.get("accespmr"), "true");

                            String techCodeA = null;
                            if (tempJson.get("codetechniquestationlignea") != null) {
                                techCodeA = (String) tempJson.get("codetechniquestationlignea");
                            }
                            String techCodeB = null;
                            if (tempJson.get("codetechniquestationligneb") != null) {
                                techCodeB = (String) tempJson.get("codetechniquestationligneb");
                            }
                            temp.technicalNames = new Pair<>(techCodeA, techCodeB);

                            temp.addMarker(main.getMap());
                            metroStationMarkers.put(temp.id, temp);
                        }
                        this.loadMapDataMetro2();
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 1");
                    }
                }, error -> Log.i("DIM", "Error while loading metro stations main"));

        // LOAD BUS
        // Arrets main : https://data.explore.star.fr/explore/dataset/tco-bus-topologie-pointsarret-td/table/
    }

    public void loadMapDataMetro2() {
        // Arrets id : https://data.explore.star.fr/explore/dataset/tco-metro-topologie-pointsarret-td/table/?sort=nom
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-topologie-pointsarret-td/records?limit=100",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            Integer generalInt = Integer.parseInt((String) tempJson.get("idstationparente"));
                            Integer subInt = Integer.parseInt((String) tempJson.get("id"));

                            if (!this.metroStationMarkers.containsKey(generalInt)) {
                                continue;
                            }

                            this.metroStationMarkers.get(generalInt).subStationsId.add(subInt);
                        }
                        this.loadLinesData();
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 2");
                    }
                }, error -> Log.i("DIM", "Error while loading metro stations 2"));
    }

    public void loadLinesData() {
        // Secondary

        // LOAD METRO
        // Main ligne : https://data.explore.star.fr/explore/dataset/tco-metro-topologie-lignes-td/table/
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-topologie-lignes-td/records?limit=20",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            MetroLine metroLine = new MetroLine();

                            metroLine.id = Integer.parseInt((String) tempJson.get("id"));
                            metroLine.shortName = (String) tempJson.get("nomcourt");
                            metroLine.name = (String) tempJson.get("nomlong");
                            metroLine.color = (String) tempJson.get("couleurligne");

                            this.metroLines.put(metroLine.id, metroLine);
                        }
                        this.loadMetroData2();
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 3");
                    }
                }, error -> Log.i("DIM", "Error while loading metro line 1"));


        // LOAD BUS
        // Main ligne : https://data.explore.star.fr/explore/dataset/tco-bus-topologie-lignes-td/table/
        // Desserte des parcours : https://data.explore.star.fr/explore/dataset/tco-bus-topologie-dessertes-td/table/
        // Parcours : https://data.explore.star.fr/explore/dataset/tco-bus-topologie-parcours-td/table/?disjunctive.nomcourtligne
        // Picto : https://data.explore.star.fr/explore/dataset/tco-bus-lignes-pictogrammes-dm/table/
    }

    public void loadMetroData2() {
        // Parcours : https://data.explore.star.fr/explore/dataset/tco-metro-topologie-parcours-td/table/
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-topologie-parcours-td/records?limit=20",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            LineRoute lineRoute = new LineRoute();

                            lineRoute.id = (String) tempJson.get("id");
                            lineRoute.userId = Integer.parseInt((String) tempJson.get("idligne"));
                            lineRoute.direction = (String) tempJson.get("senscommercial");
                            lineRoute.type = (String) tempJson.get("type");
                            lineRoute.shortName = (String) tempJson.get("libellecourt");
                            lineRoute.name = (String) tempJson.get("libellelong");
                            lineRoute.startStation = Integer.parseInt((String) tempJson.get("idarretdepart"));
                            lineRoute.endStation = Integer.parseInt((String) tempJson.get("idarretarrivee"));
                            lineRoute.stopCount = Integer.parseInt(Long.toString((long) tempJson.get("nombrearrets")));
                            lineRoute.isPMR = Objects.equals((String) tempJson.get("estaccessiblepmr"), "true");
                            lineRoute.length = (double) tempJson.get("longueur");
                            lineRoute.color = (String) tempJson.get("couleurtracetrace");

                            int id = 0;
                            for (Object object2 : ((JSONArray) Objects.requireNonNull(
                                    ((JSONObject) Objects.requireNonNull(
                                            ((JSONObject) Objects.requireNonNull(tempJson.get("parcours")))
                                                    .get("geometry"))).get("coordinates")))) {
                                JSONArray temp = (JSONArray) object2;
                                lineRoute.drawPoints.put(id, new Pair<>((Double) temp.get(0),
                                        (Double) temp.get(1)));
                                id += 1;
                            }

                            this.allLinesRoutes.put(lineRoute.id, lineRoute);
                            if (this.metroLines.containsKey(lineRoute.userId)) {
                                this.metroLines.get(lineRoute.userId).routes.put(lineRoute.id, lineRoute);
                            }
                        }

                        this.loadMetroCommercialsStop();
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 4");
                    }
                }, error -> Log.i("DIM", "Error while loading metro line 2"));


        // Etat actuel : https://data.explore.star.fr/explore/dataset/tco-metro-lignes-etat-tr/table/
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-lignes-etat-tr/records?limit=20",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            Integer parentId = Integer.parseInt((String) tempJson.get("idligne"));

                            if (!this.metroLines.containsKey(parentId)) {
                                continue;
                            }

                            this.metroLines.get(parentId).status = (String) tempJson.get("etat");
                            this.metroLines.get(parentId).statusStart = (String) tempJson.get("pannedepuis");
                            this.metroLines.get(parentId).statusEnd = (String) tempJson.get("finpanneprevue");
                        }
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 6");
                    }
                }, error -> Log.i("DIM", "Error while loading metro line 4"));

        // PictoGramme: https://data.explore.star.fr/explore/dataset/tco-metro-lignes-pictogrammes-dm/table/
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-lignes-pictogrammes-dm/records?limit=50",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            Integer parentId = Integer.parseInt((String) tempJson.get("idligne"));

                            if (!this.metroLines.containsKey(parentId)) {
                                continue;
                            }

                            String key = (String) tempJson.get("resolution");
                            Picto picto = new Picto();

                            JSONObject image = (JSONObject) tempJson.get("image");
                            picto.id = (String) image.get("id");
                            picto.thumbnail = (boolean) image.get("thumbnail");
                            picto.filename = (String) image.get("filename");
                            picto.format = (String) image.get("format");
                            picto.width = Integer.parseInt(((Long) image.get("width")).toString());
                            picto.height = Integer.parseInt(((Long) image.get("height")).toString());
                            picto.url = (String) image.get("url");

                            this.metroLines.get(parentId).pictos.put(key, picto);

                            // Images view displayed in card info marker for main map
                            if (Objects.equals(key, "1:100")) {
                                ImageView imageView = new ImageView(main.getApplicationContext());
                                imageView.setMinimumWidth(100);
                                imageView.setMaxWidth(100);
                                imageView.setMinimumHeight(100);
                                imageView.setMaxHeight(100);
                                imageView.setPadding(5, 5, 5, 5);
                                new DownloadImageTask(imageView, main.getApplicationContext())
                                        .execute(picto);
                                this.metroLines.get(parentId).pictoImageView = imageView;
                            }
                        }
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 6");
                    }
                }, error -> Log.i("DIM", "Error while loading metro line 4"));
    }

    private void loadMetroCommercialsStop() {
        // Arrets déservis : https://data.explore.star.fr/explore/dataset/tco-metro-topologie-dessertes-td/table/
        this.main.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-topologie-dessertes-td/records?limit=100",
                response -> {
                    try {
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            String parentId = (String) tempJson.get("idparcours");

                            if (!this.allLinesRoutes.containsKey(parentId)) {
                                continue;
                            }

                            int key = Integer.parseInt(((Long) tempJson.get("ordre")).toString());
                            int value = Integer.parseInt((String) tempJson.get("idarret"));
                            for (MetroStationMarker metroStationMarker : this.metroStationMarkers.values()) {
                                if (metroStationMarker.subStationsId.contains(value)) {
                                    this.allLinesRoutes.get(parentId).commercialsStops.put(key, metroStationMarker);
                                }
                            }
                        }
                    } catch (ParseException ex) {
                        Log.i("DIM", "Error while parsing json 5");
                    }
                }, error -> Log.i("DIM", "Error while loading metro line 3"));
    }
}
