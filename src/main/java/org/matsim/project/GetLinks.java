package org.matsim.project;

import com.amazonaws.http.conn.ssl.SdkTLSSocketFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class GetLinks {
    public static HashMap<String, ArrayList<String>> getLinks (){
        //read links nto hash map
        String path_to_links = Paths.get("../../../","scenarios","bilbao","input","links.json").toString();
        HashMap<String, ArrayList<String>> link = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path_to_links));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray data = (JSONArray) jsonObject.get("data");
//            Iterator iterator = data.iterator();
            ArrayList<String> pom = new ArrayList<>();
            Iterator<JSONObject> iterator =  data.iterator();
            while (iterator.hasNext()) {
                JSONObject o = iterator.next();
                ArrayList<String> p = (ArrayList<String>) o.get("links");
                for (int i=0;i<p.size();i++){

                    if (pom.contains(String.valueOf(p.get(i)))){

                        p.remove(i);
                    }
                    else {

                        pom.add(String.valueOf(p.get(i)));
                    }

                }
                link.put(String.valueOf( o.get("region")),p);
//                System.out.println(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> city_parts = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9","12","13","14","15","123","134","145","125","126","139","148","157","1236","1239","1349","1348","1458","1457","1257","1256","12369","13498","14578","12567"));
        HashMap<String, ArrayList<String>> city_parts_comb = new HashMap<>();
        for (String id:city_parts){
            //split the id
            ArrayList<String> l = new ArrayList<>();
            char[] regions;
            regions = id.toCharArray();
            for (int i =0;i<regions.length;i++){
//                System.out.println(regions[i]);
                l.addAll(link.get(String.valueOf(regions[i])));
            }
            city_parts_comb.put(id,l);

        }
        return city_parts_comb;
    }

    public static ArrayList<String> getSurroundingLinks(){
        String path_to_links = Paths.get("../../../","scenarios","bilbao","input","links.json").toString();
        HashMap<String, ArrayList<String>> link = new HashMap<>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path_to_links));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray data = (JSONArray) jsonObject.get("data");
//            Iterator iterator = data.iterator();
            ArrayList<String> pom = new ArrayList<>();
            Iterator<JSONObject> iterator =  data.iterator();
            while (iterator.hasNext()) {
                JSONObject o = iterator.next();
                ArrayList<String> p = (ArrayList<String>) o.get("links");
                for (int i=0;i<p.size();i++){

                    if (pom.contains(String.valueOf(p.get(i)))){

                        p.remove(i);
                    }
                    else {

                        pom.add(String.valueOf(p.get(i)));
                    }

                }
                link.put(String.valueOf( o.get("region")),p);
//                System.out.println(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<String> city_parts = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","8","9"));
         ArrayList<String> city_parts_comb = new ArrayList<>();
        ArrayList<String> l = new ArrayList<>();
        for (String id:city_parts){
            //split the id

            char[] regions;
            regions = id.toCharArray();
            for (int i =0;i<regions.length;i++){
                l.addAll(link.get(String.valueOf(regions[i])));
            }
            city_parts_comb.addAll(l);

        }
        return city_parts_comb;
    }

    public static void main(String[] args) {
        System.out.println(getLinks().get("14578").contains(Long.valueOf(17081)));

    }
}
