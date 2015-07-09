package com.java.model;

/**
 * Created by derlucci on 7/9/15.
 */
public class Children implements Comparable<Children>{
    public InnerData data;

    @Override
    public int compareTo(Children o) {

        int cmp = data.score < o.data.score ? 1 : data.score > o.data.score ? -1 : 0;

        return cmp;
    }
}
