import java.awt.Point;

/**
 * 
 */

/**
 * @author JS
 *Klasse repräsenttiert die Servicenafragen der Kunden
 */
public class Request implements Comparable<Request> {

	public int id;
	public double requestTime;
	public Point pickUpPoint;
	public Point dropOff;
	public int passengerNr;
	//type = 1: Kunde möchte vom Umstiegspunkt ins Stadtgebiet
	//type = 2: Kunde möchte zur Haltestation
	//type = 3: Kunde möchte innerhalb des Stadtgebietes transportiert werden
	public int requestType;
	public double serviceTime;
	/*
	 * state = 0: nicht zugewiesen & nicht eingesammelt
	 * state = 1: zugewiesen aber noch nicht eingesammelt
	 * state = 2:  zugewiesen und eingesammelt
	 * state = 3: zugewiesen und abgesetzt
	 */
	public int passengerState;
	
	
	
	
	
	public Request(int id, double requestTime, Point pickUp,Point delivery, int passengerNr, int state,int type, double serviceTime ) {
		this.id = id;
		this.requestTime = requestTime;
		this.pickUpPoint = pickUp;
		this.dropOff = delivery;
		this.passengerNr = passengerNr;
		this.passengerState = state;
		this.requestType = type;
		this.serviceTime = serviceTime;
	
		
	
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int i){
		this.id = i;
	}
	
	public double getRequestTime(){
		return requestTime;
	}
	
	public Point getPickUpPoint(){
		return pickUpPoint;
	}
	
	
	public Point getDropOffPoint(){
		return dropOff;
	}
	
	public int getPassengerNr(){
		return passengerNr;
	}
	
	public int getPassengerState(){
		return passengerState;
	}
	
	public void setPassengerState(int state){
		this.passengerState = state;
	}
	
	public double getServiceTime(){
		return serviceTime;
	}
	
	public int getType(){
		return requestType;
	}

	@Override
	public int compareTo(Request request) {
	
		return Double.compare(this.requestTime, request.getRequestTime());
	}
	
	

}
