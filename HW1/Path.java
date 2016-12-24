

public class Path{

    private String value;
    public int pathCost;
    public int estimatedCost;
    public int tag;
//    public Path parent;

    public Path(String val, int pathCost, int tag) {
    	this.value = val;
        this.pathCost = pathCost;
        this.tag = tag;
    }
    
    public void add(String newVal) {
    	this.value = this.value + " " + newVal;
    }
    
    public String get() {
		return value;	
    }
    
    public String getLastPoint() {
    	String[] str = value.split(" ");
    	return str[str.length-1];
    }


}



