import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Diese Klasse regelt das Touring, falls keine Anfrage zu zuweisen ist
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
				for(int j = 0; j < vehicles.size(); j++){
					Vehicle vehicle = vehicles.get(j);
					int tourSize = vehicle.currentTour.size();
					Point positionOfVehicle = vehicle.getPosition();
					if(tourSize == 0){
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						System.out.println("Fahrzeug wartet an der Position:" + positionOfVehicle);
					} else {
						for(int h = 0; h< vehicle.currentTour.size();h++){
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							double arrivalTime = currentStopp.getPlannedDeaparture();
							if(arrivalTime > currentTime && arrivalTime <= requestTime){
								vehicle.setPosition(pointOfCurrentStopp);
								int stoppType = currentStopp.getType();
								if(stoppType == 2){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int usedCapOfVehicle = vehicle.getUsedCap();
									if(updatedCapacity > usedCapacityOfVehicle){
										vehicle.setUsedCap(updatedCapacity);
									}
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
									for(int x =0; x < requests.size(); x++){
										Request requestOfList = requests.get(x);
										int id = requestOfList.getId();
										if(id == requestIdOfStopp){
											requestOfList.setPassengerState(2);
											vehicle.currentTour.remove(h);
											h = h - 1;
											break searchRequests;
										}
									}
									Simulation.setCurrentTime(arrivalTime);
								} else if(stoppType == 3){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									vehicle.currentTour.remove(h);
									h= h -1;
									
								
								Simulation.setCurrentTime(arrivalTime);
							} else if(arrivalTime < currentTime && arrivalTime <= requestTime){
								vehicle.setPosition(pointOfCurrentStopp);
								stoppType = currentStopp.getType();
								if(stoppType == 2){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
										for(int x =0; x < requests.size(); x++){
											Request requestOfList = requests.get(x);
											int id = requestOfList.getId();
											if(id == requestIdOfStopp){
												requestOfList.setPassengerState(2);
												vehicle.currentTour.remove(h);
												h = h - 1;
												break searchRequests;
											}
								}
								} else if(stoppType == 3){
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
	
	
	public static void touringWithCenterOfGravity(double startTime, double endTime,  ArrayList<Request> requests, ArrayList<Vehicle> vehicles,double maxWaitingTime,double maxDrivingTime,int maxCapacity,int maxMovingPosition,ArrayList<Times> waitingTimesOfCustomers,ArrayList<Point> points,Point transferPoint){
		Simulation.setCurrentTime(startTime);
		double currentTime = Simulation.getCurrentTime();
		for(int i = 0; i < requests.size(); i++){
			Request request = requests.get(i);
			double requestTime = request.getRequestTime();
			if(requestTime > currentTime) {
				for(int j = 0; j < vehicles.size(); j++){
					Vehicle vehicle = vehicles.get(j);
					int tourSize = vehicle.currentTour.size();
					Point positionOfVehicle = vehicle.getPosition();
					if(tourSize == 0){
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						System.out.println("Berechne Center-Of-Gravity");
						ArrayList<Point> reachablePoints = waitingStrategies.calculateReachablePoints(points, vehicle, currentTime, endTime, transferPoint);
						Point waitingPoint = waitingStrategies.calculateCenterOfGravity(reachablePoints);
						double distanceToWaitingPoint = Simulation.calculateDistanceBetween2Points(vehicle.getPosition(),waitingPoint);
						double driveTimeToWaitingPoint = Simulation.calculateDriveTimeToPoint(distanceToWaitingPoint);
						double arrivalTime = currentTime + driveTimeToWaitingPoint;
						if(arrivalTime < requestTime){
							vehicle.setPosition(waitingPoint);
							Simulation.setCurrentTime(arrivalTime);
							currentTime = Simulation.getCurrentTime();
							System.out.println("Center-Of-Gravity ist:" + waitingPoint);
						} else{
							Stopp stopp = new Stopp(0,waitingPoint,arrivalTime,1,arrivalTime,arrivalTime,0,0,0.0,0);
							vehicle.currentTour.add(stopp);
						}
					} else {
						for(int h = 0; h< vehicle.currentTour.size();h++){
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							double arrivalTime = currentStopp.getPlannedDeaparture();
							if(arrivalTime > currentTime && arrivalTime <= requestTime){
								vehicle.setPosition(pointOfCurrentStopp);
								int stoppType = currentStopp.getType();
								if(stoppType == 2){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
									for(int x =0; x < requests.size(); x++){
										Request requestOfList = requests.get(x);
										int id = requestOfList.getId();
										if(id == requestIdOfStopp){
											requestOfList.setPassengerState(2);
											vehicle.currentTour.remove(h);
											h = h - 1;
											break searchRequests;
										}
									}
									Simulation.setCurrentTime(arrivalTime);
								} else if(stoppType == 3){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									vehicle.currentTour.remove(h);
									h = h - 1;
								Simulation.setCurrentTime(arrivalTime);
							} else if(stoppType == 1){
								vehicle.currentTour.remove(h);
								h = h - 1;
								Simulation.setCurrentTime(arrivalTime);
							}
							else if(arrivalTime < currentTime && arrivalTime <= requestTime){
								vehicle.setPosition(pointOfCurrentStopp);
								stoppType = currentStopp.getType();
								if(stoppType == 2){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
										for(int x =0; x < requests.size(); x++){
											Request requestOfList = requests.get(x);
											int id = requestOfList.getId();
											if(id == requestIdOfStopp){
												requestOfList.setPassengerState(2);
												vehicle.currentTour.remove(h);
												h = h - 1;
												break searchRequests;
											}
								}
								} else if(stoppType == 3){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity - passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									vehicle.currentTour.remove(h);
									h = h - 1;
								} else if(stoppType == 1){
									vehicle.currentTour.remove(h);
									h = h - 1;
								}
							}
						}
					}
				}
				Simulation.setCurrentTime(requestTime);
				currentTime = Simulation.getCurrentTime();
			}	
			}
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
				for(int j = 0; j < vehicles.size(); j++){
					Vehicle vehicle = vehicles.get(j);
					int tourSize = vehicle.currentTour.size();
					Point positionOfVehicle = vehicle.getPosition();
					if(tourSize == 0){
						System.out.println("Fahrzeug:" + vehicle.getId() + " " + "hat keine aktuelle Tour");
						System.out.println("Fahrzeug wartet an der Position:" + positionOfVehicle);
					} else {
						for(int h = 0; h< vehicle.currentTour.size();h++){
							Stopp currentStopp = vehicle.currentTour.get(h);
							Point pointOfCurrentStopp = currentStopp.getStopp();
							double servingTime = currentStopp.getPlannedDeaparture();
							double arrivalTime = currentStopp.getArrivalTime();
							if(arrivalTime > currentTime && arrivalTime <= requestTime){
								vehicle.setPosition(pointOfCurrentStopp);
								Simulation.setCurrentTime(arrivalTime);
								if(servingTime > currentTime && servingTime <= requestTime){
									int stoppType = currentStopp.getType();
									if(stoppType == 2){
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity + passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										int requestIdOfStopp = currentStopp.getRequestId();
										searchRequests:
										for(int x =0; x < requests.size(); x++){
											Request requestOfList = requests.get(x);
											int id = requestOfList.getId();
											if(id == requestIdOfStopp){
												requestOfList.setPassengerState(2);
												vehicle.currentTour.remove(h);
												h = h - 1;
												break searchRequests;
											}
										}
										Simulation.setCurrentTime(servingTime);
									} else if(stoppType == 3){
										int passengersOfStopp = currentStopp.getPassengers();
										int currentCapacity = vehicle.getCapacity();
										int updatedCapacity = currentCapacity - passengersOfStopp;
										vehicle.setCapacity(updatedCapacity);
										vehicle.currentTour.remove(h);
										h= h -1;
										Simulation.setCurrentTime(servingTime);
								}
							} else if(arrivalTime < currentTime && arrivalTime <= requestTime){
								int stoppType = currentStopp.getType();
								if(stoppType == 2){
									int passengersOfStopp = currentStopp.getPassengers();
									int currentCapacity = vehicle.getCapacity();
									int updatedCapacity = currentCapacity + passengersOfStopp;
									vehicle.setCapacity(updatedCapacity);
									int requestIdOfStopp = currentStopp.getRequestId();
									searchRequests:
										for(int x =0; x < requests.size(); x++){
											Request requestOfList = requests.get(x);
											int id = requestOfList.getId();
											if(id == requestIdOfStopp){
												requestOfList.setPassengerState(2);
												vehicle.currentTour.remove(h);
												h = h - 1;
												break searchRequests;
											}
								}
								} else if(stoppType == 3){
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
	
	public static void finishTours(ArrayList<Vehicle> vehicles, ArrayList<Request> requests){
		for(int i = 0; i<vehicles.size(); i++){
			Vehicle vehicle = vehicles.get(i);
			for(int j = 0; j < vehicle.currentTour.size();j++){
				Stopp stopp = vehicle.currentTour.get(j);
				int stoppType = stopp.getType();
				int requestId = stopp.getRequestId();
				if(stoppType == 2){
					int passengersOfStopp = stopp.getPassengers();
					int currentCapacity = vehicle.getCapacity();
					int updatedCapacity = currentCapacity + passengersOfStopp;
					vehicle.setCapacity(updatedCapacity);
					searchRequests:
					for(int x =0; x < requests.size(); x++){
						Request requestOfList = requests.get(x);
						int id = requestOfList.getId();
						if(id == requestId){
							requestOfList.setPassengerState(2);
							break searchRequests;
						}
					}
			} else if(stoppType == 3){
				int passengersOfStopp = stopp.getPassengers();
				int currentCapacity = vehicle.getCapacity();
				int updatedCapacity = currentCapacity - passengersOfStopp;
				vehicle.setCapacity(updatedCapacity);
				searchRequests:
					for(int x =0; x < requests.size(); x++){
						Request requestOfList = requests.get(x);
						int id = requestOfList.getId();
						if(id == requestId){
							requestOfList.setPassengerState(2);
							break searchRequests;
						}
					}
				}
			}	
		}
	}
	
}
