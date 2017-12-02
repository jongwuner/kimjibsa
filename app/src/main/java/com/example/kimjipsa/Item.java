package com.example.kimjipsa;

/**
 * Created by User on 2017-06-01.
 */
public class Item {
    private String item_name;
    private int weight;
    private boolean bool_item;
    private String sub_name;

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public boolean isBool_item() {

        return bool_item;
    }

    public void setBool_item(boolean bool_item) {
        this.bool_item = bool_item;
    }

    public int getWeight() {

        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getItem_name() {

        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }
}
