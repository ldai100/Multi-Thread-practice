import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class Adventurer extends Thread{
	
	static int totalAdv = 0;  //class variable to keep count of adventurers;
	static Semaphore platoon = new Semaphore(0,true); //class variable used to create platoon policy;
	static int readyToGo = 0; //counts on how many adventurers are ready to go home;
	static Semaphore tableLines[];
	static Semaphore clerkLine;
	
	private Random rand = new Random();
	//following requirement, each number is randomly between 0 and 3;
	public int id;
	private int stone;
	private int ring;
	private int necklace;
	private int earring;
	private int fortune_size;
	private int satisfied_size; //the number to make adventurer leave;
	public boolean atTable; //used to make sure adventurer doesn't go further until the game is done;
	private ArrayBlockingQueue<Adventurer> toBeServed;
	private Dragon drag;
	private ClerkFlag flag;
	private boolean need_assistance;
	private Semaphore table;
	private Table tables[];

	
	
	//not really setter but used to increase value;
	//mostly used by dragon except for fortune_size;
	public void stoneIncrement(){
		stone++;
	}
	
	public void ringIncrement(){
		ring++;
	}
	
	public void necklaceIncrement(){
		necklace++;
	}
	
	public void earringIncrement(){
		earring++;
	}
	public void fortuneIncrement(){
		fortune_size++;
	}
	//decrement methods;
	//these will be used by clerks;
	//forutne_size will never decrease;
	public void stoneDecrement(){
		stone--;
	}
	
	public void ringDecrement(){
		ring--;
	}
	public void necklaceDecrement(){
		necklace--;
	}
	public void earringDecrement(){
		earring--;
	}
	
	//getters don't have to be synchronized;
	public int getStone(){
		return stone;
	}
	public int getRing(){
		return ring;
	}
	public int getNecklace(){
		return necklace;
	}
	public int getEarring(){
		return earring;
	}
	
	public boolean canMakeRing(){
		if(stone >= 1 && ring >= 1) return true;
		else return false;
	}
	public boolean canMakeNecklace(){
		if(stone >= 1 && necklace >= 1) return true;
		else return false;
	}
	public boolean canMakeEarring(){ //earring comes in pairs and need 2 stones;
		if(stone >= 2 && earring >= 2) return true;
		else return false;
	}
	
	public static long time = System.currentTimeMillis(); //added as required by project;
	public void msg(String m) {
		 System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+":"+m);
	}
	
//constructor;	
	public Adventurer(int required_size, int id,ArrayBlockingQueue<Adventurer> toBeServed,Dragon greenDragon,ClerkFlag needClerk, Semaphore table, Table tables[], Semaphore[] tableLines, Semaphore clerkLine){
		this.id = id;
		
		//randomly assign number of items;
		this.stone = rand.nextInt(4);
		this.ring = rand.nextInt(4);
		this.necklace = rand.nextInt(4);
		this.earring = rand.nextInt(4);
		this.fortune_size = 0; //every adventurer start with 0 fortune;
		this.table = table;
		this.satisfied_size = required_size;
		this.toBeServed = toBeServed;
		setName("adventurer-" + id);
		atTable = false; //initialize
		this.drag = greenDragon;
		this.flag = needClerk;
		this.tables = tables;
		Adventurer.tableLines = tableLines;
		this.clerkLine = clerkLine;
		totalAdv++;
	}
	
	public void setAssistance(boolean flag){
		this.need_assistance = flag;
	}
	
	public boolean getAssistance(){
		return need_assistance;
	}
	
	
	public void run(){
		msg("arrives to town Dudley.");
		while(fortune_size < satisfied_size){ //won't leave until satisfied;
			
			if(canMakeRing() || canMakeNecklace() || canMakeEarring()){
				msg("has enough materials and travels to shop...");

					try {
						sleep(rand.nextInt(3000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					
					} //travel to shop;
				try {
					clerkLine.acquire(); //goes to a clerk if anyone is available;
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setAssistance(true);  //arrives, set it to true and wait for a clerk;
				toBeServed.add(this); //put this thread in the queue so clerk can access it;
				while(getAssistance()){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //random instruction so this while loop doesn't stuck;
					//busy waits;
				}
			}//if
			else{  //not enough items, go to the dragon;
				
				msg("is looking for the dragon.");
				int tableNum = 0; //used to store current table index;
				
				try {
					sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
				try {
					
					table.acquire(); //get a ticket to go to any of the table;
					atTable = true; //we know he's going to get a table now;
					
					for(int i = 0; i < tables.length; i++){
						if(tables[i].getSitter() == null){
							tableLines[i].acquire(); //implemented so no two thread will be accessing the same table at the same time;
							tables[i].comesIn(this); //finds the first available seat and sit there;
							tableNum = i;
							break;
						} //if
					} //for
					
					msg("goes to table " + tables[tableNum].getName());
					sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				while(atTable){
					//busy wait;
					
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}; //random instruction so this while loop doesn't stuck;
				}
				tableLines[tableNum].release(); //left the table;
				
			} //else;
			
			
		}// while;
		msg("has accumulated his fortune and is ready to leave.");
		
		readyToGo++;  //at this point, the thread is ready to go and thus increase readyToGo variable;
		
		
		if(readyToGo == totalAdv){
			platoon.release();  //we know this is the last adventurer, so he gives out a key;
			//last adventurer will tell clerks and dragon to terminate;
			drag.setNeedDragon(false);
			flag.setFlag(false);
		}else{
			try {
				platoon.acquire();  //if not the last one, they will wait here to acquire key;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			platoon.release();
			
		} // else;
		
		
			
		
		msg("leaves town.");
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
}
