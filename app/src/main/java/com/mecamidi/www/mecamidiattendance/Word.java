/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mecamidi.www.mecamidiattendance;

/**
 * {@link Word} represents a vocabulary word that the user wants to learn.
 * It contains a default translation, a Miwok translation.
 */
public class Word {

    /** Default translation for the word */
    private String startdate;

    /** Miwok translation for the word */
    private String enddate;

    /** Audio resource ID for the word */
    private String status;

    private int id;


    public Word(String start, String end, String stat,int id) {
        startdate = start;
        enddate =end;
        status = stat;
        this.id = id;
    }
    /**
     * Get the default translation of the word.
     */
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
    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

}