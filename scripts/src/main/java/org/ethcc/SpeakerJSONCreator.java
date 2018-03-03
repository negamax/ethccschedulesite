package org.ethcc;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mohitaggarwal on 03/03/2018.
 */
public class SpeakerJSONCreator {
    public static void main(String[] args) throws IOException {
        File rawFile = new File("./data/speakers.raw");

        assert rawFile.exists();

        Document document = Jsoup.parse(rawFile, "UTF-8");
        Elements speakerNodes = document.getElementsByClass("inner");

        JSONArray speakersJson = new JSONArray();

        for(Element element : speakerNodes) {
            String imageURL = element.getElementsByClass("speaker").get(0).attr("src");
            String company = element.getElementsByTag("h4").get(0).text();
            String speakerName = element.getElementsByTag("h3").get(0).text();

            JSONObject speakerJson = new JSONObject();
            speakerJson.put("name", speakerName);
            speakerJson.put("image", imageURL);
            speakerJson.put("company", company);

            speakersJson.put(speakerJson);
        }

        File outputSpeakersFile = new File("./data/speakers.json");

        BufferedWriter writer = new BufferedWriter(new FileWriter(outputSpeakersFile));

        writer.write(speakersJson.toString());

        System.out.println(speakersJson.toString());
    }
}
