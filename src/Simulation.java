import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author JS
 *Klasse repraesentiert die Simulation
 */
 

public class Simulation {
	
	public static double startTime = 8.0;
	public static double endTime = 18.0;
	public static double lastFeasibleRequestTime = 17.5;
	public static double currentTime;
	//sechs Minuten 0.1
	//15 Minuten 0.25
	static double maxWaitingTime = 0.25;
	static double maxDrivingTime = 0.25;
	static ArrayList<Request> allRequests;
	static ArrayList<Request> allRequests2;
	static ArrayList<Request> allRequests3;
	static ArrayList<Vehicle> allVehicles;
	static ArrayList<Times> waitingTimesOfRequests;
	static ArrayList<DriveTimesOfCustomers> driveTimesOfCustomers;
	static int capacity = 6;
	public static int countVehicle = 3; //Anzahl der Fahrzeuge
	public static int typeOneRequests = 25; //Anzahl der Anfragen mit Fahrtrichtung U-->A
	public static int typeTwoRequests = 25; //Anzahl der Anfragen mit Fahrtrichtung A-->U
	public static int typeOneRequests2 = 40; //Anzahl der Anfragen mit Fahrtrichtung U-->A
	public static int typeTwoRequests2 = 40; //Anzahl der Anfragen mit Fahrtrichtung A-->U
	public static int typeOneRequests3 = 50;
	public static int typeTwoRequests3 = 50;
	
	static int speed = 30;
	static Point waitingPoint;
	static Point startPoint = new Point(2,0);
	public static int maxMovingPosition = 3;
	public static ArrayList<Times> waitingTimesOfCustomers;
	public static int waitingStrategyCenterOfGravity = 1;
	public static int waitingStrategyDriveFirst = 2;
	public static int waitingStrategyWaitFirst = 3;
	public static int waitingStrategyCombiDFWF = 4;
	
	

	/**
	 * Methode gibt die derzeitige Zeit zurueck
	 * @return currentTime
	 */
	public static double getCurrentTime(){
		return currentTime;
	}
	
	/**
	 * Setzt die aktuelle Zeit auf t
	 */
	public static void setCurrentTime(double t){
		currentTime = t;
	}
	
	/**
	 * Addiert der aktuellen Zeit, t hinzu. Bei Routenablauf benaetigt
	 */
	public void addTimeToCurTime(int t){
		currentTime = currentTime + t;
	}
	
	//Wartepunkt erstmal immer am Startpunkt, die weiteren Wartestrategien folgen noch
	//sollen dann je nach Eingabe ausgesucht werden --> switch-Case
	public static Point getWaitingPoint(){
		return waitingPoint;
	}
	
	/**
	 * Versetzt die Position des Wartepunktes
	 * @param wP = der Wartepunkt
	 */
	public static void setWaitingPoint(Point wP){
		waitingPoint = wP;
	}
	/**
	 * Gibt alle Objekte der gesamten Requestliste aus --> zu Testzwecken
	 */
	 public static void printRequests(ArrayList<Request> list){
		 for (Request r : list) {
			 int id = r.getId();
			 double rqTime = r.getRequestTime();
			 Point point1 = r.getPickUpPoint();
			 Point point2 = r.getDropOffPoint();
			 int passenger = r.getPassengerNr();
			 int state = r.getPassengerState();
			 int type = r.getType();
			 double serviceTime = r.getServiceTime();

			 System.out.println(id + ";" + " " + rqTime + ";" + " " + point1 + ";" + " " + point2 + ";" + " " + passenger + ";" + " " + state + ";" + " " + serviceTime + ";" + " " + type + ";");
		 }
	 }
	 
	 /**
	  * Die Methode erstellt die Fahrzeuge
	  * @param a = die Anzahl an zu erstellenden Fahrzeuge
	  * @param s = die Geschwindigkeit der Fahrzeuge
	  */
	 public static void createVehicles(int a, int s){
		 startPoint = new Point(2,0);
		 ArrayList <Stopp> curTour = new ArrayList<>();
		 allVehicles = new ArrayList<>();
		 int usedCap = 0;
		 for(int i = 0; i<a; i++){
			 int id = i+1;
			 Vehicle vehicle = new Vehicle(id,startPoint,0,s,curTour,usedCap);
			 allVehicles.add(vehicle);
		 }
	 }
	 
	 public static void createVehiclesWithDiffStart(int a, int s){
		 ArrayList <Stopp> curTour = new ArrayList<>();
		 allVehicles = new ArrayList<>();
		 int usedCap = 0;
		 for(int i = 0; i<a; i++){
			 int id = i+1;
			 if(id % 2 == 0){
				 startPoint = new Point(2,0);
			 }else {
				 startPoint = new Point(2,3);
			 }
			 Vehicle vehicle = new Vehicle(id,startPoint,0,s,curTour,usedCap);
			 allVehicles.add(vehicle);
		 }
	 }
	
	/**
	 * Methode zur Bestimmung der Distanz zwischen zwei Punkten
	 * Die Distanzen werden euklidisch bestimmt.
	 * @param p1 Punkt 1 der zur Berechnung benoetigt wird
	 * @param p2 Punkt 2, der zur Berechnung benoetigt wird
	 * @return die berechnete Distanz in km
	 */
	public static double calculateDistanceBetween2Points(Point p1, Point p2){
		return p1.distance(p2);
	}
	
	/**
	 * Methode zur Berechnung der Fahrzeit zwischen zwei Punkten 
	 * @param distance
	 * @return benoetigte Fahrzeit der Strecke (in Stunden)
	 */
	public static double calculateDriveTimeToPoint(double distance){
		return distance/speed;
	}

	/**
	 * Diese Methode startet die Simulation.
	 * @param waitingStrategy, die Wartestrategie, welche untersucht werden soll.
	 * @param startTime,die Zeit, wann der Betriebshorizont starten soll.
	 * @param endTime, die Zeit wann der Betriebshorizont endet
	 * @param requests, Liste mit Anfragen
	 * @param vehicles, Liste der eingesetzten Fahrzeuge
	 * @param maxWaitingTime, maximal erlaubte Wartezeit der Kunden zur Bedienung
	 * @param maxDrivingTime maximale Fahrzeit der Insassen.
	 * @param maxCapacity maximale Kapazität der Fahrzeuge
	 * @param maxMovingPosition, maximale Verschiebung der Kunden(wurde allerdings noch nicht implementiert)
	 */
	public static void startSimulation(int waitingStrategy,double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition){
		waitingTimesOfCustomers = new ArrayList<Times>();
		driveTimesOfCustomers = new ArrayList<DriveTimesOfCustomers>();
		switch (waitingStrategy) {
			case 1:
				ArrayList<Point> allPoints = waitingStrategies.createListWithAllPoints(startPoint);
				Touring.touringWithCenterOfGravity(waitingStrategy,startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers, allPoints, startPoint,driveTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, requests,vehicles,driveTimesOfCustomers);
				break;
			case 2: 
				Touring.touringWithDriveFirstWaitStrategy(waitingStrategy,startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers,driveTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, requests,vehicles,driveTimesOfCustomers);
				break;
			case 3:
				Touring.touringWithDriveFirstWaitStrategy(waitingStrategy,startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers,driveTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, requests,vehicles,driveTimesOfCustomers);
				break;
			case 4:
				Touring.touringWithDriveFirstWaitStrategy(waitingStrategy,startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers,driveTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, requests,vehicles,driveTimesOfCustomers);
				break;	
		}
		
			
		
	}
	
	public static void printOutput(ArrayList<Times> waitingTimesOfCustomers,ArrayList<Request> requests,ArrayList<Vehicle> vehicles,ArrayList<DriveTimesOfCustomers> driveTimes){
		Collections.sort(waitingTimesOfCustomers);
		for (Times time : waitingTimesOfCustomers) {
			double waitTime = time.getWaitingTime();
			double roundedWaitingTime = Math.round(waitTime * 100.0) / 100.0;
			System.out.println("Wartezeit des Kunden:" + time.getRequestId() + " " + "betraegt:" + roundedWaitingTime);
		}
		Times minTime = waitingTimesOfCustomers.get(0);
		double minWaiting = minTime.getWaitingTime();
		double roundedMinTime = Math.round(minWaiting * 60);
		System.out.println("Die minimale Wartezeit hat der Kunde:" + minTime.getRequestId() + " " + "mit einer Wartezeit von:" + roundedMinTime + " " +"Minuten.");
		int size = waitingTimesOfCustomers.size();
		int lastIndex = size - 1;
		Times maxTime = waitingTimesOfCustomers.get(lastIndex);
		double maxWaiting = maxTime.getWaitingTime();
		double roundedMaxWaiting = Math.round(maxWaiting * 100.0) / 100.0 * 60;
		System.out.println("Die maximale Wartezeit hat der Kunde:" + maxTime.getRequestId() + " " + "mit einer Wartezeit von:" + roundedMaxWaiting + " " +"Minuten.");
		double averageTime = calculateAverageWaitingTime(waitingTimesOfCustomers);
		double roundedAverageTime = Math.round(averageTime * 100.0) / 100.0;
		System.out.println("Die durchschnittliche Wartezeit betraegt:" + roundedAverageTime + " " +"Minuten.");
		double serviceLevel = calculateServiceLevel(requests);
		System.out.println("Der Servicegrad betraegt:" + serviceLevel + "%");
		int maxUsedCap = maxUsedCapacityOfVehicles(vehicles);
		System.out.println("Die Größte Kapazitätsauslastung war eine Personenanzahl von:" + maxUsedCap);
		int averageUsedCap = averageUsedCapacityOfVehicles(vehicles);
		System.out.println("Die durchschnittliche Kapazitätsauslastung war eine Personenanzahl von:" + averageUsedCap);
		double averageDriveTime = averageDriveTimeOfCustomers(driveTimes);
		double roundedAverageDriveTime = Math.round(averageDriveTime * 100.0) / 100.0 * 60;
		System.out.println("Die durchschnittliche Fahrzeit der Kunden betrug:" + roundedAverageDriveTime + " " + "Minuten.");
		
	}
	/**
	 * Diese Methode berechnet die durchschnittliche Wartezeit aller Kunden.
	 * 
	 * @param waitingTimeOfCustomers
	 * @return durchschnittliche Wartezeit der Kunden
	 * 
	 * 
	 */
	public static double calculateAverageWaitingTime(ArrayList<Times> waitingTimeOfCustomers){
		int size = waitingTimeOfCustomers.size();
		double waitingTime = 0.0;
		for (Times time : waitingTimeOfCustomers) {
			double waitingTimeOfCustomer = time.getWaitingTime();
			waitingTime = waitingTime + waitingTimeOfCustomer;
		}
		return waitingTime / size;
	}
	
	public static double calculateServiceLevel(ArrayList<Request> requests){
		int servedCustomers = 0;
		int size = requests.size();
		for (Request request : requests) {
			int requestState = request.getPassengerState();
			if (requestState != 0) {
				servedCustomers = servedCustomers + 1;
			}
		}
		System.out.println("Es wurden" + " " + servedCustomers + " " + "von insgesamt" + " " + size + " " + "Kunden bedient.");
		double servedRequests = servedCustomers;

		return (servedRequests / size) * 100;
	}
	
	public static int maxUsedCapacityOfVehicles(ArrayList<Vehicle> vehicles){
		int maxUsedCap = 0;
		for(int i = 0; i< vehicles.size(); i++){
			Vehicle v = vehicles.get(i);
			int usedCapOfVehicle = v.getUsedCap();
			if(usedCapOfVehicle > maxUsedCap){
				maxUsedCap = usedCapOfVehicle;
			}
		}
		return maxUsedCap;
	}
	
	public static int averageUsedCapacityOfVehicles(ArrayList<Vehicle> vehicles){
		int averageUsedCap = 0;
		int size = vehicles.size();
		for(int i = 0; i< vehicles.size(); i++){
			Vehicle v = vehicles.get(i);
			int usedCapOfVehicle = v.getUsedCap();
			averageUsedCap = averageUsedCap + usedCapOfVehicle; 
		}
		averageUsedCap = averageUsedCap / size;
		return averageUsedCap;
	}
	
	public static double averageDriveTimeOfCustomers(ArrayList<DriveTimesOfCustomers> driveTimes){
		double averageDriveTime = 0.0;
		for(DriveTimesOfCustomers driveTime : driveTimes){
			double pickUpTime = driveTime.getPickUpTime();
			double dropOffTime = driveTime.getDropOffTime();
			double difference = dropOffTime - pickUpTime;
			averageDriveTime = averageDriveTime + difference;
		}
		averageDriveTime = averageDriveTime / driveTimes.size();
		return averageDriveTime;
	}
	
	public static void main(String[] args) {
		File requestsFile = new File("customer_with_50.csv");

		if(!requestsFile.exists()) {
			ArrayList<Request> requests = Datengenerator.createRequests(typeOneRequests, typeTwoRequests, startTime, endTime, lastFeasibleRequestTime);
			Datengenerator.exportRequests(requests, requestsFile);
		}
		
		File requestsWith80Customers = new File("80_customer.csv");
		
		if(!requestsWith80Customers.exists()) {
			ArrayList<Request> requests2 = Datengenerator.createRequests(typeOneRequests2, typeTwoRequests2, startTime, endTime, lastFeasibleRequestTime);
			Datengenerator.exportRequests(requests2, requestsWith80Customers);
		}
		
File requestsWith120Customers = new File("100_customer.csv");
		
		if(!requestsWith120Customers.exists()) {
			ArrayList<Request> requests3 = Datengenerator.createRequests(typeOneRequests3, typeTwoRequests3, startTime, endTime, lastFeasibleRequestTime);
			Datengenerator.exportRequests(requests3, requestsWith120Customers);
		}

		allRequests = Datengenerator.importRequests(requestsFile);
		
		allRequests2 = Datengenerator.importRequests(requestsWith80Customers);
		
		allRequests3 = Datengenerator.importRequests(requestsWith120Customers);
		
		
	
		
		//printRequests(allRequests);
		//printRequests(allRequests);
		printRequests(allRequests3);
		//createVehicles(countVehicle,speed);
		createVehiclesWithDiffStart(countVehicle,speed);
		
		
		/**
		 *Hier sind die Strategien und Szenarien auszukommentieren, die betrachtet werden sollen.
		 */

		
		
		//Test mit Strategie CenterOfGravity und 80 Anfragen
		//startSimulation(waitingStrategyCenterOfGravity,startTime,endTime,allRequests2,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie CenterOfGravity und 50 Anfragen.
		//startSimulation(waitingStrategyCenterOfGravity,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie DriveFirst und 80 Anfragen:
		//startSimulation(waitingStrategyDriveFirst,startTime,endTime,allRequests2,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);

		//Test mit Strategie DriveFirst und 50 Anfragen:
		//startSimulation(waitingStrategyDriveFirst,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie DriveFirst und 100 Anfragen:
		//startSimulation(waitingStrategyDriveFirst,startTime,endTime,allRequests3,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie WaitFirst und 80 Anfragen:
		//startSimulation(waitingStrategyWaitFirst,startTime,endTime,allRequests2,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie WaitFirst und 100 Anfragen:
		startSimulation(waitingStrategyWaitFirst,startTime,endTime,allRequests3,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie WaitFirst und 50 Anfragen:
		//startSimulation(waitingStrategyWaitFirst,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie Kombi Drive-First und Wait-First und 80 Anfragen:
		//startSimulation(waitingStrategyCombiDFWF,startTime,endTime,allRequests2,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		//Test mit Strategie Kombi Drive-First und Wait-First und 50 Anfragen:
		//startSimulation(waitingStrategyCombiDFWF,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
	
	   
	   

	   
		
		
	}
}
