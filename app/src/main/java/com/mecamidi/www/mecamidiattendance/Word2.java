package com.mecamidi.www.mecamidiattendance;

public class Word2 {

    private String name;
    private String startdate;
    private String enddate;
    private String type;
    private String desc;
    private int leaveId;

    public Word2(String mname, String mstartdate, String menddate, String mtype,String desc,int leaveId) {
        name = mname;
        startdate = mstartdate;
        enddate = menddate;
        type = mtype;
        this.desc = desc;
        this.leaveId = leaveId;
    }

    public String getName() {
        return name;
    }

    public String getStartdate() {
        return startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public int getLeaveId() {
        return leaveId;
    }
}
