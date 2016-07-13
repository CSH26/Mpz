package com.example.tj.mpz.Music;

// 음원파일의 duration을 밀리세컨드로 받아들여 분과 초로 계산하는 클래스
public class DurationCalc {
    long duration;
    String sSecond;
    String sMinute;
    int maxSecond;

    public DurationCalc() {
        sSecond = "";
        sMinute = "";
    }

    public DurationCalc(long duration) {
        this.duration = duration;
        sSecond = "";
        sMinute = "";
        maxSecond = (int)duration/1000;
    }

    public void excute(){
        int second = (int)duration/1000;
        int minute = 0;

        while(true){
            if(second >= 60){
                minute++;
                second -= 60;
            }
            else{
                break;
            }
        }
        if(minute<10)
            sMinute = "0"+minute;
        else
            sMinute = Integer.toString(minute);

        if(second<10)
            sSecond = "0"+second;
        else
            sSecond = Integer.toString(second);

        setsSecond(sSecond);
        setsMinute(sMinute);
    }

    public void setDuration(int duration) {
        this.duration = (long) duration;
    }

    public int getDurationCalc(){
        return (int)duration/1000;
    }

    public String getsSecond() {
        return sSecond;
    }

    public void setsSecond(String sSecond) {
        this.sSecond = sSecond;
    }

    public String getsMinute() {
        return sMinute;
    }

    public void setsMinute(String sMinute) {
        this.sMinute = sMinute;
    }

    public int getMaxSecond() {
        return maxSecond;
    }
}
