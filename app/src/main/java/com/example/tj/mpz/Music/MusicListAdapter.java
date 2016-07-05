package com.example.tj.mpz.Music;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tjoeun on 2016-05-25.
 */
public class MusicListAdapter extends BaseAdapter {
    public static final String TAG = "MusicListAdapter";
    private Context context;
    private List<MusicItem> mItems = new ArrayList<MusicItem>();
    private Cursor cursor;


    public MusicListAdapter(Context context) {
        this.context = context;
    }

    public MusicListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void showList(Drawable icon){
        cursor.moveToFirst();
        mItems.add(new MusicItem(icon,cursor.getString(8)));

        while (cursor.moveToNext()){
            mItems.add(new MusicItem(icon,cursor.getString(8)));
        }
    }

    public void showList() {
        String mData[];
        String title;
        String artist = "복면가왕";
        String albumTitle = "여름";

        cursor.moveToFirst();
        title = cursor.getString(8);  // 타이틀
        mItems.add(new MusicItem(title,albumTitle,artist));

            while (cursor.moveToNext()) {
                mData = new String[3];
                mData[0] = cursor.getString(8);  // 타이틀
                mData[1] = albumTitle; // 앨범
                mData[2] = artist; // 아티스트
                mItems.add(new MusicItem(mData));
            }

    }

    public void itemCheck(){

        for(int i = 0; i<mItems.size();i++){
            Log.d(TAG,"타이틀은 "+mItems.get(i).getData(0)+ "가수는 "+mItems.get(i).getData(1) + "앨범명은 "+mItems.get(i).getData(2));
        }

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
        return 0;
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

    /*
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(0)_id
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(1)_data
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(2)_display_name
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(3)_size
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(4)mime_type
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(5)date_added
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(6)is_drm
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(7)date_modified
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(8)title
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(9)title_key
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(10)duration
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(11)artist_id
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(12)composer
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(13)album_id
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(14)track
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(15)year
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(16)is_ringtone
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(17)is_music
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(18)is_alarm
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(19)is_notification
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(20)is_podcast
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(21)bookmark
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(22)album_artist
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(23)artist_id:1
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(24)artist_key
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(25)artist
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(26)album_id:1
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(27)album_key
07-05 16:43:10.884 27187-27187/com.example.tj.mpz D/MusicListAdapter: 칼럼 인덱스 getString(28)album
     */
}
