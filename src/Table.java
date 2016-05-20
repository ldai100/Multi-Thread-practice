
public class Table {
	private Adventurer adv;
	private int gameCounter;
	private String name;
	
	public Table(int name){
		this.adv = null;
		this.name = "T" + Integer.toString(name);
	}
	
	public void comesIn(Adventurer adv){
		this.adv = adv;
	}
	
	public void leaves(){
		this.adv = null;
		gameCounter = 0;  //when a adventurer leaves, it game counter resets;
	}
	
	public Adventurer getSitter(){  //gets information of whose sitting here;
		return adv;
	}
	
	public int getCounter(){
		return gameCounter;
	}
	
	public void counterIncrease(){
		gameCounter++;
	}
	
	public String getName(){
		return name;
	}
	
}
