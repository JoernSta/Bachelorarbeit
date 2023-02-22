import java.awt.Point;
import java.util.ArrayList;

/**
 * @author JS
 *
 */
public class waitingStrategies {
	
	Point waitingPoint;
	static ArrayList<Point> allPoints;
	Point transferPoint;
	
	public static ArrayList<Point> createListWithAllPoints(Point transferPoint){
		allPoints = new ArrayList<Point>();
		allPoints.add(transferPoint);
		for(int y = 1; y <= 5; y++){
			for(int x = 0; x <= 4; x++){
				Point point = new Point(x,y);
				allPoints.add(point);
			}
		}
		return allPoints;
	}
	/**
	 * Diese Methode bestimmt alle Punkte welche von der aktuellen Position aus erreicht werden können.
	 * @param points
	 * @param vehicle
	 * @param currentTime
	 * @param endTime
	 * @param targetPoint
	 * @return
	 */
	public static ArrayList<Point> calculateReachablePoints(ArrayList<Point> points, Vehicle vehicle, double currentTime,double endTime,Point targetPoint){
		Point currentPosition = vehicle.getPosition();
		
		ArrayList<Point> reachablePoints= new ArrayList<Point>();
		for (Point currentPoint : points) {
			
			double distanceFromVehiclePositionToCurrentPoint = Simulation.calculateDistanceBetween2Points(currentPosition, currentPoint);
			double driveTimeFromVehiclePositionToCurrentPoint = Simulation.calculateDriveTimeToPoint(distanceFromVehiclePositionToCurrentPoint);
			double arrivalTimeToCurrentPoint = currentTime + driveTimeFromVehiclePositionToCurrentPoint;
			if (vehicle.currentTour.size() == 0) {
				double distancefromCurrentPointToPoint = Simulation.calculateDistanceBetween2Points(currentPoint, targetPoint);
				double driveTimeFromCurrentPointToPoint = Simulation.calculateDriveTimeToPoint(distancefromCurrentPointToPoint);
				double arrivalTimeFromCurrentPointToTransferPoint = arrivalTimeToCurrentPoint + driveTimeFromCurrentPointToPoint;
				if (arrivalTimeFromCurrentPointToTransferPoint <= endTime) {
					reachablePoints.add(currentPoint);
				}
			} else {
				Stopp nextStopp = vehicle.currentTour.get(0);
				double arrivalOfNextStopp = nextStopp.getArrivalTime();
				if(currentTime > arrivalOfNextStopp){
					currentTime = arrivalOfNextStopp;
				}
				distanceFromVehiclePositionToCurrentPoint = Simulation.calculateDistanceBetween2Points(currentPosition, currentPoint);
				driveTimeFromVehiclePositionToCurrentPoint = Simulation.calculateDriveTimeToPoint(distanceFromVehiclePositionToCurrentPoint);
				arrivalTimeToCurrentPoint = currentTime + driveTimeFromVehiclePositionToCurrentPoint;
				double servingTime = nextStopp.getPlannedDeaparture();
				Point nextStoppPoint = nextStopp.getStopp();
				double distanceFromPointToTargetPoint = Simulation.calculateDistanceBetween2Points(currentPoint, nextStoppPoint);
				double driveTimeFromCurrentPositionToTargetPoint = Simulation.calculateDriveTimeToPoint(distanceFromPointToTargetPoint);
				double arrivalTimeToTargetPoint = arrivalTimeToCurrentPoint + driveTimeFromCurrentPositionToTargetPoint;
				if(arrivalTimeToTargetPoint <= servingTime){
					reachablePoints.add(currentPoint);
				}
			}
		}
		return reachablePoints;
	}
	
	public static Point calculateCenterOfGravity(ArrayList<Point> points){
		Point centerOfGravity = new Point();
		int xOpt = 0;
		int yOpt = 0;
		int weightOfPoint = 1;
		int overallElements = points.size();
		for (Point point : points) {
			int xOfPoint = (int) point.getX();
			int yOfPoint = (int) point.getY();
			xOpt = xOpt + (weightOfPoint * xOfPoint);
			yOpt = yOpt + (weightOfPoint * yOfPoint);
		}
		xOpt = xOpt / overallElements;
		yOpt = yOpt /overallElements;
		centerOfGravity = new Point(xOpt,yOpt);
		System.out.println(centerOfGravity);
		return centerOfGravity;
	}
	
	/*
	 * Methode, welche dem Wartepunkt die Position des Fahrzeuges zuweist.
	 * @param vehicle
	 * @return Wartepunkt
	 */

	/* public static Point waitAtCurrentPosition(Vehicle vehicle){
		waitingPoint = vehicle.getPosition();
		return waitingPoint;
	}*/
	
	

}
