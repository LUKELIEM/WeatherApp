package com.scipio.luke.stormy.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scipio.luke.stormy.R;
import com.scipio.luke.stormy.weather.Hour;

/**
 * Created by Luke on 9/8/2015.
 */
public class HourAdapter extends RecyclerView.Adapter<HourAdapter.HourViewHolder> {

    public Hour[] mHours;
    public Context mContext;

    //constructor to pass the hourly forecast data to the adapter
    public HourAdapter(Context context, Hour[] hours){
        mContext = context;
        mHours = hours;
    }

    //3 methods for the Adapter – onCreate, OnBind and getItemCount

    // Create new views (invoked by the layout manager)
    @Override
    public HourViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hourly_list_item, parent, false);
        HourViewHolder viewHolder = new HourViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(HourViewHolder holder, int i) {
        holder.bindHour(mHours[i]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mHours.length;
    }

    // The ViewHolder Class
    public class HourViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        // These members corresponds to the items in hourly_list_item.xml
        public TextView mTimeLabel;
        public TextView mSummaryLabel;
        public TextView mTemperatureLabel;
        public ImageView mIconImageView;

        //views
        public HourViewHolder(View itemView) {
            super(itemView);

            mTimeLabel = (TextView) itemView.findViewById(R.id.timeLabel);
            mSummaryLabel = (TextView) itemView.findViewById(R.id.summaryLabel);
            mTemperatureLabel = (TextView) itemView.findViewById(R.id.temperatureLabel);
            mIconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);

            //this is necessary to activate onClickListener to the ViewHolder class
            itemView.setOnClickListener(this);
        }

        // data mapping code
        public void bindHour(Hour hour){
            mTimeLabel.setText(hour.getHour());
            mSummaryLabel.setText(hour.getSummary());
            mTemperatureLabel.setText(hour.getTemperature()+"");
            mIconImageView.setImageResource(hour.getIconId());
        }

        @Override
        public void onClick(View v) {
            String time = mTimeLabel.getText().toString();
            String temperature = mTemperatureLabel.getText().toString();
            String summary = mSummaryLabel.getText().toString();

            String message = String.format("At %s, temperature will be %s and it will be %s",
                    time,
                    temperature,
                    summary);

            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
