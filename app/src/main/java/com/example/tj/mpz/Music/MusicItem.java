package com.example.tj.mpz.Music;

import android.graphics.drawable.Drawable;

public class MusicItem {

    private boolean mSelectable;
    private Drawable mIcon;
    private String[] mItem;

    public MusicItem(Drawable icon, String[] mInfo){
        mIcon = icon;
        mItem = mInfo;
    }

    public MusicItem(Drawable icon, String musicTitle){
        mIcon = icon;

        mItem = new String[1];
        mItem[0] = musicTitle;

    }

    public MusicItem(Drawable icon, String musicTitle, String musicArtist){
        mIcon = icon;

        mItem = new String[2];
        mItem[0] = musicTitle;
        mItem[1] = musicArtist;
    }

    public MusicItem(Drawable icon, String musicTitle, String musicArtist, String musicAlbumTitle){
        mIcon = icon;

        mItem = new String[3];
        mItem[0] = musicTitle;
        mItem[1] = musicArtist;
        mItem[2] = musicAlbumTitle;
    }

    public MusicItem(String musicTitle, String musicAlbumTitle, String musicArtist){
        mItem = new String[3];
        mItem[0] = musicTitle;
        mItem[1] = musicAlbumTitle;
        mItem[2] = musicArtist;
    }

    public MusicItem(String[] musicItem){
        mItem = new String[3];
        mItem[0] = musicItem[0];
        mItem[1] = musicItem[1];
        mItem[2] = musicItem[2];
    }

    public String[] getData(){
        return mItem;
    }

    public String getData(int index){
        if(mItem == null || index >= mItem.length){
            return null;
        }

        return  mItem[index];
    }

    public boolean isSelectable(){
        return mSelectable;
    }

    public void setSelectable(boolean selectable){
        mSelectable = selectable;
    }

    public void setData(String[] obj){
        mItem = obj;
    }

    public void setIcon(Drawable icon){
        mIcon = icon;
    }

    public Drawable getIcon(){
        return mIcon;
    }
}
