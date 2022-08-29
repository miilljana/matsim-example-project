package org.matsim.project;

import com.google.inject.Injector;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.emissions.EmissionModule;
import org.matsim.contrib.emissions.utils.EmissionsConfigGroup;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.events.algorithms.EventWriterXML;
import org.matsim.core.scenario.ScenarioUtils;

import java.nio.file.Paths;

public class CalculateEmissionsOffline {

    public int start_time;
    public int end_time;
    public int numIntervals; // number of 30 minutes intervals
    public String experimentName;

    public CalculateEmissionsOffline() {
        this.start_time = 0;
        this.end_time = 23;
        this.numIntervals = 1;
        this.experimentName = "experiment2";
    }
    public CalculateEmissionsOffline(int start_time, int end_time, int numIntervals, String experimentName){
        this.start_time = start_time;
        this.end_time = end_time;
        this.numIntervals = numIntervals;
        this.experimentName = experimentName;

    }

    public void setNumIntervals(int numIntervals) {
        this.numIntervals = numIntervals;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public int getStart_time() {
        return start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public int getNumIntervals() {
        return numIntervals;
    }

    public String getExperimentName() {
        return experimentName;
    }


    public void runEmissions(){

        String inputPath = "Scenarios/BilbaoTimeVariantNetwork/"+experimentName;
        String outputPath = Paths.get("D:","simulation_data",experimentName).toString();
        Config config = ConfigUtils.loadConfig(inputPath+"/inputEmissions/config.xml", new EmissionsConfigGroup());
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

        EventWriterXML emissionEventWriter = null;
        MatsimEventsReader matsimEventsReader;
        for (int i = start_time; i < end_time; i++) {
            for (int j = 1; j < numIntervals; j++) {


                if (i*60*60+j*30*60 >= end_time*60*60){
                    break;
                }

                     emissionEventWriter = new EventWriterXML(outputPath + "/outputEmissions/output_emission_events_"+i+"_"+j+".xml");
                    emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
                    eventsManager.initProcessing();
                     matsimEventsReader = new MatsimEventsReader(eventsManager);
                    matsimEventsReader.readFile(outputPath + "/outputChangeEvent/output_" + i + "_"+j+ "/output_events.xml.gz");    //	existing	events	file	as	input
                    eventsManager.finishProcessing();
                emissionEventWriter.closeFile();
            }

        }


//        calculate the baseScenario
         emissionEventWriter	=	new	EventWriterXML(outputPath+"/outputEmissions/output_emission_event_base.xml");
        eventsManager.initProcessing();
        emissionModule.getEmissionEventsManager().addHandler(emissionEventWriter);
         matsimEventsReader	=	new	MatsimEventsReader(eventsManager);
        matsimEventsReader.readFile(outputPath+"/outputChangeEvent/output_base/output_events.xml.gz");	//	existing	events	file	as	input
        eventsManager.finishProcessing();
        emissionEventWriter.closeFile();


    }


}
