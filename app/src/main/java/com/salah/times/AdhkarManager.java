package com.salah.times;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;

public class AdhkarManager {
    private static final File ADHKAR_DIR = new File(Environment.getExternalStorageDirectory(), "SalahTimes/adhkar");
    private static final File ADHKAR_FILE = new File(ADHKAR_DIR, "adhkar.json");
    
    public static List<AdhkarItem> getAdhkarList(String type) {
        List<AdhkarItem> adhkarList = new ArrayList<>();
        try {
            JSONObject data = loadAdhkarData();
            if (data.has(type)) {
                JSONArray array = data.getJSONArray(type);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.getJSONObject(i);
                    adhkarList.add(new AdhkarItem(
                        item.getInt("id"),
                        item.getString("text"),
                        type,
                        item.getInt("position")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return adhkarList;
    }
    
    public static void saveAdhkarList(String type, List<AdhkarItem> adhkarList) {
        try {
            JSONObject data = loadAdhkarData();
            JSONArray array = new JSONArray();
            
            for (int i = 0; i < adhkarList.size(); i++) {
                AdhkarItem item = adhkarList.get(i);
                item.setPosition(i); // Update position
                
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("id", item.getId());
                jsonItem.put("text", item.getText());
                jsonItem.put("position", item.getPosition());
                array.put(jsonItem);
            }
            
            data.put(type, array);
            saveAdhkarData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void addAdhkar(String type, String text) {
        List<AdhkarItem> list = getAdhkarList(type);
        int newId = getNextId();
        int newPosition = list.size();
        
        AdhkarItem newItem = new AdhkarItem(newId, text, type, newPosition);
        list.add(newItem);
        saveAdhkarList(type, list);
    }
    
    public static void deleteAdhkar(String type, int id) {
        List<AdhkarItem> list = getAdhkarList(type);
        list.removeIf(item -> item.getId() == id);
        saveAdhkarList(type, list);
    }
    
    private static JSONObject loadAdhkarData() {
        try {
            if (!ADHKAR_FILE.exists()) {
                return createDefaultAdhkar();
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(ADHKAR_FILE));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            
            return new JSONObject(content.toString());
        } catch (Exception e) {
            return createDefaultAdhkar();
        }
    }
    
    private static void saveAdhkarData(JSONObject data) {
        try {
            ADHKAR_DIR.mkdirs();
            FileWriter writer = new FileWriter(ADHKAR_FILE);
            writer.write(data.toString(2));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static JSONObject createDefaultAdhkar() {
        try {
            JSONObject data = new JSONObject();
            data.put("morning", new JSONArray());
            data.put("evening", new JSONArray());
            saveAdhkarData(data);
            return data;
        } catch (Exception e) {
            return new JSONObject();
        }
    }
    
    private static int getNextId() {
        try {
            JSONObject data = loadAdhkarData();
            int maxId = 0;
            
            for (String type : new String[]{"morning", "evening"}) {
                if (data.has(type)) {
                    JSONArray array = data.getJSONArray(type);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject item = array.getJSONObject(i);
                        maxId = Math.max(maxId, item.getInt("id"));
                    }
                }
            }
            
            return maxId + 1;
        } catch (Exception e) {
            return 1;
        }
    }
}