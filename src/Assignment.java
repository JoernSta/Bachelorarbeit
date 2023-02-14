import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Assignment {
	
	public static double waitingTime;
	public static double waitingTimes[];
	
	public static void requestAssigment(Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime, int maxCapacity, int maxMovingPosition, double endTime,ArrayList<Times> waitingTimesOfRequest){
		int serviceType = currentRequest.getType();
		
		switch(serviceType) {
		case 1:
			assignTypeOneRequest(currentRequest,vehicles, maxWaitingTime,maxDrivingTime, maxCapacity,endTime, maxMovingPosition,waitingTimesOfRequest);
			break;
		case 2:
			assignTypeTwoRequest (currentRequest, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity,endTime, maxMovingPosition,waitingTimesOfRequest);
			break;
		}
		
		
		
	}
	/**
	 * 
	 * @param currentRequest, die aktuell zu zuweisende Anfrage
	 * @param vehicles, die Liste aller Fahrzeug
	 * @param maxWaitingTime, die maximal erlaubte Wartezeit des Kunden
	 * @param maxDrivingTime, die maximal erlaubte Fahrzeit eines Passagiers
	 * @param maxCapacity, die maximal mögliche Kapazität
	 * @param waitingTimesOfRequests
	 */
	public static void assignTypeOneRequest(Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime,int maxCapacity, double endTime, int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		int requestId = currentRequest.getId();
		System.out.println("CurrentRequestId:" + requestId);
		int passengers = currentRequest.getPassengerNr();
		int serviceType = currentRequest.getType();
		double currentTime = currentRequest.getRequestTime();
		double serviceTime = currentRequest.getServiceTime();
		Point pickUpPoint = currentRequest.getPickUpPoint();
		Point dropOffPoint = currentRequest.getDropOffPoint();
		double lastArrivalTime = serviceTime + maxWaitingTime;
		double lastDropOffTime = lastArrivalTime + maxDrivingTime;
		ArrayList<AssignedVehicle> vehicleForAssigning = new ArrayList<AssignedVehicle>();
		
		System.out.println("Assigningliste zu Beginn des Assignings:" + vehicleForAssigning.size());	
		
		//Die Liste Vehicles kopieren/clonen.
		//Gehe Liste der Fahrzeuge durch, nutze dafür eine Kopie der originalen Liste.
		for(int i = 0; i < vehicles.size(); i++){
			Vehicle vehicle = vehicles.get(i);
			ArrayList<Stopp> tourOfCurrentVehicle = vehicle.currentTour;
			System.out.println("FahrzeugId:" + "" + vehicle.getId() + "Tourengröße des Fahrzeuges" + tourOfCurrentVehicle.size());
			ArrayList<Stopp> tour = new ArrayList<Stopp>(tourOfCurrentVehicle);
			
			int tourSize = tour.size();
			System.out.println("Kopierte Tourenliste des Fahrzeuges vor der Zuweisung:" + tourSize);
			//Falls die Tour leer ist füge die Serviceanfrage vorne ein, berechne vorher die Zeiten
			if(tourSize == 0){
				Point currentPosition = vehicle.getPosition();
				double distance = Simulation.calculateDistanceBetween2Points(currentPosition, pickUpPoint);
				double driveTimeForDistance = Simulation.calculateDriveTimeToPoint(distance);
				double arrivalTimeAtPickUpPoint = currentTime + driveTimeForDistance;
				//Wenn die Ankunftszeit <= der Servicezeit ist, dann setze die Wartezeit des Kunden auf 0.0.
				if(arrivalTimeAtPickUpPoint <= serviceTime){
					waitingTime = 0.0;
					int stoppType = 2;
					System.out.println(requestId);
					Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,serviceTime,stoppType,serviceTime,lastArrivalTime,passengers,0,serviceTime,serviceType);
					//RequestId und RequestType wurden nicht richtig gespeichert.
					pickUpStopp.setRequestId(requestId);
					pickUpStopp.setRequestType(serviceType);
					tour.add(pickUpStopp);
					Stopp dropOffStopp = createDropOffStopp(dropOffPoint,maxDrivingTime,currentRequest);
					dropOffStopp.setRequestId(requestId);
					dropOffStopp.setRequestType(serviceType);
					dropOffStopp.setServiceTime(serviceTime);
					tour.add(dropOffStopp);
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
					
					System.out.println("Assigngliste nach Einfügen:" + vehicleForAssigning.size());
				//Wenn die Ankunftszeit größer der Servicezeit, überprüfe, ob diese die maximal erlaubte Wartezeit einhält.
				} else if(arrivalTimeAtPickUpPoint > serviceTime) {
					boolean checkingWaitingTime = checkWaitingTime(arrivalTimeAtPickUpPoint, lastArrivalTime);
					if(checkingWaitingTime){
						waitingTime = arrivalTimeAtPickUpPoint - serviceTime;
						System.out.println(requestId);
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint, arrivalTimeAtPickUpPoint,1,arrivalTimeAtPickUpPoint,lastArrivalTime,passengers,0,serviceTime,1);
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(serviceType);
						tour.add(pickUpStopp);
						Stopp dropOffStopp = createDropOffStopp(dropOffPoint,maxDrivingTime,currentRequest);
						dropOffStopp.setRequestId(requestId);
						dropOffStopp.setRequestType(serviceType);
						dropOffStopp.setServiceTime(serviceTime);
						tour.add(dropOffStopp);
		
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour,waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					}
				}
			}
			//Wenn die Tour bereits Punkte enthält, dann überprüfe drei mögliche Fälle
			if (tourSize > 0){
				//Fall 1: die serviceTime ist kleiner als die serviceTime der bisherigen Pick-up Points in der Tour --> Füge den Punkt vorne an und den drop-Off Point dahinter. 
				//Fall 2: die ServiceTime ist größer als die bisherigen	Pick-Up-Points --> Füge Punkt hinten an der Liste ein.
				//Fall 3: Die serviceTime ist gleich der schon bisherigen Pick-Up-Points --> Füge den Pick-Up Point an der letzten Stelle der Pick-Up Points ein, wo die gleiche Servicezeit ist.
				//Füge Drop-Off Punkt an die letzte Stelle der zugehörigen Pick-Up Points
				//Swappe Positionen der Drop-Off Punkte durch
				int indexOfPickUpPoint = 0;
				int indexOfDropOffPoint = 0;
				int swapCounter = 0;
				//Bestimme den Indexpunkt, wo der Pick-Up-Point eingefügt werden soll.
				//Gehe dabei alle aktuellen Stopps der Tour durch.
				for(int j = 0; j < tourSize; j++){
					Stopp stopp = tour.get(j);
					double serviceTimeOfCurrentStopp = stopp.getServiceTime();
					int stoppType = stopp.getType();
					int requestType = stopp.getRequestType();
					//Wenn die Servicezeit < der Servicezeit des aktuell betrachteten Stopps ist und dieser auch ein Pick-Up-Point ist, dann verringere den Index um eine Stelle.
					if(requestType != 1){
						indexOfPickUpPoint = indexOfPickUpPoint + 1;
					} else if(serviceTime > serviceTimeOfCurrentStopp){
						indexOfPickUpPoint = indexOfPickUpPoint + 1;
						
					} else if(serviceTime == serviceTimeOfCurrentStopp && stoppType == 2) {
							indexOfPickUpPoint = indexOfPickUpPoint + 1;
							swapCounter = swapCounter + 1;
					}
				}
				//Überprüfe, ob der zuvor festgelegte Punkt kleiner als null ist, wenn ja, dann füge den Pick-Up-Point und den DropOff-Point an den Anfang der Tour ein.
				if(indexOfPickUpPoint >= tour.size()){
					//0.0 sind Platzhalter, die Sachen werden noch berechnet.
					Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,0.0,2,0.0,0.0,passengers,0,serviceTime,1);
					//Fehler bei Id und serviceTime
					pickUpStopp.setRequestId(requestId);
					pickUpStopp.setRequestType(serviceType);
					tour.add(pickUpStopp);
					// Die Werte 0.0 sind Platzhalter und werden in der Methode "calculateTimesOfTour" angepasst.
					Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,0.0,3,0.0,0.0,passengers,0,serviceTime,1);
					dropOffStopp.setRequestId(requestId);
					dropOffStopp.setRequestType(serviceType);
					dropOffStopp.setServiceTime(serviceTime);
					tour.add(dropOffStopp);
					calculateTimesOfTour(tour,maxWaitingTime,maxDrivingTime);
					double waitingTime = calculateWaitingTimeOfTour(tour,maxWaitingTime);
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);	
				} else {
					indexOfDropOffPoint = indexOfPickUpPoint + 1;
					if(indexOfPickUpPoint ==0){
						Point currentPosition = vehicle.getPosition();
						double distance = Simulation.calculateDistanceBetween2Points(currentPosition, pickUpPoint);
						double driveTimeForDistance = Simulation.calculateDriveTimeToPoint(distance);
						double arrivalTimeAtPickUpPoint = currentTime + driveTimeForDistance;
						waitingTime = 0.0;
						int stoppType = 2;
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,serviceTime,stoppType,serviceTime,lastArrivalTime,passengers,0,serviceTime,serviceType);
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(serviceType);
						tour.add(indexOfPickUpPoint,pickUpStopp);
						
					} else {
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,0.0,2,0.0,0.0,passengers,0,serviceTime,1);
						//Fehler bei Id und serviceTime
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(serviceType);
						tour.add(indexOfPickUpPoint, pickUpStopp);
					}
					Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,0.0,3,0.0,0.0,passengers,0,serviceTime,1);
					dropOffStopp.setRequestId(requestId);
					dropOffStopp.setRequestType(serviceType);
					dropOffStopp.setServiceTime(serviceTime);
					tour.add(indexOfDropOffPoint,dropOffStopp);
					calculateTimesOfTour(tour,maxWaitingTime,maxDrivingTime);
					double waitingTime = calculateWaitingTimeOfTour(tour,maxWaitingTime);
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
					
					if(swapCounter > 0){
						int startSwapIndex = indexOfDropOffPoint;
						int swapIndex = (indexOfDropOffPoint + indexOfPickUpPoint) - 1;
						swapPointsInTour(startSwapIndex,swapIndex,tour, vehicle, vehicleForAssigning,maxWaitingTime,maxDrivingTime);
					}
				}	
			}
			/**
			vehicle.currentTour = new ArrayList<>(tour);
			System.out.println("Tourengröße nach Assigning:" + vehicle.currentTour.size());
			for(int h=0; h<tour.size();h++){
				Stopp stopp = tour.get(h);
				System.out.println("FahrzeugId:" + "" + vehicle.getId() + " " + "RequestId:" + stopp.getRequestId() + " " + stopp.getStopp() + " " + "ArrivalTime:" + stopp.getArrivalTime() + " " + "Abfahrtszeit:" + stopp.getPlannedDeaparture() + " " + "LatestArivalTime:" + stopp.getLatestArrivalTime() + " " + "Servicezeit:" + stopp.getServiceTime());

			}
			System.out.println("Tourengröße nach Assigning:" + vehicle.currentTour.size());
			tour.clear();
			System.out.println("Tourengröße nach clear:" + tour.size());
			**/
		}
		chooseTour(vehicleForAssigning, endTime, maxMovingPosition,  maxCapacity, currentRequest, vehicles, waitingTimesOfCustomers,maxWaitingTime);
		
		System.out.println("Assigningliste nach Zuweisungen:" + vehicleForAssigning.size());
		
	}
	
	public static void assignTypeTwoRequest(Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime,int maxCapacity,double endTime, int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		
		int requestId = currentRequest.getId();
		System.out.println("CurrentRequestId:" + requestId);
		int passengers = currentRequest.getPassengerNr();
		int serviceType = currentRequest.getType();
		double currentTime = currentRequest.getRequestTime();
		double serviceTime = currentRequest.getServiceTime();
		Point pickUpPoint = currentRequest.getPickUpPoint();
		Point dropOffPoint = currentRequest.getDropOffPoint();
		double lastArrivalTime = serviceTime;
		double earliestArrivalTimeAtDropOff = serviceTime - maxWaitingTime;
		double earliestDropOffTime = lastArrivalTime - maxDrivingTime;
		ArrayList<AssignedVehicle> vehicleForAssigning = new ArrayList<AssignedVehicle>();
		
		System.out.println("Assigningliste zu Beginn des Assignings:" + vehicleForAssigning.size());
		
		for(int i = 0; i < vehicles.size(); i++){
			Vehicle vehicle = vehicles.get(i);
			ArrayList<Stopp> tourOfCurrentVehicle = vehicle.currentTour;
			ArrayList<Stopp> tour = new ArrayList<Stopp>(tourOfCurrentVehicle);
			System.out.println("FahrzeugId:" + "" + vehicle.getId() + "Tourengröße des Fahrzeuges" + tourOfCurrentVehicle.size());
			int tourSize = tour.size();
			System.out.println("Kopierte Tourenliste des Fahrzeuges vor der Zuweisung:" + tour.size());
			//Falls die Tour leer ist füge die Serviceanfrage vorne ein, Fange mit dem Drop-Off Punkt an und setze den Pick-up Punkt davor.
			if(tourSize == 0) {
				
				int indexOfPickUpPoint = 0;
				Point posVehicle = vehicle.getPosition();
				double distance = Simulation.calculateDistanceBetween2Points(posVehicle,pickUpPoint);
				double driveTime = Simulation.calculateDriveTimeToPoint(distance);
				double arrivalTime = driveTime + currentTime;
				double distanceToDropOffPoint = Simulation.calculateDistanceBetween2Points(pickUpPoint,dropOffPoint);
				double driveTimeToDropOffPoint = Simulation.calculateDriveTimeToPoint(distanceToDropOffPoint);
				double arrivalTimeToDropOff = arrivalTime + driveTimeToDropOffPoint;
				double departureTime = 0.0;
				double latestDeparture = 0.0;
				//Wenn die geplante Ankunft am DropOff Punkt kleiner als der frühestmögliche Zeitpunkt ist, dann erhöhe die Abfahrt von dem Pick-Up Punkt um die Zeitdifferenz. 
				if(arrivalTimeToDropOff < earliestArrivalTimeAtDropOff){
					double timeDifference = earliestArrivalTimeAtDropOff - arrivalTimeToDropOff;
					departureTime = arrivalTime + timeDifference;
					latestDeparture = departureTime + maxWaitingTime;	
				} else{
					departureTime = arrivalTimeToDropOff;
					if(departureTime < lastArrivalTime){
						double timeDifference = lastArrivalTime - arrivalTimeToDropOff;
						latestDeparture = departureTime + timeDifference;
					}
				}
				Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTime,2,departureTime, latestDeparture,passengers,0,serviceTime,2);
				pickUpStopp.setRequestId(requestId);
				pickUpStopp.setRequestType(2);
				tour.add(indexOfPickUpPoint,pickUpStopp);
				
				earliestDropOffTime = departureTime + driveTimeToDropOffPoint;
				arrivalTimeToDropOff = earliestDropOffTime;
				Stopp dropOffStop = new Stopp(requestId, dropOffPoint,arrivalTimeToDropOff,3,earliestDropOffTime,serviceTime,passengers, 0,serviceTime,serviceType);
				dropOffStop.setRequestId(requestId);
				dropOffStop.setRequestType(2);
				tour.add(dropOffStop);
				AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, 0.0);
				vehicleForAssigning.add(potentialVehicle);
				
				//Erstellung des PickUpStopp;
				
			} else{
				//Gehe die aktuelle Tour durch und bestimme indizes fürs einfügen.
				int indexOfPickUp = 0;
				int indexOfDropOff = 0;
				//wird gebraucht um die Tour nachher zu swapen.
				int swapCounter = 0;
				for(int j = 0; j<tour.size();j++){
					Stopp stopp = tour.get(j);
					int requestType = stopp.getRequestType();
					double serviceTimeOfStopp = stopp.getServiceTime();
					int stoppType = stopp.getType();
					if(requestType != 2){
						indexOfPickUp = indexOfPickUp + 1;
					}
					else if(serviceTime > serviceTimeOfStopp && requestType ==2){
						indexOfPickUp = indexOfPickUp + 1;
					}
					else if(serviceTime == serviceTimeOfStopp && stoppType == 2){
						indexOfPickUp = indexOfPickUp +1;
						swapCounter = swapCounter + 1;
					}
				}
				//Einfügen der Punkte in die Tour:
				//Wenn der Index größer gleich der Tourengröße --> füge beide Punkte ans Ende an
				//Sonst füge die Punkte entsprechend an dem index ein.
				if(indexOfPickUp >= tour.size()){
					Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,0.0,2,0.0, 0.0,passengers,0,serviceTime,2);
					pickUpStopp.setRequestId(requestId);
					pickUpStopp.setRequestType(2);
					tour.add(pickUpStopp);
					Stopp dropOffStop = new Stopp(requestId, dropOffPoint,0.0,3,earliestDropOffTime,serviceTime,passengers, 0,serviceTime,serviceType);
					dropOffStop.setRequestId(requestId);
					dropOffStop.setRequestType(2);
					tour.add(dropOffStop);
					calculateTimesOfTour(tour,maxWaitingTime,maxDrivingTime);
					double waitingTime = calculateWaitingTimeOfTour(tour,maxWaitingTime);
					//Hinzufügen zur Auswahlliste
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
				} else {
					if(indexOfPickUp == 0){
						Point posVehicle = vehicle.getPosition();
						double distance = Simulation.calculateDistanceBetween2Points(posVehicle,pickUpPoint);
						double driveTime = Simulation.calculateDriveTimeToPoint(distance);
						double arrivalTime = driveTime + currentTime;
						double distanceToDropOffPoint = Simulation.calculateDistanceBetween2Points(pickUpPoint,dropOffPoint);
						double driveTimeToDropOffPoint = Simulation.calculateDriveTimeToPoint(distanceToDropOffPoint);
						double arrivalTimeToDropOff = arrivalTime + driveTimeToDropOffPoint;
						double departureTime = 0.0;
						double latestDeparture = 0.0;
						//Wenn die geplante Ankunft am DropOff Punkt kleiner als der frühestmögliche Zeitpunkt ist, dann erhöhe die Abfahrt von dem Pick-Up Punkt um die Zeitdifferenz. 
						if(arrivalTimeToDropOff < earliestArrivalTimeAtDropOff){
							double timeDifference = earliestArrivalTimeAtDropOff - arrivalTimeToDropOff;
							departureTime = arrivalTime + timeDifference;
							latestDeparture = departureTime + maxWaitingTime;	
						} else{
							departureTime = arrivalTimeToDropOff;
							if(departureTime < lastArrivalTime){
								double timeDifference = lastArrivalTime - arrivalTimeToDropOff;
								latestDeparture = departureTime + timeDifference;
							}
						}
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTime,2,departureTime, latestDeparture,passengers,0,serviceTime,2);
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(2);
						tour.add(indexOfPickUp,pickUpStopp);
						indexOfDropOff = indexOfPickUp + 1;
						earliestDropOffTime = departureTime + driveTimeToDropOffPoint;
						arrivalTimeToDropOff = earliestDropOffTime;
						Stopp dropOffStop = new Stopp(requestId, dropOffPoint,arrivalTimeToDropOff,3,earliestDropOffTime,serviceTime,passengers, 0,serviceTime,serviceType);
						dropOffStop.setRequestId(requestId);
						dropOffStop.setRequestType(2);
						tour.add(indexOfDropOff,dropOffStop);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, 0.0);
						vehicleForAssigning.add(potentialVehicle);
					} else {
						indexOfDropOff = indexOfPickUp;
						Stopp dropOffStop = new Stopp(requestId, dropOffPoint,0.0,3,earliestDropOffTime,serviceTime,passengers, 0,serviceTime,serviceType);
						dropOffStop.setRequestId(requestId);
						dropOffStop.setRequestType(2);
						tour.add(indexOfDropOff,dropOffStop);
						
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,0.0,2,0.0, 0.0,passengers,0,serviceTime,2);
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(2);
						tour.add(indexOfPickUp,pickUpStopp);
						calculateTimesOfTour(tour,maxWaitingTime,maxDrivingTime);
						double waitingTime = calculateWaitingTimeOfTour(tour,maxWaitingTime);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
						
						if(swapCounter >0){
						int swapStartIndex = indexOfPickUp - swapCounter;
						int swapEndIndex = indexOfPickUp - 1;
						swapPointsInTour(swapStartIndex, swapEndIndex,tour,vehicle,vehicleForAssigning,maxWaitingTime, maxDrivingTime);
					}
					
					}
				}
			}
			/**
			vehicle.currentTour = new ArrayList<>(tour);
			System.out.println("Tourengröße nach Assigning:" + vehicle.currentTour.size());
			for(int h=0; h<tour.size();h++){
				Stopp stopp = tour.get(h);
				System.out.println("FahrzeugId:" + "" + vehicle.getId() + " " + "RequestId:" + stopp.getRequestId() + " " + stopp.getStopp() + " " + "ArrivalTime:" + stopp.getArrivalTime() + " " + "Abfahrtszeit:" + stopp.getPlannedDeaparture() + " " + "LatestArivalTime:" + stopp.getLatestArrivalTime() + " " + "Servicezeit:" + stopp.getServiceTime());
			}
			System.out.println("Assigninglise nach Assigning:" + vehicle.currentTour.size());
			tour.clear();
			System.out.println("Tourengröße nach clear:" + tour.size());
			**/
		}
		//Zuweisung der Anfrage zur Tour.
		
		System.out.println("Assigningliste nach Zuweisungen:" + vehicleForAssigning.size());
		//Hier kommt dann das Assignment hin.
		chooseTour(vehicleForAssigning, endTime, maxMovingPosition,  maxCapacity, currentRequest,vehicles,waitingTimesOfCustomers,maxWaitingTime);
	}
	//Ende for-Schleife
		
		
		
	
	
	public static boolean checkWaitingTime(double time1, double time2){
		if(time1 < time2){
			return true;
		} else {
			return false;
		} 
	}
	/**
	 * Diese Methode überprüft eine Tour des Fahrzuegs, ob die Kapazitätsrestriktion eingehlaten wird.
	 * Wenn es sich um einen Pick-Up Punkt handelt, wird die aktuelle Kapazität um die anzahl an Passagieren erhöht.
	 * Wenn es sich um einen Drop-Off Punkt handelt, wird die aktuelle Kapazität um die Anzahl verringert.
	 * @param vehicle das zu überprüfende Fahrzeug
	 * @param tour die zum Fahrzueg zugehörige Tour, welche überprüft werden soll
	 * @param maxCapacity, die maximale Kapazität der Fahrzeuge
	 * @return einen Boolean, der entweder true ist, falls die maximale Kapazität des Fahrzeuges nicht überschritten wird oder false, falls die Kapazität überschritten wird
	 */
	public static boolean checkCapacity(Vehicle vehicle, ArrayList<Stopp> tour, int maxCapacity){
		boolean checkCapacity = true;
		int currentCapacity = vehicle.getCapacity();
		checkingCapacity:
		for(int i = 0; i< tour.size();i++){
			Stopp stopp = tour.get(i);
			int passengers = stopp.getPassengers();
			int stoppType = stopp.getType();
			if(stoppType == 2){
				currentCapacity = currentCapacity + passengers;
				if(currentCapacity > maxCapacity){
					checkCapacity = false;
					break checkingCapacity;
				}
			} else if(stoppType == 3){
				currentCapacity = currentCapacity - passengers;
				if(currentCapacity < 0){
					checkCapacity = false;
					break checkingCapacity;
				}
			}
		}
		return checkCapacity;
	}
	/**
	 * Diese Methode überprüft, ob die Rückkehr zum Umsteigepunkt am Ende der Tour zeitlich möglich ist.
	 * Falls ja, nimmt der  boolean den "true" an, sonst den wert "false".
	 * @param tour, die zu überprüfende Tour
	 * @param endTime, Ende des Betriebshorzont.
	 * @return checkingReturn
	 */
	public static boolean checkReturnToTansferPoint(ArrayList<Stopp>tour, double endTime){
		boolean checkingReturn;
		Point transferPoint = new Point(0,2);
		Stopp lastStopp = tour.get(tour.size() - 1);
		Point PointOfLastStopp = lastStopp.getStopp();
		double departureFromLastStopp = lastStopp.getPlannedDeaparture();
		double distanceToTransferPoint = Simulation.calculateDistanceBetween2Points(PointOfLastStopp, transferPoint);
		double driveTimeToTransferPoint = Simulation.calculateDriveTimeToPoint(distanceToTransferPoint);
		double arrivalTimeAtTransferPoint = driveTimeToTransferPoint + departureFromLastStopp;
		if(arrivalTimeAtTransferPoint > endTime){
			checkingReturn = false;
		} else {
			checkingReturn = true;
		}
		return checkingReturn;
	}
	/**
	 * Diese Methode überprüft, ob die spätest möglichen Zeiten eingehalten werden
	 * Damit ist die die Einhaltung der maximalen Wartzeit und der maximalen Fahrzeit gemeint
	 * @param tour
	 * @param maxWaitingTime
	 * @return
	 */
	public static boolean checkTimesOfTour(ArrayList<Stopp> tour){
		boolean checkingTimes = true;
		checkingTime:
		for(int i = 0; i< tour.size(); i++){
			Stopp stopp = tour.get(i);
			double arrival = stopp.getArrivalTime();
			double lastArrival = stopp.getLatestArrivalTime();
			if(arrival > lastArrival){
				checkingTimes = false;
				break checkingTime;
			}
		}
		return checkingTimes;
	}
	/**
	 * Diese Methode überprüft, ob eine Passagier zu oft nach hinten verschoben wurde.
	 * @param tour die überprüfende Tour
	 * @param maxSwap, der Wert, welche die maximale Anzahl an Verschiebungen innerhalb der Tour vorgibt
	 * @return
	 */
	public static boolean checkSwap(ArrayList<Stopp> tour, int maxSwap){
		boolean checkingSwap = true;
		checking:
		for(int i = 0; i<tour.size();i++){
			Stopp stopp = tour.get(i);
			int swapInTour = stopp.getMaxMovedPosition();
			if(swapInTour > maxSwap){
				checkingSwap = false;
				break checking;
			}
		}
		return checkingSwap;
	}
	
	public static void chooseTour(ArrayList<AssignedVehicle> assignedVehicles,double endTime, int maxMovingPosition, int maxCapacity, Request request, ArrayList<Vehicle> vehicles,ArrayList<Times> waitingTimesOfCustomers,double maxWaitingTime){
		//ArrayList<AssignedVehicle> copyOfList = new ArrayList<>(assignedVehicles);
		
		for(int i = 0; i < assignedVehicles.size();i++){
			int index = i;
			AssignedVehicle currentElement = assignedVehicles.get(i);
			Vehicle currentVehicle = currentElement.getVehicle();
			int currentVehicleCapacity = currentVehicle.getCapacity();
			ArrayList<Stopp> currentTour = currentElement.getTour();
			//Überprüfe die Einhaltung der Restriktionen:
			//Fehler bei der Kapazitätsrestriktion
			if(!checkCapacity(currentVehicle, currentTour,maxCapacity)){
				assignedVehicles.remove(index);
				i = index - 1;
			}
			if(!checkReturnToTansferPoint(currentTour,endTime)){
				assignedVehicles.remove(index);
				i = index - 1;
			}
			if(!checkTimesOfTour(currentTour)){
				assignedVehicles.remove(index);
				i = index - 1;
			}
			if(!checkSwap(currentTour,maxMovingPosition)){
				assignedVehicles.remove(index);
				i = index - 1;
			}
		}
		
		int size = assignedVehicles.size();
		if(size == 0){
		System.out.println("Die Anfrage konnte aufgrund der Restriktionen nicht bedient und somit zu keinem der Fahrzeuge zugewiesen werden!");
		} else {
			Collections.sort(assignedVehicles);
			AssignedVehicle choosenVehicleElement = assignedVehicles.get(0);
			Vehicle choosenVehicle = choosenVehicleElement.getVehicle();
			int choosenVehicleId = choosenVehicle.getId();
			ArrayList<Stopp> choosenTour = choosenVehicleElement.getTour();
			System.out.println("Das zugewählte Fahrzeug ist:" + " " + choosenVehicle.getId());
			for(int x =0; x<vehicles.size();x++){
				Vehicle vehicle = vehicles.get(x);
				int vehicleId = vehicle.getId();
				if(choosenVehicleId == vehicleId){
					vehicle.currentTour = new ArrayList<>(choosenTour);
					actualizeWaitingTime(vehicle.currentTour,waitingTimesOfCustomers,request,maxWaitingTime);
					for(int h=0; h<vehicle.currentTour.size();h++){
						Stopp stopp = vehicle.currentTour.get(h);
						System.out.println("Seine Tour lautet:" + "" + "RequestId:" + stopp.getRequestId() + " " + stopp.getStopp() + " " + "ArrivalTime:" + stopp.getArrivalTime() + " " + "Abfahrtszeit:" + stopp.getPlannedDeaparture() + " " + "LatestArivalTime:" + stopp.getLatestArrivalTime() + " " + "Servicezeit:" + stopp.getServiceTime());
					}
				}
				
			}
			int requestState = 1;
			request.setPassengerState(requestState);
			
			//Fehlt noch, wenn es mehrere Objekte mit minimaler Wartezeit gibt, dann soll das Fahrzeug ausgewählt werden, mit der geringsten Fahrzeit.
		}
		
		//assignedVehicles.clear();
		
		
		
	}
	
	
	
	public static Stopp createDropOffStopp(Point dropOff, double maxDriveTime,Request request){
		int id = request.getId();
		int passengers = request.getPassengerNr() ;
		Point pickUpPoint = request.getPickUpPoint();
		int type = request.getType();
		double serviceTime = request.getRequestTime();
		double departureTimeFromPickUpPoint = request.getServiceTime();
		double distance = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOff);
		double driveTime = Simulation.calculateDriveTimeToPoint(distance);
		double arrivalTime = departureTimeFromPickUpPoint + driveTime;
		double departure = arrivalTime;
		double latestDeparture = departureTimeFromPickUpPoint + maxDriveTime;
		Stopp dropOffPoint = new Stopp(id,dropOff,arrivalTime,3,departure,latestDeparture, passengers, 0,serviceTime,type);
		return dropOffPoint;
	}
	
	public static Stopp createPickUpPointForTypeOne(Request request, Vehicle vehicle, double maxWaitingTime){
		Point vehiclePosition = vehicle.getPosition();
		Point pickUpPoint = request.getPickUpPoint();
		int requestId = request.getId();
		int passengers = request.getPassengerNr();
		double serviceTime = request.getServiceTime();
		double currentTime = request.getRequestTime();
		double distance = Simulation.calculateDistanceBetween2Points(vehiclePosition, pickUpPoint);
		double driveTimeForDistance = Simulation.calculateDriveTimeToPoint(distance);
		double arrivalTime = currentTime + driveTimeForDistance;
		double departure = arrivalTime;
		double latestArrival = serviceTime + maxWaitingTime;
		Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTime,2,departure,latestArrival,passengers,0,currentTime,1);
		return pickUpStopp;
	}
	
	public static void swapPointsInTour(int start, int end,ArrayList<Stopp> tour,Vehicle vehicle,ArrayList<AssignedVehicle> aV, double maxWaitingTime,double maxDrivingTime){
		if(start == 0){
			start = start + 1;
		}
		for(int i = start; i <= end; i++ ){
			ArrayList<Stopp> copyTour = new ArrayList<Stopp>(tour);
			int z = i + 1;
			if(z<copyTour.size()){
				System.out.println(start + ";" + z);
				Collections.swap(copyTour, i, z);
				//System.out.println("getauschte Elemente:" + i + " " + z);
				calculateTimesOfTour(tour,maxWaitingTime,maxDrivingTime);
				//calculateWaitingTimeOfTour(tour);
				//for(int j = 0; j<copyTour.size(); j++){
					//System.out.println("Liste nach Vertauschen:");
					//Stopp stopp = copyTour.get(j);
					//System.out.println("RequestId:" + stopp.getRequestId() + " " + stopp.getStopp());
				//}
				AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle,tour, waitingTime);
				aV.add(potentialVehicle);
			}
		}
	}
	
	public static void calculateTimesOfTour(ArrayList<Stopp >tour,double maxWaitingTime, double maxDrivingTime){
		for(int i = 1; i < tour.size();i++){
			int previousInt = i - 1;
			Stopp previousStopp = tour.get(previousInt);
			double previousDeparture = previousStopp.getPlannedDeaparture();
			Point previousPoint = previousStopp.getStopp();
			Stopp currentStopp = tour.get(i);
			Point currentPoint = currentStopp.getStopp();
			int stoppType = currentStopp.getType();
			int requestType = currentStopp.getRequestType();
			int requestId = currentStopp.getRequestId();
			double serviceTimeOfCurrentStopp = currentStopp.getServiceTime();
			double distanceFromPreviousToCurrentStopp = Simulation.calculateDistanceBetween2Points(previousPoint,currentPoint);
			double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPreviousToCurrentStopp);
			double arrivalTimeAtCurrentStopp = previousDeparture + driveTime;
			currentStopp.setArrivalTime(arrivalTimeAtCurrentStopp);
			//Wenn es sich um den ersten Typ und um einen Pick-Up Point handelt, dann berechne die Ankunftszeit, Abfahrt und die späteste Abfahrt.
			if(requestType == 1 && stoppType == 2){
				double serviceTime = currentStopp.getServiceTime();
				if(serviceTime > arrivalTimeAtCurrentStopp){
					double departureTime = serviceTime;
					double latestDepartureTime = departureTime + maxWaitingTime;
					currentStopp.setPlannedDeparture(departureTime);
					currentStopp.setLatestArrivalTime(latestDepartureTime);
				} else {
					double departureTime = arrivalTimeAtCurrentStopp;
					double latestDepartureTime = serviceTime + maxWaitingTime;
					currentStopp.setPlannedDeparture(departureTime);
					currentStopp.setLatestArrivalTime(latestDepartureTime);
				}
			}
			//Wenn es sich um einen DropOff Punkt des ersten Typs handelt(Punkt im Bezirk, dann such nach dem Pick-Up Punkt des Kunden und setze diesen um.
			if(requestType == 1 && stoppType ==3){
				double departureTime = arrivalTimeAtCurrentStopp;
				currentStopp.setPlannedDeparture(departureTime);
				for(int j = 0; j < tour.size(); j++){
					Stopp pickUpStopp = tour.get(j);
					int pickUpId = pickUpStopp.getRequestId();
					if(requestId == pickUpId){
						double departure = pickUpStopp.getPlannedDeaparture();
						double latestdeparture = departure + maxDrivingTime;
						currentStopp.setLatestArrivalTime(latestdeparture);
					}
				}
			}
			//Wenn es sich um den zweiten Typ handelt und um einen Pick-Up-Point dann, betrachte die Distanz zum Umsteigepunkt und schaue wie sich die Ankunftszeit mit der fühsmöglichen Ankunftszeit verhält.
			if(requestType == 2 && stoppType == 2){
				//Position des Umsteigepunktes
				Point targetPoint = new Point(0,2);
				double distanceToDropOffPoint = Simulation.calculateDistanceBetween2Points(currentPoint,targetPoint);
				double driveTimeToDropOffPoint = Simulation.calculateDriveTimeToPoint(distanceToDropOffPoint);
				double arrivalTimeToDropOff = arrivalTimeAtCurrentStopp + driveTimeToDropOffPoint;
				double earliestArrivalTimeAtDropOff = serviceTimeOfCurrentStopp - maxWaitingTime;
				//Wenn die geplante Ankunft am DropOff Punkt kleiner ist als der frühestmögliche Zeitpunkt ist, dann erhöhe die geplante Ankunft/Abfahrt von dem Pick-Up Punkt um die Zeitdifferenz. 
				if(arrivalTimeToDropOff < earliestArrivalTimeAtDropOff){
					double timeDifference = earliestArrivalTimeAtDropOff - arrivalTimeToDropOff;
					double departureTime = arrivalTimeAtCurrentStopp + timeDifference;
					double latestDeparture = departureTime + maxWaitingTime;
					currentStopp.setPlannedDeparture(departureTime);
					currentStopp.setLatestArrivalTime(latestDeparture);
				} else{
					double departureTime = arrivalTimeAtCurrentStopp;
					double timeDifference = serviceTimeOfCurrentStopp - arrivalTimeToDropOff;
					double latestDeparture = departureTime + timeDifference;
					currentStopp.setPlannedDeparture(departureTime);
					currentStopp.setLatestArrivalTime(latestDeparture);
					}
				}
		
			//Wenn es sich um den zweiten Typen und dem dropOff Punkt handel, dann suche den zugehörigen Pick-Up-Point und setzte als letztmöglich Ankunft/Abfahrt die Abfahrtszeit des Pick-Up Punkts + die maximale Fahrzeit.
			if(requestType == 2 && stoppType ==3){
				double departureTime = arrivalTimeAtCurrentStopp;
				currentStopp.setPlannedDeparture(departureTime);
				for(int j = 0; j < tour.size(); j++){
					Stopp pickUpStopp = tour.get(j);
					int pickUpId = pickUpStopp.getRequestId();
					if(requestId == pickUpId){
						double departure = pickUpStopp.getPlannedDeaparture();
						double latestDeparture = departure + maxDrivingTime;
						currentStopp.setLatestArrivalTime(latestDeparture);
					}
				}
			}
		}
		
	}
	/**
	 * 
	 * @param tour
	 * @param maxWaitingTime
	 * @return
	 */
	public static double calculateWaitingTimeOfTour(ArrayList<Stopp>tour, double maxWaitingTime){
		double waitingTime = 0.0;
		//Gehe aktuelle Tour des Fahrzeuges durch
		for(int i = 0; i<tour.size();i++){
			Stopp stopp = tour.get(i);
			int stoppType = stopp.getType();
			int requestType = stopp.getRequestType();
			//Wenn es sich um PU-Punkt und requestType = 1 handelt, dann schaue dir die Differenz der ServiceZeit(Zeitpunkt des Abholens) und der tatsächlichen Abholung an
			if(stoppType == 2 && requestType == 1){
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				double waiting = arrival - serviceTime;
				waitingTime = waitingTime + waiting;
			}
			//Wenn es sich um einen DO Punkt handelt, schaue dir die Differenz zwischen dem frühestmöglichen Zeitpunkt und dem Zeitpunkt der tatsächlichen Ankunft an.
			if(stoppType == 3 && requestType ==2){
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				double waitDiff = serviceTime - maxWaitingTime;
				double waiting = arrival - waitDiff;
				waitingTime = waitingTime + waiting;
			}
		}
		return waitingTime;
	}
	/**
	 * Diese Methode ermittelt für alle Stopps einer Tour die Wartezeit des Kunden und aktualisiert diese in der Liste.
	 *
	 * @param tour
	 * @param waitingTimesOfCustomers
	 * @param request
	 */
	public static void actualizeWaitingTime(ArrayList<Stopp> tour, ArrayList<Times> waitingTimesOfCustomers,Request request,double maxWaitingTime){
		int requestId = request.getId();
		double waitingTime = 0.0;
		Times timeOfRequest = new Times(requestId, waitingTime);
		waitingTimesOfCustomers.add(timeOfRequest);
		
		for(int i =0; i< tour.size();i++){
			Stopp stopp = tour.get(i);
			int stoppType = stopp.getType();
			int requestType = stopp.getRequestType();
			requestId = stopp.getRequestId();
			if(stoppType == 2 && requestType == 1){
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				waitingTime = arrival - serviceTime;
				for(int j = 0; j<waitingTimesOfCustomers.size(); j++){
					Times time = waitingTimesOfCustomers.get(j);
					int requestIdOfWaitingTimes = time.getRequestId();
					if(requestIdOfWaitingTimes == requestId){
						time.setWaitingTime(waitingTime);
					}
					
				}
			}
			if(stoppType == 3 && requestType ==2){
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				double earliestArrival = serviceTime - maxWaitingTime;
				waitingTime = arrival - earliestArrival;
				for(int j = 0; j<waitingTimesOfCustomers.size(); j++){
					Times time = waitingTimesOfCustomers.get(j);
					int requestIdOfWaitingTimes = time.getRequestId();
					if(requestIdOfWaitingTimes == requestId){
						time.setWaitingTime(waitingTime);
					}
			}
			
		}
	}
	}
}

