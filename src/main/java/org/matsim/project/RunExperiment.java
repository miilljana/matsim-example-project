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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

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
        HashMap<String, ArrayList<String>> link = new HashMap<String,ArrayList<String>>();
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(path_to_links));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray data = (JSONArray) jsonObject.get("data");
            Iterator iterator = data.iterator();
//            Iterator<JSONObject> iterator =  data.iterator();
            while (iterator.hasNext()) {
                JSONObject o = (JSONObject) iterator.next();
                link.put(String.valueOf( o.get("region")), (ArrayList<String>) o.get("links"));
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
               l.addAll(link.get(String.valueOf(regions[i])));
           }
           city_parts_comb.put(id,l);
       }


       //define start hours    from 7am to 10pm  discretized into 30 min intervals
        ArrayList<Integer> hours = new ArrayList<>();
        for (int i = 11;i<=15;i++){
            hours.add(i);
        }
        //define duration of closure from 30 min to 10pm, discretized into 30 min intervals (1 meand 30 min...)
        ArrayList<Integer> duration = new ArrayList<>();
        for (int i=1;i<=4;i++){
            duration.add(i);
        }

//       //iterate over city parts
        for (String id:city_parts){

            //iterate over start hours
            for (int h:hours){
                // iterate over duration
                for (int d:duration){

                    int start_time= h*60*60;
                    if (start_time+d*60*60>=17*60*60)
                        break;

                    Config config = ConfigUtils.loadConfig(Paths.get("scenarios","bilbao","input","config.xml").toString());
                    config.network().setTimeVariantNetwork(true);
                    Scenario sc = ScenarioUtils.loadScenario(config);


                    createNetworkChangeEventClose(h*60*60, city_parts_comb.get(id), sc);
                    createnetworkChangeEventOpen(h*60*60+d*60*60, city_parts_comb.get(id), sc);

                    Controler controler = new Controler(sc);
                    controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
                    controler.getConfig().controler().setWriteEventsInterval(50);
                    controler.getConfig().controler().setLastIteration(200);

                    controler.getConfig().qsim().setStorageCapFactor(0.3);
                    controler.getConfig().qsim().setFlowCapFactor(0.1);
                    controler.getConfig().plans().setActivityDurationInterpretation(PlansConfigGroup.ActivityDurationInterpretation.minOfDurationAndEndTime);

//                    controler.getConfig().qsim().setStuckTime(4 * 60 * 60);

                    controler.getConfig().controler().setOutputDirectory(Paths.get("scenarios","bilbao","output","output_"+id+"_"+h+"_"+d).toString());
                    controler.run();

                }
            }
        }

    }

    public static void main(String[] args) throws IOException {



        runSimulation();

    }
}
