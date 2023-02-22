/**
 * 
 */

/**
 * @author JS
 *
 * Diese Klasse dient zur Berechnung der durchschnittlichen Fahrzeiten.
 *
 */
public class DriveTimesOfCustomers {
	
	int requestId;
	double pickUpTime;
	double dropOffTime;

	/**
	 * 
	 */
	public DriveTimesOfCustomers(int id, double pickUpTime,double dropOffTime) {
		this.requestId = id;
		this.pickUpTime = pickUpTime;
		this.dropOffTime = dropOffTime;
	}
	
	public int getRequestId(){
		return requestId;
	}
	/**
	 * Methode gibt die PickUpTime zurück.
	 * @return pickUpTime der Anfrage
	 */
	public double getPickUpTime(){
		return pickUpTime;
	}
	/**
	 * Diese Methode setzt pickUptime.
	 * @param t, die Zeit, auf die die PickUpTime gesetzt werden soll.
	 */
	public void setPickUpTime(double t){
		this.pickUpTime = t;
	}
	/**
	 * Diese Methode gibt die dropOffTime einer Anfrage zurück.
	 * @return
	 */
	public double getDropOffTime(){
		return dropOffTime;
	}
	
	public void setDropOffTime(double t){
		this.dropOffTime = t;
	}

}
