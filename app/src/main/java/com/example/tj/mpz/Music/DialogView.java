package com.example.tj.mpz.Music;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.tj.mpz.R;

/**
 * Created by TJ on 2016-07-11.
 */
public class DialogView extends LinearLayout {

    EditText fileName;
    public DialogView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_view,this,true);

        fileName = (EditText)findViewById(R.id.fileName);
    }

    public LinearLayout getDialogView(){
        return this;
    }

    public String getFileName(){
        return fileName.getText().toString();
    }


}
