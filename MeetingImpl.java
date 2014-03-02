import java.util.Calendar;
import java.util.Set;

public class MeetingImpl{
	private int id;
	private Calendar date;
	Set<Contact> contacts;

	public MeetingImpl(int id, Calendar date, Set<Contact> contacts){
		this.id = id;
		this.date = date;
		this.contacts = contacts;
	}

//returns the meeting ID
	public int getId(){
		return id;
	}

//returns the date of the meeting
	public Calendar getDate(){
		return date;
	}

//returns the details of the people that attended the meeting
	public Set<Contact> getContacts(){
		return contacts;
	}

}