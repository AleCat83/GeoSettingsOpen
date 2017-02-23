package com.alecat.geosettingsopen.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.alecat.geosettingsopen.helper.AreaHelper;
import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.helper.ProfileHelper;
import com.alecat.geosettingsopen.models.AreaModel;
import com.alecat.geosettingsopen.models.ProfileModel;

import java.util.ArrayList;
import java.util.List;

public class DialogAreaGeneralFragment extends Fragment {

    private Long mAreaID;

    private List<Long> mProfileIDList = new ArrayList<>();
    private List<String> mProfileLabelList = new ArrayList<>();

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

        Context ctx = getContext();

        final AreaModel area = AreaHelper.getArea(ctx, mAreaID);

        EditText areaNameView = (EditText) view.findViewById(R.id.area_name);
        areaNameView.setText(area.name);
        areaNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                saveName(s.toString());
            }
        });

        Spinner radiusSpinner = (Spinner) view.findViewById(R.id.area_radius);

        ArrayAdapter<CharSequence> adapterRadius = ArrayAdapter.createFromResource(
                ctx, R.array.area_radius, android.R.layout.simple_spinner_item);
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

        //init exit profile spinner

        AreaModel areaModel = AreaHelper.getArea(ctx, mAreaID);
        List<ProfileModel> profileList = ProfileHelper.getAllProfiles(ctx);

        mProfileLabelList.add(ctx.getResources().getString(R.string.profile_default_profile));
        mProfileIDList.add(ProfileHelper.DEFAUL_PROFILE);

        mProfileLabelList.add(ctx.getResources().getString(R.string.profile_no_profile));
        mProfileIDList.add(ProfileHelper.NO_PROFILE);

        for (int i=0; i<profileList.size(); i++) {
            mProfileIDList.add(profileList.get(i).id);
            String profileName = profileList.get(i).name;
            if(profileName.length() > 15){
                profileName = profileName.substring(0,15)+"...";
            }

            mProfileLabelList.add(profileName);

        }

        Spinner profileChooserSpinner = (Spinner) view.findViewById(R.id.profile_chooser);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx,
                android.R.layout.simple_spinner_item,
                mProfileLabelList);

        profileChooserSpinner.setAdapter(adapter);

        profileChooserSpinner.setSelection(mProfileIDList.indexOf(areaModel.exit_profile), false);

        profileChooserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                saveExitProfile(mProfileIDList.get(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //init delete button
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_button);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArea();
            }
        });

    }

    private void saveName(String name){

        Context ctx = getContext();
        AreaModel areaModel = AreaHelper.getArea(ctx, mAreaID);
        areaModel.name = name;
        AreaHelper.saveArea(ctx, areaModel);

    }

    private void saveRadius(String radius){

        Context ctx = getContext();
        AreaModel areaModel = AreaHelper.getArea(ctx,mAreaID);
        areaModel.radius = Integer.valueOf(radius.substring(0, radius.length()-2));
        AreaHelper.saveArea(ctx, areaModel);
    }

    private void saveExitProfile(Long profileID){

        Context ctx = getContext();
        AreaModel areaModel = AreaHelper.getArea(ctx, mAreaID);
        areaModel.exit_profile = profileID;
        AreaHelper.saveArea(ctx, areaModel);
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