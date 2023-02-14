import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Datengenerator {
	
	public int countRequests;
	
	public int typeOneRequests;
	public int typeTwoRequests;
	public int typeThreeRequest;
	
	public static double[] trainTravelTimes;
	
	public static double[] createTrainTravelTimes(double endTime, double startTime){
		double countTravelTimes = (endTime - startTime) *2;
		countTravelTimes = countTravelTimes + 1;
		trainTravelTimes = new double[(int) countTravelTimes];
		trainTravelTimes[0] = startTime;
		for(int i = 1; i < countTravelTimes; i++){
			int lastIndex = i-1;
			trainTravelTimes[i] = trainTravelTimes[lastIndex] + 0.5;
		}
		return trainTravelTimes;
	}
	
	
	
	public static ArrayList<Request> createRequests(int a, int b, double start, double end, double lastRequestTime){
		ArrayList<Request> createRequests = new ArrayList<>();
		double [] trainTimes = createTrainTravelTimes(end,start);
		createTypeOneRequests(a,createRequests,start,end,trainTimes, lastRequestTime);
		createTypeTwoRequests(b,createRequests,start,end,trainTimes, lastRequestTime);
		
		
		
		//Sortierung der Liste nach requestTime
		Collections.sort(createRequests);
		//Id' werden neu gesetzt
		int size = createRequests.size();
		for(int j = 0; j < size; j++){
			int id = j+1;
			Request req = createRequests.get(j);
			req.setId(id);
			
		}
		return createRequests;
	}
	
	public static void createTypeOneRequests(int typeOneRequests, ArrayList <Request> requests, double startTime, double endTime, double[] arrayTrainTimes, double  lastRequestTime){
		for(int i = 0; i < typeOneRequests;i++){
			Random random = new Random();
			double randomRequestTime = random.nextDouble() * (lastRequestTime - startTime) + startTime;
			int pickUpX = 2;
			int pickUpY = 0;
			Point pickUp = new Point(pickUpX,pickUpY);
			//Zufallsgenerierung der Abhol-Koordinaten
			//x zwischen 0 & 4
			//y: zwischen 1 & 5
			int randomDropX = random.nextInt(5);
			int randomDropY = createRandomNumber(1,6);
			Point dropOff = new Point(randomDropX,randomDropY);
			double lateWindow = randomRequestTime + 1.0;
			double window = random.nextDouble() * (lateWindow - randomRequestTime) + randomRequestTime;
			for(int j = 0; j < arrayTrainTimes.length; j++){
				if(window < arrayTrainTimes[j]){
					window = arrayTrainTimes[j-1];
					if(window < randomRequestTime){
						window = window + 0.5;
					}
					break;
				}
				if(window > arrayTrainTimes[arrayTrainTimes.length-1]){
					window = arrayTrainTimes[arrayTrainTimes.length-1];
					break;
				}
				
			}
			int passengerNr = createRandomNumber(1,5);
			int type = 1;
			int id = i+1;
			int state = 0;
			Request request = new Request(id,randomRequestTime,pickUp,dropOff,passengerNr,state, type,window);
			requests.add(request);
		}	
	}
	
	public static void createTypeTwoRequests(int typeTwoRequests, ArrayList <Request> requests, double startTime, double endTime, double[] arrayTrainTimes, double lastRequestTime){
		for(int i = 0; i < typeTwoRequests;i++){
			Random random = new Random();
			double randomRequestTime = random.nextDouble() * (lastRequestTime - startTime) + startTime;
			int randomPickUpX = random.nextInt(5);
			int randomPickUpY = createRandomNumber(1,6);
			Point pickUp = new Point(randomPickUpX,randomPickUpY);
			int dropOffX = 2;
			int dropOffY = 0;
			Point dropOff = new Point(dropOffX,dropOffY);
			// Das Zeitfenster soll zwischen der Zeit der Anfrage und dem lateWindow liegen
			double lateWindow = randomRequestTime + 1.0;
			double window = random.nextDouble() * (lateWindow - randomRequestTime) + randomRequestTime;
			for(int j = 0; j < arrayTrainTimes.length; j++){
				if(window < arrayTrainTimes[j]){
					window = arrayTrainTimes[j-1];
					if(window < randomRequestTime){
						window = window + 0.5;
					}
					break;
				}
				
			}
			int passengerNr = createRandomNumber(1,5);
			int type = 2;
			int id = i+1;
			int state = 0;
			Request request = new Request(id,randomRequestTime,pickUp,dropOff,passengerNr,state,type,window);
			requests.add(request);
		}	
	}
	/*
	public static void createTypeThreeRequests(int typeThreeRequests, ArrayList <Request> requests, double startTime, double endTime){
		for(int i = 0; i < typeThreeRequests;i++){
			Random random = new Random();
			double randomRequestTime = random.nextDouble() * (endTime - startTime) + startTime;
			int randomPickUpX = random.nextInt(4);
			int randomPickUpY = createRandomNumber(1,5);
			Point pickUp = new Point(randomPickUpX,randomPickUpY);
			int randomDropX = random.nextInt(4);
			int randomDropY = createRandomNumber(1,5);
			Point dropOff = new Point(randomDropX,randomDropY);
			double lateWindow = randomRequestTime +1;
			double window = random.nextDouble() * (lateWindow - randomRequestTime) + randomRequestTime;
			int passengerNr = createRandomNumber(1,4);
			int type = 3;
			int id = i+1;
			int state = 0;
			Request request = new Request(id,randomRequestTime,pickUp,dropOff,passengerNr,state,type,window);
			requests.add(request);
		}
		
	}
*/	
	public static int createRandomNumber(int untereGrenze, int obereGrenze){
		Random random = new Random();
		int randomNumber = random.nextInt(obereGrenze);
		
		while(randomNumber < untereGrenze){
			randomNumber = random.nextInt(obereGrenze);
		}
		return randomNumber;
	}

}
