package uqac.dim.rse.fragments.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import uqac.dim.rse.R;
import uqac.dim.rse.objects.AMarker;
import uqac.dim.rse.objects.LineRoute;

public class RouteLinesListCustomAdapter extends RecyclerView.Adapter<RouteLinesListCustomAdapter.RouteLinesListViewHolder> {

    public static RouteLinesListCustomAdapter instance;

    private final DateFormat format3 = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);

    public LineRoute currentRoute;

    public HashMap<Integer, Date> nextTrain = new HashMap<>();

    public RouteLinesListCustomAdapter(LineRoute currentRoute) {
        super();
        this.currentRoute = currentRoute;
        RouteLinesListCustomAdapter.instance = this;
    }

    public static class RouteLinesListViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textNext;

        public RouteLinesListViewHolder(View itemView) {
            super(itemView);
            this.textName = (TextView) itemView.findViewById(R.id.route_list_list_name);
            this.textNext = (TextView) itemView.findViewById(R.id.route_list_list_next);
        }
    }

    @Override
    public RouteLinesListViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_list_list_card_view, parent, false);

        RouteLinesListViewHolder metroListViewHolder = new RouteLinesListViewHolder(view);
        return metroListViewHolder;
    }

    @Override
    public void onBindViewHolder(final RouteLinesListViewHolder holder, final int listPosition) {
        AMarker marker = this.currentRoute.commercialsStops.get(listPosition + 1);

        if (marker == null) {
            return;
        }

        holder.textName.setText(marker.name);

        int id = this.currentRoute.commercialsStopsIds.get(listPosition + 1);
        if (this.nextTrain.containsKey(id)) {
            holder.textNext.setText(format3.format(this.nextTrain.get(id)));
        }
    }

    @Override
    public int getItemCount() {
        return this.currentRoute.commercialsStops.values().size();
    }
}
