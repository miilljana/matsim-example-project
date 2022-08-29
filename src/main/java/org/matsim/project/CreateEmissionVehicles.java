package org.matsim.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.*;

import java.util.Random;

public class CreateEmissionVehicles {
    public static void run(String	inputPoopulationFilename,	String	outputVehiclesFilename) {
        Scenario scenario	=	ScenarioUtils.createScenario(ConfigUtils.createConfig());
        new PopulationReader(scenario).readFile(inputPoopulationFilename);
        Vehicles vehicles	= scenario.getVehicles();
        VehiclesFactory factory	=	vehicles.getFactory();
        VehicleType avgCarType	=	factory.createVehicleType(Id.create("car_average",	VehicleType.class));
        avgCarType.setDescription("BEGIN_EMISSIONSPASSENGER_CAR;average;average;averageEND_EMISSIONS");
        vehicles.addVehicleType(avgCarType);
        VehicleType	petrolCarType	=	factory.createVehicleType(Id.create("car_petrol",	VehicleType.class));
        petrolCarType.setDescription("BEGIN_EMISSIONSPASSENGER_CAR;petrol	(4S);>=2L;PC-P-Euro-1END_EMISSIONS");
        vehicles.addVehicleType(petrolCarType);
        Random r	=	new	Random(123456);
        for	(Person person	:	scenario.getPopulation().getPersons().values())	{
            VehicleType	vehType	=	avgCarType;
            if	(r.nextDouble()	<	0.5)	{
                vehType	=	petrolCarType;
            }
            Vehicle vehicle	=	factory.createVehicle(Id.create(person.getId().toString(),	Vehicle.class),	vehType);
            vehicles.addVehicle(vehicle);
        }
        new MatsimVehicleWriter(vehicles).writeFile(outputVehiclesFilename);

    }

    public static void main(String[] args) {
        String populationFileName = "Scenarios/BilbaoTimeVariantNetwork/input/population_10Percents.xml";
        String outputVehiclesFileName = "Scenarios/BilbaoTimeVariantNetwork/inputEmissions/vehicles.xml";
        run(populationFileName,outputVehiclesFileName);
    }
}
