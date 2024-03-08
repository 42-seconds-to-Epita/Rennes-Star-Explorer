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
import uqac.dim.rse.objects.SubwayTrain;

public class TrainCustomAdapter extends RecyclerView.Adapter<TrainCustomAdapter.TrainListViewHolder> {

    public static class TrainListViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle;
        TextView textName;
        TextView textCapacity;
        TextView textDate;
        TextView textLength;
        ImageView image;

        public TrainListViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.train_list_card_title);
            this.textName = (TextView) itemView.findViewById(R.id.train_list_card_long_name);
            this.textCapacity = (TextView) itemView.findViewById(R.id.train_list_card_capacity);
            this.textDate = (TextView) itemView.findViewById(R.id.train_list_card_date);
            this.textLength = (TextView) itemView.findViewById(R.id.train_list_card_length);
            this.image = (ImageView) itemView.findViewById(R.id.train_list_card_picto);
        }
    }

    @Override
    public TrainCustomAdapter.TrainListViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.train_list_card_view, parent, false);

        TrainCustomAdapter.TrainListViewHolder trainListViewHolder = new TrainCustomAdapter.TrainListViewHolder(view);
        return trainListViewHolder;
    }

    @Override
    public void onBindViewHolder(final TrainCustomAdapter.TrainListViewHolder holder, final int listPosition) {
        SubwayTrain train = DataManager.instance.subwayTrains.get(listPosition);

        if (train == null) {
            return;
        }

        holder.textTitle.setText(train.name);
        holder.textName.setText(String.format("%s, %s %s", train.brand, train.model, train.version == null ? "" : train.version));
        holder.textCapacity.setText(String.format("%s places assises et %s places debout", train.seatingCapacity, train.standingCapacity));
        holder.textDate.setText(String.format("Mise en service : %s", train.commissioningDate));
        holder.textLength.setText(String.format("Longueur : %s mètres", train.length));
        holder.image.setBackgroundResource(train.model.equals("CityVal") ? R.drawable.cityval : R.drawable.val);
    }

    @Override
    public int getItemCount() {
        Log.i("DIM", String.valueOf(DataManager.instance.subwayTrains.size()));
        return DataManager.instance.subwayTrains.size();
    }
}
