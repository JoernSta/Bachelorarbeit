import java.awt.Point;

/**
 * @author JS
 *Klasse repraesenttiert die Servicenafragen der Kunden
 */
public class Request implements Comparable<Request> {

	public int id;
	public double requestTime;
	public Point pickUpPoint;
	public Point dropOff;
	public int passengerNr;
	//type = 1: Kunde moechte vom Umstiegspunkt ins Stadtgebiet
	//type = 2: Kunde moechte zur Haltestation
	//type = 3: Kunde moechte innerhalb des Stadtgebietes transportiert werden
	public int requestType;
	public double serviceTime;
	/*
	 * state = 0: nicht zugewiesen & nicht eingesammelt
	 * state = 1: zugewiesen aber noch nicht eingesammelt
	 * state = 2: zugewiesen und eingesammelt
	 * state = 3: zugewiesen und abgesetzt
	 */
	public int passengerState;

	
	public Request(int id, double requestTime, Point pickUp, Point delivery, int passengerNr, int state, int type, double serviceTime) {
		this.id = id;
		this.requestTime = requestTime;
		this.pickUpPoint = pickUp;
		this.dropOff = delivery;
		this.passengerNr = passengerNr;
		this.passengerState = state;
		this.requestType = type;
		this.serviceTime = serviceTime;
	}

	public String toCsv() {
		return this.id + "," + this.requestTime + "," + this.pickUpPoint.x + "," + this.pickUpPoint.y + "," +
				this.dropOff.x + "," + this.dropOff.y + "," + this.passengerNr + "," + this.passengerState + "," +
				this.requestType + "," + this.serviceTime;
	}

	public static Request fromCsv(String csv) {
		String [] parts = csv.split(",");

		if(parts.length != 10) {
			throw new IllegalArgumentException("Invalid CSV line while parsing `Request`: " + csv);
		}

		int id = Integer.parseInt(parts[0]);
		double requestTime = Double.parseDouble(parts[1]);
		int pickUpPointX = Integer.parseInt(parts[2]);
		int pickUpPointY = Integer.parseInt(parts[3]);
		int dropOffX = Integer.parseInt(parts[4]);
		int dropOffY = Integer.parseInt(parts[5]);
		int passengerNr = Integer.parseInt(parts[6]);
		int passengerState = Integer.parseInt(parts[7]);
		int requestType = Integer.parseInt(parts[8]);
		double serviceTime = Double.parseDouble(parts[9]);

		return new Request(id, requestTime, new Point(pickUpPointX, pickUpPointY), new Point(dropOffX,dropOffY),
				passengerNr, passengerState, requestType, serviceTime);
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
