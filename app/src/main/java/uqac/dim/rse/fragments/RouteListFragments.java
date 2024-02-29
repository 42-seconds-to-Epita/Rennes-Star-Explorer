package uqac.dim.rse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import uqac.dim.rse.MainActivity;
import uqac.dim.rse.R;
import uqac.dim.rse.fragments.recyclers.MetroLinesCustomAdapter;
import uqac.dim.rse.fragments.recyclers.RouteLinesCustomAdapter;
import uqac.dim.rse.objects.LineRoute;

public class RouteListFragments extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    private List<LineRoute> defaultRoutes = new ArrayList<>();

    OnBackPressedCallback callback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            MainActivity.instance.hideAllFragments();

            FragmentManager fragmentManager = MainActivity.instance.getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag("metroListFragTag") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("metroListFragTag")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container_view, new MetroListFragments(), "metroListFragTag").commit();
            }
        }
    };

    public RouteListFragments(Collection<LineRoute> values) {
        super();
        this.defaultRoutes = new ArrayList<>(values);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_recycler_list, container, false);

        recyclerView = result.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RouteLinesCustomAdapter(this.defaultRoutes);
        recyclerView.setAdapter(adapter);
        return result;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            callback.remove();
        }
    }
}

