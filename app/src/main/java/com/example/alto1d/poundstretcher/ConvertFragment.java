package com.example.alto1d.poundstretcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;



/**
 * Created by alto1d on 12/06/15.
 */
public class ConvertFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder theDialog = new AlertDialog.Builder(getActivity());
        final MainActivity act = (MainActivity) getActivity();
        String finalResult = null;
        finalResult = act.getData();
        if(finalResult!=null) {
            theDialog.setTitle(act.getString(R.string.conversion_done));
            theDialog.setMessage(act.getString(R.string.converted_to) + finalResult + "BGN");
            theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), act.getString(R.string.you_like_the_rate), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            theDialog.setTitle(act.getString(R.string.conversion_aborted));
            theDialog.setMessage(act.getString(R.string.only_numeric));
            theDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), act.getString(R.string.enter_new_value), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return theDialog.create();
    }



}
