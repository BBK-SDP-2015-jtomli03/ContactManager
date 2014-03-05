import java.util.Calendar;
import java.util.Set;

public class PastMeetingImpl extends MeetingImpl implements Meeting{
	private String notes = "";

public PastMeetingImpl(int id, Calendar date, Set<Contact> contacts, String notes){
		super();
		this.notes = notes;
	}

//returns the notes from the meeting
public String getNotes(){
	return notes;
}	

	

}