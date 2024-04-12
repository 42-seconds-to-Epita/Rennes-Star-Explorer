package uqac.dim.rse.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import uqac.dim.rse.MainActivity;
import uqac.dim.rse.R;
import uqac.dim.rse.fragments.recyclers.RouteLinesListCustomAdapter;
import uqac.dim.rse.objects.LineRoute;

public class RouteListListFragments extends Fragment {

    public static RouteListListFragments instance;

    private final DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.FRANCE);
    private final DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.FRANCE);

    private final JSONParser parser = new JSONParser();

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private LineRoute defaultRoute;

    public RouteListListFragments(LineRoute defaultRoute) {
        RouteListListFragments.instance = this;
        this.defaultRoute = defaultRoute;
    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            MainActivity.instance.hideAllFragments();

            FragmentManager fragmentManager = MainActivity.instance.getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag("routeListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("routeListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new MetroListFragments(), "routeListFragTag").commit();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        updateNextTrain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_recycler_route_list_list, container, false);

        recyclerView = result.findViewById(R.id.recycler_list_list);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RouteLinesListCustomAdapter(defaultRoute);
        recyclerView.setAdapter(adapter);
        return result;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            callback.remove();
        } else {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
            adapter.notifyDataSetChanged();
            updateNextTrain();
        }
    }

    private void updateNextTrain() {
        // Next train at station https://data.explore.star.fr/explore/dataset/tco-metro-circulation-deux-prochains-passages-tr/information/
        MainActivity.instance.requestStarAPI("https://data.explore.star.fr/api/explore/v2.1/catalog/datasets/tco-metro-circulation-deux-prochains-passages-tr/records?limit=60",
                response -> {
                    try {
                        ((RouteLinesListCustomAdapter) adapter).nextTrain.clear();
                        JSONObject json = (JSONObject) parser.parse(response);
                        for (Object object : ((JSONArray) Objects.requireNonNull(json.get("results")))) {
                            JSONObject tempJson = (JSONObject) object;
                            int id = Integer.parseInt((String) tempJson.get("idarret"));
                            String nextStop = (String) tempJson.get("arriveefirsttrain");

                            if (Objects.equals(nextStop, "Non desservi")) {
                                continue;
                            }

                            Date date;

                            if (nextStop == null || nextStop.equals("")) {
                                nextStop = ((String) tempJson.get("arriveefirsttrain")).replace("T", " ");
                                date = format1.parse(nextStop);
                                date.setHours(date.getHours() + 1);
                            } else {
                                date = format2.parse(nextStop);
                            }

                            if (((RouteLinesListCustomAdapter) adapter).nextTrain.containsKey(id)) {
                                if (((RouteLinesListCustomAdapter) adapter).nextTrain.get(id).after(date)) {
                                    ((RouteLinesListCustomAdapter) adapter).nextTrain.put(id, date);
                                }
                            } else {
                                ((RouteLinesListCustomAdapter) adapter).nextTrain.put(id, date);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } catch (ParseException | java.text.ParseException ex) {
                        Log.i("DIM", "Error while parsing json for next train");
                    }
                }, error -> Log.i("DIM", "Error while loading next train"));
    }
}

