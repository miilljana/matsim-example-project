package org.matsim.project;

import com.google.inject.Injector;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;

import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.ConfigWriter;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;


import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class EmissionsOffline {



    public static void calculateEmissions(Path input_path,Path input_emissions,Path results_path) {

        // AddRoadType is run once
        //CreateEmissionVehicles is run for every simulation
        try {
            CreateEmissionVehicles.run(Paths.get(results_path.toString(), "output_allVehicles.xml.gz"), Paths.get(results_path.toString(), "emission_vehicles.xml.gz"));


            Path emissions_network_path = Paths.get(input_emissions.toString(), "network.xml");
            Path emissions_vehicles_path = Paths.get(results_path.toString(), "emission_vehicles.xml.gz");

            //create config file for emissions
            Config config = ConfigUtils.loadConfig(Paths.get(input_path.toString(), "config.xml").toString());

            config.network().setInputFile(emissions_network_path.toAbsolutePath().toString());
            config.vehicles().setVehiclesFile(emissions_vehicles_path.toAbsolutePath().toString());

            EmissionsConfigGroup ecg = new EmissionsConfigGroup();
            config.addModule(ecg);
            ecg.setAverageColdEmissionFactorsFile(Paths.get(input_emissions.toString(), "EFA_ColdStart_Vehcat_2020Average.txt").toAbsolutePath().toString());
            ecg.setAverageWarmEmissionFactorsFile(Paths.get(input_emissions.toString(), "EFA_HOT_Vehcat_2020Average.txt").toAbsolutePath().toString());

            ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
            ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
            ecg.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);


            ConfigWriter cw = new ConfigWriter(config);
            cw.write(Paths.get(input_emissions.toString(), "config.xml").toString());

            final Scenario scenario = ScenarioUtils.loadScenario(config);

            final EventsManager eventsManager = EventsUtils.createEventsManager();
            AbstractModule module = new AbstractModule() {
                public void install() {
                    this.bind(Scenario.class).toInstance(scenario);
                    this.bind(EventsManager.class).toInstance(eventsManager);
                    this.bind(EmissionModule.class);
                }
            };

            Injector injector = org.matsim.core.controler.Injector.createInjector(config, module);
            EmissionModule emissionModule = injector.getInstance(EmissionModule.class);

            EventWriterXML emissionEventWriter;
            MatsimEventsReader matsimEventsReader;


            emissionEventWriter = new EventWriterXML(Paths.get(results_path.toString(), "emissionEvents.xml").toString());
            emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
            eventsManager.initProcessing();
            matsimEventsReader = new MatsimEventsReader(eventsManager);
            eventsManager.finishProcessing();

            matsimEventsReader.readFile(Paths.get(results_path.toString(), "output_events.xml.gz").toString());    //	existing	events	file	as	input
            emissionEventWriter.closeFile();

        }
        catch (Exception e){
            System.out.println("file not found");
        }
    }

    public static void main(String[] args) {
        ArrayList<String> city_parts = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "12", "13", "14", "15", "123", "134", "145", "125", "126", "139", "148", "157", "1236", "1239", "1349", "1348", "1458", "1457", "1257", "1256", "12369", "13498", "14578", "12567"));
        ArrayList<Integer> start_hour = new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17));
        ArrayList<Integer> duration = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        int[][] a;
        a = new int[][]{{20, 6, 2}, {17, 9, 4}, {10, 2, 2}, {14, 11, 3}, {6, 11, 1}, {28, 9, 2}, {9, 1, 2}, {15, 5, 2}, {22, 2, 4}, {14, 5, 1}, {17, 7, 1}, {25, 11, 4}, {32, 8, 4}, {7, 4, 4}, {8, 2, 1}, {19, 4, 2}, {5, 6, 1}, {11, 8, 4}, {25, 5, 2}, {20, 10, 4}, {16, 5, 2}, {29, 11, 3}, {26, 6, 3}, {25, 1, 1}, {26, 11, 3}, {32, 2, 1}, {21, 5, 3}, {22, 4, 1}, {26, 2, 1}, {31, 7, 3}, {1, 10, 2}, {16, 1, 4}, {3, 9, 3}, {3, 6, 1}, {13, 10, 4}, {2, 11, 3}, {31, 5, 4}, {14, 1, 3}, {21, 3, 2}, {18, 2, 1}, {33, 3, 1}, {8, 5, 3}, {19, 3, 1}, {2, 11, 4}, {6, 5, 4}, {3, 7, 4}, {29, 10, 1}, {21, 5, 4}, {20, 6, 3}, {32, 4, 1}, {12, 3, 1}, {25, 7, 1}, {26, 4, 3}, {27, 1, 3}, {12, 11, 4}, {5, 3, 3}, {24, 4, 4}, {11, 7, 1}, {23, 5, 2}, {26, 10, 2}, {28, 10, 3}, {2, 8, 1}, {11, 3, 4}, {10, 3, 3}, {30, 8, 2}, {7, 9, 4}, {19, 4, 1}, {19, 9, 3}, {11, 9, 3}, {31, 10, 2}, {5, 11, 1}, {27, 8, 3}, {4, 8, 4}, {33, 11, 3}, {22, 1, 1}, {12, 1, 2}, {13, 1, 2}, {3, 11, 3}, {10, 1, 4}, {1, 11, 1}, {12, 10, 1}, {2, 5, 2}, {18, 6, 4}, {1, 3, 1}, {9, 2, 2}, {9, 8, 1}, {33, 9, 2}, {18, 7, 2}, {23, 3, 4}, {21, 11, 2}, {30, 2, 4}, {15, 4, 4}, {17, 6, 2}, {28, 4, 1}, {4, 7, 3}, {24, 9, 3}, {15, 10, 1}, {7, 6, 1}, {31, 2, 4}, {28, 2, 1}, {11, 9, 4}, {19, 11, 2}, {28, 3, 1}, {3, 9, 1}, {23, 4, 4}, {1, 5, 2}, {13, 4, 3}, {6, 8, 2}, {4, 1, 2}, {29, 7, 2}, {28, 9, 1}, {19, 10, 2}, {31, 2, 3}, {31, 1, 4}, {10, 6, 4}, {9, 3, 1}, {7, 4, 2}, {18, 1, 1}, {16, 10, 4}, {30, 10, 4}, {30, 7, 2}, {21, 1, 4}, {16, 8, 4}, {22, 10, 2}, {8, 6, 3}, {1, 8, 1}, {20, 9, 3}, {5, 8, 3}, {30, 6, 2}, {17, 7, 3}, {23, 4, 3}, {24, 2, 1}, {10, 8, 2}, {9, 3, 4}, {1, 10, 3}, {22, 7, 2}, {14, 4, 3}, {6, 7, 3}, {22, 6, 2}, {14, 6, 3}, {18, 3, 1}, {22, 6, 3}, {18, 5, 3}, {13, 2, 2}, {11, 9, 2}, {8, 2, 3}, {20, 6, 1}, {24, 8, 4}, {6, 7, 2}, {25, 2, 1}, {27, 3, 4}, {15, 9, 3}, {27, 6, 1}, {32, 1, 2}, {9, 5, 3}, {8, 7, 1}, {16, 8, 2}, {4, 11, 4}, {9, 9, 3}, {20, 2, 2}, {12, 10, 3}, {7, 5, 4}, {29, 5, 1}, {3, 1, 3}, {32, 3, 2}, {29, 10, 4}, {17, 1, 2}, {15, 8, 2}, {25, 6, 3}, {20, 7, 4}, {26, 9, 4}, {27, 11, 4}, {2, 8, 2}, {12, 7, 1}, {24, 3, 3}, {13, 2, 3}, {15, 6, 3}, {5, 10, 4}, {13, 11, 2}, {27, 1, 2}, {2, 6, 1}, {24, 8, 3}, {12, 10, 4}, {5, 4, 3}, {33, 1, 1}, {33, 4, 3}, {16, 2, 2}, {7, 1, 2}, {30, 5, 4}, {23, 8, 3}, {14, 7, 4}, {31, 4, 2}};

//        for (int i = 120; i < 192; i++) {
//            String id = city_parts.get(a[i][0] - 1);
//            Integer h = start_hour.get(a[i][1] - 1);
//            Integer d = duration.get(a[i][2] - 1);
//            Path input_path = Paths.get("../../..","scenarios", "bilbao", "input");
//            Path input_emissions = Paths.get("../../..","scenarios", "bilbao", "input_emissions");
////            Path results_path = Paths.get("D:", "Users", "miljana", "simulation", "output_"+id+"_"+h+"_"+d);
//            Path results_path = Paths.get("../../..","scenarios", "bilbao", "output","output_"+id+"_"+h+"_"+d);
//
//            calculateEmissions(input_path, input_emissions, results_path);
//        }

            Path input_path = Paths.get("../../..","scenarios", "bilbao", "input");
            Path input_emissions = Paths.get("../../..","scenarios", "bilbao", "input_emissions");
//            Path results_path = Paths.get("D:", "Users", "miljana", "simulation", "output_"+id+"_"+h+"_"+d);
            Path results_path = Paths.get("../../..","scenarios", "bilbao", "output","output_base");

            calculateEmissions(input_path, input_emissions, results_path);
    }
}
