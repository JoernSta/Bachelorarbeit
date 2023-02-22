/**
 * @author JS
 * Klasse repr�sentiert das Fahrzeug
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
	int usedCap;

	

	public Vehicle(int id, Point p, int cap, int speed, ArrayList<Stopp> curTour,int usedCap) {
		this.id = id;
		this.position =  p;
		this.capacity = cap;
		this.speed = speed;
		this.currentTour = curTour;
		this.usedCap = usedCap;
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
		return currentTour.size() != 0;
	}
	
	public void setHasTour(boolean b){
		this.hasTour = b;
	}
	
	public int getUsedCap(){
		return usedCap;
	}
	
	public void setUsedCap(int i){
		this.usedCap = i;
	}
	
	
	
	
	
}
