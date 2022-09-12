package org.matsim.project;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsManagerImpl;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.vehicles.Vehicle;


import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.ArrayList;


public class test {
public static void checkFlow(EventsManager events, Path results, ArrayList<String> links, int start_hour, int duration){

    events.addHandler(new LinkEnterEventHandler() {
        @Override
        public void handleEvent(LinkEnterEvent linkEnterEvent) {
            Id<Link> link = linkEnterEvent.getLinkId();
            Double time = linkEnterEvent.getTime();
            Id<Vehicle> vehicleId = linkEnterEvent.getVehicleId();
//            if (time >= start_hour * 60.0 * 60 && time <= (start_hour + duration) * 60.0 * 60) {
            if (String.valueOf(vehicleId).matches("veh_2932_bus")){
                try {
//                    if (links.contains(Long.valueOf(String.valueOf(link)))) {
                        System.out.println(linkEnterEvent);

//                    }

                } catch (Exception e) {
                }

            }
        }

    });
    new MatsimEventsReader(events).readFile(results.toString());
}

    public static void main(String[] args) {
        EventsManager events = new EventsManagerImpl();
        Path results  = Paths.get("D:","Users","miljana","simulation","output_14578_14_4","output_events.xml.gz");



        checkFlow(events,results,GetLinks.getLinks().get("14578"),14,4);
//        System.out.println(GetLinks.getLinks().get("14578"));
    }

}


