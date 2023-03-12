
public class Times implements Comparable<Times> {
	
	public int requestId;
	double waitingTime;
	

	public Times(int id, double waitingTime) {
		this.requestId = id;
		this.waitingTime = waitingTime;
	}
	
	
	public double getWaitingTime(){
		return waitingTime;
	}
	
	public void setWaitingTime(double time){
		this.waitingTime = time;
	}
	
	public int getRequestId(){
		return requestId;
	}
	
	@Override
	public int compareTo(Times times) {
	
		return Double.compare(this.waitingTime, times.getWaitingTime());
	}

}
