import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author JS
 *Klasse repraesentiert die Simulation
 */
 

public class Simulation {
	
	public static double startTime = 8.0;
	public static double endTime = 18.0;
	public static double lastFeasibleRequestTime = 17.5;
	public static double currentTime;
	//sechs Minuten 0.08
	//15 Minuten 0.25
	static double maxWaitingTime = 0.25;
	static double maxDrivingTime = 0.5;
	static ArrayList<Request> allRequests;
	static ArrayList<Vehicle> allVehicles;
	static ArrayList<Times> waitingTimesOfRequests;
	static ArrayList<DriveTimesOfCustomers> driveTimesOfCustomers;
	static int capacity = 6;
	public static int countVehicle = 2;
	public static int countRequests = 8;
	public static int typeOneRequests = 40;
	public static int typeTwoRequests = 40;
	static int speed = 30;
	static Point waitingPoint;
	static Point startPoint = new Point(0,2);
	public static int maxMovingPosition = 3;
	public static ArrayList<Times> waitingTimesOfCustomers;
	// nicht für die BA relevant
	public static int waitingStrategyAtCurrentPosition = 0;
	public static int waitingStrategyCenterOfGravity = 1;
	public static int waitingStrategyDriveFirst = 2;

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
	
	/**
	 * Methode zur Bestimmung der Distanz zwischen zwei Punkten.
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


	
	
	public static void testMethodSwapping(ArrayList <Vehicle> v){
		ArrayList<Vehicle> vehicleCopy = new ArrayList<Vehicle>(v);
		for (Vehicle vehicle : vehicleCopy) {
			System.out.println("Kopierte Liste:" + vehicle.getId() + ";" + " ohne swapping");
		}
		Collections.swap(vehicleCopy, 0, 1);
		for (Vehicle vehicle : vehicleCopy) {
			System.out.println("Kopierte Liste:" + vehicle.getId() + ";" + "mit swapping");
		}
		for (Vehicle vehicle : v) {
			System.out.println("Ursprungsliste:" + vehicle.getId() + ";");
		}
	}

	
	public static void createTestData(ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime, int cap){
		waitingTimesOfCustomers = new ArrayList<Times>();
		Point depot = new Point(0,2);
		Point p1 = new Point(4,1);
		Point p2 = new Point (0,1);
		Point p3 = new Point(3,5);
		Point p4 = new Point (1,1);
		Point p5 = new Point (2,1);
		Request r1 = new Request(1,8.2,depot,p1,1,0,1,8.5);
		Request r2 = new Request(2,8.3,depot,p2,1,0,1,8.5);
		Request r3 = new Request(3,8.4,depot,p3,1,0,1,9.0);
		Request r4 = new Request(4,8.2,depot,p4,1,0,1,8.5);
		Request r5 = new Request(5,7.0, depot,p5, 1,0,1,9.0);
		Request r6 = new Request(6,8.6,p1,depot,1,0,2,9.0);
		Request r7 = new Request(7,8.6,p2,depot,1,0,2,9.0);
		Request r8 = new Request(8,8.5,p3,depot,1,0,2,9.0);
		Request capacityRequest = new Request(10,8.3,depot,p2,9,0,1,8.5);
		ArrayList<Request> requestList = new ArrayList<Request>();
		requestList.add(r1);
		requestList.add(capacityRequest);
		requestList.add(r2);

		// requestList.add(r3);
		// requestList.add(r4);
		// requestList.add(r5);
		// requestList.add(r6);
		// requestList.add(r7);
		// requestList.add(r8);

		for (Request curRequest : requestList) {
			int id = curRequest.getId();
			System.out.println("ID:" + id);
			Assignment.requestAssigment(curRequest, vehicles, maxWaitingTime, maxDrivingTime, cap, maxMovingPosition, endTime, waitingTimesOfCustomers);
		}
		
		
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
	 * @param maxMovingPosition, maximale Verschiebung der Kunden (wurde allerdings noch nicht implementiert)
	 */
	public static void startSimulation(int waitingStrategy,double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition){
		waitingTimesOfCustomers = new ArrayList<Times>();
		driveTimesOfCustomers = new ArrayList<DriveTimesOfCustomers>();
		switch (waitingStrategy) {
			case 0:
				Touring.touringWithWaitAtCurrentPosition(startTime, endTime, requests, vehicles, maxWaitingTime, maxWaitingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, allRequests,vehicles);
				break;
			case 1: 
				ArrayList<Point> allPoints = waitingStrategies.createListWithAllPoints(startPoint);
				Touring.touringWithCenterOfGravity(startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers, allPoints, startPoint);
				printOutput(waitingTimesOfCustomers, allRequests,vehicles);
				break;
			case 2: 
				Touring.touringWithDriveFirstWaitStrategy(startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, allRequests,vehicles);
				break;
			
		}
		
			
		
	}
	
	public static void printOutput(ArrayList<Times> waitingTimesOfCustomers,ArrayList<Request> requests,ArrayList<Vehicle> vehicles){
		Collections.sort(waitingTimesOfCustomers);
		for (Times time : waitingTimesOfCustomers) {
			double waitTime = time.getWaitingTime();
			double roundedWaitingTime = Math.round(waitTime * 10.0) / 10.0;
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
		double roundedMaxWaiting = Math.round(maxWaiting * 10.0) / 10.0 * 60;
		System.out.println("Die maximale Wartezeit hat der Kunde:" + maxTime.getRequestId() + " " + "mit einer Wartezeit von:" + roundedMaxWaiting + " " +"Minuten.");
		double averageTime = calculateAverageWaitingTime(waitingTimesOfCustomers);
		double roundedAverageTime = Math.round(averageTime * 100.0) / 100.0;
		System.out.println("Die durchschnittliche Wartezeit betraegt:" + roundedAverageTime + " " +"Minuten.");
		double serviceLevel = calculateServiceLevel(requests);
		System.out.println("Der Servicegrad betraegt:" + serviceLevel + "%");
		int maxUsedCap = maxUsedCapacityOfVehicles(vehicles);
		System.out.println("Die Größte Kapazitätsauslastung war eine Personenanzahl von:" + maxUsedCap);
		int averageUsedCap = averageUsedCapacityOfVehicles(vehicles);
		System.out.println("Die durchschnittliche Kapazitätsauslastung war eine Personenanzahl von:" + maxUsedCap);
		
	}
	/**
	 * Diese Methode berechnet die durchschnittliche Wartezeit aller Kunden.
	 * 
	 * @param waitingTimeOfCustomers
	 * @return
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
		for (Vehicle v : vehicles) {
			int usedCapOfVehicle = v.getUsedCap();
			if (usedCapOfVehicle > maxUsedCap) {
				maxUsedCap = usedCapOfVehicle;
			}
		}
		return maxUsedCap;
	}
	
	public static int averageUsedCapacityOfVehicles(ArrayList<Vehicle> vehicles){
		int averageUsedCap = 0;
		int size = vehicles.size();
		for (Vehicle v : vehicles) {
			int usedCapOfVehicle = v.getUsedCap();
			averageUsedCap = averageUsedCap + usedCapOfVehicle;
		}
		averageUsedCap = averageUsedCap / size;
		return averageUsedCap;
	}
	
	public static void main(String[] args) {
		File requestsFile = new File("customer_with_50.csv");

		if(!requestsFile.exists()) {
			ArrayList<Request> requests = Datengenerator.createRequests(typeOneRequests, typeTwoRequests, startTime, endTime, lastFeasibleRequestTime);
			Datengenerator.exportRequests(requests, requestsFile);
		}
		
		File requestsWith80Customers = new File("80_customer.csv");
		
		if(!requestsWith80Customers.exists()) {
			ArrayList<Request> requests = Datengenerator.createRequests(typeOneRequests, typeTwoRequests, startTime, endTime, lastFeasibleRequestTime);
			Datengenerator.exportRequests(requests, requestsWith80Customers);
		}

		allRequests = Datengenerator.importRequests(requestsFile);
		ArrayList<Request> allRequests2 = Datengenerator.importRequests(requestsWith80Customers);

		Point p1 = new Point(2,2);
		Point p2 = new Point(4,1);
		double distance = calculateDistanceBetween2Points(p1,p2);
		double driveTime = calculateDriveTimeToPoint(distance);
		System.out.println("Fahrzeit:" + driveTime);
		
	
		
		
		//printRequests(allRequests);
		printRequests(allRequests2);
		createVehicles(countVehicle,speed);
		
		

		
		// Test mit Strategie 0
		//startSimulation(waitingStrategyAtCurrentPosition,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		//Test mit Strategie 1
		//startSimulation(waitingStrategyCenterOfGravity,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		//Test mit Strategie 2:
		startSimulation(waitingStrategyDriveFirst,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);

		
		//printOutput(waitingTimesOfCustomers,allRequests);
		ArrayList<Point> points = waitingStrategies.createListWithAllPoints(startPoint);
		waitingStrategies.calculateCenterOfGravity(points);

	}
}
