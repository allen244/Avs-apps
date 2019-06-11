package com.avs.apps;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsoupInlineLinks {

    public static String stripDomain(String contentUrl) {

        if (null != contentUrl && contentUrl != "") {

            if (contentUrl.startsWith("https")) {
                contentUrl = contentUrl.replaceAll("https://.+?/(.*)", "/$1");
            } else if (contentUrl.startsWith("http")) {
                contentUrl = contentUrl.replaceAll("http://.+?/(.*)", "/$1");
            }
        }
        return contentUrl;
    }
//
//    public static String getBaseDomain(String contentUrl) {
//
//        if (null != contentUrl && contentUrl != "") {
//
//            if (contentUrl.startsWith("https")) {
//                contentUrl = contentUrl.replaceAll("https://", "");
//                contentUrl=contentUrl.substring(0, contentUrl.indexOf("/"));
//            } else if (contentUrl.startsWith("http")) {
//                contentUrl = contentUrl.replaceAll("http://", "").substring(0, contentUrl.indexOf("/"));
//            }
//        }
//        return contentUrl;
//    }


    public static String getBaseDomain(String contentUrl) {
        URL url = null;
        try {
            url = new URL(contentUrl);
            return url.getHost();
        } catch (MalformedURLException e) {
            return contentUrl;
        }
    }

    public static void main(String[] args) {


        //test
        String s = "https://www.nydailynews.com/";
        System.out.println(s.replaceAll("https://", ""));

        String marketDomains = "www.latimes.com,www.chicagotribune.com,www.nydailynews.com,www.courant.com";
        final String NAV_TYPE = "#instory-link";
        final String HREF = "href";
        final String A = "a";
        String siteUrl = "https://www.nydailynews.com";

        System.out.println("were are here hoooooooooo");
        final String output = "<a href=\"https://www.nydailynews.com/entertainment/broadway/ny-ent-lifespan-fact-broadway-review-1019-story.html\" target=\"_blank\">The Lifespan of a Fact</a>";
        Document doc = Jsoup.parseBodyFragment(output);
        doc.outputSettings().prettyPrint(false);
        Elements aHrefs = doc.select("a[href]");
        for (Element item : aHrefs) {

            String link = JsoupInlineLinks.getBaseDomain(item.attr(HREF));
            List<String> globalAttrs = Arrays.asList(marketDomains.split(","));
            System.out.println();
            if (globalAttrs.contains(link)) {
                item.attr("href", item.attr(HREF).concat(NAV_TYPE));
                System.out.println(item.attr(HREF));
            }
        }
        // System.out.println(doc.body().html());
    }
}
