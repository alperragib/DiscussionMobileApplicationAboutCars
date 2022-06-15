package com.yazilimmuhendisim.arababam.app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import com.yazilimmuhendisim.arababam.app.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class UyelikSozlesmesi extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.uyelik_sozlesmesi,container,false);

        TextView textView = v.findViewById(R.id.textViewUserAgg);

        String okunan = dahiliOku("user_agr.txt");
        if(okunan != null && !okunan.isEmpty() && okunan.length()>1){
            textView.setText(okunan);
        }

        Button button = v.findViewById(R.id.buttonDialogKapat);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public String dahiliOku(String fileName){
        try {
            FileInputStream fis = getContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader okuyucu = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String satir = "";

            while ((satir = okuyucu.readLine()) != null){
                sb.append(satir+"\n");
            }
            okuyucu.close();
            return sb.toString();

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}