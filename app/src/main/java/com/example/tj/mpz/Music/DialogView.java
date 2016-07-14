package com.example.tj.mpz.Music;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tj.mpz.R;

// save 시에 Dialog창을 띄우는 클래스
public class DialogView extends LinearLayout {

    EditText fileName;
    TextView mrInfo;
    public DialogView(final Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dialog_view,this,true);

        fileName = (EditText)findViewById(R.id.fileName);
        mrInfo = (TextView)findViewById(R.id.mrInfo);
    }

    public LinearLayout getDialogView(){
        return this;
    }

    public String getFileName(){
        return fileName.getText().toString();
    }

    public void setMrInfo(String title){
        this.mrInfo.setText(title);
    }

}
