public class ContactImpl implements Contact{
	private static int uniqueId = 1;
	private int id = 0;
	private String name = "";
	private String notes = "";

	public ContactImpl(String name, String notes){
		this.name = name;
		this.notes = notes;
		this.id = uniqueId;
		uniqueId ++;
	}

	public ContactImpl(int id, String name, String notes){
		this.id = id;
		this.name = name;
		this.notes = notes;
	}


	public ContactImpl(){

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

//adds notes to a contact
	public void addNotes(String note){
		String completeNotes = this.notes + " " + note;
		this.notes = completeNotes;
	}

//returns each contacts details as a String
	@Override
	public String toString(){
		return "ID = " + id + ", name = " + name + ", notes = " + notes;
	}


}