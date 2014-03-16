import java.util.Calendar;
import java.util.Set;

public abstract class MeetingImpl implements Meeting, Comparable<Meeting>{
	private int id;
	private Calendar date;
	private Set<Contact> contacts;
	
	
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

/**
*Compares the dates and times of meetings to sort them into chronological order
*
*@param Meeting the meeting to be compared
*@return int used to sort the meetings into chronological order; 
*        0 = same date/time, -1 = this meeting is chronologically before the meeting being compared, 
*        1 = this meeting is chronologically after the meeting being compared.
*/
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

}