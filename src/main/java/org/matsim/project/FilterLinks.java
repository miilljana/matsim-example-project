package org.matsim.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class FilterLinks {
    public static void removeDuplicate(){
        //read links nto hash map
        String path_to_links = Paths.get("scenarios","bilbao","input","links.json").toString();
        HashMap<String, ArrayList> link = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path_to_links));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray data = (JSONArray) jsonObject.get("data");
            Iterator<JSONObject> iterator = data.iterator();
            ArrayList<String> pom = new ArrayList<>();
            int sum=0;
            while (iterator.hasNext()) {
                JSONObject o = iterator.next();
                ArrayList<String> p = (ArrayList<String>) o.get("links");
                for (int i=0;i<p.size();i++){

                    if (pom.contains(String.valueOf(p.get(i)))){
                        System.out.println("duplicate found: "+String.valueOf(p.get(i))+" in: "+o.get("region"));
                        p.remove(i);
                    }
                    else {

                        pom.add(String.valueOf(p.get(i)));
                    }

                }

                sum+=p.size();

                link.put(String.valueOf( o.get("region")),p);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        removeDuplicate();
    }
}
