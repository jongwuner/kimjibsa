package com.example.kimjipsa;

/**
 * Created by HaJinHo on 2017-06-02.
 */
import android.app.Application;
public class Gloval extends Application {
    private static String cycleTime;
    private static int time;
    private static boolean popup;

    @Override
    public void onCreate() {
        //전역 변수 초기화
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setTime(int Time) {
        Gloval.time = Time;
    }

    public static int getTime() {
        if(time!=0){
            return time;}
        else{
            return 1;
        }
    }

    public static void setPopup(boolean Popup) {
        Gloval.popup = Popup;
    }

    public static boolean getPopup(){
        return popup;
    }
    public static void setCycleTime(String CycleTime){
        int  t= Integer.parseInt(CycleTime);
        t=t*6000;
        Gloval.cycleTime=String.valueOf(t);
    }
    public static String getCycleTime(){
        if(cycleTime!="0") {
            return cycleTime;
        }else
            return "6000";
    }
}
