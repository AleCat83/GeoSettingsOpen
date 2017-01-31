package com.alecat.geosettingsopen.dialogs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alecat.geosettingsopen.R;
import com.alecat.geosettingsopen.engine.AreaTrainer;
import com.alecat.geosettingsopen.managers.AreaManager;

public class DialogAreaThirdTab extends Fragment {

    private Long mAreaID;
    private View mView;

    public static DialogAreaThirdTab newInstance(Long areaID) {
        DialogAreaThirdTab fragment = new DialogAreaThirdTab();
        Bundle args = new Bundle();
        args.putLong("area_id", areaID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAreaID = getArguments().getLong("area_id");
        mView = inflater.inflate(R.layout.dialog_area_third_tab, container, false);
        initItems(mView);

        return mView;
    }

    private void initItems(final View view){

        if(AreaManager.getCurrentArea(getContext()).equals(mAreaID)){
            if (AreaTrainer.isTrainingActive(getContext())){
                setTrainingEnabled(true);
            }
            else{
                setTrainingEnabled(false);
            }
        }
        else{
            TextView trainingMessageView = (TextView) mView.findViewById(R.id.training_message);
            trainingMessageView.setText(getResources().getString(R.string.area_training_current_area_warning));
        }
    }

    private void  setTrainingEnabled(boolean enabled){

        if(enabled){
            RelativeLayout trainingTimeContainer = (RelativeLayout) mView.findViewById(R.id.training_time_container);
            trainingTimeContainer.setVisibility(View.INVISIBLE);



            Button toogleButton = (Button) mView.findViewById(R.id.training_enable);
            toogleButton.setText(getResources().getString(R.string.area_training_disable));


            toogleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AreaTrainer.stopTraining(getContext());
                    setTrainingEnabled(false);
                }
            });

        }
        else{
            RelativeLayout trainingTimeContainer = (RelativeLayout) mView.findViewById(R.id.training_time_container);
            trainingTimeContainer.setVisibility(View.VISIBLE);

            Spinner trainingTimeSpinner = (Spinner) mView.findViewById(R.id.training_time);

            ArrayAdapter<CharSequence> adapterTrainingTime = ArrayAdapter.createFromResource(
                    getContext(), R.array.training_time, android.R.layout.simple_spinner_item);
            adapterTrainingTime.setDropDownViewResource(android.R.layout.simple_spinner_item);
            trainingTimeSpinner.setAdapter(adapterTrainingTime);

            trainingTimeSpinner.setSelection(1, false);

            Button toogleButton = (Button) mView.findViewById(R.id.training_enable);
            toogleButton.setText(getResources().getString(R.string.area_training_enable));

            toogleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Spinner trainingTimeSpinner = (Spinner) mView.findViewById(R.id.training_time);
                    AreaTrainer.startTraining(getContext(), mAreaID, Long.valueOf(trainingTimeSpinner.getSelectedItem().toString()));

                    setTrainingEnabled(true);
                }
            });

        }

        RelativeLayout trainingButtonContainer = (RelativeLayout) mView.findViewById(R.id.training_enable_container);
        trainingButtonContainer.setVisibility(View.VISIBLE);

        RelativeLayout trainingMessageContainer = (RelativeLayout) mView.findViewById(R.id.training_message_container);
        trainingMessageContainer.setVisibility(View.GONE);

    }
}

