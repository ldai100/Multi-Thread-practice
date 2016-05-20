
public class ClerkFlag {
	private boolean needClerk;
	
	public ClerkFlag(boolean needClerk){  //constructor;
		this.needClerk = needClerk;
	}
	
	public boolean getFlag(){  //get method;
		return needClerk;
	}
	
	public void setFlag(boolean needClerk){  //set method;
		this.needClerk = needClerk;
	}
	
}
