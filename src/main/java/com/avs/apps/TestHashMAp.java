package com.avs.apps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestHashMAp {

    public static void main(String[] args) {
        List<String> adList = new ArrayList<String>();

        adList.add("300x600|300x250");
        adList.add("1280x220|970x250|970x90|728x90|320x100|320x50");
        adList.add("1280x220|970x250|970x90|728x90|320x100|320x50");
        adList.add("300x250");
        adList.add("300x250");
        int counter = 0;

        Map<String, Integer> myMap = new HashMap<String, Integer>();

        for (String entry : adList) {
            if (myMap.containsKey(entry)) {
                myMap.put(entry, myMap.get(entry) + 1);

            } else {
                myMap.put(entry, 1);
            }
        }
        System.out.println(myMap.get("300x250"));

    }
}
