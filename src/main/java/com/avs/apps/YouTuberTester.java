package com.avs.apps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YouTuberTester {

    public static void main(String[] args) {
        String[] youtubeUrls = {
                "https://www.youtube.com/watch?v=hzgtpUFzxcU&feature=youtu.be",
                "https://youtu.be/QXAho8vlmAI",
                "https://www.youtube.com/watch?v=UzRtrjyDwx0",
                "https://youtu.be/6butf1tEVKs?t=22s",
                "https://youtu.be/R46-XgqXkzE?t=2m52s",
                "http://youtu.be/dQw4w9WgXcQ",
                "http://www.youtube.com/?v=dQw4w9WgXcQ",
                "http://www.youtube.com/?v=dQw4w9WgXcQ&feature=player_embedded",
                "http://www.youtube.com/watch?v=dQw4w9WgXcQ",
                "http://www.youtube.com/watch?v=dQw4w9WgXcQ&feature=player_embedded",
                "http://www.youtube.com/v/dQw4w9WgXcQ",
                "http://www.youtube.com/e/dQw4w9WgXcQ",
                "http://www.youtube.com/embed/dQw4w9WgXcQ"
        };

        String pattern = "\\W([\\w-]{9,})(\\W|$)";
        Pattern pattern2 = Pattern.compile(pattern);

        for (int i=0; i<youtubeUrls.length; i++){
            Matcher matcher2 = pattern2.matcher(youtubeUrls[i]);
            if (matcher2.find()){
                System.out.println(matcher2.group(1));
            }
            else System.out.println("Not found");
        }
    }
}
