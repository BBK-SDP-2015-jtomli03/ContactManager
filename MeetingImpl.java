import java.util.Calendar;
import java.util.Set;

public abstract class MeetingImpl implements Meeting, Comparable<Meeting>{
	private int id;
	private Calendar date;
	private Set<Contact> contacts;
	private String notes = "";
	
	
	public MeetingImpl(int id, Calendar date, Set<Contact> contacts){
		this.id = id;
		this.date = date;
		this.contacts = contacts;
	}

//returns the meeting ID
	@Override
	public int getId(){
		return id;
	}

//returns the date of the meeting
	@Override
	public Calendar getDate(){
		return date;
	}

//returns the details of the people that attended the meeting
	@Override
	public Set<Contact> getContacts(){
		return contacts;
	}

//to sort meetings into chronological order
	@Override
	public int compareTo(Meeting meeting){
		if(this.getDate().equals(meeting.getDate())){
			return 0;
		}
		else if(this.getDate().before(meeting.getDate())){
			return -1;
		}
		else{
			return 1;
		}
	}

//gets meetings empty notes
	public String getNotes(){
		return notes;
	}

}