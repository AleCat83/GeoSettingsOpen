package com.alecat.geosettingsopen.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.manager.AreaHelper;
import com.alecat.geosettingsopen.model.AreaModel;

public class DialogAreaGeneralFragment extends Fragment {

    private Long mAreaID;

    public static DialogAreaGeneralFragment newInstance(Long areaID) {
        DialogAreaGeneralFragment fragment = new DialogAreaGeneralFragment();
        Bundle args = new Bundle();
        args.putLong("area_id", areaID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAreaID = getArguments().getLong("area_id");
        View view = inflater.inflate(R.layout.fragment_area_dialog_general, container, false);
        initItems(view);

        return view;
    }

    private void initItems(View view){

        final AreaModel area = AreaHelper.getArea(getContext(), mAreaID);

        EditText areaNameView = (EditText) view.findViewById(R.id.area_name);
        areaNameView.setText(area.name);
        areaNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveName(v.getText().toString());
                }
                return false;
            }
        });

        Spinner radiusSpinner = (Spinner) view.findViewById(R.id.area_radius);

        ArrayAdapter<CharSequence> adapterRadius = ArrayAdapter.createFromResource(
                getContext(), R.array.area_radius, android.R.layout.simple_spinner_item);
        adapterRadius.setDropDownViewResource(android.R.layout.simple_spinner_item);
        radiusSpinner.setAdapter(adapterRadius);

        radiusSpinner.setSelection(adapterRadius.getPosition(String.valueOf(area.radius)+" m"), false);

        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                saveRadius((String) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView thresholdView = (TextView) view.findViewById(R.id.area_threshold);
        thresholdView.setText(String.valueOf(area.threshold)+" m");

        TextView latView = (TextView) view.findViewById(R.id.area_latitude);
        latView.setText(String.format("%.8f", area.latitude));

        TextView longView = (TextView) view.findViewById(R.id.area_longitude);
        longView.setText(String.format("%.8f", area.longitude));

        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArea();
            }
        });

    }

    private void saveName(String name){

        AreaModel areaModel = AreaHelper.getArea(getContext(), mAreaID);
        areaModel.name = name;
        AreaHelper.saveArea(getContext(), areaModel);

    }

    private void saveRadius(String radius){

        AreaModel areaModel = AreaHelper.getArea(getContext(),mAreaID);
        areaModel.radius = Integer.valueOf(radius.substring(0, radius.length()-2));
        AreaHelper.saveArea(getContext(), areaModel);
    }

    private void deleteArea(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setTitle(getContext().getResources().getString(R.string.general_are_you_sure)+"?")
                .setMessage(getContext()
                        .getResources().getString(R.string.area_delete_area_message))
                .setPositiveButton(getContext().getResources().getString(R.string.general_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AreaHelper.deleteArea(getContext(), mAreaID);
                    }
                })
                .setNegativeButton(getContext().getResources().getString(R.string.general_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}