import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;

public class Assignment {
	
	public static double waitingTime;
	public static double waitingTimes[];
	
	public static void requestAssigment(int waitingStrategy, Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime, int maxCapacity, int maxMovingPosition, double endTime,ArrayList<Times> waitingTimesOfRequest){
		int serviceType = currentRequest.getType();

		switch (serviceType) {
			case 1:
					assignTypeOneRequest(waitingStrategy,currentRequest, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, endTime, maxMovingPosition, waitingTimesOfRequest);
					break;
			case 2:
					assignTypeTwoRequest(waitingStrategy,currentRequest, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, endTime, maxMovingPosition, waitingTimesOfRequest);
					break;
		}
	}
	/**
	 * 
	 * @param currentRequest, die aktuell zuzuweisende Anfrage
	 * @param vehicles, die Liste aller Fahrzeug
	 * @param maxWaitingTime, die maximal erlaubte Wartezeit des Kunden
	 * @param maxDrivingTime, die maximal erlaubte Fahrzeit eines Passagiers
	 * @param maxCapacity, die maximal moegliche Kapazitaet
	 */
	public static void assignTypeOneRequest(int waitingStrategy,Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime,int maxCapacity, double endTime, int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		int requestId = currentRequest.getId();
		System.out.println("CurrentRequestId:" + requestId + " " + "RequestTime:" + currentRequest.getRequestTime());
		int passengers = currentRequest.getPassengerNr();
		int serviceType = currentRequest.getType();
		double currentTime = currentRequest.getRequestTime();
		double serviceTime = currentRequest.getServiceTime();
		Point pickUpPoint = currentRequest.getPickUpPoint();
		Point dropOffPoint = currentRequest.getDropOffPoint();
		double lastArrivalTime = serviceTime + maxWaitingTime;
		ArrayList<AssignedVehicle> vehicleForAssigning = new ArrayList<AssignedVehicle>();
			
		
		//Die Liste Vehicles kopieren/clonen.
		//Gehe Liste der Fahrzeuge durch, nutze dafuer eine Kopie der originalen Liste.
		for (Vehicle vehicle : vehicles) {
			ArrayList<Stopp> tourOfCurrentVehicle = vehicle.currentTour;
			System.out.println("FahrzeugId:" + "" + vehicle.getId() + "Tourengroesse des Fahrzeuges" + tourOfCurrentVehicle.size());
			ArrayList<Stopp> tour = new ArrayList<Stopp>(tourOfCurrentVehicle);

			int tourSize = tour.size();
			System.out.println("Kopierte Tourenliste des Fahrzeuges vor der Zuweisung:" + tourSize);
			//Falls die Tour leer ist fuege die Serviceanfrage vorne ein, berechne vorher die Zeiten
			//wenn die Strategie
			if (tourSize == 0) {
				Point currentPosition = vehicle.getPosition();
				double distance = Simulation.calculateDistanceBetween2Points(currentPosition, pickUpPoint);
				double driveTimeForDistance = Simulation.calculateDriveTimeToPoint(distance);
				double arrivalTimeAtPickUpPoint = currentTime + driveTimeForDistance;
				//Wenn die Wartestrategie = waitFirst lautet dann:
				//Berechne die spätmöglichste Ankunft am Punkt.
				if(waitingStrategy == 3){
					if(arrivalTimeAtPickUpPoint < serviceTime){
						arrivalTimeAtPickUpPoint = serviceTime;
					}
					double servingTime = arrivalTimeAtPickUpPoint;
					double latestServingTime = lastArrivalTime;
					//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
					Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUpPoint,2,servingTime,latestServingTime, passengers, 0, serviceTime, 1);
					double distanceFromPickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
					double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPickUpToDropOff);
					double arrivalTimeAtDropOff = servingTime + driveTime;
					Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,arrivalTimeAtDropOff, passengers, 0, serviceTime, 1);
					tour.add(pickUpStopp);
					tour.add(dropOffStopp);
					double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
					//Wenn es sich um die Center-of-Gravity Strategie handelt, dann fahre erst zur frühesten Servicezeit zu dem Punkt.
				} else if(waitingStrategy == 1){
					if(arrivalTimeAtPickUpPoint < serviceTime){
						double servingTime = serviceTime;
						double distanceFromPickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPickUpToDropOff);
						double arrivalTimeAtDropOff = servingTime + driveTime;
						double latestArrivalTimeAtDropOff = serviceTime + maxDrivingTime;
						Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUpPoint,2,servingTime,lastArrivalTime, passengers, 0, serviceTime, 1);
						Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,latestArrivalTimeAtDropOff,passengers,0,serviceTime,1);
						tour.add(pickUpStopp);
						tour.add(dropOffStopp);
						double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					} else {
					double servingTime = arrivalTimeAtPickUpPoint;
					double distanceFromPickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
					double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPickUpToDropOff);
					double arrivalTimeAtDropOff = servingTime + driveTime;
					double latestArrivalTimeAtDropOff = serviceTime + maxDrivingTime;
					Stopp pickUpStopp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUpPoint,2,servingTime,lastArrivalTime, passengers, 0, serviceTime, 1);
					Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,latestArrivalTimeAtDropOff,passengers,0,serviceTime,1);
					tour.add(pickUpStopp);
					tour.add(dropOffStopp);
					double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
				}
				}else {
					if (arrivalTimeAtPickUpPoint <= serviceTime) {
						waitingTime = 0.0;
						int stoppType = 2;
						System.out.println(requestId);
						Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, arrivalTimeAtPickUpPoint, stoppType, serviceTime, lastArrivalTime, passengers, 0, serviceTime, serviceType);
						tour.add(pickUpStopp);
						double distanceToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTimeToDropOffTime = Simulation.calculateDriveTimeToPoint(distanceToDropOff);
						double arrivalTimeAtDropOff = serviceTime + driveTimeToDropOffTime;
						double latestArrivalTimeAtDropOff = serviceTime + maxDrivingTime;
						Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,latestArrivalTimeAtDropOff,passengers,0,serviceTime,1);
						tour.add(dropOffStopp);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					} else if (arrivalTimeAtPickUpPoint <= lastArrivalTime) {
						waitingTime = arrivalTimeAtPickUpPoint - serviceTime;
						System.out.println(requestId);
						Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, arrivalTimeAtPickUpPoint, 1, arrivalTimeAtPickUpPoint, lastArrivalTime, passengers, 0, serviceTime, 1);
						pickUpStopp.setRequestId(requestId);
						pickUpStopp.setRequestType(serviceType);
						tour.add(pickUpStopp);
						double distanceToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTimeToDropOffTime = Simulation.calculateDriveTimeToPoint(distanceToDropOff);
						double arrivalTimeAtDropOff = serviceTime + driveTimeToDropOffTime;
						double latestArrivalTimeAtDropOff = serviceTime + maxDrivingTime;
						Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,latestArrivalTimeAtDropOff,passengers,0,serviceTime,1);
						tour.add(dropOffStopp);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					}
				}
			} else {
				//Wenn die Tour bereits Punkte enthaelt, dann ueberpruefe drei moegliche Faelle
				//Fall 1: die serviceTime ist kleiner als die serviceTime der bisherigen Pick-up Points in der Tour und Fuege den Punkt vorne an und den drop-Off Point dahinter.
				//Fall 2: die ServiceTime ist groesser als die bisherigen	Pick-Up-Points --> Fuege Punkt hinten an der Liste ein.
				//Fall 3: Die serviceTime ist gleich der schon bisherigen Pick-Up-Points --> Fuege den Pick-Up Point an der letzten Stelle der Pick-Up Points ein, wo die gleiche Servicezeit ist.
				//Fuege Drop-Off Punkt an die letzte Stelle der zugehoerigen Pick-Up Points
				//Swappe Positionen der Drop-Off Punkte durch
				int indexOfPickUpPoint = 0;
				int indexOfDropOffPoint = 0;
				int swapCounter = 0;
				for (Stopp stopp : tour) {
					double serviceTimeOfCurrentStopp = stopp.getServiceTime();
					int stoppType = stopp.getType();
					int requestType = stopp.getRequestType();

					//Wenn es sich nicht um den serviceTypen 1 handelt oder die Servicezeit groeßer als die aktuelle ist, dann ueberspringe diese und zähle den Index hoch
					if (requestType != 1 || serviceTime > serviceTimeOfCurrentStopp) {
						// skip
					} else if (serviceTime == serviceTimeOfCurrentStopp && stoppType == 2 && requestType == 1) {
						swapCounter = swapCounter + 1;
					} else {
						break;
					}
					indexOfPickUpPoint = indexOfPickUpPoint + 1;
				}
				//Wenn der Punkt innerhalb der aktuellen Tour eingefuegt werden kann.
				//Dann fuege ihn an entsprechender stelle ein
				//Berechne die cheapest Insertion
				if (indexOfPickUpPoint < tour.size()) {
					indexOfDropOffPoint = indexOfPickUpPoint + 1;
					Stopp pickUpStopp;
					//Ueberlegen, ob das Sinn macht, da die Annahme bei drive-First besagt, dass wenn wir bereits bei einem Punkt ankommen, dann fahren wir von dort nicht wieder weg.
					//Das heißt, dass macht nur Sinn, bei der Wait-First-Methode, dass wir ueberpruefen, ob ein Punkt noch vorher eingefuegt werden kann.
					//Wenn ja, dann wird der Punkt am Anfang eingefügt und es wird ueberprüft, ob das so funktioniert.
					if (indexOfPickUpPoint == 0) {
						Point currentPosition = vehicle.getPosition();
						Stopp firstStopp = vehicle.currentTour.get(0);
						Point stoppPoint = firstStopp.getStopp();
						if(waitingStrategy == 3){
							double arrivalTime = serviceTime + maxWaitingTime;
							double servingTime = arrivalTime;
							double latestServingTime = servingTime;
							//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
							Stopp pickUpStopp1 = new Stopp(requestId,pickUpPoint,arrivalTime,2,servingTime,latestServingTime, passengers, 0, serviceTime, 1);
							double distanceFromPickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
							double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPickUpToDropOff);
							double arrivalTimeAtDropOff = servingTime + driveTime;
							Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,arrivalTimeAtDropOff, passengers, 0, serviceTime, 1);
							tour.add(pickUpStopp1);
							tour.add(dropOffStopp);
							double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
							AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
							vehicleForAssigning.add(potentialVehicle);
						} else{
							//Wenn sich das Fahrzeug noch nicht am Punkt des Kunden aufhaelt, dann fuege den Punkt an den Anfang der Tour, sonst nicht.
							if(currentPosition != stoppPoint){
								double distance = Simulation.calculateDistanceBetween2Points(currentPosition, pickUpPoint);
								double driveTimeForDistance = Simulation.calculateDriveTimeToPoint(distance);
								double arrivalTimeAtPickUpPoint = currentTime + driveTimeForDistance;
								//Warte Zeit ist entweder null, wenn die Ankunft vor der Servicezeit geschieht oder die Differenz, der Ankunftszeit und der Servicezeit.
								waitingTime = Math.max(arrivalTimeAtPickUpPoint - serviceTime, 0.0);
								pickUpStopp = new Stopp(requestId, pickUpPoint, serviceTime, 2, serviceTime, lastArrivalTime, passengers, 0, serviceTime, serviceType);
								tour.add(indexOfPickUpPoint, pickUpStopp);
								Stopp dropOffStopp = new Stopp(requestId, dropOffPoint, 0.0, 3, 0.0, 0.0, passengers, 0, serviceTime, serviceType);
								tour.add(indexOfDropOffPoint, dropOffStopp);
								
								calculateTimesOfTour(waitingStrategy,tour, maxWaitingTime, maxDrivingTime);
								double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
								AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
								vehicleForAssigning.add(potentialVehicle);
							}
						}
						
					} else {
						//Wenn es sich um die Wartestrategie Wait-First handelt, dann fuege den Punkt in die Tour ein und berechne die Fahrtzeiten.
						if(waitingStrategy == 3 || waitingStrategy == 4) {
							Stopp previousStopp = tour.get(indexOfPickUpPoint - 1);
							Point previousPoint = previousStopp.getStopp();
							double departureFromPrevoiusPoint = previousStopp.getPlannedDeaparture();
							double distanceToPickUpPoint = Simulation.calculateDistanceBetween2Points(previousPoint, pickUpPoint);
							double driveTime = Simulation.calculateDriveTimeToPoint(distanceToPickUpPoint);
							double arrivalTimeAtPickUpPoint = departureFromPrevoiusPoint + driveTime;
							if(arrivalTimeAtPickUpPoint < serviceTime){
								arrivalTimeAtPickUpPoint = serviceTime;
							}
							double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
							double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
							double arrivalAtDropOff = arrivalTimeAtPickUpPoint + driveTimeToDropOff;
							double latestArrivalAtDropOff = arrivalTimeAtPickUpPoint + maxDrivingTime;
							pickUpStopp = new Stopp(requestId, pickUpPoint, arrivalTimeAtPickUpPoint, 2, arrivalTimeAtPickUpPoint, lastArrivalTime, passengers, 0, serviceTime, serviceType);
							Stopp dropOffStopp = new Stopp(requestId, dropOffPoint, arrivalAtDropOff, 3, arrivalAtDropOff,latestArrivalAtDropOff , passengers, 0, serviceTime, serviceType);
							tour.add(indexOfPickUpPoint, pickUpStopp);
							tour.add(indexOfDropOffPoint, dropOffStopp);
							
							//Hier soll dann die cheapest Insertion der erfolgen, also der kuerzeste Weg.
							//Betrachte das Intervall von dem ersten Drop-Off Punkt des Servicetypen + Requestypen der betrachteten Request.
							double waitingTime = arrivalTimeAtPickUpPoint - serviceTime;
							AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
							vehicleForAssigning.add(potentialVehicle);
							//Wenn es sich nicht um die
						} else {
							Stopp pickUpStopp1 = new Stopp(requestId, pickUpPoint, 0.0, 2, 0.0, 0.0, passengers, 0, serviceTime, 1);
							tour.add(indexOfPickUpPoint,pickUpStopp1);
							//Die Werte 0.0 sind Platzhalter und werden in der Methode "calculateTimesOfTour" angepasst.
							Stopp dropOffStopp = new Stopp(requestId, dropOffPoint, 0.0, 3, 0.0, 0.0, passengers, 0, serviceTime, 1);
							tour.add(indexOfDropOffPoint,dropOffStopp);
							//Berechne hier den kuerzesten Weg, der Drop-Off Punkte und berechne die Abfahrtszeiten neu.
							//:::::::::::::::::::::::::::::::::::::::
							
							calculateTimesOfTour(waitingStrategy,tour, maxWaitingTime, maxDrivingTime);
							double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
							AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
							vehicleForAssigning.add(potentialVehicle);
							
						}
						// Sonst fuege den Punkt an dem IndexPunkt hinzu.
						//Fuege den Drop-Off Punkt dahinter
						//Berechne den kuerzesten Weg von den Drop-Off Punkten und berechne die Fahrzeiten neu
						//Skip Fahrzeug, Punkt kann nicht hinzugefuegt werden.
					}
					/**
					if (swapCounter > 0) {
						int swapIndex = (indexOfDropOffPoint + indexOfPickUpPoint) - 1;
						swapPointsInTour(waitingStrategy,indexOfDropOffPoint, swapIndex, tour, vehicle, vehicleForAssigning, maxWaitingTime, maxDrivingTime);
					}
					**/
					//Wenn der Index groeßer als die Tour ist, dann fuege den Punkt hinten an der Tour an.
				} else {
					//Wenn es sich um die dritte Wartestrategie handelt  setze die Abfahrtszeit/Bedienzeit
					if(waitingStrategy ==3){
						int sizeOfTour = vehicle.currentTour.size();
						Stopp lastStopp = vehicle.currentTour.get(sizeOfTour-1);
						Point pointOfLastStopp = lastStopp.getStopp();
						double distanceFromlastStoppToPickUp = Simulation.calculateDistanceBetween2Points(pointOfLastStopp, pickUpPoint);
						double driveTimeToPickUp = Simulation.calculateDriveTimeToPoint(distanceFromlastStoppToPickUp);
						double arrivalTime = currentTime + driveTimeToPickUp;
						double servingTime = serviceTime + maxWaitingTime;//spaetester Zeitpunkt der Einsammlung
						if(arrivalTime < serviceTime){
							arrivalTime = serviceTime;
						} 
						double latestServingTime = servingTime;
						//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
						Stopp pickUpStopp1 = new Stopp(requestId,pickUpPoint,arrivalTime,2,arrivalTime,latestServingTime, passengers, 0, serviceTime, 1);
						double distanceFromPickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTime = Simulation.calculateDriveTimeToPoint(distanceFromPickUpToDropOff);
						double arrivalTimeAtDropOff = arrivalTime + driveTime;
						Stopp dropOffStopp = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,arrivalTimeAtDropOff,arrivalTimeAtDropOff, passengers, 0, serviceTime, 1);
						tour.add(pickUpStopp1);
						tour.add(dropOffStopp);
						double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					} else {
						Stopp lastStopp = tour.get(tourSize - 1);
						Point lastPoint = lastStopp.getStopp();
						double departure = lastStopp.getPlannedDeaparture();
						double distanceLastPointToPickUp = Simulation.calculateDistanceBetween2Points(lastPoint, pickUpPoint);
						double driveTimeOfDistance = Simulation.calculateDriveTimeToPoint(distanceLastPointToPickUp);
						double arrivalAtPickUp = departure + driveTimeOfDistance;
						double servingTime;
						if(arrivalAtPickUp > serviceTime){
							servingTime = arrivalAtPickUp;
						} else { 
							servingTime = serviceTime;
						}
						double latestServingTime = servingTime + maxWaitingTime;
						Stopp pickUpStopp = new Stopp(requestId, pickUpPoint,arrivalAtPickUp , 2,servingTime , latestServingTime, passengers, 0, serviceTime, 1);
						tour.add(pickUpStopp);
						double distanceToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint,dropOffPoint);
						double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distanceToDropOff);
						double arrivalAtDropOff = servingTime + driveTimeToDropOff;
						double latestDropOff = arrivalAtDropOff + maxDrivingTime;
						Stopp dropOffStopp = new Stopp(requestId, dropOffPoint,arrivalAtDropOff , 3, arrivalAtDropOff, latestDropOff, passengers, 0, serviceTime, 1);
						tour.add(dropOffStopp);
						//calculateTimesOfTour(waitingStrategy,tour, maxWaitingTime, maxDrivingTime);
						double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					}
				}
			}
		}
		chooseTour(vehicleForAssigning, endTime, maxMovingPosition,  maxCapacity, currentRequest, vehicles, waitingTimesOfCustomers,maxWaitingTime);
		
		
	}
	
	public static void assignTypeTwoRequest(int waitingStrategy,Request currentRequest, ArrayList<Vehicle> vehicles, double maxWaitingTime, double maxDrivingTime,int maxCapacity,double endTime, int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		int requestId = currentRequest.getId();
		System.out.println("CurrentRequestId:" + requestId + " " + currentRequest.getRequestTime());
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

		for (Vehicle vehicle : vehicles) {
			ArrayList<Stopp> tourOfCurrentVehicle = vehicle.currentTour;
			ArrayList<Stopp> tour = new ArrayList<Stopp>(tourOfCurrentVehicle);
			System.out.println("FahrzeugId:" + "" + vehicle.getId() + "Tourengroesse des Fahrzeuges" + tourOfCurrentVehicle.size());
			int tourSize = tour.size();
			System.out.println("Kopierte Tourenliste des Fahrzeuges vor der Zuweisung:" + tour.size());
			//Falls die Tour leer ist fuege die Serviceanfrage vorne ein, Fange mit dem Drop-Off Punkt an und setze den Pick-up Punkt davor.
			if (tourSize == 0) {
				
				/**
				 * Hier muessen noch die unterschiedlichen Wartestrategien beruecksichtigt werden.
				 * Bei der Drive-First Strateg wird so vorgegangen, wie bei der im folgenden Code
				 * Bei der Wait-First Strategie, werden die Zeiten anders berechnet.
				 * 
				 */
				//Wenn die Wartestrategie gleich der Wait-First Strategy ist dann nehme den Drop-Off Punkt und rechne rueckwaerts
				if(waitingStrategy ==3 || waitingStrategy == 4){
					double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
					double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
					double arrivalTimeAtDropOff = serviceTime;
					double servingTimeAtPickUp = serviceTime - driveTimeToDropOff;
					Point vehiclePos = vehicle.getPosition();
					double distancePosToPickUp = Simulation.calculateDistanceBetween2Points(pickUpPoint, vehiclePos);
					double driveTimePosToPickUp = Simulation.calculateDriveTimeToPoint(distancePosToPickUp);
					double arrivalAtPickUp = currentTime + driveTimePosToPickUp;
					if(arrivalAtPickUp < servingTimeAtPickUp){
						arrivalAtPickUp = servingTimeAtPickUp;
					}
					//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
					Stopp pickUp = new Stopp(requestId,pickUpPoint,arrivalAtPickUp,2,servingTimeAtPickUp,servingTimeAtPickUp,passengers,0,serviceTime,2);
					tour.add(pickUp);
					Stopp dropOff = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,serviceTime,serviceTime,passengers,0,serviceTime,2);
					tour.add(dropOff);
					double waitingTime = serviceTime - arrivalTimeAtDropOff;
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
				} else if(waitingStrategy == 2){
					double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
					double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
					double arrivalTimeAtDropOff = serviceTime;
					double servingTimeAtPickUp = serviceTime - driveTimeToDropOff;
					Point vehiclePos = vehicle.getPosition();
					double distancePosToPickUp = Simulation.calculateDistanceBetween2Points(pickUpPoint, vehiclePos);
					double driveTimePosToPickUp = Simulation.calculateDriveTimeToPoint(distancePosToPickUp);
					double arrivalAtPickUp = currentTime + driveTimePosToPickUp;
					//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
					Stopp pickUp = new Stopp(requestId,pickUpPoint,arrivalAtPickUp,2,servingTimeAtPickUp,servingTimeAtPickUp,passengers,0,serviceTime,2);
					tour.add(pickUp);
					Stopp dropOff = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,serviceTime,serviceTime,passengers,0,serviceTime,2);
					tour.add(dropOff);
					double waitingTime = serviceTime - arrivalTimeAtDropOff;
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
				} else {
					int indexOfPickUpPoint = 0;
					Point posVehicle = vehicle.getPosition();
					double distance = Simulation.calculateDistanceBetween2Points(posVehicle, pickUpPoint);
					double driveTime = Simulation.calculateDriveTimeToPoint(distance);
					double arrivalTimeAtPickUp = driveTime + currentTime;
					double distanceToDropOffPoint = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
					double driveTimeToDropOffPoint = Simulation.calculateDriveTimeToPoint(distanceToDropOffPoint);
					double arrivalTimeToDropOff = arrivalTimeAtPickUp + driveTimeToDropOffPoint;
					double departureTime = 0.0;
					double latestDeparture = 0.0;
					//Wenn die geplante Ankunft am DropOff Punkt kleiner als der fruehestmoegliche Zeitpunkt ist, dann erhoehe die Abfahrt von dem Pick-Up Punkt um die Zeitdifferenz.
					if (arrivalTimeToDropOff < earliestArrivalTimeAtDropOff) {
						departureTime = earliestArrivalTimeAtDropOff - driveTimeToDropOffPoint;
						latestDeparture = serviceTime - driveTimeToDropOffPoint;
					} else {
						departureTime = arrivalTimeToDropOff;
						latestDeparture = serviceTime - driveTimeToDropOffPoint;
					} 
					Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, arrivalTimeAtPickUp, 2, departureTime, latestDeparture, passengers, 0, serviceTime, 2);
					pickUpStopp.setRequestId(requestId);
					pickUpStopp.setRequestType(2);
					tour.add(indexOfPickUpPoint, pickUpStopp);

					earliestDropOffTime = departureTime + driveTimeToDropOffPoint;
					arrivalTimeToDropOff = departureTime + driveTimeToDropOffPoint;
					Stopp dropOffStopp = new Stopp(requestId, dropOffPoint, arrivalTimeToDropOff, 3, earliestDropOffTime, serviceTime, passengers, 0, serviceTime, serviceType);
					dropOffStopp.setRequestId(requestId);
					dropOffStopp.setRequestType(2);
					tour.add(dropOffStopp);
					double waitingTime = serviceTime - dropOffStopp.getPlannedDeaparture();
					AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
					vehicleForAssigning.add(potentialVehicle);
				}
			} else {
				//Gehe die aktuelle Tour durch und bestimme indizes fuers einfuegen.
				int indexOfPickUp = 0;
				int indexOfDropOff = 0;
				//wird gebraucht um die Tour nachher zu swappen.
				int swapCounter = 0;
				/**
				 * Hier muss noch das gleiche angepasst werden, wie oben in Zeile:
				 * Hier werden die Pick-Up-Punkte nicht richtig eingefuegt.
				 */
				for (Stopp stopp : tour) {
					int requestType = stopp.getRequestType();
					double serviceTimeOfStopp = stopp.getServiceTime();
					int stoppType = stopp.getType();
					if (requestType != 2 && serviceTimeOfStopp > serviceTime) {
						indexOfPickUp = indexOfPickUp + 1;
					} else if (serviceTime > serviceTimeOfStopp) {
						indexOfPickUp = indexOfPickUp + 1;
					} else if (serviceTime == serviceTimeOfStopp && stoppType == 2 && requestType == 2) {
						indexOfPickUp = indexOfPickUp + 1;
						swapCounter = swapCounter + 1;
					}
				}
				if (indexOfPickUp >= tour.size()) {
					//Wenn es sich um die Strategie Wait-First handelt.
					if(waitingStrategy == 3 || waitingStrategy == 4){
						int indexLastStopp = tour.size() - 1;
						//Berechnung der Zeiten von der letzten Position der Tour:
						Stopp lastStoppOfTour = vehicle.currentTour.get(indexLastStopp);
						Point pointOfLastStopp = lastStoppOfTour.getStopp();
						double distanceLastStoppToPickUp = Simulation.calculateDistanceBetween2Points(pointOfLastStopp, pickUpPoint);
						double driveTimeLastStopToPickUp = Simulation.calculateDriveTimeToPoint(distanceLastStoppToPickUp);
						double arrivalTimeAtPickUpFromLastStopp = currentTime + driveTimeLastStopToPickUp;
						double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
						double arrivalTimeAtDropOff = serviceTime;
						double servingTimeAtPickUp = serviceTime - driveTimeToDropOff;
						Point vehiclePos = vehicle.getPosition();
						double distancePosToPickUp = Simulation.calculateDistanceBetween2Points(pickUpPoint, vehiclePos);
						double driveTimePosToPickUp = Simulation.calculateDriveTimeToPoint(distancePosToPickUp);
						if(arrivalTimeAtPickUpFromLastStopp < servingTimeAtPickUp){
							arrivalTimeAtPickUpFromLastStopp = servingTimeAtPickUp;
						}
						//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
						Stopp pickUp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUpFromLastStopp,2,servingTimeAtPickUp,servingTimeAtPickUp,passengers,0,serviceTime,2);
						tour.add(pickUp);
						Stopp dropOff = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,serviceTime,serviceTime,passengers,0,serviceTime,2);
						tour.add(dropOff);
						double waitingTime = serviceTime - arrivalTimeAtDropOff;
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
					} else {
						int indexLastStopp = tour.size() - 1;
						//Berechnung der Zeiten von der letzten Position der Tour:
						Stopp lastStoppOfTour = vehicle.currentTour.get(indexLastStopp);
						Point pointOfLastStopp = lastStoppOfTour.getStopp();
						double departureFromLastStopp = lastStoppOfTour.getPlannedDeaparture();
						double distanceLastStoppToPickUp = Simulation.calculateDistanceBetween2Points(pointOfLastStopp, pickUpPoint);
						double driveTimeLastStopToPickUp = Simulation.calculateDriveTimeToPoint(distanceLastStoppToPickUp);
						double arrivalTimeAtPickUpFromLastStopp = departureFromLastStopp + driveTimeLastStopToPickUp;
						double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
						double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
						double arrivalTimeAtDropOff = serviceTime;
						double servingTimeAtPickUp = serviceTime - driveTimeToDropOff;
						Point vehiclePos = vehicle.getPosition();
						double distancePosToPickUp = Simulation.calculateDistanceBetween2Points(pickUpPoint, vehiclePos);
						double driveTimePosToPickUp = Simulation.calculateDriveTimeToPoint(distancePosToPickUp);
						//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
						Stopp pickUp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUpFromLastStopp,2,servingTimeAtPickUp,servingTimeAtPickUp,passengers,0,serviceTime,2);
						tour.add(pickUp);
						Stopp dropOff = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,serviceTime,serviceTime,passengers,0,serviceTime,2);
						tour.add(dropOff);
						double waitingTime = serviceTime - arrivalTimeAtDropOff;
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
				
					}
					
				} else {
					//Wenn der index am Anfang ist, dann ueberprüfe, ob sich das Fahrzeug schon auf den Weg befindet, falls nicht dann setze den Punkt vorne ein
					//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					if (indexOfPickUp == 0) {
						if(waitingStrategy == 3 || waitingStrategy == 4){
							//Bestimme die Fahrzeug Position
							if(!vehicleIsDriving(vehicle,currentTime)){
								Point posVehicle = vehicle.getPosition();
								double distanceFromCurPosToPickUp = Simulation.calculateDistanceBetween2Points(posVehicle, pickUpPoint);
								double driveTimeToPickUp = Simulation.calculateDriveTimeToPoint(distanceFromCurPosToPickUp);
								double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
								double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
								double arrivalTimeAtPickUp = currentTime + driveTimeToPickUp;
								double arrivalTimeAtDropOff = serviceTime;
								double servingTimeAtPickUp = serviceTime - driveTimeToDropOff;
								Point vehiclePos = vehicle.getPosition();
								double distancePosToPickUp = Simulation.calculateDistanceBetween2Points(pickUpPoint, vehiclePos);
								double driveTimePosToPickUp = Simulation.calculateDriveTimeToPoint(distancePosToPickUp);
								if(arrivalTimeAtPickUp < servingTimeAtPickUp){
									arrivalTimeAtPickUp = servingTimeAtPickUp;
								}
								//public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType)
								Stopp dropOff = new Stopp(requestId,dropOffPoint,arrivalTimeAtDropOff,3,serviceTime,serviceTime,passengers,0,serviceTime,2);
								tour.add(indexOfPickUp,dropOff);
								Stopp pickUp = new Stopp(requestId,pickUpPoint,arrivalTimeAtPickUp,2,servingTimeAtPickUp,servingTimeAtPickUp,passengers,0,serviceTime,2);
								tour.add(indexOfPickUp,pickUp);
								double waitingTime = serviceTime - arrivalTimeAtDropOff;
								AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
								vehicleForAssigning.add(potentialVehicle);
							}
						} else {
							Point posVehicle = vehicle.getPosition();
							Stopp firstStopp = vehicle.currentTour.get(0);
							Point firstPoint = firstStopp.getStopp();
							if(firstPoint != posVehicle){
								double distance = Simulation.calculateDistanceBetween2Points(posVehicle, pickUpPoint);
								double driveTime = Simulation.calculateDriveTimeToPoint(distance);
								double arrivalTime = driveTime + currentTime;
								double distanceToDropOffPoint = Simulation.calculateDistanceBetween2Points(pickUpPoint, dropOffPoint);
								double driveTimeToDropOffPoint = Simulation.calculateDriveTimeToPoint(distanceToDropOffPoint);
								double arrivalTimeToDropOff = arrivalTime + driveTimeToDropOffPoint;
								double departureTime = 0.0;
								double latestDeparture = 0.0;
								//Wenn die geplante Ankunft am DropOff Punkt kleiner als der fruehestmoegliche Zeitpunkt ist, dann erhoehe die Abfahrt von dem Pick-Up Punkt um die Zeitdifferenz.
								if (arrivalTimeToDropOff < earliestArrivalTimeAtDropOff) {
									departureTime = earliestArrivalTimeAtDropOff - driveTimeToDropOffPoint;
									latestDeparture = serviceTime - driveTimeToDropOffPoint;
								} else {
									departureTime = arrivalTimeToDropOff;
									latestDeparture = serviceTime - driveTimeToDropOffPoint;
								}
								Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, arrivalTime, 2, departureTime, latestDeparture, passengers, 0, serviceTime, 2);
								pickUpStopp.setRequestId(requestId);
								pickUpStopp.setRequestType(2);
								tour.add(indexOfPickUp, pickUpStopp);
								indexOfDropOff = indexOfPickUp + 1;
								earliestDropOffTime = departureTime + driveTimeToDropOffPoint;
								arrivalTimeToDropOff = earliestDropOffTime;
								Stopp dropOffStop = new Stopp(requestId, dropOffPoint, arrivalTimeToDropOff, 3, earliestDropOffTime, serviceTime, passengers, 0, serviceTime, serviceType);
								dropOffStop.setRequestId(requestId);
								dropOffStop.setRequestType(2);
								tour.add(indexOfDropOff, dropOffStop);
								AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, 0.0);
								vehicleForAssigning.add(potentialVehicle);
							} else{
								//Skip Anfrage kann nicht hinzugefuegt werden.
							}
						}
						//.............................................
					} else {
						//Brauche den Bereich der Punkte, mit der gleichen Servicezeit
						//Dann checke, ob das Fahrzeug unterwegs ist oder noch wartet,
						//Ermittle die aktuelle Position des Fahrzeugs
						//Gehe anschließend die Liste rueckwaerts des Bereichs durch und berechne die neuen Zeiten.
						//Anschließend betrachte den Punkt vor dem Pick-Up Punkt und berechne von diesem die Abfahrt und rechne bis zum Bereich der pick-Up Punkte, die Punkte neu.
						indexOfDropOff = indexOfPickUp;
						Stopp dropOffStop = new Stopp(requestId, dropOffPoint, 0.0, 3, earliestDropOffTime, serviceTime, passengers, 0, serviceTime, serviceType);
						tour.add(indexOfDropOff, dropOffStop);
						Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, 0.0, 2, 0.0, 0.0, passengers, 0, serviceTime, 2);
						tour.add(indexOfPickUp, pickUpStopp);
						double waitingTime = 0.0;
						int startIndex = 0; //Der Index wo die Pick-Ups zur gleichen servicezeit beginnt
						//Gehe Tour bis zum index von dem eingefuegten PickUp Punkt durch.
						searchStartIndex: for(int i = 0; i <= indexOfPickUp; i++){
							Stopp currentStopp = tour.get(i);
							int requestType = currentStopp.getRequestType();
							int stoppType = currentStopp.getType();
							double service = currentStopp.getServiceTime();
							if(requestType == 2 && stoppType == 2 && service == serviceTime){
								startIndex = i;
								break searchStartIndex;
							}	
						}
						int prevStartIndex = startIndex - 1;
						if(prevStartIndex < 0){
							prevStartIndex = 0;
						}
						Stopp previousStoppOfIndex = tour.get(prevStartIndex);
						Point previousStoppPointOfIndex = previousStoppOfIndex.getStopp();
						double servingTimeOfPrevoiusStopp = previousStoppOfIndex.getPlannedDeaparture();
							for(int j = indexOfPickUp; j >= startIndex;j--){
									Stopp curStopp = tour.get(j);
									Stopp nextStopp = tour.get(j+1);
									Point curPoint = curStopp.getStopp();
									Point nextPoint = nextStopp.getStopp();
									int stoppTypeOfNext = nextStopp.getType();
									double distanceCurPointToNext = Simulation.calculateDistanceBetween2Points(curPoint, nextPoint);
									double driveTimeToNextPoint = Simulation.calculateDriveTimeToPoint(distanceCurPointToNext);
									if(stoppTypeOfNext == 3){
										double arrivalTimeAtNextStopp = serviceTime;
										double servingTimeAtCurPoint = serviceTime - driveTimeToNextPoint;
										double servingTimeAtNextStopp = arrivalTimeAtNextStopp;
										nextStopp.setArrivalTime(servingTimeAtNextStopp);
										nextStopp.setPlannedDeparture(servingTimeAtNextStopp);
										nextStopp.setLatestArrivalTime(servingTimeAtNextStopp);
										waitingTime = serviceTime - arrivalTimeAtNextStopp;
										curStopp.setArrivalTime(servingTimeAtCurPoint);
										curStopp.setPlannedDeparture(servingTimeAtCurPoint);
										curStopp.setLatestArrivalTime(servingTimeAtCurPoint);
										
									} else {
										double arrivalTimeAtCurStopp = nextStopp.getPlannedDeaparture() - driveTimeToNextPoint;
										double servingTimeAtCurStopp = arrivalTimeAtCurStopp;
										if(waitingStrategy == 3 || waitingStrategy == 4){
											if(arrivalTimeAtCurStopp < servingTimeAtCurStopp){
												arrivalTimeAtCurStopp = servingTimeAtCurStopp;
											}
										}
										curStopp.setArrivalTime(arrivalTimeAtCurStopp);
										curStopp.setPlannedDeparture(servingTimeAtCurStopp);
										curStopp.setLatestArrivalTime(servingTimeAtCurStopp);
									}
									
								}
					
							Stopp stoppOfStartIndex = tour.get(startIndex);
							Point stoppPointOfStartIndex = stoppOfStartIndex.getStopp();
							double servingTimeOfStartIndex = stoppOfStartIndex.getPlannedDeaparture();
							double distancePrevoiusStoppToStartIndex = Simulation.calculateDistanceBetween2Points(previousStoppPointOfIndex,stoppPointOfStartIndex);
							double driveTimePreviousStoppToStartIndex = Simulation.calculateDriveTimeToPoint(distancePrevoiusStoppToStartIndex);
							double arrivalAtStart = servingTimeOfPrevoiusStopp + driveTimePreviousStoppToStartIndex;
							//Wenn durch die Rueckwartsberechnung die Zeit niedriger ist, als die Bedienungszeit des Vorgaengers, dann berechne die Zeiten vorwaerts neu.
							if(arrivalAtStart > servingTimeOfStartIndex){
								//Gehe die Tour beginnend von dem Vorgaenger durch und berechne die Zeiten neu.
								for(int h = startIndex; h <= indexOfPickUp; h++){
									Stopp previousStopp =  tour.get(prevStartIndex);
									Stopp curStopp = tour.get(h);
									Point curPoint = curStopp.getStopp();
									Point prevPoint = previousStopp.getStopp();
									double servingTimePrevStopp = previousStopp.getPlannedDeaparture();
									double distancePrevoiusStoppToCurStopp = Simulation.calculateDistanceBetween2Points(prevPoint,curPoint);
									double driveTimePreviousStoppToCurStopp = Simulation.calculateDriveTimeToPoint(distancePrevoiusStoppToCurStopp);
									double arrivalAtCurStopp = servingTimePrevStopp + driveTimePreviousStoppToCurStopp;
									curStopp.setArrivalTime(arrivalAtCurStopp);
									curStopp.setPlannedDeparture(arrivalAtCurStopp);
									curStopp.setLatestArrivalTime(arrivalAtCurStopp);
								}
							}
							
							
						AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
						vehicleForAssigning.add(potentialVehicle);
						
						
						
						
						/**
						if(waitingStrategy == 3 || waitingStrategy == 4) {
							indexOfDropOff = indexOfPickUp;
							Stopp dropOffStop = new Stopp(requestId, dropOffPoint, 0.0, 3, earliestDropOffTime, serviceTime, passengers, 0, serviceTime, serviceType);
							tour.add(indexOfDropOff, dropOffStop);
							Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, 0.0, 2, 0.0, 0.0, passengers, 0, serviceTime, 2);
							tour.add(indexOfPickUp, pickUpStopp);
							double waitingTime = 0.0;
							int startIndex = 0; //Der Index wo die Pick-Ups zur gleichen servicezeit beginnt
							//Gehe Tour bis zum index von dem eingefuegten PickUp Punkt durch.
							searchStartIndex: for(int i = 0; i <= indexOfPickUp; i++){
								Stopp currentStopp = tour.get(i);
								int requestType = currentStopp.getRequestType();
								int stoppType = currentStopp.getType();
								double service = currentStopp.getServiceTime();
								if(requestType == 2 && stoppType == 2 && service == serviceTime){
									startIndex = i;
									break searchStartIndex;
								}	
							}
							int prevStartIndex = startIndex - 1;
							if(prevStartIndex < 0){
								prevStartIndex = 0;
							}
							Stopp previousStoppOfIndex = tour.get(prevStartIndex);
							Point previousStoppPointOfIndex = previousStoppOfIndex.getStopp();
							double servingTimeOfPrevoiusStopp = previousStoppOfIndex.getPlannedDeaparture();
								for(int j = indexOfPickUp; j >= startIndex;j--){
										Stopp curStopp = tour.get(j);
										Stopp nextStopp = tour.get(j+1);
										Point curPoint = curStopp.getStopp();
										Point nextPoint = nextStopp.getStopp();
										int stoppTypeOfNext = nextStopp.getType();
										
							
										double distanceCurPointToNext = Simulation.calculateDistanceBetween2Points(curPoint, nextPoint);
										double driveTimeToNextPoint = Simulation.calculateDriveTimeToPoint(distanceCurPointToNext);
										if(stoppTypeOfNext == 3){
											double arrivalTimeAtNextStopp = serviceTime;
											double servingTimeAtCurPoint = serviceTime - driveTimeToNextPoint;
											double servingTimeAtNextStopp = arrivalTimeAtNextStopp;
											nextStopp.setArrivalTime(servingTimeAtNextStopp);
											nextStopp.setPlannedDeparture(servingTimeAtNextStopp);
											nextStopp.setLatestArrivalTime(servingTimeAtNextStopp);
											waitingTime = serviceTime - arrivalTimeAtNextStopp;
											curStopp.setArrivalTime(servingTimeAtCurPoint);
											curStopp.setPlannedDeparture(servingTimeAtCurPoint);
											curStopp.setLatestArrivalTime(servingTimeAtCurPoint);
											
										} else {
											double arrivalTimeAtCurStopp = nextStopp.getPlannedDeaparture() - driveTimeToNextPoint;
											double servingTimeAtCurStopp = arrivalTimeAtCurStopp;
											if(arrivalTimeAtCurStopp < servingTimeAtCurStopp){
												arrivalTimeAtCurStopp = servingTimeAtCurStopp;
											}
											curStopp.setArrivalTime(arrivalTimeAtCurStopp);
											curStopp.setPlannedDeparture(servingTimeAtCurStopp);
											curStopp.setLatestArrivalTime(servingTimeAtCurStopp);
										}
										
									}
						
								Stopp stoppOfStartIndex = tour.get(startIndex);
								Point stoppPointOfStartIndex = stoppOfStartIndex.getStopp();
								double servingTimeOfStartIndex = stoppOfStartIndex.getPlannedDeaparture();
								double distancePrevoiusStoppToStartIndex = Simulation.calculateDistanceBetween2Points(previousStoppPointOfIndex,stoppPointOfStartIndex);
								double driveTimePreviousStoppToStartIndex = Simulation.calculateDriveTimeToPoint(distancePrevoiusStoppToStartIndex);
								double arrivalAtStart = servingTimeOfPrevoiusStopp + driveTimePreviousStoppToStartIndex;
								//Wenn durch die Rueckwartsberechnung die Zeit niedriger ist, als die Bedienungszeit des Vorgaengers, dann berechne die Zeiten vorwaerts neu.
								if(arrivalAtStart > servingTimeOfStartIndex){
									//Gehe die Tour beginnend von dem Vorgaenger durch und berechne die Zeiten neu.
									for(int h = startIndex; h <= indexOfPickUp; h++){
										Stopp previousStopp =  tour.get(prevStartIndex);
										Stopp curStopp = tour.get(h);
										Point curPoint = curStopp.getStopp();
										Point prevPoint = previousStopp.getStopp();
										double servingTimePrevStopp = previousStopp.getPlannedDeaparture();
										double distancePrevoiusStoppToCurStopp = Simulation.calculateDistanceBetween2Points(prevPoint,curPoint);
										double driveTimePreviousStoppToCurStopp = Simulation.calculateDriveTimeToPoint(distancePrevoiusStoppToCurStopp);
										double arrivalAtCurStopp = servingTimePrevStopp + driveTimePreviousStoppToCurStopp;
										curStopp.setArrivalTime(arrivalAtCurStopp);
										curStopp.setPlannedDeparture(arrivalAtCurStopp);
										curStopp.setLatestArrivalTime(arrivalAtCurStopp);
									}
								}
								
								
							AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
							vehicleForAssigning.add(potentialVehicle);
							
						} else {
							indexOfDropOff = indexOfPickUp;
							Stopp dropOffStop = new Stopp(requestId, dropOffPoint, 0.0, 3, earliestDropOffTime, serviceTime, passengers, 0, serviceTime, serviceType);
							tour.add(indexOfDropOff, dropOffStop);
							Stopp pickUpStopp = new Stopp(requestId, pickUpPoint, 0.0, 2, 0.0, 0.0, passengers, 0, serviceTime, 2);
							tour.add(indexOfPickUp, pickUpStopp);
							//Hier sollen dann die pick-up Orte Kostenmininmal hinzugefuegt werden. Dabei soll geschaut werden, ob sich das Fahrzeug schon an dem ersten Punkt befindet des intervalls Punkt befindet oder nicht.
							//calculateTimesOfTour(waitingStrategy,tour, maxWaitingTime, maxDrivingTime);
							double waitingTime = calculateWaitingTimeOfTour(tour, maxWaitingTime);
							AssignedVehicle potentialVehicle = new AssignedVehicle(vehicle, tour, waitingTime);
							vehicleForAssigning.add(potentialVehicle);

							if (swapCounter > 0) {
								int swapStartIndex = indexOfPickUp - swapCounter;
								int swapEndIndex = indexOfPickUp - 1;
								swapPointsInTour(waitingStrategy,swapStartIndex, swapEndIndex, tour, vehicle, vehicleForAssigning, maxWaitingTime, maxDrivingTime);
							}
						}
						**/
						//:::
					}
				}
			}
		}
		//Zuweisung der Anfrage zur Tour.
		
		System.out.println("Assigningliste nach Zuweisungen:" + vehicleForAssigning.size());
		//Methode chooseTour wählt die Zuweisung aus, oder die Ablehnung einer Anfrage
		chooseTour(vehicleForAssigning, endTime, maxMovingPosition,  maxCapacity, currentRequest,vehicles,waitingTimesOfCustomers,maxWaitingTime);
	}
	//Ende for-Schleife
		
		
		
	
	/**
	 * Diese Methode überprüft, ob die Wartezeit des Kunden die maximale Wartezeit nicht überschreitet.
	 * @param time1, Wartezeit der Kunden.
	 * @param time2, maximaleWartezeit
	 * @return
	 */
	public static boolean checkWaitingTime(double time1, double time2){
		return time1 < time2;
	}
	/**
	 * Diese Methode ueberprueft eine Tour des Fahrzeugs, ob die Kapazitaetsrestriktion eingehalten wird.
	 * Wenn es sich um einen Pick-Up Punkt handelt, wird die aktuelle Kapazitaet um die anzahl an Passagieren erhoeht.
	 * Wenn es sich um einen Drop-Off Punkt handelt, wird die aktuelle Kapazitaet um die Anzahl verringert.
	 * @param vehicle das zu ueberpruefende Fahrzeug
	 * @param tour die zum Fahrzeug zugehoerige Tour, welche ueberprueft werden soll
	 * @param maxCapacity, die maximale Kapazitaet der Fahrzeuge
	 * @return einen Boolean, der entweder true ist, falls die maximale Kapazitaet des Fahrzeuges nicht ueberschritten wird oder false, falls die Kapazitaet ueberschritten wird
	 */
	public static boolean checkCapacity(Vehicle vehicle, ArrayList<Stopp> tour, int maxCapacity){
		boolean checkCapacity = true;
		int currentCapacity = vehicle.getCapacity();

		for (Stopp stopp : tour) {
			int passengers = stopp.getPassengers();
			int stoppType = stopp.getType();
			if (stoppType == 2) {
				currentCapacity = currentCapacity + passengers;
				if (currentCapacity > maxCapacity) {
					checkCapacity = false;
					break;
				}
			} else if (stoppType == 3) {
				currentCapacity = currentCapacity - passengers;
				if (currentCapacity < 0) {
					checkCapacity = false;
					break;
				}
			}
		}
		return checkCapacity;
	}
	/**
	 * Diese Methode ueberprueft, ob die Rueckkehr zum Umsteigepunkt am Ende der Tour zeitlich moeglich ist.
	 * Falls ja, nimmt der boolean den "true" an, sonst den wert "false".
	 * @param tour, die zu ueberpruefende Tour
	 * @param endTime, Ende des Betriebshorizonts.
	 * @return checkingReturn
	 */
	public static boolean checkReturnToTransferPoint(ArrayList<Stopp>tour, double endTime){
		boolean checkingReturn;
		Point transferPoint = new Point(2,0);
		Stopp lastStopp = tour.get(tour.size() - 1);
		Point PointOfLastStopp = lastStopp.getStopp();
		double departureFromLastStopp = lastStopp.getPlannedDeaparture();
		double distanceToTransferPoint = Simulation.calculateDistanceBetween2Points(PointOfLastStopp, transferPoint);
		double driveTimeToTransferPoint = Simulation.calculateDriveTimeToPoint(distanceToTransferPoint);
		double arrivalTimeAtTransferPoint = driveTimeToTransferPoint + departureFromLastStopp;
		checkingReturn = !(arrivalTimeAtTransferPoint > endTime);
		return checkingReturn;
	}
	/**
	 * Diese Methode ueberprueft, ob die spaetest moeglichen Zeiten eingehalten werden
	 * damit ist die Einhaltung der maximalen Wartezeit und der maximalen Fahrzeit gemeint
	 * @param tour
	 * @return
	 */
	public static boolean checkTimesOfTour(ArrayList<Stopp> tour, double maxWaitingTime){
		boolean checkingTimes = true;
		for (Stopp stopp : tour) { 
			double serviceTime = stopp.getServiceTime();
			double arrival = stopp.getArrivalTime();
			double lastArrival = stopp.getLatestArrivalTime();
			double servingTime = stopp.getPlannedDeaparture();
			int stoppType = stopp.getType();
			int requestType = stopp.getRequestType();
			//Fallunterscheidung:
			//Fall1: Es handlt sich um die Fahrtrichtung U-Punkt in Bezirk
			//Dann ueberpruefe, ob die Ankunftszeit am Pick-Up Punkt die Servicezeit + maximale Wartezeit ueberschritten wird.
			//Fall2: ueberpruefe, ob der DO Punkt, die maximale Fahrzeit ueberschreitet.(Ist in dem Fall, die spaeteste Ankunftszeit/plannedDeparture)
			//Fall3: es handelt sich um die Fahrtrichtung Bezirk --> Umsteigepunkt.
			//ueberprüfe, ob die Ankunftszeit am Pick-Up-Punkt > Servicezeit ist.
			//Fall 4: es handelt sich um den DO, betrachte auch hier die Ankunftszeit und die spaeteste Ankunftszeit.
			if(stoppType == 2 && requestType == 1){//Pick-Up-Punkt und Fahrtrichtung U-->A
				double latestServing = serviceTime + maxWaitingTime;
				if(servingTime > latestServing){
					checkingTimes = false;
					break;
				}
			}else if(stoppType == 3 && requestType ==2){//Fahrtrichtung A-->U & Drop-Off Punkt
				if(arrival> serviceTime){
					checkingTimes = false;
					break;
				}
			} else {//Generell soll ueberprueft werden, ob die Ankunft die spaeteste Ankunftszeit ueberschritten wird.
			if (arrival > lastArrival) {
				checkingTimes = false;
				break;
				}
			}
		}
		return checkingTimes;
	}
	/**
	 * Diese Methode ueberprueft, ob ein Passagier zu oft nach hinten verschoben wurde.
	 * @param tour die ueberpruefende Tour
	 * @param maxSwap, der Wert, welche die maximale Anzahl an Verschiebungen innerhalb der Tour vorgibt
	 * @return
	 */
	public static boolean checkSwap(ArrayList<Stopp> tour, int maxSwap){
		boolean checkingSwap = true;

		for (Stopp stopp : tour) {
			int swapInTour = stopp.getMaxMovedPosition();
			if (swapInTour > maxSwap) {
				checkingSwap = false;
				break;
			}
		}

		return checkingSwap;
	}
	
	public static void chooseTour(ArrayList<AssignedVehicle> assignedVehicles,double endTime, int maxMovingPosition, int maxCapacity, Request request, ArrayList<Vehicle> vehicles,ArrayList<Times> waitingTimesOfCustomers,double maxWaitingTime){
		//ArrayList<AssignedVehicle> copyOfList = new ArrayList<>(assignedVehicles);
		
		for(int i=0; i < assignedVehicles.size(); i++) {
			AssignedVehicle currentElement = assignedVehicles.get(i);
			Vehicle currentVehicle = currentElement.getVehicle();
			ArrayList<Stopp> currentTour = currentElement.getTour();

			//ueberpruefe die Einhaltung der Restriktionen:
			if(!(
					checkCapacity(currentVehicle, currentTour,maxCapacity) &&
					checkReturnToTransferPoint(currentTour,endTime) &&
					checkTimesOfTour(currentTour,maxWaitingTime) &&
					checkSwap(currentTour,maxMovingPosition)
			)) {
				assignedVehicles.remove(i);
				i--;
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
			System.out.println("Das zugewaehlte Fahrzeug ist:" + " " + choosenVehicle.getId());
			for (Vehicle vehicle : vehicles) {
				int vehicleId = vehicle.getId();
				if (choosenVehicleId == vehicleId) {
					vehicle.currentTour = new ArrayList<>(choosenTour);
					actualizeWaitingTime(vehicle.currentTour, waitingTimesOfCustomers, request, maxWaitingTime);
					for (int h = 0; h < vehicle.currentTour.size(); h++) {
						Stopp stopp = vehicle.currentTour.get(h);
						System.out.println("Seine Tour lautet:" + "" + "RequestId:" + stopp.getRequestId() + " " + stopp.getStopp() + " " + "ArrivalTime:" + stopp.getArrivalTime() + " " + "Abfahrtszeit:" + stopp.getPlannedDeaparture() + " " + "LatestArrivalTime:" + stopp.getLatestArrivalTime() + " " + "Servicezeit:" + stopp.getServiceTime());
					}
				}

			}
			int requestState = 1;
			request.setPassengerState(requestState);
			
			//Fehlt noch, wenn es mehrere Objekte mit minimaler Wartezeit gibt, dann soll das Fahrzeug ausgewaehlt werden, mit der geringsten Fahrzeit.
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
	
	public static void swapPointsInTour(int waitingStrategy,int start, int end,ArrayList<Stopp> tour,Vehicle vehicle,ArrayList<AssignedVehicle> aV, double maxWaitingTime,double maxDrivingTime){
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
				calculateTimesOfTour(waitingStrategy,tour,maxWaitingTime,maxDrivingTime);
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
	
	public static void calculateTimesOfTour(int waitingStrategy,ArrayList<Stopp >tour,double maxWaitingTime, double maxDrivingTime){
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
			//Wenn es sich um den ersten Typ und um einen Pick-Up Point handelt, dann berechne die Ankunftszeit, Abfahrt und die spï¿½teste Abfahrt.
			if(requestType == 1 && stoppType == 2){
				//Wenn es sich um die Wartestrategie Drive-First handelt.
				if(waitingStrategy == 3){
					//spaetester Zeitpunkt den Kunden einzusammeln
					double servingTime = serviceTimeOfCurrentStopp + maxWaitingTime;
					if(servingTime > arrivalTimeAtCurrentStopp){
						arrivalTimeAtCurrentStopp = servingTime;
						currentStopp.setPlannedDeparture(servingTime);
						currentStopp.setLatestArrivalTime(servingTime);
					} else {
						
						currentStopp.setPlannedDeparture(arrivalTimeAtCurrentStopp);
						currentStopp.setLatestArrivalTime(arrivalTimeAtCurrentStopp);
					}
				} else{
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
			}
			//Wenn es sich um einen DropOff Punkt des ersten Typs handelt (Punkt im Bezirk, dann such nach dem Pick-Up Punkt des Kunden und setze diesen um).
			if(requestType == 1 && stoppType ==3){
				double departureTime = arrivalTimeAtCurrentStopp;
				currentStopp.setPlannedDeparture(departureTime);
				for (Stopp pickUpStopp : tour) {
					int pickUpId = pickUpStopp.getRequestId();
					if (requestId == pickUpId) {
						double departure = pickUpStopp.getPlannedDeaparture();
						double latestDeparture = departure + maxDrivingTime;
						currentStopp.setLatestArrivalTime(latestDeparture);
					}
				}
			}
			//Wenn es sich um den zweiten Typ handelt und um einen Pick-Up-Point dann, betrachte die Distanz zum Umsteigepunkt und schaue wie sich die Ankunftszeit mit der fuehsmoeglichen Ankunftszeit verhaelt.
			//Dann bestimme zuerst das Intervall der Anzahl an Drop-Off Punkte:
			if(requestType == 2 && stoppType == 2){
				if(waitingStrategy == 4){
					//Skip
				} else{
					//Position des Umsteigepunktes
					Point targetPoint = new Point(2,0);
					int countOfTypeTwoStopps = 0;
					for(int x = i;x< tour.size(); x++){
						Stopp curStopp = tour.get(x);
						int sType = curStopp.getType();
						int rType = curStopp.getRequestType();
						if(sType == 2 && rType == 2){
							countOfTypeTwoStopps = countOfTypeTwoStopps++;
						}
					}
					if(countOfTypeTwoStopps == 1){
						Stopp nextStopp = tour.get(i +1);// bei einem Intervall von 1 muesste der naechste Punkt der DropOff-Punkt sein.
						Point nextPoint = nextStopp.getStopp();
						double distancePickUpToDropOff = Simulation.calculateDistanceBetween2Points(currentPoint, nextPoint);
						double driveTimeToDropOff = Simulation.calculateDriveTimeToPoint(distancePickUpToDropOff);
						double servingTimeAtPickUp = serviceTimeOfCurrentStopp - driveTimeToDropOff;
						currentStopp.setArrivalTime(arrivalTimeAtCurrentStopp);
						currentStopp.setPlannedDeparture(servingTimeAtPickUp);
						
						} else {
							//Wenn es mehrere Typen der Anfrage zwei gibt, dann....
					}
					}
				}
			//Wenn es sich um den zweiten Typen und dem dropOff Punkt handel, dann suche den zugehoerigen Pick-Up-Point und setzte als letztmoeglich Ankunft/Abfahrt die Abfahrtszeit des Pick-Up Punkts + die maximale Fahrzeit.
			if(requestType == 2 && stoppType ==3){
				//Wenn es sich um die Kombi-Strategie handelt, dann überspringe die neue Berechnung für die Tour
				if(waitingStrategy == 4){
					//Skip
				} else {
					double departureTime = arrivalTimeAtCurrentStopp;
					currentStopp.setPlannedDeparture(departureTime);
					for (Stopp pickUpStopp : tour) {
						int pickUpId = pickUpStopp.getRequestId();
						if (requestId == pickUpId) {
							double latestDeparture =  previousDeparture + maxDrivingTime;
							currentStopp.setLatestArrivalTime(latestDeparture);
						}
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
		for (Stopp stopp : tour) {
			int stoppType = stopp.getType();
			int requestType = stopp.getRequestType();
			//Wenn es sich um PU-Punkt und requestType = 1 handelt, dann schaue dir die Differenz der ServiceZeit(Zeitpunkt des Abholens) und der tatsaechlichen Abholung an
			if (stoppType == 2 && requestType == 1) {
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				double waiting = arrival - serviceTime;
				waitingTime = waitingTime + waiting;
			}
			//Wenn es sich um einen DO Punkt handelt, schaue dir die Differenz zwischen dem fruehestmoeglichen Zeitpunkt und dem Zeitpunkt der tatsaechlichen Ankunft an.
			if (stoppType == 3 && requestType == 2) {
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
	 * Diese Methode ermittelt fuer alle Stopps einer Tour die Wartezeit des Kunden und aktualisiert diese in der Liste.
	 *
	 * @param tour, die Tour des Fahrzeuges
	 * @param waitingTimesOfCustomers Liste in der die Wartezeiten berechnet werden.
	 * @param request aktuelle Anfrage
	 */
	public static void actualizeWaitingTime(ArrayList<Stopp> tour, ArrayList<Times> waitingTimesOfCustomers,Request request,double maxWaitingTime){
		int requestId = request.getId();
		double waitingTime = 0.0;
		Times timeOfRequest = new Times(requestId, waitingTime);
		waitingTimesOfCustomers.add(timeOfRequest);

		for (Stopp stopp : tour) {
			int stoppType = stopp.getType();
			int requestType = stopp.getRequestType();
			requestId = stopp.getRequestId();
			if (stoppType == 2 && requestType == 1) {
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getPlannedDeaparture();
				waitingTime = arrival - serviceTime;
				for (Times time : waitingTimesOfCustomers) {
					int requestIdOfWaitingTimes = time.getRequestId();
					if (requestIdOfWaitingTimes == requestId) {
						time.setWaitingTime(waitingTime);
					}
				}
			}
			else if (stoppType == 3 && requestType == 2) {
				double serviceTime = stopp.getServiceTime();
				double arrival = stopp.getArrivalTime();
				double earliestArrival = serviceTime - maxWaitingTime;
				waitingTime = serviceTime - arrival ;
				for (Times time : waitingTimesOfCustomers) {
					int requestIdOfWaitingTimes = time.getRequestId();
					if(requestIdOfWaitingTimes == requestId){
						time.setWaitingTime(waitingTime);
					}
				}
			}
		}
	}
	
	
	public static boolean vehicleIsDriving(Vehicle vehicle, double currentTime){
		boolean isDriving;
		Point vehiclePosition = vehicle.getPosition();
		Stopp nextStoppOfCurrentTour = vehicle.currentTour.get(0);
		Point PointOfNextStopp = nextStoppOfCurrentTour.getStopp();
		double arrivalTimeOfStopp = nextStoppOfCurrentTour.getArrivalTime();
		double distanceToNextStopp = Simulation.calculateDistanceBetween2Points(vehiclePosition, PointOfNextStopp);
		double driveTime = Simulation.calculateDriveTimeToPoint(distanceToNextStopp);
		double time = currentTime + driveTime;
		if(time <= arrivalTimeOfStopp){
			isDriving = false;
		} else {
			isDriving = true;
		}
		return isDriving;
	}
	
	
}

