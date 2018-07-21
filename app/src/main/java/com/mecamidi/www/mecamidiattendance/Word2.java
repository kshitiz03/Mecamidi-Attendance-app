package com.mecamidi.www.mecamidiattendance;

public class Word2 {

    private String name;
    private String startdate;
    private String enddate;
    private String type;


    public Word2(String mname, String mstartdate, String menddate, String mtype) {
        name = mname;
        startdate = mstartdate;
        enddate = menddate;
        type = mtype;
    }

    public String getName() {
        return name;
    }

    public String getStartdate() {
        return startdate;
    }

    /**
     * Get the Miwok translation of the word.
     */
    public String getEnddate() {
        return enddate;
    }


    /**
     * Return the image resource ID of the word.
     */
    public String getType() {
        return type;
    }
}
