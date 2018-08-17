package com.avs.apps;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class JsoupTester {

    public static boolean isValidURL(String url) {

        URL u = null;

        try {
            u = new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }

        try {
            u.toURI();
        } catch (URISyntaxException e) {
            return false;
        }

        return true;
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size(); ) {
            Node child = node.childNode(i);
            if (child instanceof Comment)
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    public static String ampSanitize(String element) {


        String vaildAAttrs = "download,href,hreflang,media,ping,referrerpolicy,rel,target,type,class,dir,hidden,id,itemid,itemprop,itemref,itemscope,itemtype,lang,name,tabindex,title,data-,";
        String vaildARelValue = "alternate,author,bookmark,help,license,next,nofollow,noreferrer,prefetch,prev,search,tag,standout,";
        String htmlTags = "base,frame,frameset,object,param,applet,embed,form,img,video,audio,inline";
//
//        Whitelist siteList = new Whitelist();
//       // siteList.addTags(htmlTags);
//        //siteList.addAttributes(vaildAAttrs);
        // System.out.println( Jsoup.clean(element, siteList));


        Document doc = Jsoup.parseBodyFragment(element);
        doc.outputSettings().prettyPrint(false);
        removeComments(doc);


        for (String htmlTagName : htmlTags.split(",")) {
            Elements prohibitedTags = doc.select(htmlTagName);
            for (Element item : prohibitedTags) {
                item.remove();
            }
        }

        Elements inputTags = doc.select("input");
        for (Element item : inputTags) {
            if (item.hasAttr("type")
                    && !item.attr("type").toLowerCase().trim().equalsIgnoreCase("button")) {
                item.remove();
            }
        }

        Elements metaTags = doc.select("meta");
        for (Element item : metaTags) {
            if (item.hasAttr("http-equiv")) {
                item.removeAttr("http-equiv");
            }
        }

        Elements aTags = doc.select("a");

        for (Element item : aTags) {
            Attributes aTagAttrs = item.attributes();
            for (Attribute attr : aTagAttrs) {
                if (!vaildAAttrs.contains(attr.getKey())) {
                    item.removeAttr(attr.getKey());
                }
            }
            String relValue = "";
            boolean hasRel = false;
            if (item.hasAttr("rel")) {
                relValue = item.attr("rel").toLowerCase().trim();
                if (relValue.indexOf("nof") >= 0) {

                    item.attr("rel", "nofollow");
                    hasRel = true;
                    relValue = "nofollow";
                } else if (item.attr("rel").isEmpty() || !vaildARelValue.contains(relValue + ",")) {

                    item.removeAttr("rel");
                } else {
                    hasRel = true;
                }
            }
        }

        return doc.toString();
    }

    public static void main(String[] args) {

        System.out.println("were are here yooo");

        final String html = "<p><b>ThisFF  <i>is</i></b> <i>my sentence</i> of text.</p>";
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings().prettyPrint(false);

        System.out.println(doc.body().html());

//        String html = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><p>An <a href='http://example.com/'><b>example</b></a> link.</p><frameset></frameset><input type='button'></input>";
//
//        TestJsoup.ampSanitize(html);
//
//
//        System.out.println("We are ffhere " + TestJsoup.ampSanitize(html));

    }
}
