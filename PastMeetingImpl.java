import java.util.Calendar;
import java.util.Set;

public class PastMeetingImpl extends MeetingImpl implements PastMeeting{
	private String notes = "";

public PastMeetingImpl(Meeting meeting, String notes){
		super(meeting.getId(), meeting.getDate(), meeting.getContacts());
		this.notes = notes;
	}

public PastMeetingImpl(int id, Calendar date, Set<Contact> contacts, String notes){
		super(id, date, contacts);
		this.notes = notes;
	}

//returns the notes from the meeting
	@Override
	public String getNotes(){
		return notes;
	}

//sets the notes for the meeting
	public void setNotes(String notes){
		this.notes = this.notes + " " + notes;
	}	
	
}