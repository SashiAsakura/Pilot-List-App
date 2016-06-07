package com.example.hisashi.listviewlearning;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;

/**
 * Created by hisashi on 2015-10-28.
 */
public class JSONParser {

    public static ArrayList<Record> parseMatterJson(String matterJsonString) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(matterJsonString);
        if (jsonObject == null) {
            System.out.println("jsonObject is null...");
            return null;
        }

        ArrayList<Record> records = getMattersBasicAttributes(jsonObject);
        return records;
    }

    // get display name, client name, description, open date, and status
    private static ArrayList<Record> getMattersBasicAttributes(JSONObject jsonObject) {
        ArrayList<Record> records = new ArrayList<>();
        JSONArray matters = (JSONArray) jsonObject.get("matters");

        for (int i = 0; i < matters.size(); i++) {
            JSONObject matter = (JSONObject) matters.get(i);
            JSONObject client = (JSONObject) matter.get("client");
            String clientName = (String) client.get("name");

            String displayNumber = "";
            String description = (String) matter.get("description");
            String openDate = (String) matter.get("open_date");
            String status = (String) matter.get("status");

            System.out.println("displayNumber=" + displayNumber + ", clientName=" + clientName + ", description="
                + description + ", openDate=" + openDate + ", status=" + status);
            records.add(new Record(clientName, displayNumber, description, openDate, status));
        }

        return records;
    }

    public static void parseSimpleJson(String jsonString) {
        JSONObject jsonObject = (JSONObject) JSONValue.parse(jsonString);
        if (jsonObject == null) {
            System.out.println("jsonObject is null...");
            return;
        }

        getName(jsonObject);
        getMessages(jsonObject);
    }

    private static void getMessages(JSONObject jsonObject) {
        JSONArray array = (JSONArray) jsonObject.get("messages");
        if (array == null) {
            System.out.println("array is null...");
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            String message = (String) array.get(i);
            System.out.println("message=" + message);
        }
    }

    private static void getName(JSONObject jsonObject) {
        String name = (String) jsonObject.get("name");
        System.out.println("name=" + name);
    }
}
