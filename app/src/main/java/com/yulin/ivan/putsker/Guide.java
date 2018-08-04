package com.yulin.ivan.putsker;

import java.util.Date;

/**
 * Created by tyizchak on 8/1/2018.
 */

public class Guide {
    String uid;
    String firstName;
    String lastName;
    String email;
    boolean senior;
    Date insuranceExpiration;
    Date licenseExpiration;

    public Guide(){

    }

    public Guide(String uid, String firstName, String lastName, String email, boolean senior, Date insuranceExpiration, Date licenseExpiration) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.senior = senior;
        this.insuranceExpiration = insuranceExpiration;
        this.licenseExpiration = licenseExpiration;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSenior() {
        return senior;
    }

    public void setSenior(boolean senior) {
        this.senior = senior;
    }

    public Date getInsuranceExpiration() {
        return insuranceExpiration;
    }

    public void setInsuranceExpiration(Date insuranceExpiration) {
        this.insuranceExpiration = insuranceExpiration;
    }

    public Date getLicenseExpiration() {
        return licenseExpiration;
    }

    public void setLicenseExpiration(Date licenseExpiration) {
        this.licenseExpiration = licenseExpiration;
    }
}

