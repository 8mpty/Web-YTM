package com.empty.simplewebytm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.empty.simplewebytm.Services.DownloadService;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

public class CustomDialogs extends Dialog{
    private final Context context;
    private AlertDialog alertDialog;
    private final AlertDialog.Builder builder;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = Const_Prefs.MAIN_SHARED_PREFS;
    private static final String TOOLBAR = Const_Prefs.TOOLBAR;


    private EditText txtUrl;
    SwitchMaterial sw_list,sw_feature;
    String choice;
    AutoCompleteTextView actv;
    private String newUrl;
    public static String title;

    public CustomDialogs(@NonNull Context context) {
        super(context);
        this.context = context;

        pref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        editor = pref.edit();
        builder = new AlertDialog.Builder(context, R.style.DialogBackground);

    }

    public void HideToolbar() {
        builder.setTitle("Hide Toolbar")
                .setMessage("! IMPORTANT ! " +
                        "\n\nTo UNHIDE the Toolbar, Long press any blank space. ")
                .setPositiveButton("OK", (dialog, which) -> editor.putBoolean(TOOLBAR, true).apply())
                .setNegativeButton("Cancel", (dialog, which) -> {
                    editor.putBoolean(TOOLBAR, false).apply();
                    alertDialog.dismiss();
                });

        alertDialog = builder.create();
        alertDialog.show();

        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.85);
        alertDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    public void DownloadAudio(String oriurl){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.download_dialog, null);


        TextInputLayout lay_link = view.findViewById(R.id.textInputLayout);
        TextInputLayout lay_types = view.findViewById(R.id.types);
        ConstraintLayout cl_list = view.findViewById(R.id.sw_list);
        ConstraintLayout cl_feature = view.findViewById(R.id.sw_feature);

        txtUrl = view.findViewById(R.id.link_txt);
        sw_list = view.findViewById(R.id.dw_switch);
        sw_feature = view.findViewById(R.id.dw_switch2);
        actv = view.findViewById(R.id.typeTxt);


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, R.layout.list_item, context.getResources().getStringArray(R.array.dw_types));
        actv.setAdapter(adapter);

        actv.setOnItemClickListener((parent, view1, position, id) -> choice =  parent.getItemAtPosition(position).toString());

        lay_link.setHint(title);
        txtUrl.setText(oriurl);
        txtUrl.setKeyListener(null);
        actv.setKeyListener(null);

        cl_list.setVisibility(View.VISIBLE);
        cl_feature.setVisibility(View.GONE);

        lay_types.setVisibility(View.VISIBLE);
        choice = adapter.getItem(0).toString();

        /* https://stackoverflow.com/questions/19377262/regex-for-youtube-url */
        if(oriurl.contains("&list")){
            HelperTextListener(lay_link,"*Contains Lists!*",R.color.red);
            sw_list.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    String[] removeList = oriurl.split("&[A-Za-z]+=",2); // remove &list=
                    newUrl = removeList[0];
                    txtUrl.setText(newUrl);
                    HelperTextListener(lay_link,"*URL Looks Good!*",R.color.teal_200);

                }else{
                    txtUrl.setText(oriurl);
                    HelperTextListener(lay_link,"*Contains Lists!*",R.color.red);
                }
            });
        }else if (!oriurl.contains("&list") && oriurl.contains("watch?v=") ||
                !oriurl.contains("&list") && !oriurl.contains("&feature") && oriurl.contains("watch?v=")){
            HelperTextListener(lay_link,"*URL Looks Good!*",R.color.teal_200);
            cl_list.setVisibility(View.GONE);
        }else{
            HelperTextListener(lay_link,"*Invalid URL!*",R.color.red);
            cl_list.setVisibility(View.GONE);
            lay_types.setVisibility(View.GONE);
        }

        builder.setView(view)
                .setPositiveButton("OK",(dialog, which) -> {})
                .setNegativeButton("Cancel",(dialog, which) -> {});

        alertDialog = builder.create();
        alertDialog.show();

        //https://stackoverflow.com/questions/40055504/unable-to-set-the-width-of-custom-dialog-in-android
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.85);
        alertDialog.getWindow().setLayout(width, RelativeLayout.LayoutParams.WRAP_CONTENT);

        Button posBtn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negBtn = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        posBtn.setTextColor(Color.parseColor("#FF03DAC5"));
        negBtn.setTextColor(Color.parseColor("#FF03DAC5"));


        posBtn.setOnClickListener(v -> {
            if(!txtUrl.getText().toString().contains("watch?v=")){
                Toast.makeText(context,"Invalid URL",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }else{
                DownloadService ds = new DownloadService(context);
                ds.DownloadCall(txtUrl.getText().toString(),choice);
                Toast.makeText(context,"Downloading " + choice + " " + title,Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        negBtn.setOnClickListener(v -> alertDialog.dismiss());
    }

    private void HelperTextListener(TextInputLayout til,String text, int color){
        til.setHelperText(text);
        til.setHelperTextColor(ColorStateList.valueOf(context.getResources().getColor(color, context.getTheme())));
    }
}
