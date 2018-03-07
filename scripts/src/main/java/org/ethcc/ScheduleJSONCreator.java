package org.ethcc;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mohitaggarwal on 06/03/2018.
 */
public class ScheduleJSONCreator {
    public static void main(String[] args) throws IOException, InvalidFormatException, ParseException {
        File scheduleFile = new File("./data/schedule.xlsx");

        assert scheduleFile.exists();

        Workbook workbook = WorkbookFactory.create(scheduleFile);

        Sheet sheet = workbook.getSheetAt(0);

        DataFormatter dataFormatter = new DataFormatter();


        int rowCounter = -1;

        List<Event> eventList = new ArrayList<Event>();

        for (Row row: sheet) {
            rowCounter++;
            if (rowCounter == 0) {
                continue;
            }

            Event event = new Event();

            String talk = dataFormatter.formatCellValue(row.getCell(0));
            String speakers = dataFormatter.formatCellValue(row.getCell(1));
            String venue = dataFormatter.formatCellValue(row.getCell(2));
            String startTime = dataFormatter.formatCellValue(row.getCell(3));
            String endTime = dataFormatter.formatCellValue(row.getCell(4));
            String day = dataFormatter.formatCellValue(row.getCell(5));
            String theme = dataFormatter.formatCellValue(row.getCell(6));
            String company = dataFormatter.formatCellValue(row.getCell(7));
            String special = dataFormatter.formatCellValue(row.getCell(8));

            event.talk = talk;
            event.speakers = getSpeakers(speakers);
            event.venue = capitalizeWord(venue);
            event.startTime = startTime;
            event.endTime = endTime;
            event.day = Integer.parseInt(day);
            event.theme = formatTheme(capitalizeWord(theme));
            event.company = company;
            event.special = special;

            eventList.add(event);
        }

        List<String> venues = getVenues(eventList);
        List<String> themes = getThemes(eventList);

        //get events by day - in each day - in each venue - sorted by time
        //with speaker name linked to value in speakers.json

        HashMap<Integer, HashMap<String, Event>> eventMap = new HashMap<Integer, HashMap<String, Event>>();

        //first pass, just create buckets - sort and process each bucket later
        for (Event event : eventList) {
            HashMap<String, Event> venueMap = eventMap.get(event.day);

            if (venueMap == null ) {
                venueMap = new HashMap<String, Event>();
            }

            venueMap.put(event.venue, event);
        }

        //second pass, for each key, value in eventMap - sort and link (embed value from speakers json into each event)

        File speakersJson = new File("./data/speakers.json");

        assert speakersJson.exists();

        JSONArray speakersArray = (JSONArray) (new org.json.simple.parser.JSONParser()).parse(new FileReader(speakersJson));

        List<Speaker> speakers = new ArrayList<Speaker>();

        for (Object object : speakersArray) {
            JSONObject speakerJson = (JSONObject)object;

            Speaker speaker = new Speaker();

            speaker.name = (String) speakerJson.get("name");
            speaker.company = (String) speakerJson.get("company");
            speaker.image = (String) speakerJson.get("image");

            speakers.add(speaker);
        }

        //check events which have no speakers from speakers list
        for (Event event : eventList) {
            for (String eventSpeaker : event.speakers) {

                boolean found = false;

                for (Speaker speaker : speakers) {
                    if (eventSpeaker.equalsIgnoreCase(speaker.name)) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println(event.talk);
                    System.out.println(Arrays.asList(event.speakers));
                    break;
                }
            }
        }

        System.out.println(themes);
    }

    private static String formatTheme(String theme) {
        theme = theme.replace("Dapp", "DApp");

        return theme;
    }

    private static List<String> getThemes(List<Event> eventList) {
        List<String> themes = new ArrayList<String>();

        for(Event event : eventList) {
            if(!themes.contains(event.theme)) {
                themes.add(event.theme);
            }
        }

        return themes;
    }

    private static List<String> getVenues(List<Event> eventList) {
        List<String> venues = new ArrayList<String>();

        for(Event event : eventList) {
            if(!venues.contains(event.venue)) {
                venues.add(event.venue);
            }
        }

        return venues;
    }


    private static String capitalizeWord(String word) {
        if (Character.isLowerCase(word.charAt(0))) {
            return Character.toUpperCase(word.charAt(0)) + word.substring(1);
        }

        return word;
    }

    private static String[] getSpeakers(String speakers) {
        String names[] = speakers.split(",");

        for (int count = 0; count < names.length; count++) {
            names[count] = names[count].trim();
        }

        return names;
    }

    private static class Event {
        String talk;
        String speakers[];
        String venue;
        String startTime;
        String endTime;
        int day;
        String theme;
        String company;
        String special;
        Speaker speakersObj[];
    }

    private static class Speaker {
        String name;
        String image;
        String company;
    }
}
