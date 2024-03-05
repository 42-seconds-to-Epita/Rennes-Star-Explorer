package uqac.dim.rse.fragments.recyclers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import uqac.dim.rse.DataManager;
import uqac.dim.rse.R;
import uqac.dim.rse.objects.Alert;
import uqac.dim.rse.objects.SubwayTrain;

public class AlertCustomAdapter extends RecyclerView.Adapter<AlertCustomAdapter.AlertListViewHolder> {


    public static class AlertListViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textName;
        TextView textCapacity;
        TextView textDate;
        TextView textLength;
        TextView textDesc;
        ImageView image;

        public AlertListViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.alert_name);
            this.textName = (TextView) itemView.findViewById(R.id.alert_niveau);
            this.textCapacity = (TextView) itemView.findViewById(R.id.alert_name_ligne);
            this.textDate = (TextView) itemView.findViewById(R.id.alert_debut);
            this.textLength = (TextView) itemView.findViewById(R.id.alert_fin);
            this.textDesc = (TextView) itemView.findViewById(R.id.alert_description);
            this.image = (ImageView) itemView.findViewById(R.id.alert_list_card_picto);
        }
    }

    @Override
    public AlertCustomAdapter.AlertListViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alert_list_card_view, parent, false);

        AlertCustomAdapter.AlertListViewHolder alertListViewHolder = new AlertCustomAdapter.AlertListViewHolder(view);
        return alertListViewHolder;
    }

    @Override
    public void onBindViewHolder(final AlertCustomAdapter.AlertListViewHolder holder, final int listPosition) {
        Alert alert = DataManager.instance.alertList.get(listPosition);

        if (alert == null) {
            return;
        }

        holder.textTitle.setText(alert.name);
        holder.textName.setText(String.format("Niveau : %s", alert.niveau.substring(2, alert.niveau.length() - 2)));
        holder.textCapacity.setText(String.format("Ligne : %s", alert.nomligne));
        holder.textDate.setText(String.format("Debut : %s", alert.debut.substring(0, 10)));
        holder.textLength.setText(String.format("Fin : %s", alert.fin.substring(0, 10)));
        holder.textDesc.setText(String.format(alert.decription));
    }

    @Override
    public int getItemCount() {
        Log.i("DIM", String.valueOf(DataManager.instance.alertList.size()));
        return DataManager.instance.alertList.size();
    }
}


