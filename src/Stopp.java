import java.awt.Point;
/**
 * Diese Klasse stellt einen Halt einer Tourenliste dar
 * @author JS
 *
 */

public class Stopp {


	public Point stoppPoint;
	public double arrivalTime;
	public int requestId;
	public double plannedDepartureTime;
	public double latestArrival;
	//Anzahl an zu transportierende Personen
	public int passengers;
	
	/*
	 * type = 1: Wartepunkt
	 * type = 2: PickUpPoint
	 * type = 3: DropOffPoint
	 */
	public int type;
	public int maxMovedPosition;
	public double serviceTime;
	public int requestType;

	
	public Stopp(int id, Point p, double aTime, int type,double departureTime,  double latestDeparture, int passengers, int maxSwap, double service, int requestType) {
		this.requestId = id;
		this.stoppPoint = p;
		this.arrivalTime = aTime;
		this.type = type;
		this.plannedDepartureTime = departureTime;
		this.latestArrival = latestDeparture;
		this.passengers = passengers;
		this.maxMovedPosition = maxSwap;
		this.serviceTime = service;
		this.requestId = requestType;
	}
	
	public int getRequestId(){
		return requestId;
	}
	
	public void setRequestId(int i){
		this.requestId = i;
	}
	
	public Point getStopp(){
		return stoppPoint;
	}
	
	public void setStopp(Point p){
		this.stoppPoint = p;
	}
	
	public double getArrivalTime(){
		return arrivalTime;
	}
	
	public void setArrivalTime(double a){
		this.arrivalTime = a;
	}
	
	public int getType(){
		return type;
	}
	
	public void setType(int t){
		this.type = t;
	}
	
	public double getPlannedDeaparture(){
		return plannedDepartureTime;
	}
	
	public void setPlannedDeparture(double time){
		this.plannedDepartureTime = time;
	}
	
	public double getLatestArrivalTime(){
		return latestArrival;
	}
	
	public void setLatestArrivalTime(double time){
		this.latestArrival = time;
	}
	
	public int getPassengers(){
		return passengers;
	}
	
	public int getMaxMovedPosition(){
		return maxMovedPosition;
	}
	public void increaseMaxMovedPosition(int i){
		maxMovedPosition = maxMovedPosition + i;
	}
	
	public double getServiceTime(){
		return serviceTime;
	}
	
	public void setServiceTime(double t){
		this.serviceTime = t;
	}
	
	public int getRequestType(){
		return requestType;
	}
	
	public void setRequestType(int i){
		this.requestType = i;
	}

}
