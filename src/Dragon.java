import java.util.Random;
import java.util.concurrent.Semaphore;

public class Dragon extends Thread{
	Random random = new Random(); //used to roll dice;
	private boolean NeedDragon;
	boolean DragonWon;
	private int advScore;
	private int dragScore;
	private Semaphore table;
	private Table[] tables;  //array of Table object;
	private int currentTableIndex;  //used to keep track of current table;
	private Table currentTable; 
	
	public void setNeedDragon(boolean flag){
		this.NeedDragon = flag;
	}
	
	public static long time = System.currentTimeMillis();

	public void msg(String m) {
	System.out.println("["+(System.currentTimeMillis()-time)+"] " + getName() + ":" + m);
	}
	
	public Dragon(Semaphore table, Table tables[]) { //dragon doesn't need an id because there's only one dragon;
		 setName("Green Dragon");
		 this.NeedDragon = true;
		 this.table = table;
		 this.tables = tables;
	}
	
	public void play(){  //this is where the game happens;
		advScore = 0;
		dragScore = 0;
		//initialized so it'll go to the loop;
		while(advScore == dragScore){
		advScore = random.nextInt(6)+1; //random from 1-6;
		dragScore = random.nextInt(6)+1;
		}
		if(dragScore > advScore){
			msg("won");
			DragonWon = true;
		}
		else {
			msg("lost");
			DragonWon = false;
		}
		
	} //play;
	
	private Adventurer currentAdv;
	
	public void run(){
		msg("wakes up.");
		currentTableIndex = 0; //start with table 0;
		while(NeedDragon){
			
			currentTableIndex = currentTableIndex%tables.length; //keeps going through tables 0-num_table;
			currentTable = tables[currentTableIndex];
			currentAdv = currentTable.getSitter();
			//randomly interrupts one adventure;
			if(currentAdv == null){
				currentTableIndex++;
				continue;  //if no one is at the table, it goes straight to the next table;
			}

			
			msg("battles " + currentAdv.getName() + " at table " + currentTable.getName());
			try {
				sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			play();
			
			//below are the cases could happen if dragon loses;
			if(!DragonWon){
				int indicator = random.nextInt(4);  //variable used to determine which item is awarded;
				if(indicator == 0){  //if 0, a magic stone is awarded;
					currentAdv.stoneIncrement();
					msg(currentAdv.getName() + " acquired a magic stone. Current stones:" + currentAdv.getStone());
				}
				else if(indicator == 1) {  //if 0, a ring is awarded;
					currentAdv.ringIncrement();
					msg(currentAdv.getName() + " acquired a ring. Current rings:" + currentAdv.getRing());
				}
				else if(indicator == 2) {  //if 0, a necklace is awarded;
					currentAdv.necklaceIncrement();
					msg(currentAdv.getName() + " acquired a necklace. Current necklace:" + currentAdv.getNecklace());
				}
				else {  //else, a earring is awarded;
					currentAdv.earringIncrement();
					msg(currentAdv.getName() + " acquired a earring. Current earrings:"  + currentAdv.getEarring());
				}
			} //if
			
			try {
				sleep(100);  //simulate giving the item;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			msg("goes to the next table.");
			try {
				sleep(100);  //simulate going to the next table;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			currentTable.counterIncrease(); //increase the table game counter;
			
			if(currentTable.getCounter() == 3){
				
				currentTable.leaves(); //if counter is 3, adventurer leaves the table;
				currentAdv.atTable = false; //no longer at the table, adventurer has to through the process again;
				table.release(); //signals that there are tables available;
			}
			currentTableIndex++; //go to the next table;
		} //while
		msg("goes to sleep.");
		return;
	} //run
	
	
}
