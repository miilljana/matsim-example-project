package org.matsim.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;


public class CreateEmissionVehicles {

    public static void run(Path input_vehicles, Path output_vehicles){


        Config config = ConfigUtils.loadConfig(Paths.get("scenarios","bilbao","input","config.xml").toString());

        config.vehicles().setVehiclesFile(input_vehicles.toString());
        Scenario scenario = ScenarioUtils.loadScenario(config);

        Vehicles vehiclesHbefa = VehicleUtils.createVehiclesContainer();
        VehiclesFactory factory = vehiclesHbefa.getFactory();

        VehicleType bicycleType =  factory.createVehicleType(Id.create("bicycle",VehicleType.class));
//        bicycleType.setDescription("BEGIN_EMISSIONSPASSENGER_CAR;average;average;averageEND_EMISSIONS");
        bicycleType.getEngineInformation().getAttributes().putAttribute("HbefaVehicleCategory","NON_HBEFA_VEHICLE");

        vehiclesHbefa.addVehicleType(bicycleType);

        VehicleType carType = factory.createVehicleType(Id.create("car",VehicleType.class));
//        carType.setDescription("BEGIN_EMISSIONSPASSENGER_CAR;average;average;averageEND_EMISSIONS");
        carType.getEngineInformation().getAttributes().putAttribute("HbefaVehicleCategory","PASSENGER_CAR");
        carType.getEngineInformation().getAttributes().putAttribute("HbefaTechnology","average");
        carType.getEngineInformation().getAttributes().putAttribute("HbefaSizeClass","average");
        carType.getEngineInformation().getAttributes().putAttribute("HbefaEmissionsConcept","average");
        vehiclesHbefa.addVehicleType(carType);

        VehicleType busType = factory.createVehicleType(Id.create("bus",VehicleType.class));
//        busType.setDescription("BEGIN_EMISSIONSPASSENGER_CAR;average;average;averageEND_EMISSIONS");
        busType.getEngineInformation().getAttributes().putAttribute("HbefaVehicleCategory","PASSENGER_CAR");
        busType.getEngineInformation().getAttributes().putAttribute("HbefaTechnology","average");
        busType.getEngineInformation().getAttributes().putAttribute("HbefaSizeClass","average");
        busType.getEngineInformation().getAttributes().putAttribute("HbefaEmissionsConcept","average");
        vehiclesHbefa.addVehicleType(busType);

        Map<Id<Vehicle>, Vehicle> vehicles = scenario.getVehicles().getVehicles();


        for (Id<Vehicle> v: vehicles.keySet()){
            Vehicle vehicle = factory.createVehicle(v,vehicles.get(v).getType());
            vehiclesHbefa.addVehicle(vehicle);
        }

        new MatsimVehicleWriter(vehiclesHbefa).writeFile(output_vehicles.toString());


    }

    public static void main(String[] args) {


        Path input_vehicles = Paths.get("D:","Users","miljana","simulation","output_1_7_1","output_allVehicles.xml.gz");
        Path output_vehicles = Paths.get("D:","Users","miljana","simulation","output_1_7_1","emission_vehicles.xml.gz");

        run(input_vehicles,output_vehicles);
    }
}
