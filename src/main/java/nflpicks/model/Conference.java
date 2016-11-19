package nflpicks.model;

public class Conference {

	protected int id;
	
	protected String name;
	
	public Conference(){
	}
	
	public Conference(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		
		String thisObjectAsAString = "id = " + id + 
									 ", name = " + name;
		
		return thisObjectAsAString;
	}
}
