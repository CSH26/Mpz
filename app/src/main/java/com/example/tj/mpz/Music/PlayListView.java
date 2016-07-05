package com.example.tj.mpz.Music;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tj.mpz.R;

/**
 * Created by tjoeun on 2016-05-25.
 */
public class PlayListView extends LinearLayout {

   // private ImageView mIcon;
    private TextView musicTitle;
    private TextView musicArtist;
    private TextView musicAlbumTitle;

    public PlayListView(Context context, MusicItem mItem){
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem,this,true);

      //  mIcon = (ImageView)findViewById(R.id.iconItem);   이미지가 있다면 이미지 뷰로 추가
     //   mIcon.setImageDrawable(mItem.getIcon());

        musicTitle = (TextView)findViewById(R.id.musicTitle);
        musicTitle.setText(mItem.getData(0));
        musicArtist = (TextView)findViewById(R.id.musicAlbumTitle);
        musicArtist.setText(mItem.getData(1));
        musicAlbumTitle = (TextView)findViewById(R.id.musicArtist);
        musicAlbumTitle.setText(mItem.getData(2));
    }

    public void setText(int index, String data){
        if(index == 0){
            musicTitle.setText(data);
        }
        else if(index==1){
            musicArtist.setText(data);
        }
        else if(index==2){
            musicAlbumTitle.setText(data);
        }
        else
        {
            throw new IllegalArgumentException();

        }
    }

    /*public void setIcon(Drawable icon){
        mIcon.setImageDrawable(icon);
    }*/
}
