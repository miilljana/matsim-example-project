package org.matsim.project;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkChangeEvent;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;
import scala.util.parsing.combinator.testing.Str;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
@SuppressWarnings("unchecked")
public class RunExperiment {


    public static void createNetworkChangeEventClose(int start_time, ArrayList<String> links, Scenario sc) {

        //start of closure
        NetworkChangeEvent networkChangeEventStart = new NetworkChangeEvent(start_time );
//        networkChangeEventStart.setFreespeedChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, 0));
        networkChangeEventStart.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, 0));


        for (int i=0;i<links.size();i++) {
            Id<Link> l;
            Link linkToClose;
            l = Id.createLinkId(String.valueOf(links.get(i)));
            linkToClose = sc.getNetwork().getLinks().get(l);
            networkChangeEventStart.addLink(linkToClose);

        }


        NetworkUtils.addNetworkChangeEvent(sc.getNetwork(), networkChangeEventStart);

    }

    public static void createnetworkChangeEventOpen(int start_time, ArrayList<String> links, Scenario sc) {
        //start of closure
        NetworkChangeEvent networkChangeEventStart = new NetworkChangeEvent(start_time );
//        networkChangeEventStart.setFreespeedChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, 13));
        networkChangeEventStart.setFlowCapacityChange(new NetworkChangeEvent.ChangeValue(NetworkChangeEvent.ChangeType.ABSOLUTE_IN_SI_UNITS, 3600));


        for (int i =0;i<links.size();i++) {
            Id<Link> l;
            Link linkToClose;
            l = Id.createLinkId(String.valueOf(links.get(i)));
            linkToClose = sc.getNetwork().getLinks().get(l);
            networkChangeEventStart.addLink(linkToClose);

        }

        System.out.println("network: "+sc.getNetwork().getName());
        NetworkUtils.addNetworkChangeEvent(sc.getNetwork(), networkChangeEventStart);
    }


    public static void runSimulation() {
        //read links nto hash map
        String path_to_links = Paths.get("scenarios","bilbao","input","links.json").toString();
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
                System.out.println(p);
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
               System.out.println(regions[i]);
               l.addAll(link.get(String.valueOf(regions[i])));
           }
           city_parts_comb.put(id,l);

       }



        ArrayList<Integer> start_hour = new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));
        ArrayList<Integer> duration = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        int[][] a;
        a = new int[][]{{20, 6, 2}, {17, 9, 4}, {10, 2, 2}, {14, 11, 3}, {6, 11, 1}, {28, 9, 2}, {9, 1, 2}, {15, 5, 2}, {22, 2, 4}, {14, 5, 1}, {17, 7, 1}, {25, 11, 4}, {32, 8, 4}, {7, 4, 4}, {8, 2, 1}, {19, 4, 2}, {5, 6, 1}, {11, 8, 4}, {25, 5, 2}, {20, 10, 4}, {16, 5, 2}, {29, 11, 3}, {26, 6, 3}, {25, 1, 1}, {26, 11, 3}, {32, 2, 1}, {21, 5, 3}, {22, 4, 1}, {26, 2, 1}, {31, 7, 3}, {1, 10, 2}, {16, 1, 4}, {3, 9, 3}, {3, 6, 1}, {13, 10, 4}, {2, 11, 3}, {31, 5, 4}, {14, 1, 3}, {21, 3, 2}, {18, 2, 1}, {33, 3, 1}, {8, 5, 3}, {19, 3, 1}, {2, 11, 4}, {6, 5, 4}, {3, 7, 4}, {29, 10, 1}, {21, 5, 4}, {20, 6, 3}, {32, 4, 1}, {12, 3, 1}, {25, 7, 1}, {26, 4, 3}, {27, 1, 3}, {12, 11, 4}, {5, 3, 3}, {24, 4, 4}, {11, 7, 1}, {23, 5, 2}, {26, 10, 2}, {28, 10, 3}, {2, 8, 1}, {11, 3, 4}, {10, 3, 3}, {30, 8, 2}, {7, 9, 4}, {19, 4, 1}, {19, 9, 3}, {11, 9, 3}, {31, 10, 2}, {5, 11, 1}, {27, 8, 3}, {4, 8, 4}, {33, 11, 3}, {22, 1, 1}, {12, 1, 2}, {13, 1, 2}, {3, 11, 3}, {10, 1, 4}, {1, 11, 1}, {12, 10, 1}, {2, 5, 2}, {18, 6, 4}, {1, 3, 1}, {9, 2, 2}, {9, 8, 1}, {33, 9, 2}, {18, 7, 2}, {23, 3, 4}, {21, 11, 2}, {30, 2, 4}, {15, 4, 4}, {17, 6, 2}, {28, 4, 1}, {4, 7, 3}, {24, 9, 3}, {15, 10, 1}, {7, 6, 1}, {31, 2, 4}, {28, 2, 1}, {11, 9, 4}, {19, 11, 2}, {28, 3, 1}, {3, 9, 1}, {23, 4, 4}, {1, 5, 2}, {13, 4, 3}, {6, 8, 2}, {4, 1, 2}, {29, 7, 2}, {28, 9, 1}, {19, 10, 2}, {31, 2, 3}, {31, 1, 4}, {10, 6, 4}, {9, 3, 1}, {7, 4, 2}, {18, 1, 1}, {16, 10, 4}, {30, 10, 4}, {30, 7, 2}, {21, 1, 4}, {16, 8, 4}, {22, 10, 2}, {8, 6, 3}, {1, 8, 1}, {20, 9, 3}, {5, 8, 3}, {30, 6, 2}, {17, 7, 3}, {23, 4, 3}, {24, 2, 1}, {10, 8, 2}, {9, 3, 4}, {1, 10, 3}, {22, 7, 2}, {14, 4, 3}, {6, 7, 3}, {22, 6, 2}, {14, 6, 3}, {18, 3, 1}, {22, 6, 3}, {18, 5, 3}, {13, 2, 2}, {11, 9, 2}, {8, 2, 3}, {20, 6, 1}, {24, 8, 4}, {6, 7, 2}, {25, 2, 1}, {27, 3, 4}, {15, 9, 3}, {27, 6, 1}, {32, 1, 2}, {9, 5, 3}, {8, 7, 1}, {16, 8, 2}, {4, 11, 4}, {9, 9, 3}, {20, 2, 2}, {12, 10, 3}, {7, 5, 4}, {29, 5, 1}, {3, 1, 3}, {32, 3, 2}, {29, 10, 4}, {17, 1, 2}, {15, 8, 2}, {25, 6, 3}, {20, 7, 4}, {26, 9, 4}, {27, 11, 4}, {2, 8, 2}, {12, 7, 1}, {24, 3, 3}, {13, 2, 3}, {15, 6, 3}, {5, 10, 4}, {13, 11, 2}, {27, 1, 2}, {2, 6, 1}, {24, 8, 3}, {12, 10, 4}, {5, 4, 3}, {33, 1, 1}, {33, 4, 3}, {16, 2, 2}, {7, 1, 2}, {30, 5, 4}, {23, 8, 3}, {14, 7, 4}, {31, 4, 2}};

      for (int i = 120; i < 192; i++) {
            String id = city_parts.get(a[i][0] - 1);
            Integer h = start_hour.get(a[i][1] - 1);
            Integer d = duration.get(a[i][2] - 1);

            Path path = Paths.get("D:", "Users", "miljana", "experiment3", "output_" + id + "_" + h + "_" + d);

//            Path path = Paths.get("../../../", "scenarios", "bilbao", "output", "output_" + id + "_" + h + "_" + d);

            boolean isDir = Files.isDirectory(path);
            if (!isDir){
                System.out.println("Simulation does not exists. Will run now!"+"output_" + id + "_" + h + "_" + d);
//                do the simulation

        //filter duplicates of city_parts_comb

        ArrayList<String> p = new ArrayList<>();
        ArrayList<String> cp = city_parts_comb.get(id);
        for(int k=0;k<cp.size();k++){
            if (p.contains(String.valueOf(cp.get(k)))){
                System.out.println("duplicate");
            }
            else{
                p.add(String.valueOf(cp.get(k)));
            }

        }

                    Config config = ConfigUtils.loadConfig(Paths.get("scenarios","bilbao","input","config.xml").toString());
                    config.network().setTimeVariantNetwork(true);
                    Scenario sc = ScenarioUtils.loadScenario(config);


                    createNetworkChangeEventClose(h*60*60, p, sc);
//                    createNetworkChangeEventClose(12*60*60, cp, sc);
                    createnetworkChangeEventOpen(h*60*60+d*60*60, p, sc);
//                    createnetworkChangeEventOpen(12*60*60+1*60*60, cp, sc);

                    Controler controler = new Controler(sc);
                    controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
                    controler.getConfig().controler().setWriteEventsInterval(50);
                    controler.getConfig().controler().setLastIteration(200);

                    controler.getConfig().qsim().setStorageCapFactor(0.3);
                    controler.getConfig().qsim().setFlowCapFactor(0.1);
                    controler.getConfig().plans().setActivityDurationInterpretation(PlansConfigGroup.ActivityDurationInterpretation.minOfDurationAndEndTime);

//                    controler.getConfig().qsim().setStuckTime(4 * 60 * 60);

                    controler.getConfig().controler().setOutputDirectory(Paths.get("scenarios","bilbao","output","output_"+id+"_"+h+"_"+d).toString());
//                    controler.getConfig().controler().setOutputDirectory(Paths.get("scenarios","bilbao","output","output_test").toString());
                    controler.run();



                }
            }


    }

    public static void main(String[] args) throws IOException {

        runSimulation();

    }
}
