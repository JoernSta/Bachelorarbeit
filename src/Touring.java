import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse regelt das Touring, falls keine Anfrage zu zugewiesen werden muss oder ruft die Zuweisungd der Fahrzeuge auf.
 * @author JS
 *
 */
public class Touring {
	
	public static void touringWithWaitAtCurrentPosition(double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		Simulation.setCurrentTime(startTime);
		double currentTime = Simulation.getCurrentTime();
		for(int i = 0; i < requests.size(); i++){
			Request request = requests.get(i);
			double requestTime = request.getRequestTime();
			if(requestTime > currentTime) {
				for (Vehicle vehicle : vehicles) {
					int tourSize = vehicle.currentTour.size();
					Point positionOfVehicle = vehicle.getPosition();
					if (tourSize == 0) {
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						System.out.println("Fahrzeug wartet an der Position:" + positionOfVehicle);
					} else {
						for (int h = 0; h < vehicle.currentTour.size(); h++) {
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							double arrivalTime = currentStopp.getPlannedDeaparture();
							if (arrivalTime > currentTime && arrivalTime <= requestTime) {
								vehicle.setPosition(pointOfCurrentStopp);
								int stoppType = currentStopp.getType();
								if (stoppType == 2) {
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int usedCapacityOfVehicle = vehicle.getUsedCap();
									if(updatedCapacity > usedCapacityOfVehicle){
										vehicle.setUsedCap(updatedCapacity);
									}
									int requestIdOfStopp = currentStopp.getRequestId();

									for (Request requestOfList : requests) {
										int id = requestOfList.getId();
										if (id == requestIdOfStopp) {
											requestOfList.setPassengerState(2);
											vehicle.currentTour.remove(h);
											h = h - 1;
											break;
										}
									}
									Simulation.setCurrentTime(arrivalTime);
								} else if (stoppType == 3) {
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									vehicle.currentTour.remove(h);
									h = h - 1;


									Simulation.setCurrentTime(arrivalTime);
								} else if (arrivalTime < currentTime && arrivalTime <= requestTime) {
									vehicle.setPosition(pointOfCurrentStopp);
									stoppType = currentStopp.getType();
									if (stoppType == 2) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity + passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										int requestIdOfStopp = currentStopp.getRequestId();

										for (Request requestOfList : requests) {
											int id = requestOfList.getId();
											if (id == requestIdOfStopp) {
												requestOfList.setPassengerState(2);
												vehicle.currentTour.remove(h);
												h = h - 1;
												break;
											}
										}
									} else if (stoppType == 3) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										vehicle.currentTour.remove(h);
										h = h - 1;
									}
								}
							}
						}
					}
					Simulation.setCurrentTime(requestTime);
				}
				
			}
			Assignment.requestAssigment(request, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, endTime,waitingTimesOfCustomers);
		}
		finishTours(vehicles,requests);
	}
	
	/**
	 * Diese Methode bestimmt das Fahrzeugverhalten bei der Center-Of-Gravity-Strategie
	 * @param startTime startzeit des Betriebshorizonts
	 * @param endTime endZeit des Betriebshorizonts
	 * @param requests alle betrachteten Anfragen
	 * @param vehicles alle eingesetzten Fahrzeuge
	 * @param maxWaitingTime die maximal erlaubte Wartezeit
	 * @param maxDrivingTime maximale Fahrzeit der Fahrgäste
	 * @param maxCapacity maximale Kapazität der Fahrzeuge
	 * @param maxMovingPosition maximal nach hinten verschobene Personen (noch nicht implemnetiert)
	 * @param waitingTimesOfCustomers Wartezeiten der einzelnen Kunden
	 * @param points Punkte des Servicegebiets
	 * @param transferPoint Umsteigepunkt
	 */
	public static void touringWithCenterOfGravity(double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers,ArrayList<Point> points,Point transferPoint){

		
		Simulation.setCurrentTime(startTime);
		double currentTime = Simulation.getCurrentTime();
		//Gehe Liste der Anfragen durch
		for(int i = 0; i < requests.size(); i++){
			Request request = requests.get(i);
			double requestTime = request.getRequestTime();
			if(requestTime > currentTime) {
				for (Vehicle vehicle : vehicles) {
					int tourSize = vehicle.currentTour.size();
					printTour(vehicle);
					System.out.println("Fahrzeug:" + vehicle.getId() + "Zu Beginn des Tourings." + tourSize);
					Point positionOfVehicle = vehicle.getPosition();
					//Wenn das Fahrzeug eine leere Liste hat, so wird ein Wartepunkt berechnet, an welcher Stelle das Fahrzeug warten soll.
					if (tourSize == 0) {
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						ArrayList<Point> reachablePoints = waitingStrategies.calculateReachablePoints(points, vehicle, currentTime, endTime, transferPoint);
						//Den nächsten Punkt aus allen erreichbaren Punkten bestimmen.
						Point waitingPoint = waitingStrategies.calculateCenterOfGravity(reachablePoints);
						double distanceToWaitingPoint = Simulation.calculateDistanceBetween2Points(vehicle.getPosition(), waitingPoint);
						double driveTimeToWaitingPoint = Simulation.calculateDriveTimeToPoint(distanceToWaitingPoint);
						double arrivalTimeToWaitingPoint = currentTime + driveTimeToWaitingPoint;
						if (arrivalTimeToWaitingPoint <= requestTime) {
							vehicle.setPosition(waitingPoint);
							Simulation.setCurrentTime(arrivalTimeToWaitingPoint);
							currentTime = Simulation.getCurrentTime();
							System.out.println("Center-Of-Gravity ist:" + waitingPoint);
						} else {
							Stopp stopp = new Stopp(0, waitingPoint, arrivalTimeToWaitingPoint, 1, arrivalTimeToWaitingPoint, arrivalTimeToWaitingPoint, 0, 0, 0.0, 0);
							vehicle.currentTour.add(stopp);
						}
						// hat das Fahrzeug eine Tour, so soll die Tour solange bearbeitet werden, bis die neue Anfrage eintrifft.
					} else {
						for (int h = 0; h < vehicle.currentTour.size(); h++){
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							int stoppType = currentStopp.getType();
							if(stoppType == 1 && vehicle.currentTour.size()>1){
								Stopp nextStopp = vehicle.currentTour.get(h+1);
								double servingTimeOfNextStopp = nextStopp.getPlannedDeaparture();
								Point pointOfNextStopp = nextStopp.getStopp();
								double distanceToNextPoint = Simulation.calculateDistanceBetween2Points(pointOfCurrentStopp, pointOfNextStopp);
								double driveTimeToNextStopp = Simulation.calculateDriveTimeToPoint(distanceToNextPoint);
								double departureFromWaitingPointToNextPoint = servingTimeOfNextStopp - driveTimeToNextStopp;
								currentStopp.setPlannedDeparture(departureFromWaitingPointToNextPoint);
								currentStopp.setLatestArrivalTime(departureFromWaitingPointToNextPoint);
								currentStopp.setServiceTime(departureFromWaitingPointToNextPoint);
								double arrivalTimeAtNextStop = departureFromWaitingPointToNextPoint + driveTimeToNextStopp;
								nextStopp.setArrivalTime(arrivalTimeAtNextStop);
								//
							
								//Berechne Fahrt zu Punkt, so dass wir die späteste Abfahrt und die Servicezeit zu dem nächsten Punkt haben.
						
							}
							double servingTime = currentStopp.getPlannedDeaparture();
							double arrivalTime = currentStopp.getArrivalTime();
							//Wir fügen einen Warteort ein, wenn wir ein Zeitpuffer haben, dieser ergibt sich aus der Ankunftszeit des Knoten und Zeit der Kundenbedienung(servingTime)
							
							if(servingTime > arrivalTime && stoppType != 1 && arrivalTime <= requestTime){
								printTour(vehicle);
								System.out.println("Wir haben ein Zeitpuffer, Fahrzeug bewegt sich zum Center-Of-Gravity");
								ArrayList<Point> reachablePoints = waitingStrategies.calculateReachablePoints(points, vehicle, currentTime, endTime, pointOfCurrentStopp);
								Point waitingPoint;
								if(reachablePoints.size() == 0){
									 waitingPoint = pointOfCurrentStopp;
								} else {
									waitingPoint = waitingStrategies.calculateCenterOfGravity(reachablePoints);
								}
								Stopp waitingStopp = createStoppPoint(vehicle, currentTime,waitingPoint);
								vehicle.currentTour.add(h,waitingStopp);
								System.out.println("Center-Of-Gravity liegt bei:" + waitingPoint);
								h = h +1;
								double arrivalTimeOfWaitingPoint = waitingStopp.getArrivalTime();
								double departureTimeFromWaitingPoint = waitingStopp.getPlannedDeaparture();
								if(arrivalTimeOfWaitingPoint <= requestTime){
									Simulation.setCurrentTime(arrivalTimeOfWaitingPoint);
									vehicle.setPosition(waitingPoint);
									if(departureTimeFromWaitingPoint <= requestTime){
										Simulation.setCurrentTime(departureTimeFromWaitingPoint);
										vehicle.currentTour.remove(waitingStopp);
										//Wartepunkt muss gelöscht werden, bei h-1 und
										//Der Punkt muss gelöscht werden.
										h = -1;
									
									}
								}
								printTour(vehicle);
								System.out.println("Tourengröße:" + vehicle.currentTour.size());
							}
				
							if (servingTime >= currentTime && servingTime <= requestTime) {
								vehicle.setPosition(pointOfCurrentStopp);
								Simulation.setCurrentTime(servingTime);
								currentTime = Simulation.getCurrentTime();
								if (stoppType == 2) {
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int maxUsedCapacity = vehicle.getUsedCap();
									if(maxUsedCapacity < updatedCapacity){
										vehicle.setUsedCap(updatedCapacity);
									}
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
									for (Request requestOfList : requests) {
										int id = requestOfList.getId();
										if (id == requestIdOfStopp) {
											requestOfList.setPassengerState(2);
											break searchRequests;
										}
									}
									vehicle.currentTour.remove(currentStopp);
									h = -1;
								} else if (stoppType == 3) {
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									vehicle.currentTour.remove(currentStopp);
									h = -1;
								} else if (stoppType == 1) {
									vehicle.currentTour.remove(currentStopp);
									h = -1;
								}
								} else if (arrivalTime < currentTime && arrivalTime <= requestTime) {
									vehicle.setPosition(pointOfCurrentStopp);
									if (stoppType == 2) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity + passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										int maxUsedCapacity = vehicle.getUsedCap();
										if(maxUsedCapacity < updatedCapacity){
											vehicle.setUsedCap(updatedCapacity);
										}
										int requestIdOfStopp = currentStopp.getRequestId();
										searchRequest:
										for (Request requestOfList : requests) {
											int id = requestOfList.getId();
											if (id == requestIdOfStopp) {
												requestOfList.setPassengerState(2);
												break searchRequest;
											}
										}
										vehicle.currentTour.remove(currentStopp);
										h =-1;
									} else if (stoppType == 3) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										vehicle.currentTour.remove(currentStopp);
										h = -1;
									} else if (stoppType == 1) {
										vehicle.currentTour.remove(currentStopp);
										h = -1;
									}
								}
							}
						}
					}
				}
			Simulation.setCurrentTime(requestTime);
			currentTime = Simulation.getCurrentTime();
			Assignment.requestAssigment(request, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, endTime,waitingTimesOfCustomers);
			}
		finishTours(vehicles,requests);
		}
	

	
	public static void touringWithDriveFirstWaitStrategy(double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers){
		Simulation.setCurrentTime(startTime);
		double currentTime = Simulation.getCurrentTime();
		for(int i = 0; i < requests.size(); i++){
			Request request = requests.get(i);
			double requestTime = request.getRequestTime();
			if(requestTime > currentTime) {
				for (Vehicle vehicle : vehicles) {
					int tourSize = vehicle.currentTour.size();
					Point positionOfVehicle = vehicle.getPosition();
					if (tourSize == 0) {
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						System.out.println("Fahrzeug wartet an der Position:" + positionOfVehicle);
					} else {
						for (int h = 0; h < vehicle.currentTour.size(); h++) {
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							double servingTime = currentStopp.getPlannedDeaparture();
							double arrivalTime = currentStopp.getArrivalTime();
							if (arrivalTime > currentTime && arrivalTime <= requestTime) {
								vehicle.setPosition(pointOfCurrentStopp);
								Simulation.setCurrentTime(arrivalTime);
								currentTime = Simulation.getCurrentTime();
								if (servingTime >= arrivalTime && servingTime <= requestTime) {
									int stoppType = currentStopp.getType();
									if (stoppType == 2) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity + passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										int maxUsedCapacity = vehicle.getUsedCap();
										if(maxUsedCapacity < updatedCapacity){
											vehicle.setUsedCap(updatedCapacity);
										}
										int requestIdOfStopp = currentStopp.getRequestId();
										
										searchRequest: for (Request requestOfList : requests) {
											int id = requestOfList.getId();
											if (id == requestIdOfStopp) {
												requestOfList.setPassengerState(2);
												break searchRequest;
											}
										}
										vehicle.currentTour.remove(h);
										h = h - 1;
										Simulation.setCurrentTime(servingTime);
									} else if (stoppType == 3) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										vehicle.currentTour.remove(h);
										h = h - 1;
									}
									Simulation.setCurrentTime(servingTime);
								}
								} else if (arrivalTime <= currentTime && arrivalTime <= requestTime) {
									vehicle.setPosition(pointOfCurrentStopp);
									if(servingTime >= arrivalTime && servingTime <= requestTime){
										int stoppType = currentStopp.getType();
										if (stoppType == 2) {
											int passengersOfStopp = currentStopp.getPassengers();
											int currentCapacity = vehicle.getCapacity();
											int updatedCapacity = currentCapacity + passengersOfStopp;
											vehicle.setCapacity(updatedCapacity);
											int maxUsedCapacity = vehicle.getUsedCap();
											if(maxUsedCapacity < updatedCapacity){
												vehicle.setUsedCap(updatedCapacity);
											}
											int requestIdOfStopp = currentStopp.getRequestId();
											for (Request requestOfList : requests) {
												int id = requestOfList.getId();
												if (id == requestIdOfStopp) {
													requestOfList.setPassengerState(2);
													vehicle.currentTour.remove(h);
													h = h - 1;
													break;
												}
											}
										} else if (stoppType == 3) {
											int passengersOfStopp = currentStopp.getPassengers();
											int currentCapacity = vehicle.getCapacity();
											int updatedCapacity = currentCapacity - passengersOfStopp;
											vehicle.setCapacity(updatedCapacity);
											vehicle.currentTour.remove(h);
											h = h - 1;
										}
									}
									
								}
							}
						}
					}
					Simulation.setCurrentTime(requestTime);
					Assignment.requestAssigment(request, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, endTime,waitingTimesOfCustomers);
				}
			}
			finishTours(vehicles,requests);
	}
	
	
		
	
	
	public static void finishTours(ArrayList<Vehicle> vehicles, ArrayList<Request> requests){
		for (Vehicle vehicle : vehicles) {
			for (int j = 0; j < vehicle.currentTour.size(); j++) {
				Stopp stopp = vehicle.currentTour.get(j);
				int stoppType = stopp.getType();
				int requestId = stopp.getRequestId();
				if (stoppType == 2) {
					int passengersOfStopp = stopp.getPassengers();
					int currentCapacity = vehicle.getCapacity();
					int updatedCapacity = currentCapacity + passengersOfStopp;
					vehicle.setCapacity(updatedCapacity);

					for (Request requestOfList : requests) {
						int id = requestOfList.getId();
						if (id == requestId) {
							requestOfList.setPassengerState(2);
							break;
						}
					}
				} else if (stoppType == 3) {
					int passengersOfStopp = stopp.getPassengers();
					int currentCapacity = vehicle.getCapacity();
					int updatedCapacity = currentCapacity - passengersOfStopp;
					vehicle.setCapacity(updatedCapacity);

					for (Request requestOfList : requests) {
						int id = requestOfList.getId();
						if (id == requestId) {
							requestOfList.setPassengerState(2);
							break;
						}
					}
				}
			}
		}
	}
	/**
	 * Diese Methode setzt die Ankunftszeit,Abfahrtszeit, und die latestAbfahrtszeit. CenterOfGravity
	 * Dabei wird über die FahrzeugTour iteriert und wenn wir einen Wartpunkt finden, dann erstellen wir ausgehend von unserer aktuellen Position des Fahrzeugs die Ankunftszeiten
	 * und die Abfahrtszeit entspricht der Zeit, wann das Fahrzeug abfahren muss, um beim nächsten Kunden pünktlich zu sein.
	 * @param vehicle
	 * @param currentTime
	 * @param p Punkt der übergeben wird
	 */
	public static Stopp createStoppPoint(Vehicle vehicle,double currentTime, Point p){
		Stopp waitingStopp = new Stopp(0,p,0.0, 1,0.0,  0.0, 0, 0, 0, 0);
		Point stoppPoint = p;
		Point vehiclePosition = vehicle.getPosition();
		double distanceTimeToWaitingPoint = Simulation.calculateDistanceBetween2Points(vehiclePosition, stoppPoint);
		double driveTime = Simulation.calculateDriveTimeToPoint(distanceTimeToWaitingPoint);
		double arrivalTime = currentTime + driveTime;
		Stopp nextStopp = vehicle.currentTour.get(0);
		Point nextPoint = nextStopp.getStopp();
		double servingTimeFromNextStopp = nextStopp.getPlannedDeaparture();
		double distanceFromstoppPointToNextPoint = Simulation.calculateDistanceBetween2Points(stoppPoint, nextPoint);
		double driveTimeFromStoppPointToNextPoint = Simulation.calculateDriveTimeToPoint(distanceFromstoppPointToNextPoint);
		double departureTime = servingTimeFromNextStopp - driveTimeFromStoppPointToNextPoint;
		waitingStopp.setArrivalTime(arrivalTime);
		waitingStopp.setPlannedDeparture(departureTime);
		waitingStopp.setLatestArrivalTime(departureTime);
		nextStopp.setArrivalTime((departureTime + driveTimeFromStoppPointToNextPoint));
		
			
			
		
		return waitingStopp;
	}
	
	//Testmethode:
	public static void printTour(Vehicle vehicle){
		for(int i = 0; i < vehicle.currentTour.size();i++){
			Stopp stopp = vehicle.currentTour.get(i);
			int id = stopp.getRequestId();
			Point point = stopp.getStopp();
			double arrival = stopp.getArrivalTime();
			double departure = stopp.getPlannedDeaparture();
			System.out.println("FahrzuegId:" + vehicle.getId() + " " + "Tour:" + " " + id + " " + point + " " + arrival + " " + departure);
		}
	}
	
}
