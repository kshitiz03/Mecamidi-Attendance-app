package com.mecamidi.www.mecamidiattendance;

import java.sql.Date;

public class Member {

    private String name;
    private String aadharid;
    private Date dateOfJoining;
    private int id;

    public Member(String name,String aadharid,Date date) {
        this.aadharid = aadharid;
        this.name = name;
        this.dateOfJoining = date;
    }

    public String getName() {
        return this.name;
    }

    public String getAadharid() {
        return this.aadharid;
    }

    public Date getDateOfJoining() {
        return this.dateOfJoining;
    }

}
