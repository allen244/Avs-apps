package com.avs.apps;

public class Tester {

    public static void main(String[] args) {
        String test ="data-block-on-consent =\"\"";
        String globalAttrs="data-block-on-consent";
        if (!test.contains(globalAttrs)) {
            System.out.println("here");
        }
    }
}
