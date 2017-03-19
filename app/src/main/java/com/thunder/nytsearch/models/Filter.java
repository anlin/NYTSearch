package com.thunder.nytsearch.models;

import java.util.Date;

/**
 * Created by anlinsquall on 19/3/17.
 */

public class Filter {
    public Date beginDate;
    public String sortOrderBy;
    public boolean isArts;
    public boolean isFashionSytles;
    public boolean isSports;

    @Override
    public String toString() {
        return "Filter{" +
                "beginDate=" + beginDate +
                ", sortOrderBy='" + sortOrderBy + '\'' +
                ", isArts=" + isArts +
                ", isFashionSytles=" + isFashionSytles +
                ", isSports=" + isSports +
                '}';
    }
}
