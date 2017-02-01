package com.alecat.geosettingsopen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.manager.TimebandHelper;
import com.alecat.geosettingsopen.model.TimebandModel;

import java.text.DecimalFormat;
import java.util.List;

public class TimebandListAdapter extends ArrayAdapter<TimebandModel> {

    private List<TimebandModel> items;
    private Context mContext;


    public TimebandListAdapter(Context context, List<TimebandModel> items) {
        super(context, R.layout.listitem_timeband, items);
        this.items = items;
        this.mContext = context;
    }


    public void addItem(TimebandModel item){
        this.items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final TimebandModel timebandModel = items.get(position);

        int row_layout = R.layout.listitem_timeband;
        View rowView = inflater.inflate(row_layout, parent, false);

        DecimalFormat decimalFormatter = new DecimalFormat("00");

        TextView startTimeTextView = (TextView) rowView.findViewById(R.id.tc_time);

        startTimeTextView.setText(String.valueOf(decimalFormatter.format(timebandModel.start_hour))+
                ":"+String.valueOf(decimalFormatter.format(timebandModel.start_minute))+
                " - "+String.valueOf(decimalFormatter.format(timebandModel.stop_hour))+
                ":"+String.valueOf(decimalFormatter.format(timebandModel.stop_minute))
        );

        TextView daysTextView = (TextView) rowView.findViewById(R.id.tc_days);
        String daysValue = "";

        if(timebandModel.mo){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_monday);
        }
        if(timebandModel.tu){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_tuesday);
        }
        if(timebandModel.we){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_wednesday);
        }
        if(timebandModel.th){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_thursday);
        }
        if(timebandModel.fr){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_friday);
        }
        if(timebandModel.sa){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_saturday);
        }
        if(timebandModel.su){
            daysValue  += ", "+mContext.getResources().getString(R.string.days_sunday);
        }

        if(daysValue.length() >= 2){
            daysValue = daysValue.substring(2);
        }

        daysTextView.setText(daysValue);

        ImageButton deleteButton = (ImageButton) rowView.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimebandHelper.deleteTimeCondition(mContext, timebandModel.id);
                items.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}