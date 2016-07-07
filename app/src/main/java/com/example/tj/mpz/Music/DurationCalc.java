package com.example.tj.mpz.Music;

import java.util.ArrayList;

/**
 * Created by TJ on 2016-07-07.
 */
public class DurationCalc {

    long duration;
    ArrayList<Integer> arrayList;
    public DurationCalc(long duration) {
        this.duration = duration;
    }

    public int excute(){
        int second = (int)duration*1000;

        return second;
    }
}
