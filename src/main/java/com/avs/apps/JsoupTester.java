package com.avs.apps;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JsoupTester {


    // Host Pattern
    static final Pattern DOMAIN_PATTERN = Pattern.compile("^(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z][a-z0-9-]{0,61}[a-z0-9]$", Pattern.CASE_INSENSITIVE);
    // Whitelist Tags
    static final List<String> htmlTagsToProhibit = Arrays.asList("applet", "audio", "base", "embed", "font", "form", "frame", "frameset", "img", "inline", "object", "param", "video");
    static final List<String> unwrapTags = Arrays.asList("article_body", "articlecontent", "runtime|include", "runtime|topic");
    // Whitelist or Global Attributes
    static final List<String> globalAttrs = Arrays.asList("accesskey", "class", "data-", "dir", "dropzone", "draggable", "hidden", "id", "itemid", "itemprop", "itemref", "itemscope", "itemtype", "lang", "spellcheck", "style", "tabindex", "title", "translate");
    static final List<String> globalEventAttrs = Arrays.asList("onafterprint", "onbeforeprint", "onbeforeunload", "onerror", "onhashchange", "itemid", "itemprop", "itemref", "itemscope", "itemtype", "lang", "tabindex", "title", "translate");
    static final List<String> validAAttrs = Arrays.asList("border", "download", "href", "hreflang", "media", "name", "referrerpolicy", "rel", "role", "target", "tabindex", "type");
    static final List<String> validARelValue = Arrays.asList("alternate", "author", "bookmark", "help", "license", "next", "nofollow", "noreferrer", "prefetch", "prev", "search", "standout", "tag");
    static final List<String> validHrTagAttrs = globalAttrs;
    static final List<String> validPTagAttrs = globalAttrs;

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


        Document doc = Jsoup.parseBodyFragment(element);
        doc.outputSettings().prettyPrint(false);
        removeComments(doc);

        for (String htmlTagName : htmlTagsToProhibit) {
            Elements prohibitedTags = doc.select(htmlTagName);
            for (Element item : prohibitedTags) {
                item.remove();
            }
        }

        for (String unwrapTagName : unwrapTags) {
            Elements unwrapElements = doc.select(unwrapTagName);
            for (Element item : unwrapElements) {
                item.unwrap();
            }
        }

        Elements styleElements = doc.body().getElementsByTag("style");
        for (Element elem : styleElements) {
            elem.remove();
        }

        // a tag
        Elements aTags = doc.select("a");
        for (Element item : aTags) {
            Attributes aTagAttrs = item.attributes();
            for (Attribute attr : aTagAttrs) {
                if (!validAAttrs.contains(attr.getKey()) && !globalAttrs.contains(attr.getKey())) {
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
                } else if (item.attr("rel").isEmpty() || !validARelValue.contains(relValue)) {
                    item.removeAttr("rel");
                } else {
                    hasRel = true;
                }
            }

            String hrefValue = item.hasAttr("href") ? (item.attr("href")).replaceAll("\\\\", "/").trim() : null;
            if (hrefValue != null && !hrefValue.startsWith("mailto:")) {
                String scheme = "http";
                if (scheme == null || scheme.isEmpty()) {
                    scheme = "http";
                }
                if (hrefValue.startsWith("//")) {
                    hrefValue = scheme + ":" + hrefValue;
                } else if (hrefValue.startsWith("www")) {
                    hrefValue = scheme + "://" + hrefValue;
                } else if (!hrefValue.contains("://")) {
                    String siteUrl = "http://nydailynews.com";
                    if (siteUrl != null && !siteUrl.isEmpty()) {
                        hrefValue = siteUrl + (hrefValue.startsWith("/") ? hrefValue : "/" + hrefValue);
                    }
                } else if (hrefValue.startsWith("http://.")) {
                    hrefValue = scheme + "://" + hrefValue;
                }
                if (isValidURL(hrefValue)) {
                    item.attr("href", hrefValue);
                    item.attr("target", "_blank");
                    if (hasRel) {
                        item.attr("rel", relValue.concat(" noopener"));
                    } else {
                        item.attr("rel", "noopener");
                    }
                } else {
                    item.unwrap();
                }
            }

            if (hrefValue != null && hrefValue.startsWith("mailto:")) {
                item.attr("target", "_blank");
            }

            if ((!item.hasAttr("href")) && item.hasAttr("target") && !item.attr("target").toLowerCase().trim().equals("_blank")) {
                item.removeAttr("target");
            }
        }
        // hr tag
        Elements hrTags = doc.select("hr");
        for (Element item : hrTags) {
            Attributes hrTagAttrs = item.attributes();
            for (Attribute attr : hrTagAttrs) {
                if (!validHrTagAttrs.contains(attr.getKey())) {
                    item.removeAttr(attr.getKey());
                }
            }
        }
        // input tag
        Elements inputTags = doc.select("input");
        for (Element item : inputTags) {
            if (item.hasAttr("type") && !item.attr("type").toLowerCase().trim().equals("button")) {
                item.remove();
            }
        }
        // meta tag
        Elements metaTags = doc.select("meta");
        for (Element item : metaTags) {
            if (item.hasAttr("http-equiv")) {
                item.removeAttr("http-equiv");
            }
//            if (item.hasAttr("charset")) {
//                item.removeAttr("charset");
//            }
        }
        // p tag
        Elements pTags = doc.select("p");
        for (Element item : pTags) {
            Attributes pTagAttrs = item.attributes();
            for (Attribute attr : pTagAttrs) {
                if (!validPTagAttrs.contains(attr.getKey())) {
                    item.removeAttr(attr.getKey());
                }
            }
        }

        return doc.toString();
    }

    public static void main(String[] args) {

        System.out.println("were are here hoooooooooo");

        final String html = "<meta charset=\"UTF-8\"/>";


        System.out.println( JsoupTester.ampSanitize(html));

//        Document doc = Jsoup.parseBodyFragment(html);
//        doc.outputSettings().prettyPrint(false);

       // System.out.println(doc.body().html());

//        String html = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><p>An <a href='http://example.com/'><b>example</b></a> link.</p><frameset></frameset><input type='button'></input>";
//
//        TestJsoup.ampSanitize(html);
//
//
//        System.out.println("We are ffhere " + TestJsoup.ampSanitize(html));

    }
}
