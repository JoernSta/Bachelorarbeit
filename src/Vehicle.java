/**
 * 
 */

/**
 * @author JS
 *Klasse repräsentiert das Fahrzeug
 */

import java.awt.Point;
import java.util.ArrayList;

public class Vehicle {

	int id;
	Point position; 
	int capacity;
	int speed;
	public ArrayList<Request> assignedRequests;
	public ArrayList<Request> currentPassengers;
	public ArrayList<Stopp> currentTour;
	public boolean hasTour;

	

	public Vehicle(int id, Point p, int cap, int speed, ArrayList<Request> assignedReq, ArrayList<Request> curPas, ArrayList<Stopp> curTour) {
		this.id = id;
		this.position =  p;
		this.capacity = cap;
		this.speed = speed;
		this.assignedRequests = assignedReq;
		this.currentPassengers = curPas;
		this.currentTour = curTour;		
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int i){
		this.id = i;
	}
	
	public Point getPosition(){
		return position;
	}
	
	public void setPosition(Point p){
		this.position = p;
	}
	
	public int getCapacity(){
		return capacity;
	}
	
	public void setCapacity(int c){
		this.capacity = c;	
	}
	
	public int getSpeed(){
		return speed;
	}
	
	public void setSpeed(int s){
		this.speed = s;
	}
	
	public boolean hasTour(){
		int size = currentTour.size();
		if(size == 0){
			return false;
		} else {
			return true;
		}
	}
	
	public void setHasTour(boolean b){
		this.hasTour = b;
	}
	
	public void pickUpPassenger(Request passenger, Vehicle vehicle){
		vehicle.currentPassengers.add(passenger);
		int passengerNr = passenger.getPassengerNr();
		int updatedCapacity = vehicle.getCapacity() - passengerNr;
		vehicle.setCapacity(updatedCapacity);
	}
	
	public void deliverPassenger(Request passenger, Vehicle vehicle){
		int index = 0;
		int requestId = passenger.getId();
		int passengerNr = passenger.getPassengerNr();
		int updatedCapacity = vehicle.getCapacity() + passengerNr;
		for(int i = 0; i < vehicle.currentPassengers.size(); i++){
			Request request = vehicle.currentPassengers.get(i);
			int id = request.getId();
			if(id == requestId){
				index = i;
			}
		}
		vehicle.setCapacity(updatedCapacity);
		vehicle.currentPassengers.remove(index);
	}
	
	//gibt die Anfrage mit der eingegebenen id zurück
	public Request getSpecificRequest(int id, Vehicle vehicle, Stopp currentStopp){
		Request specificRequest = vehicle.assignedRequests.get(0);
		for(int i = 0; i<vehicle.assignedRequests.size();i++){
			Request request = vehicle.assignedRequests.get(i);
			int requestId = request.getId();
			if(requestId == id){
				specificRequest = request;
			}
		}
		return specificRequest;
	}
	
	//Methode, welche das Touring darstellt
	public void touring(){
		
	}
	
	
	
	
}
