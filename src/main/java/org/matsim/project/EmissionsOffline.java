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


import java.nio.file.Path;
import java.nio.file.Paths;



public class EmissionsOffline {



    public static void calculateEmissions(Path input_path,Path input_emissions,Path results_path){


        Path emissions_network_path = Paths.get(input_emissions.toString(), "network.xml");
        Path emissions_vehicles_path = Paths.get(results_path.toString(), "emission_vehicles.xml");

        //create config file for emissions
        Config config = ConfigUtils.loadConfig(Paths.get(input_path.toString(), "config.xml").toString());

        config.network().setInputFile(emissions_network_path.toAbsolutePath().toString());
        config.vehicles().setVehiclesFile(emissions_vehicles_path.toAbsolutePath().toString());

        EmissionsConfigGroup ecg = new EmissionsConfigGroup();
        config.addModule(ecg);
        ecg.setAverageColdEmissionFactorsFile(Paths.get(input_emissions.toString(),"EFA_ColdStart_Vehcat_2020Average.txt").toAbsolutePath().toString());
        ecg.setAverageWarmEmissionFactorsFile(Paths.get(input_emissions.toString(),"EFA_HOT_Vehcat_2020Average.txt").toAbsolutePath().toString());

        ecg.setHbefaRoadTypeSource(EmissionsConfigGroup.HbefaRoadTypeSource.fromLinkAttributes);
        ecg.setDetailedVsAverageLookupBehavior(EmissionsConfigGroup.DetailedVsAverageLookupBehavior.directlyTryAverageTable);
        ecg.setNonScenarioVehicles(EmissionsConfigGroup.NonScenarioVehicles.ignore);


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

        matsimEventsReader.readFile(Paths.get(results_path.toString(), "output_events.xml.gz").toString());	//	existing	events	file	as	input
        emissionEventWriter.closeFile();

    }

    public static void main(String[] args) {
        Path input_path = Paths.get("scenarios","bilbao","input");
        Path input_emissions = Paths.get("scenarios","bilbao","input_emissions");
        Path results_path = Paths.get("D:","Users","miljana","simulation","output_1_7_1");

        calculateEmissions(input_path,input_emissions,results_path);
    }
}
