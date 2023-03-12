import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse regelt das Touring, falls keine Anfrage zu zugewiesen werden muss oder ruft die Zuweisung der Fahrzeuge auf.
 * @author JS
 *
 */
public class Touring {
	
	
	
	/**
	 * Diese Methode bestimmt das Fahrzeugverhalten bei der Center-Of-Gravity-Strategie
	 * @param startTime startzeit des Betriebshorizonts
	 * @param endTime endZeit des Betriebshorizonts
	 * @param requests alle betrachteten Anfragen
	 * @param vehicles alle eingesetzten Fahrzeuge
	 * @param maxWaitingTime die maximal erlaubte Wartezeit
	 * @param maxDrivingTime maximale Fahrzeit der Fahrgäste
	 * @param maxCapacity maximale Kapazität der Fahrzeuge
	 * @param maxMovingPosition maximal nach hinten verschobene Personen (noch nicht implementiert)
	 * @param waitingTimesOfCustomers Wartezeiten der einzelnen Kunden
	 * @param points Punkte des Servicegebiets
	 * @param transferPoint Umsteigepunkt
	 */
	public static void touringWithCenterOfGravity(int waitingStrategy, double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers,ArrayList<Point> points,Point transferPoint,ArrayList<DriveTimesOfCustomers> driveTimes){

		
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
						//Wenn das Fahrzeug den Wartepunkt vor dem Eintreffen der nächsten Anfrage erreicht, dann setze die Position des Fahrzeuges um.
						if (arrivalTimeToWaitingPoint <= requestTime) {
							vehicle.setPosition(waitingPoint);
							Simulation.setCurrentTime(arrivalTimeToWaitingPoint);
							currentTime = Simulation.getCurrentTime();
							//System.out.println("Center-Of-Gravity ist:" + waitingPoint);
						} else {
							Stopp stopp = new Stopp(0, waitingPoint, arrivalTimeToWaitingPoint, 1, arrivalTimeToWaitingPoint, arrivalTimeToWaitingPoint, 0, 0, arrivalTimeToWaitingPoint, 0);
							vehicle.currentTour.add(stopp);
						}
						// hat das Fahrzeug eine Tour, so soll die Tour so lange bearbeitet werden, bis die neue Anfrage eintrifft.
					} else {
						for (int h = 0; h < vehicle.currentTour.size(); h++){
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							int stoppType = currentStopp.getType();
							//Wenn wir einen Wartestopp in der Liste haben, und weitere Punkte in unserer Tour, dann berechne die Abfahrtszeit von diesem Punkt.
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
							}
							
							double servingTime = currentStopp.getPlannedDeaparture();
							double arrivalTime = currentStopp.getArrivalTime();
							//Wir fügen einen Warteort ein, wenn wir einen Zeitpuffer haben, dieser ergibt sich aus der Ankunftszeit des Knotens und Zeit der Kundenbedienung(servingTime)
							
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
								h = -1;
								double arrivalTimeOfWaitingPoint = waitingStopp.getArrivalTime();
								double departureTimeFromWaitingPoint = waitingStopp.getPlannedDeaparture();
								if(arrivalTimeOfWaitingPoint <= requestTime){
									Simulation.setCurrentTime(arrivalTimeOfWaitingPoint);
									vehicle.setPosition(waitingPoint);
									if(departureTimeFromWaitingPoint <= requestTime){
										Simulation.setCurrentTime(departureTimeFromWaitingPoint);
										vehicle.currentTour.remove(waitingStopp);
										// Wartepunkt muss gelöscht werden, bei h-1 und
										// der Punkt muss gelöscht werden.
										h = -1;
									
									}
								}
								printTour(vehicle);
								System.out.println("Tourengroeße:" + vehicle.currentTour.size());
							}
				  
							else if (servingTime >= currentTime && servingTime <= requestTime) {
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
									//Fuege die Pick-Up Zeit des Kunden in die Liste der Fahrzeiten ein.
									double pickUpTime = servingTime;
									DriveTimesOfCustomers driveTimeOfCustomer = new DriveTimesOfCustomers(requestIdOfStopp,pickUpTime,0.0);
									driveTimes.add(driveTimeOfCustomer);
									//Loesche den Pick-Up-Punkt aus der aktuellen Tour
									vehicle.currentTour.remove(currentStopp);
									h = -1;
								} else if (stoppType == 3) {
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									// hier die DropOfftime einfuegen
									vehicle.currentTour.remove(currentStopp);
									h = -1;
									//Setze die Drop-Off Zeit fuer den Kunden neu.
									// Gehe Liste der DriveTimes durch und setze die dropOff Zeit für die entsprechende Anfrage neu.
									searchDriveTime: for(DriveTimesOfCustomers driveTime : driveTimes){
										int idOfRequest = currentStopp.getRequestId();
										int id = driveTime.getRequestId();
										double dropOffTime = servingTime;
										if(idOfRequest == id){
											driveTime.setDropOffTime(dropOffTime);
											break searchDriveTime;
										}
									}
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
										//Fuege die Pick-Up Zeit des Kunden in die Liste der Fahrzeiten ein.
										double pickUpTime = servingTime;
										DriveTimesOfCustomers driveTimeOfCustomer = new DriveTimesOfCustomers(requestIdOfStopp,pickUpTime,0.0);
										driveTimes.add(driveTimeOfCustomer);
										//Loesche den Pick-Up-Punkt aus der aktuellen Tour
										vehicle.currentTour.remove(currentStopp);
										h =-1;
									} else if (stoppType == 3) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										//Setze die Drop-Off Zeit fuer den Kunden neu.
										// Gehe Liste der DriveTimes durch und setze die dropOff Zeit für die entsprechende Anfrage neu.
										searchDriveTime: for(DriveTimesOfCustomers driveTime : driveTimes){
											int idOfRequest = currentStopp.getRequestId();
											int id = driveTime.getRequestId();
											double dropOffTime = servingTime;
											if(idOfRequest == id){
												driveTime.setDropOffTime(dropOffTime);
												break searchDriveTime;
											}
										}
										vehicle.currentTour.remove(currentStopp);
										h = -1;
									}// Ergibt das Sinn, wenn wir nur ankommen und nicht abfahren, dass wir den loeschen? 
									else if (stoppType == 1) {
										vehicle.setPosition(currentStopp.getStopp());
									}
								}
							}
						}
					}
				}
			Simulation.setCurrentTime(requestTime);
			currentTime = Simulation.getCurrentTime();
			Assignment.requestAssigment(waitingStrategy,request, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, endTime,waitingTimesOfCustomers);
			}
		finishTours(vehicles,requests,driveTimes);
		}
	

	
	public static void touringWithDriveFirstWaitStrategy(int waitingStrategy,double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers,ArrayList<DriveTimesOfCustomers> driveTimes){
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
											if(vehicle.getUsedCap() > 6){
												System.out.println("..............................................Bei dieser Anfrage erhoeht er auf ueber 6:" + " " + currentStopp.getRequestId());
												
											}
										}
										int requestIdOfStopp = currentStopp.getRequestId();
										//Setze den Status der Anfrage auf 2 = bedient.
										searchRequest: for (Request requestOfList : requests) {
											int id = requestOfList.getId();
											if (id == requestIdOfStopp) {
												requestOfList.setPassengerState(2);
												break searchRequest;
											}
										}
										//Fuege die Pick-Up Zeit des Kunden in die Liste der Fahrzeiten ein.
										
										double pickUpTime = servingTime;
										DriveTimesOfCustomers driveTimeOfCustomer = new DriveTimesOfCustomers(requestIdOfStopp,pickUpTime,0.0);
										driveTimes.add(driveTimeOfCustomer);
										//Loesche den Pick-Up-Punkt aus der aktuellen Tour
										vehicle.currentTour.remove(h);
										h = h - 1;
										//Setze die aktuelle Zeit auf die Bedienungszeit
										Simulation.setCurrentTime(servingTime);
									} else if (stoppType == 3) {
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										
										//Setze die Drop-Off Zeit fuer den Kunden neu.
										// Gehe Liste der DriveTimes durch und setze die dropOff Zeit für die entsprechende Anfrage neu.
										searchDriveTime: for(DriveTimesOfCustomers driveTime : driveTimes){
											int idOfRequest = currentStopp.getRequestId();
											int id = driveTime.getRequestId();
											double dropOffTime = servingTime;
											if(idOfRequest == id){
												driveTime.setDropOffTime(dropOffTime);
												break searchDriveTime;
											}
										}
										vehicle.currentTour.remove(h);
										h = h - 1;
									}
									Simulation.setCurrentTime(servingTime);
								}
								//Diese else-If Abfrage dient dazu, falls ein weiteres Fahrzeug auch eine Tour zu bedienen hat und die arrivalTime allerdings kleiner als die aktuelle Zeit,
								//welche ggf. vorher hoch gesetzt wurde niedriger ist
								//Dann soll das Fahrzeug seine Tour auch noch bedienen.
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
											searchRequest: for (Request requestOfList : requests) {
												int id = requestOfList.getId();
												if (id == requestIdOfStopp) {
													requestOfList.setPassengerState(2);
													break searchRequest;
												}
											}
											//Fuege die Pick-Up Zeit des Kunden in die Liste der Fahrzeiten ein.
											int idOfPickedUpRequest = currentStopp.getRequestId();
											double pickUpTime = servingTime;
											DriveTimesOfCustomers driveTimeOfCustomer = new DriveTimesOfCustomers(idOfPickedUpRequest,pickUpTime,0.0);
											driveTimes.add(driveTimeOfCustomer);
											
											vehicle.currentTour.remove(h);
											h = h - 1;
										} else if (stoppType == 3) {
											int passengersOfStopp = currentStopp.getPassengers();
											int currentCapacity = vehicle.getCapacity();
											int updatedCapacity = currentCapacity - passengersOfStopp;
											vehicle.setCapacity(updatedCapacity);
											//Setze die Drop-Off Zeit fuer den Kunden neu.
											// Gehe Liste der DriveTimes durch und setze die dropOff Zeit für die entsprechende Anfrage neu.
											searchDriveTime: for(DriveTimesOfCustomers driveTime : driveTimes){
												int idOfRequest = currentStopp.getRequestId();
												int id = driveTime.getRequestId();
												double dropOffTime = servingTime;
												if(idOfRequest == id){
													driveTime.setDropOffTime(dropOffTime);
													break searchDriveTime;
												}
											}
											vehicle.currentTour.remove(h);
											h = h - 1;
										}
									}
									
								}
							}
						}
					}
					Simulation.setCurrentTime(requestTime);
					Assignment.requestAssigment(waitingStrategy,request, vehicles, maxWaitingTime, maxDrivingTime, maxCapacity, maxMovingPosition, endTime,waitingTimesOfCustomers);
				}
			}
			finishTours(vehicles,requests, driveTimes);
	}
	
	
		
	
	/**
	 * Methode, welche die Tour nach dem Eingnag der letzten Anfrage abfaehrt und die Fahrzeiten nochmal anpasst.
	 * @param vehicles Anzahl der eingesetzten Fahrzeuge
	 * @param requests Anzahl der Auftraege
	 * @param driveTimes Liste mit den Fahrzeiten.
	 */
	public static void finishTours(ArrayList<Vehicle> vehicles, ArrayList<Request> requests,ArrayList<DriveTimesOfCustomers> driveTimes){
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
					searchPassengers: for (Request requestOfList : requests) {
						int id = requestOfList.getId();
						if (id == requestId) {
							requestOfList.setPassengerState(2);
							break searchPassengers;
						}
					}
					int idOfPickedUpRequest = stopp.getRequestId();
					double pickUpTime = stopp.getPlannedDeaparture();
					DriveTimesOfCustomers driveTimeOfCustomer = new DriveTimesOfCustomers(idOfPickedUpRequest,pickUpTime,0.0);
					driveTimes.add(driveTimeOfCustomer);
					
				} else if (stoppType == 3) {
					int passengersOfStopp = stopp.getPassengers();
					int currentCapacity = vehicle.getCapacity();
					int updatedCapacity = currentCapacity - passengersOfStopp;
					vehicle.setCapacity(updatedCapacity);
					searchDriveTime: for(DriveTimesOfCustomers driveTime : driveTimes){
						int idOfRequest = stopp.getRequestId();
						int id = driveTime.getRequestId();
						double dropOffTime = stopp.getPlannedDeaparture();
						if(idOfRequest == id){
							driveTime.setDropOffTime(dropOffTime);
							break searchDriveTime;
						}
					}

				}
			}
		}
	}
	/**
	 * Diese Methode setzt die Ankunftszeit, Abfahrtszeit und die latestAbfahrtszeit. CenterOfGravity.
	 * Dabei wird über die FahrzeugTour iteriert und wenn wir einen Wartepunkt finden, dann erstellen wir ausgehend von unserer aktuellen Position des Fahrzeugs die Ankunftszeiten
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
		double distanceFromStoppPointToNextPoint = Simulation.calculateDistanceBetween2Points(stoppPoint, nextPoint);
		double driveTimeFromStoppPointToNextPoint = Simulation.calculateDriveTimeToPoint(distanceFromStoppPointToNextPoint);
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
			System.out.println("FahrzeugId:" + vehicle.getId() + " " + "Tour:" + " " + id + " " + point + " " + arrival + " " + departure);
		}
	}
	
}
