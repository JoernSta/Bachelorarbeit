import java.util.ArrayList;

public class AssignedVehicle implements Comparable<AssignedVehicle> {
	
	
	public double waitingTime;
	public Vehicle vehicle;
	public ArrayList<Stopp> stopps;

	public AssignedVehicle(Vehicle vehicle, ArrayList<Stopp> stopps, double waitingTime){
		this.vehicle = vehicle;
		this.stopps = stopps;
		this.waitingTime = waitingTime;
	}
	
	
	public double getWaitingTime(){
		return waitingTime;
	}
	
	public Vehicle getVehicle(){
		return vehicle;
	}
	
	public ArrayList<Stopp> getTour(){
		return stopps;
	}
	
	public int compareTo(AssignedVehicle assignedVehicle) {
		
		return Double.compare(this.waitingTime, assignedVehicle.getWaitingTime());
	}

}
