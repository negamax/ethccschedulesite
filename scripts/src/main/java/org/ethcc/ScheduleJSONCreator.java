package org.ethcc;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohitaggarwal on 06/03/2018.
 */
public class ScheduleJSONCreator {
    public static void main(String[] args) throws IOException, InvalidFormatException {
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
        return speakers.split(",");
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
    }
}