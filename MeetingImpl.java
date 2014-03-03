import java.util.Calendar;
import java.util.Set;

public class MeetingImpl implements Meeting{
	private int id;
	private Calendar date;
	private Set<Contact> contacts;
	

	public MeetingImpl(int id, Calendar date, Set<Contact> contacts){
		this.id = id;
		this.date = date;
		this.contacts = contacts;
	}

	public MeetingImpl(){
		
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

//returns each meetings details as a String
	@Override
	public String toString(){
		String contactsInMeeting = " ";
		for(Contact contact: contacts){
			contactsInMeeting = contactsInMeeting + "\n" + contact.toString();
		}
		return "Meeting ID " + id + " on; " + date.getTime() + "\n" + "Contacts;" + contactsInMeeting + "\n";
	}


}