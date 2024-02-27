package uqac.dim.rse.fragments.recyclers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import uqac.dim.rse.DataManager;
import uqac.dim.rse.DownloadImageTask;
import uqac.dim.rse.R;
import uqac.dim.rse.objects.Lines.MetroLine;

public class MetroLinesCustomAdapter extends RecyclerView.Adapter<MetroLinesCustomAdapter.MetroListViewHolder> {

    public static class MetroListViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textName;
        TextView textStatus;
        TextView textCount;
        ImageView imagePicto;

        public MetroListViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.metro_list_card_title);
            this.textName = (TextView) itemView.findViewById(R.id.metro_list_card_long_name);
            this.textStatus = (TextView) itemView.findViewById(R.id.metro_list_card_status);
            this.textCount = (TextView) itemView.findViewById(R.id.metro_list_card_count);
            this.imagePicto = (ImageView) itemView.findViewById(R.id.metro_list_card_picto);
        }
    }

    @Override
    public MetroListViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.metro_list_card_view, parent, false);

        MetroListViewHolder metroListViewHolder = new MetroListViewHolder(view);
        return metroListViewHolder;
    }

    @Override
    public void onBindViewHolder(final MetroListViewHolder holder, final int listPosition) {
        MetroLine metroLine = DataManager.instance.metroLines.get(DataManager.instance.metroLines.keySet().stream().sorted().toArray()[listPosition]);

        if (metroLine == null) {
            return;
        }

        holder.textTitle.setText("Ligne " + metroLine.shortName);
        holder.textName.setText(metroLine.name);
        holder.textStatus.setText("Status : " + metroLine.status);
        holder.textCount.setText(metroLine.routes.size() + " parcours");
        new DownloadImageTask(holder.imagePicto, holder.itemView.getContext())
                .execute(metroLine.pictos.get("1:100"));
    }

    @Override
    public int getItemCount() {
        return DataManager.instance.metroLines.size();
    }
}