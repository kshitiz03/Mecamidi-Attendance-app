package com.mecamidi.www.mecamidiattendance;

public class Word1 {

    private String date;
    private String punchin;
    private String punchout;
    private String status;


    public Word1(String mdate, String mpunchin, String mpunchout, String stat) {
        date = mdate;
        punchin = mpunchin;
        punchout = mpunchout;
        status = stat;
    }

    public String getDate() {
        return date;
    }

    /**
     * Get the Miwok translation of the word.
     */
    public String getPunchin() {
        return punchin;
    }

    public String getPunchout() {
        return punchout;
    }


    /**
     * Return the image resource ID of the word.
     */
    public String getStatus() {
        return status;
    }
}