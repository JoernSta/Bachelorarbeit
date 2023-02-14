import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author JS
 *Klasse repraesentiert die Simulation
 */
 

public class Simulation {
	
	public static double startTime = 8.0;
	public static double endTime = 18.0;
	public static double lastFeasibleRequestTime = 17.25;
	public static double currentTime;
	static double maxWaitingTime = 0.25;
	static double maxDrivingTime = 0.5;
	static ArrayList<Request> allRequests;
	static ArrayList<Vehicle> allVehicles;
	static ArrayList<Times> waitingTimesOfRequests;
	static int capacity = 6;
	public static int countVehicle = 2;
	public static int countRequests = 8;
	public static int typeOneRequests = 25;
	public static int typeTwoRequests = 25;
	static int speed = 50;
	static Point waitingPoint;
	static Point startPoint = new Point(0,2);
	public static int maxMovingPosition = 3;
	public static ArrayList<Times> waitingTimesOfCustomers;
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
		 startPoint = new Point(0,2);
		 ArrayList <Request> assignedRequest = new ArrayList<>();
		 ArrayList <Request> curPassengers = new ArrayList<>();
		 ArrayList <Stopp> curTour = new ArrayList<>();
		 allVehicles = new ArrayList<>();
		 for(int i = 0; i<a; i++){
			 int id = i+1;
			 Vehicle vehicle = new Vehicle(id,startPoint,0,s,assignedRequest,curPassengers,curTour);
			 allVehicles.add(vehicle);
		 }
	 }
	
	/**
	 * Methode zur Bestimmung der Distanz zwischen zwei Punkten
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


	/*
	 * Diese Methode ist veraltet, hier sollte die Zuweisung erfolgen. Wird nun aber in der Klasse Assignment vorgenommen
	 * @param currentRequest, derzeitige Serviceanfrage
	 * @param vehicleList, Liste aller Fahrzeuge
	 */
	/*public static void assignRequestToVehicle(Request currentRequest, ArrayList<Vehicle> vehicleList){
		int id = currentRequest.getId();
		System.out.println("Weise Kundenanfrage" + " " + id + " " +  "ein Fahrzeug zu!");
		double curReqTime = currentRequest.getRequestTime();
		Point reqPickUpPoint = currentRequest.getPickUpPoint();
		Point reqDeliveryPoint = currentRequest.getDropOffPoint();
		Point posVehicle;
		double arrivalTimeOfLastStopp;
		double arrivalTimeToPickUpPoint;
		int assignedVehicle = 0;
		double[] arrivalTimes = new double[vehicleList.size()];
		
		for(int i = 0; i<vehicleList.size(); i++){
			Vehicle vehicle = vehicleList.get(i);
			//Checke, ob Fahrzeug aktuelle Tour besitzt und passe jeweils arrivalTimeOfLastStopp an 
			int tourSize = vehicle.currentTour.size();
			if(tourSize ==0){
			posVehicle = vehicle.getPosition();
			arrivalTimeOfLastStopp = currentTime;
			} else {
			Stopp lastStoppOfCurrentTour = vehicle.currentTour.get(tourSize-1);
			posVehicle = lastStoppOfCurrentTour.getStopp();
			arrivalTimeOfLastStopp = lastStoppOfCurrentTour.getArrivalTime();
			}
			//Berechnung der Distanz und Fahrzeit
			double distanceToPickUpPoint = calculateDistanceBetween2Points(posVehicle, reqPickUpPoint);
			double driveTimeToPickUpPoint = calculateDriveTimeToPoint(distanceToPickUpPoint);
			//Berechnung der Ankunftszeit:
			arrivalTimeToPickUpPoint = arrivalTimeOfLastStopp + driveTimeToPickUpPoint;
			//Ankunftszeiten werden in Array gespeichert
			arrivalTimes[i] = arrivalTimeToPickUpPoint;
			System.out.println("Fahrzeug" + i+1 + " " + "hat eine Ankunftszeit" + " " + arrivalTimes[i]);
		}
		//Die niedrigsteArrivalTime wird bestimmt
		double minArrivalTime = arrivalTimes[0];
		for(int j =0; j<arrivalTimes.length; j++){
			if(arrivalTimes[j] < minArrivalTime){
				assignedVehicle = j;
			}
		}
		Vehicle choosenVehicle = vehicleList.get(assignedVehicle);
		int vehicleId = choosenVehicle.getId();
		double minArrivalTimeToPickUpPoint = arrivalTimes[assignedVehicle];
		Stopp pickUpStopp = new Stopp(id, reqPickUpPoint, minArrivalTimeToPickUpPoint,2);
		double distanceToDeliveryPoint = calculateDistanceBetween2Points(reqPickUpPoint, reqDeliveryPoint);
		double driveTimeToDeliveryPoint = calculateDriveTimeToPoint(distanceToDeliveryPoint);
		double arrivalTimeToDeliveryPoint1 = minArrivalTimeToPickUpPoint + driveTimeToDeliveryPoint;
		Stopp deliveryStopp = new Stopp(id, reqDeliveryPoint, arrivalTimeToDeliveryPoint1,3);
		choosenVehicle.currentTour.add(pickUpStopp);
		choosenVehicle.currentTour.add(deliveryStopp);
		double waitingTimeOfRequest =  minArrivalTimeToPickUpPoint;
		System.out.println("Kundenanfrage:" + " " + currentRequest.getId() + " " + "wurde Fahrzeug:" + " " + vehicleId + " " + "zugewiesen!");
		System.out.println("Wartezeit" + " " +"der" + " " + "Kundenanfrage" + currentRequest.getId() + ":" + " " + waitingTimeOfRequest);
	}*/
	
	/*
	 * Diese Methode startet die Simulation, allerdings veraltet, da Sachen ausgelagert werden
	 * @param requests, Liste aller Serviceanfragen
	 * @param vehicles, Liste aller Fahrzeuge
	 */
	/*public static  void flowSimulation(ArrayList<Request> requests, ArrayList<Vehicle> vehicles){
		setCurrentTime(startTime);
		Point wP = new Point(1,1);
		setWaitingPoint(wP);
		waitingPoint = getWaitingPoint();
		currentTime = getCurrentTime();
		for (int i = 0; i< requests.size(); i++){
			Request request = requests.get(i);
			double requestTime = request.getRequestTime();
			// Verhalten des Fahrzeuges bis eine neue Anfrage reinkommt
			while(requestTime > currentTime){
				for (int j = 0; j< vehicles.size(); j++){
					Vehicle vehicle = vehicles.get(j);
					//Wenn Fahrzeug keine aktuelle Tour besitzt: 
					if(vehicle.currentTour.size() == 0){
						Point vehPos = vehicle.getPosition();
						Point waitPoint = getWaitingPoint();
						double distance = calculateDistanceBetween2Points(vehPos, waitPoint);
						double timeToPoint = calculateDriveTimeToPoint(distance);
						double arrival = currentTime + timeToPoint;
						Stopp stopp = new Stopp(0, waitPoint, arrival, 1);
						vehicle.currentTour.add(stopp);
						System.out.println("Wartepunkt wurde der Tour hinzugefuegt!");
						//Verhalten, falls das Fahrzeug eine Tour besitzt.
					} else {
						System.out.println("Fahrzeug" + " " + vehicle.getId() + " " + "hat noch eine Tour");
						for(int k= 0; k < vehicle.currentTour.size(); k++){
							Stopp tourStopp = vehicle.currentTour.get(0);
							System.out.println(tourStopp.getStopp() + " " + tourStopp.getArrivalTime());
							double tourTime = tourStopp.getArrivalTime();
							//P1 = 8.0 request 8.30
							if(tourTime < requestTime){
								Point tourPoint = tourStopp.getStopp();
								vehicle.setPosition(tourPoint);
								// Der Typ sagt dann eine Aktion an: entweder Warten, einsammeln oder absetzen
								int stoppType = tourStopp.getType();
								if(stoppType == 1){
									System.out.println("Es handelt sich um einen Wartepunkt");
									vehicle.currentTour.remove(tourStopp);
									int sizeAfterRemoving = vehicle.currentTour.size();
									if(sizeAfterRemoving == 0){
										setCurrentTime(requestTime);
									}
								} else if(stoppType == 2){
									System.out.println("Es handelt sich um einen Pick-Up-Point!");
									System.out.println("Methode zum Einsammeln des Kunden aufrufen!");
									vehicle.currentTour.remove(tourStopp);
									int sizeAfterRemoving = vehicle.currentTour.size();
									if(sizeAfterRemoving == 0){
										setCurrentTime(requestTime);
									}
								} else {
									System.out.println("Es handelt sich um einen DeliveryPoint");
									System.out.println("Methode zum Absetzen des Kunden aufrufen!");
									vehicle.currentTour.remove(tourStopp);
									int sizeAfterRemoving = vehicle.currentTour.size();
									if(sizeAfterRemoving == 0){
										setCurrentTime(requestTime);
									}
								}
								System.out.println(vehicle.getId()+ ":" + vehicle.getPosition());
								
							}
							
						} setCurrentTime(requestTime);
					}
				}
			}
			assignRequestToVehicle(request,vehicles);
		}
		/*Berechnung der gesamten Wartezeit
		double waitingTimeOverall = 0;
		for(int i = 0; i< requests.size(); i++){
			Request r = requests.get(i);
			//double waitingTime = r.getWaitingTime();
			//waitingTimeOverall =  waitingTimeOverall + waitingTime;
		}
		System.out.println("Wartezeit aller Anfragen:" + " " + waitingTimeOverall);
		double requestCount = requests.size();
		double averageWaitingTime = waitingTimeOverall / requestCount;
		System.out.println("Durchschnittliche Wartezeit der Anfragen:" + " " + averageWaitingTime);
		*/
		
	// }
	
	/*
	public static double[] createTrainTravelTimes(double endTime, double startTime){
		double countTravelTimes = (endTime - startTime) *2;
		double [] trainTravelTimes = new double[(int) countTravelTimes];
		System.out.println("Anzahl Fahrzeiten:" + countTravelTimes);
		trainTravelTimes[0] = startTime;
		for(int i = 1; i < countTravelTimes; i++){
			int lastIndex = i-1;
			double lastTravelTime = trainTravelTimes[lastIndex];
			trainTravelTimes[i] = lastTravelTime + 0.5;
			System.out.println("Abfahrtzeit:" + trainTravelTimes[i]);
		}
		return trainTravelTimes;
	}
	*/
	
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
	
	/*public static void testSwappingWithIndex(int x, int y){
		int a =7;
		int c = 5;
		int s = 50;
		createVehicles(7,5, 50);
		System.out.println(allVehicles.size());
		for(int i = 0; i<allVehicles.size();i++){
			Vehicle vehicle = allVehicles.get(i);
			System.out.println("Fahrzeugliste vor Swap:" + vehicle.getId());
		}
		ArrayList<TestList> testList = new ArrayList<TestList>();
		for(int i = y; i > x; i-- ){
			ArrayList<Vehicle> copyVehicles = new ArrayList<Vehicle>(allVehicles);
			int z = i - 1;
			Collections.swap(copyVehicles, i, z);
			System.out.println("getauschte Elemente:" + i + " " + z);
			TestList test = new TestList(copyVehicles);
			testList.add(test);
			for(int k = 0; k<allVehicles.size();k++){
				Vehicle v = allVehicles.get(k);
				System.out.println("Fahrzeugliste nach Swap pro Schritt:" + v.getId());
		}
		}
		for(int i = 0; i<allVehicles.size();i++){
			Vehicle v = allVehicles.get(i);
			System.out.println("Fahrzeugliste nach Swap:" + v.getId());
		}
		
		
	}*/
	
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
	
	public static void startSimulation(int waitingStrategy,double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition){
		waitingTimesOfCustomers = new ArrayList<Times>();
		switch (waitingStrategy) {
			case 0 -> {
				Touring.touringWithWaitAtCurrentPosition(startTime, endTime, requests, vehicles, maxWaitingTime, maxWaitingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, allRequests);
			}
			case 1 -> {
				ArrayList<Point> allPoints = waitingStrategies.createListWithAllPoints(startPoint);
				Touring.touringWithCenterOfGravity(startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers, allPoints, startPoint);
				printOutput(waitingTimesOfCustomers, allRequests);
			}
			case 2 -> {
				Touring.touringWithDriveFirstWaitStrategy(startTime, endTime, requests, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, waitingTimesOfCustomers);
				printOutput(waitingTimesOfCustomers, allRequests);
			}
		}
		
			
		
	}
	
	public static void printOutput(ArrayList<Times> waitingTimesOfCustomers,ArrayList<Request> requests){
		Collections.sort(waitingTimesOfCustomers);
		for (Times time : waitingTimesOfCustomers) {
			double waitTime = time.getWaitingTime();
			double roundedWaitingTime = Math.round(waitTime * 10.0) / 10.0;
			System.out.println("Wartezeit des Kunden:" + time.getRequestId() + " " + "betraegt:" + roundedWaitingTime);
		}
		Times minTime = waitingTimesOfCustomers.get(0);
		double minWaiting = minTime.getWaitingTime();
		double roundedMinTime = Math.round(minWaiting * 10.0) / 10.0 * 60;
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
		
	}
	
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

	
	public static void main(String[] args) {
		allRequests = Datengenerator.createRequests(typeOneRequests,typeTwoRequests,startTime,endTime, lastFeasibleRequestTime);
		printRequests(allRequests);
		createVehicles(countVehicle,speed);
		// testMethodSwapping(allVehicles);
		// flowSimulation(allRequests,allVehicles);
		// createTrainTravelTimes(endTime, startTime);
		// testSwappingWithIndex(2,4);
		// createTestData(allVehicles,maxWaitingTime,maxDrivingTime,capacity);
		
		// Test mit Strategie 0
		// startSimulation(waitingStrategyAtCurrentPosition,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		// Test mit Strategie 1
		// startSimulation(waitingStrategyCenterOfGravity,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		// Test mit Strategie 2:
		startSimulation(waitingStrategyDriveFirst,startTime,endTime,allRequests,allVehicles,maxWaitingTime,maxDrivingTime,capacity,maxMovingPosition);
		
		// printOutput(waitingTimesOfCustomers,allRequests);
		// ArrayList<Point> points = waitingStrategies.createListWithAllPoints(startPoint);
		// waitingStrategies.calculateCenterOfGravity(points);

	}
}
