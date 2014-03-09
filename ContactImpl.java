public class ContactImpl implements Contact{
	private int id = 0;
	private String name = "";
	private String notes = "";

	public ContactImpl(String name, String notes){
		this.name = name;
		this.notes = notes;
	}

	public ContactImpl(int id, String name, String notes){
		this.id = id;
		this.name = name;
		this.notes = notes;
	}


//gets the ID of the contact
	@Override
	public int getId(){
		return id;
	}

//gets the name of the contact
	@Override
	public String getName(){
		return name;
	}

//gets any notes relating to a contact
	@Override
	public String getNotes(){
		return notes;
	}

//adds notes to a contact
	@Override
	public void addNotes(String note){
		String completeNotes = notes + " " + note;
		notes = completeNotes;
	}

//returns each contacts details as a String
	@Override
	public String toString(){
		return "ID;" + id + " Name; " + name + " Notes;" + notes;
	}


}