package com.yulin.ivan.putsker;

/**
 * Created by tyizchak on 8/21/2018.
 */

public class Student {
    private String name;
    private String phone;
    private Boolean hasGear;

    public Student(String name, String phone, Boolean hasGear) {
        this.name = name;
        this.phone = phone;
        this.hasGear = hasGear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getHasGear() {
        return hasGear;
    }

    public void setHasGear(Boolean hasGear) {
        this.hasGear = hasGear;
    }
}
