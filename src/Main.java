import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class Main {
	

	static ArrayBlockingQueue<Adventurer> toBeServed;  //used to store adventurers which need to be served;
	static Adventurer[] adventurers;
	static Clerk[] clerks;
	static Dragon greenDragon;
	static ClerkFlag needClerk;
	static Semaphore table; //number of tables available;
	static Table tables[];
	static Semaphore tableLines[]; //an array of binary semaphore used to prevent race condition;
	static Semaphore clerkLine;

	
	public static void main(String[] args) throws InterruptedException {
		//default values;
		int num_adv = 8;
		int num_clerk = 2;
		int fortune_size = 3;
		int num_table = 3;
		//above values given by project instruction;
		
		if(args.length > 0){
			num_adv = Integer.parseInt(args[0]);
			num_clerk = Integer.parseInt(args[1]);
			fortune_size = Integer.parseInt(args[2]);
			//project instruction only stated num_adv and fortune_size
			//must be entered as command line arguments, but to be safe, num_clerk
			//will be added as well;
			num_table = Integer.parseInt(args[3]);
		}//if no argument, default value will be used;

		needClerk = new ClerkFlag(true); //flag to see if clerks are still needed;
		table = new Semaphore(num_table);  //this simulates how many tables there are.  Also, fairness is set to true;
		tables = new Table[num_table];  //creating an array of tables;
		tableLines = new Semaphore[num_table]; //creating an array of Semaphores;
		clerkLine = new Semaphore(num_clerk, false);  //simulates lines to get served, since fairness is set to false, it's random and not first come first serve;
		for(int i = 0; i < num_table;i++){
			tables[i] = new Table(i); //creating table objects;
			tableLines[i] = new Semaphore(1); //binary semaphores;
			
		}
		
		
		toBeServed = new ArrayBlockingQueue<Adventurer>(num_adv); //create the size of the queue to be adventurer size;
		adventurers = new Adventurer[num_adv]; //adventurer object array;
		greenDragon = new Dragon(table, tables); //dragon object;
		clerks = new Clerk[num_clerk];  //Clerk object array;
		
		for(int i = 0; i < num_adv; i++){
			adventurers[i] = new Adventurer(fortune_size, i, toBeServed, greenDragon, needClerk, table, tables, tableLines, clerkLine);
			adventurers[i].start();
			//creating adventurer objects and start them;
		}
		
		
		for(int i = 0; i < num_clerk; i++){
			clerks[i] = new Clerk(i, toBeServed, needClerk, clerkLine);
			clerks[i].start();
			//creating clerk objects and start them;
		}
		
		greenDragon.start();
			
		System.out.println("Main program ends.");
		
	}

}
