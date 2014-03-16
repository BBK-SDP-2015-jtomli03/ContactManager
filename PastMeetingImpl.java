import java.util.Calendar;
import java.util.Set;
import java.lang.Exception;

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


@Override	
public String getNotes(){
	return notes;
}

/**
*Sets the notes for a meeting
*
*@param String the notes to be set to the meeting
*@throws IllegalArgumentexception if the notes are null
*/
public void setNotes(String notes)throws IllegalArgumentException{
	if(notes == null){
		throw new IllegalArgumentException("The notes are null.");
	}else{
		this.notes = this.notes + " " + notes;
	}
}	
	
}