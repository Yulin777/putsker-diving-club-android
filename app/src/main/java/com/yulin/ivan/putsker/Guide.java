package com.yulin.ivan.putsker;

public class Guide {
    String uid;
    String name;
    String email;
    boolean senior;
    String insuranceExpiration;
    String licenseExpiration;

    public Guide() {

    }

    public Guide(String uid, String name, String email, boolean senior, String insuranceExpiration, String licenseExpiration) {
        this.uid = uid;
        this.name = name;
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

//    public Date getInsuranceExpiration() {
//        return insuranceExpiration;
//    }
//
//    public void setInsuranceExpiration(Date insuranceExpiration) {
//        this.insuranceExpiration = insuranceExpiration;
//    }
//
//    public Date getLicenseExpiration() {
//        return licenseExpiration;
//    }
//
//    public void setLicenseExpiration(Date licenseExpiration) {
//        this.licenseExpiration = licenseExpiration;
//    }
}

