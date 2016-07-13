package com.example.tj.mpz.Music;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    public static final String TAG = "MusicListAdapter";
    private Context context;
    public List<MusicItem> mItems = new ArrayList<MusicItem>();
    private Cursor cursor;

    public MusicListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void showList() {
        String mData[];
        String title;
        String artist;
        String albumTitle;
        long musicId;

        cursor.moveToFirst();
        title = subString(cursor.getString(cursor.getColumnIndex("title")));  // 타이틀
        albumTitle = "AlbumTitle";
        artist = cursor.getString(cursor.getColumnIndex("artist"));
        musicId = Long.parseLong(cursor.getString(0));
        mItems.add(new MusicItem(musicId, title,albumTitle,artist));

            while (cursor.moveToNext()) {
                mData = new String[3];
                mData[0] = subString(cursor.getString(cursor.getColumnIndex("title"))); // 타이틀
                mData[1] = "AlbumTitle"; // 앨범
                mData[2] = cursor.getString(cursor.getColumnIndex("artist")); // 아티스트
                mItems.add(new MusicItem(Long.parseLong(cursor.getString(0)), mData));
            }

    }

    // 벨소리 등 의 문자 제거 메소드
    public String subString(String title){
        String returnString = "";
        if(title.contains("벨소리")){
            returnString = title.replace("벨소리","");
        }else if(title.contains("_후렴")){
            returnString = title.replace("_후렴","");
        }else if(title.contains("_앞부분")){
            returnString = title.replace("_앞부분","");
        }else if(title.contains("_뒷부분")){
            returnString = title.replace("_뒷부분","");
        }else {
            return title;
        }
        return returnString;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MusicItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).getId();
    }

    public void addItem(MusicItem musicItem){
        mItems.add(musicItem);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        PlayListView playListView;

        if(convertView == null){
            playListView = new PlayListView(context,mItems.get(position));

        }else {
            playListView = (PlayListView) convertView;
        }
      //  playListView.setIcon(mItems.get(position).getIcon());
        playListView.setText(0, mItems.get(position).getData(0));
        playListView.setText(1,mItems.get(position).getData(1));
        playListView.setText(2,mItems.get(position).getData(2));
        return playListView;
    }
}
