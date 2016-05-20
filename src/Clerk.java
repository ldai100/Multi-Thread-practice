import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class Clerk extends Thread{
	static Semaphore queueSem = new Semaphore(1); //mutex used to make sure only 1 clerk access queue at once;
	private Adventurer currentAdv;  //stores current adventurer;
	private ArrayBlockingQueue<Adventurer> toBeServed;  //this queue stores adventurers need to be served;
	/*toBeServed is not used to simulate a line, but only used for clerk to access adventurers.
	 * please note that clerks randomly picks adventurers because semaphore fairness is set to false.
	 *so that means clerks who were waiting on acquire() might not go to the queue to get served first.
	 *so it does satisfy the condition that "clerks pick a random adventurer in the line";
	*/
	private ClerkFlag flag;
	private Semaphore clerkLine;
			
	//used to get the adventurer in the front of the line;
	private Adventurer dequeue(ArrayBlockingQueue<Adventurer> line){
		return line.poll();
	}
	
	public static long time = System.currentTimeMillis();

	public void msg(String m) {
		System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+ m);
	 }
			
	// Default constructor
	public Clerk(int id, ArrayBlockingQueue<Adventurer> toBeServed, ClerkFlag flag, Semaphore clerkLine) {
		setName("Clerk-" + id);
		this.toBeServed = toBeServed;
		this.flag = flag;
		this.clerkLine = clerkLine;
	}
	
	public void run(){
		msg("comes to work.");
		try {
			sleep(200);   //simulates coming to work;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // wait for everything to start;
		
		while(flag.getFlag()){

			if(!toBeServed.isEmpty()){  //make sure the line is not empty;
				try {
					queueSem.acquire();  //this mutex makes sure only one clerk has access to the front of the line;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				currentAdv = dequeue(toBeServed);  //set the currently serving adventurer and remove him from the line;
				queueSem.release(); 
				
				while(currentAdv != null && (currentAdv.canMakeRing()||currentAdv.canMakeEarring()||currentAdv.canMakeNecklace())){
					//cases of what to make for the adventurer;
					//it will keep making stuff for the adventurer until there's not enough items;
					if(currentAdv.canMakeRing()){  //if magical ring can be made, ring and stone decrease by 1, and fortune increase by 1;
						currentAdv.ringDecrement();
						currentAdv.stoneDecrement();
						currentAdv.fortuneIncrement();
						msg("Ding Ding Ding..making a Magical ring for Adventurer-" + currentAdv.id);
						try {
							sleep(200);  //simulates making item;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(currentAdv.canMakeNecklace()){  //if magical necklace can be made, necklace and stone decrease by 1, and fortune increase by 1;
						currentAdv.necklaceDecrement();
						currentAdv.stoneDecrement();
						currentAdv.fortuneIncrement();
						msg("Ding Ding Ding..making a Magical necklace for Adventurer-" + currentAdv.id);
						try {
							sleep(200); //simulates making item;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else if(currentAdv.canMakeEarring()){  //if magical earrings can be made, earring and stone decrease by 2, and fortune increase by 1;
						currentAdv.earringDecrement();
						currentAdv.earringDecrement();
						currentAdv.stoneDecrement();
						currentAdv.stoneDecrement();
						currentAdv.fortuneIncrement();
						msg("Ding Ding Ding..making a pair of Magical earring for Adventurer-" + currentAdv.id);
						try {
							sleep(200); //simulates making item;
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//as the instruction states, a pair of
						//earring only counts as one fortune_size
						//but requires the most items to create;
					} //else if
				} //while
				if(currentAdv != null) currentAdv.setAssistance(false);  //finished serving;
				clerkLine.release(); //finished serving and release a ticket;
			} //if
			else continue;  //line is empty and keep waiting until clerkFlag is set to false;
				
		} //while
		msg("goes home.");
		try {
			sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
}
