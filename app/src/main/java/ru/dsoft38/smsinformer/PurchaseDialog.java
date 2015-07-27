package ru.dsoft38.smsinformer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by diesel on 27.07.2015.
 */
public class PurchaseDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        /*
        final String[] catNamesArray = {"Васька", "Рыжик", "Мурзик"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("")
                // добавляем переключатели
                .setSingleChoiceItems(catNamesArray, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int item) {
                                Toast.makeText(
                                        getActivity(),
                                        "Любимое имя кота: "
                                                + catNamesArray[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        */


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_purchase, null);
        builder.setView(view);



        Button btnOk = (Button) view.findViewById(R.id.btnYes);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((About) getActivity()).okClicked();
            }
        });

        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((About) getActivity()).cancelClicked();
            }
        });


        return builder.create();

    }

}
