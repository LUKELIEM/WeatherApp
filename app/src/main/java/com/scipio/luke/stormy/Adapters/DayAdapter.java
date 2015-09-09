package com.scipio.luke.stormy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scipio.luke.stormy.R;
import com.scipio.luke.stormy.weather.Day;

/**
 * Created by Luke on 8/19/2015.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Day[] mDays;

    //Constructor
    public DayAdapter(Context context, Day[] days){
        mContext = context;
        mDays = days;
    }


    @Override
    public int getCount() {
        return mDays.length;
    }

    @Override
    public Object getItem(int position) {
        return mDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // not using it
    }

    // This is where the mapping takes place
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //The adapters are built to reuse Views, when a View is scrolled so that is no longer
        // visible, it can be used for one of the new Views appearing. This reused View is the
        // convertView.

        ViewHolder holder;

        if (convertView == null){

            //The first time it was loaded, convertView is null (there is no recycled View).
            //We have a create a new one:

            // Inflate our list item layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item,null);

            //Find the TextView via findViewById() and assign it to the ViewHolder
            holder = new ViewHolder();

            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayLabel);

            //set the ViewHolder as tag of convertView.
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        Day day = mDays[position];

        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");

        if (position == 0){ //If today
            holder.dayLabel.setText("Today");
        }
        else{
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }

        return convertView;
    }

    private static class ViewHolder{
        ImageView iconImageView;
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
