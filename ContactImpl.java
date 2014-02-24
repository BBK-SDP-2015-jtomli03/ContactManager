public class ContactImpl{
	private int id = null;
	private String name = "";
	private String notes = "";

	public ContactImpl(int id, String name, String notes){
		this.id = id;
		this.name = name;
		this.notes = notes;
	}

//gets the ID of the contact
	public int getId(){
		return this.id;
	}

//gets the name of the contact
	public String getName(){
		return this.name;
	}

//gets any notes relating to a contact
	public String getNotes(){
		return this.notes;
	}
}