package com.example.kimjipsa;

import java.util.Date;

/**
 * Created by User on 2017-06-01.
 */
public class Memo {
    private static String title;
    private static String voicetitle;
    private static int cnt=1;
    private static int memo_num;
    private static String text;
    private static Date save_date;

    public Date getSave_date() {
        return save_date;
    }

    public void setSave_date(Date save_date) {
        this.save_date = save_date;
    }
    public void setCnt(int CNT){this.cnt+=CNT;}
    public int getCnt(){return cnt;}
    public String getText() {

        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getMemo_num() {

        return memo_num;
    }

    public void setMemo_num(int memo_num) {
        this.memo_num = memo_num;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getVoicetitle() {

        return voicetitle;
    }

    public void setVoicetitle(int cnt) {
        this.voicetitle = "Voice"+cnt;
    }
}
