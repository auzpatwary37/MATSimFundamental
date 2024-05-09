package MATSimRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;

public class PopulationModification {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Population population = PopulationUtils.readPopulation("siouxfalls-2014/Siouxfalls_population.xml.gz");
		Config config = ConfigUtils.createConfig();
		ConfigUtils.loadConfig(config, "siouxfalls-2014/config_default.xml");
		config.plans().setInputFile("siouxfalls-2014/Siouxfalls_population.xml.gz");
		config.facilities().setInputFile("siouxfalls-2014/Siouxfalls_facilities.xml.gz");
		config.network().setInputFile("siouxfalls-2014/Siouxfalls_network_PT.xml");
		config.transit().setTransitScheduleFile("siouxfalls-2014/Siouxfalls_transitSchedule.xml");
		config.transit().setVehiclesFile("siouxfalls-2014/Siouxfalls_vehicles.xml");
	

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Population population = scenario.getPopulation();
		ActivityFacilities facility = scenario.getActivityFacilities();
	
		
		PopulationFactory popFac = population.getFactory();
		
		for(int i = 0; i<6500; i++) {
			Person person = popFac.createPerson(Id.create("p_o1_d_"+i, Person.class));
			Plan plan = popFac.createPlan();
			ActivityFacility fac1= drawRandomFacility(facility.getFacilitiesForActivityType("home"));
			Activity act1 = popFac.createActivityFromActivityFacilityId("home", fac1.getId());
			act1.setCoord(fac1.getCoord());
			act1.setEndTime((int)(7+(9-7)*Math.random())*3600);
			String mode;
			if(Math.random()>=.7)mode= "car";
			else mode = "pt";
			Leg leg = popFac.createLeg(mode);
			ActivityFacility fac2 = drawRandomFacility(facility.getFacilitiesForActivityType("work"), fac1.getCoord(), 5000);
			Activity act2 = popFac.createActivityFromActivityFacilityId("work", fac2.getId());
			act2.setEndTime((int)(10+(19-10)*Math.random())*3600);
			act1.setCoord(fac1.getCoord());
			String mode2;
			if(Math.random()>=.7)mode2= "car";
			else mode2 = "pt";
			Leg leg2 = popFac.createLeg(mode2);
			ActivityFacility fac3 = drawRandomFacility(facility.getFacilitiesForActivityType("secondary"), fac2.getCoord(), 5000);
			Activity act3 = popFac.createActivityFromActivityFacilityId("secondary", fac3.getId());
			act2.setEndTime((int)(19+(22-19)*Math.random())*3600);
			String mode3;
			if(Math.random()>=.7)mode3= "car";
			else mode3 = "pt";
			Leg leg3 = popFac.createLeg(mode3);
			plan.addActivity(act1);
			plan.addLeg(leg);	
			plan.addActivity(act2);
			plan.addLeg(leg2);
			plan.addActivity(act3);
			plan.addLeg(leg3);
			person.addPlan(plan);
			population.addPerson(person);
		}
		
		new PopulationWriter(population).write("siouxfalls-2014/population_6500.xml");

	}
	
	public static ActivityFacility drawRandomFacility(Map<Id<ActivityFacility>,ActivityFacility> facilities) {
	       Random generator = new Random();
	       List<Map.Entry<Id<ActivityFacility>,ActivityFacility>> entries = new ArrayList<>(facilities.entrySet());
	       ActivityFacility randomValue = entries.get(generator.nextInt(entries.size())).getValue();
	       return randomValue;
	   }
	
	public static ActivityFacility drawRandomFacility(Map<Id<ActivityFacility>,ActivityFacility> facilities, Coord coord, double dist) {
		boolean ifOkay = false;
		int maxIter = 50;
		int i = 0;
		ActivityFacility randomValue = null;
		while(!ifOkay && i<maxIter) {
			Random generator = new Random();
			List<Map.Entry<Id<ActivityFacility>,ActivityFacility>> entries = new ArrayList<>(facilities.entrySet());
		    randomValue = entries.get(generator.nextInt(entries.size())).getValue();
			if(NetworkUtils.getEuclideanDistance(coord, randomValue.getCoord())<=dist)ifOkay = true;
			
			i++;
		}
		return randomValue;
	}

}
