package com.refreshgerator33obon;



public class WindowItem {
    private int id;
    private String name;
    private String endDate;
    private String dateDifference;

    public WindowItem(int id, String name, String endDate, String dateDifference) {
        this.id = id;
        this.name = name;
        this.endDate = endDate;
        this.dateDifference = dateDifference;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDateDifference() {
        return dateDifference;
    }
}