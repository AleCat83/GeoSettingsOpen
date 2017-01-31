package com.alecat.geosettingsopen.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.managers.TimebandManager;
import com.alecat.geosettingsopen.models.TimebandModel;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

public class TimeBandListFragment extends Fragment {

    private View mView;
    private TimeBandListAdapter mTimeBandListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.timeband_list_fragment, container, false);

        Long area_id = getArguments().getLong("area_id");
        init(area_id);
        return mView;

    }

    private void init(final Long area_id){

        ListView timeConditionList = (ListView) mView.findViewById(R.id.time_conditions_list);
        timeConditionList.setEmptyView(mView.findViewById(R.id.empty_list_item));

        List<TimebandModel> timebandList = TimebandManager.getAllTimeConditionByArea(getContext(), area_id);
        mTimeBandListAdapter = new TimeBandListAdapter(getActivity(), timebandList);

        timeConditionList.setAdapter(mTimeBandListAdapter);

        FloatingActionButton addTCButton = (FloatingActionButton) mView.findViewById(R.id.add_time_condition);
        addTCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeConditionPopup(area_id, mTimeBandListAdapter);

            }
        });

    }


    private void openTimeConditionPopup(final long area_id, final TimeBandListAdapter adapter){

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.timeband_insert_fragment, null);

        final CrystalRangeSeekbar timeRangeSeekbar = (CrystalRangeSeekbar) dialogView.findViewById(R.id.time_range_seekbar);
        final TextView timeRangeMinValue = (TextView) dialogView.findViewById(R.id.time_range_min_value);
        final TextView timeRangeMaxValue = (TextView) dialogView.findViewById(R.id.time_range_max_value);

        timeRangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                timeRangeMinValue.setText(String.format("%02d", (Long)minValue/2)+":"+String.format("%02d", ((Long)minValue%2)*30));
                timeRangeMaxValue.setText(String.format("%02d", (Long)maxValue/2)+":"+String.format("%02d", ((Long)maxValue%2)*30));
            }
        });



        alert.setPositiveButton(getResources().getString(R.string.general_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CrystalRangeSeekbar timeRangeSeekbar = (CrystalRangeSeekbar) dialogView.findViewById(R.id.time_range_seekbar);

                int startHour = timeRangeSeekbar.getSelectedMinValue().intValue()/2;
                int startMinute = timeRangeSeekbar.getSelectedMinValue().intValue()%2*30;
                int endHour = timeRangeSeekbar.getSelectedMaxValue().intValue()/2;
                int endMinute = timeRangeSeekbar.getSelectedMaxValue().intValue()%2*30;

                MultiSelectToggleGroup singleSelect = (MultiSelectToggleGroup) dialogView.findViewById(R.id.multi_day_selector);
                Set<Integer> checkedDays = singleSelect.getCheckedPositions();

                TimebandModel timebandModel = new TimebandModel(null,
                        area_id,
                        startHour,
                        startMinute,
                        endHour,
                        endMinute,
                        checkedDays.contains(0),
                        checkedDays.contains(1),
                        checkedDays.contains(2),
                        checkedDays.contains(3),
                        checkedDays.contains(4),
                        checkedDays.contains(5),
                        checkedDays.contains(6)
                );

                TimebandManager.saveTimeCondition(getContext(), timebandModel);
                adapter.addItem(timebandModel);
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.general_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setView(dialogView);
        alert.show();
    }
}

class TimeBandListAdapter extends ArrayAdapter<TimebandModel> {

    private List<TimebandModel> items;
    private Context mContext;


    public TimeBandListAdapter(Context context, List<TimebandModel> items) {
        super(context, R.layout.timeband_list_singleitem, items);
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

        int row_layout = R.layout.timeband_list_singleitem;
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

                TimebandManager.deleteTimeCondition(mContext, timebandModel.id);
                items.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }


}