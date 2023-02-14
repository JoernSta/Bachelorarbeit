import java.awt.Point;
import java.util.ArrayList;

/**
 * 
 */

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
	
	public static ArrayList<Point> calculateReachablePoints(ArrayList<Point> points, Vehicle vehicle, double currentTime,double endTime,Point transferPoint){
		Point currentPosition = vehicle.getPosition();
		double arrivalTimeFromCurrentPointToTransferPoint = 0.0;
		ArrayList<Point> reachablePoints= new ArrayList<Point>();
		for(int i = 0; i < points.size(); i++){
			Point currentPoint = points.get(i);
			double distanceFromVehiclePositionToCurrentPoint = Simulation.calculateDistanceBetween2Points(currentPosition, currentPoint);
			double driveTimeFromVehiclePositionToCurrentPoint = Simulation.calculateDriveTimeToPoint(distanceFromVehiclePositionToCurrentPoint);
			double arrivalTimeToCurrentPoint = currentTime + driveTimeFromVehiclePositionToCurrentPoint;
			if(vehicle.currentTour.size()==0){
				double distancefromCurrentPointToTransferPoint = Simulation.calculateDistanceBetween2Points(currentPoint, transferPoint);
				double driveTimeFromCurrentPointToTransferPoint = Simulation.calculateDriveTimeToPoint(distancefromCurrentPointToTransferPoint);
				arrivalTimeFromCurrentPointToTransferPoint = arrivalTimeToCurrentPoint + driveTimeFromCurrentPointToTransferPoint;
			} else{
				int lastIndex = vehicle.currentTour.size() - 1;
				Stopp lastStopp = vehicle.currentTour.get(lastIndex);
				Point pointOfLastStopp = lastStopp.getStopp();
				double departureTime = lastStopp.getPlannedDeaparture();
				double distanceFromLastStoppToTransferPoint = Simulation.calculateDistanceBetween2Points(pointOfLastStopp, transferPoint);
				double driveTimeFromLastStoppToTransferPoint = Simulation.calculateDriveTimeToPoint(distanceFromLastStoppToTransferPoint);
				arrivalTimeFromCurrentPointToTransferPoint = departureTime + driveTimeFromLastStoppToTransferPoint;	
			}
			if(arrivalTimeToCurrentPoint < endTime && arrivalTimeFromCurrentPointToTransferPoint <= endTime){
				reachablePoints.add(currentPoint);
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
		for(int i = 0; i < points.size(); i++){
			Point point = points.get(i);
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
	
	/**
	 * Methode, welche dem Wartepunkt die Position des Fahrzeuges zuweist.
	 * @param vehicle
	 * @return Wartepunkt
	 */
	public static Point waitAtCurrentPosition(Vehicle vehicle){
		waitingPoint = vehicle.getPosition();
		return waitingPoint;
	}
	
	

}
