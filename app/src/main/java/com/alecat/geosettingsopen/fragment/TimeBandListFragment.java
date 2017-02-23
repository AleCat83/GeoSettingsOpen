package com.alecat.geosettingsopen.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.adapter.TimebandListAdapter;
import com.alecat.geosettingsopen.helper.TimebandHelper;
import com.alecat.geosettingsopen.models.TimebandModel;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;

import java.util.List;
import java.util.Set;

public class TimebandListFragment extends Fragment {

    private View mView;
    private TimebandListAdapter mTimeBandListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_timeband_list, container, false);

        Long area_id = getArguments().getLong("area_id");
        init(area_id);
        return mView;

    }

    private void init(final Long area_id){

        ListView timeConditionList = (ListView) mView.findViewById(R.id.time_conditions_list);
        timeConditionList.setEmptyView(mView.findViewById(R.id.empty_list_item));

        List<TimebandModel> timebandList = TimebandHelper.getAllTimeConditionByArea(getContext(), area_id);
        mTimeBandListAdapter = new TimebandListAdapter(getActivity(), timebandList);

        timeConditionList.setAdapter(mTimeBandListAdapter);

        FloatingActionButton addTCButton = (FloatingActionButton) mView.findViewById(R.id.add_time_condition);
        addTCButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeConditionPopup(area_id, mTimeBandListAdapter);

            }
        });

    }


    private void openTimeConditionPopup(final long area_id, final TimebandListAdapter adapter){

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.fragment_timeband_create, null);

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

                TimebandHelper.saveTimeCondition(getContext(), timebandModel);
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