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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class EmissionsOffline {

    static Logger logger = LoggerFactory.getLogger(EmissionsOffline.class);

    public static void calculateEmissions(String simulated_city, String simulation_id, Path results_path){

        copyScheduleToInputEmmision(simulated_city, simulation_id);

        Path input_emissions = Paths.get("data", simulated_city, "input_emissions");
        Path emissions_network_path = Paths.get(input_emissions.toString(), "network.xml");
        Path emissions_vehicles_path = Paths.get(input_emissions.toString(), "vehicles.xml");

        //create config file for emissions
        Config config = ConfigUtils.loadConfig(Paths.get(input_emissions.toString(), "config.xml").toString());

        config.network().setInputFile(emissions_network_path.toAbsolutePath().toString());
        config.vehicles().setVehiclesFile(emissions_vehicles_path.toAbsolutePath().toString());

        EmissionsConfigGroup ecg = new EmissionsConfigGroup();
        config.addModule(ecg);
        ecg.setAverageColdEmissionFactorsFile(Paths.get(input_emissions.toString(),"EFA_ColdStart_Vehcat_2020Average.txt").toAbsolutePath().toString());
        ecg.setAverageWarmEmissionFactorsFile(Paths.get(input_emissions.toString(),"EFA_HOT_Vehcat_2020Average.txt").toAbsolutePath().toString());

        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);

//        AddRoadType.run(
//                emissions_network_path.toString(),
//                emissions_network_path.toString()
//        );

        CreateEmissionVehicles.run(results_path, Paths.get(results_path.toString(), "output_allVehicles.xml").toAbsolutePath().toString(), emissions_vehicles_path.toAbsolutePath().toString());

        ConfigWriter cw = new ConfigWriter(config);
        cw.write(Paths.get(input_emissions.toString(),"config.xml").toString());

        final Scenario scenario = ScenarioUtils.loadScenario(config);

        final EventsManager eventsManager = EventsUtils.createEventsManager();
        AbstractModule module	=	new	AbstractModule()	{
            public void install() {
                this.bind(Scenario.class).toInstance(scenario);
                this.bind(EventsManager.class).toInstance(eventsManager);
                this.bind(EmissionModule.class);
            }
        };

        Injector injector	=	org.matsim.core.controler.Injector.createInjector(config,	module);
        EmissionModule	emissionModule	=	injector.getInstance(EmissionModule.class);

        EventWriterXML emissionEventWriter ;
        MatsimEventsReader matsimEventsReader;


        emissionEventWriter	=	new	EventWriterXML(Paths.get(results_path.toString(), "emissionEvents.xml").toString());
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
        eventsManager.initProcessing();
        matsimEventsReader	=	new	MatsimEventsReader(eventsManager);
        eventsManager.finishProcessing();

        matsimEventsReader.readFile(Paths.get(results_path.toString(), "output_events.xml").toString());	//	existing	events	file	as	input
        emissionEventWriter.closeFile();

    }

    private static void copyScheduleToInputEmmision(String city, String sim_id) {
        Path source = Paths.get("data", city, "simulations", sim_id, "schedule.xml");
        Path target = Paths.get("data", city, "input_emissions", "schedule.xml");
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.error("Could not copy PT schedule to input_emissions.");
            e.printStackTrace();
        }
    }
}
