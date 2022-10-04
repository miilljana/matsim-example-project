package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.nio.file.Paths;

public class RunBaseScenario {
    public static void main(String[] args) {
        Config config = ConfigUtils.loadConfig(Paths.get("scenarios","bilbao","input","config.xml").toString());
        config.network().setTimeVariantNetwork(true);
        Scenario sc = ScenarioUtils.loadScenario(config);


        Controler controler = new Controler(sc);
        controler.getConfig().controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        controler.getConfig().controler().setWriteEventsInterval(50);
        controler.getConfig().controler().setLastIteration(200);

        controler.getConfig().qsim().setStorageCapFactor(0.3);
        controler.getConfig().qsim().setFlowCapFactor(0.1);
        controler.getConfig().plans().setActivityDurationInterpretation(PlansConfigGroup.ActivityDurationInterpretation.minOfDurationAndEndTime);

//                    controler.getConfig().qsim().setStuckTime(4 * 60 * 60);

        controler.getConfig().controler().setOutputDirectory(Paths.get("../../../","scenarios","output","simulation","output_base").toString());
//                    controler.getConfig().controler().setOutputDirectory(Paths.get("scenarios","bilbao","output","output_test").toString());
        controler.run();

    }
}
