import java.util.Calendar;
import java.util.Set;

public class PastMeetingImpl extends MeetingImpl implements PastMeeting{
	private String notes = "";

public PastMeetingImpl(FutureMeeting futureMeeting, String notes){
		super(futureMeeting.getId(), futureMeeting.getDate(), futureMeeting.getContacts());
		this.notes = notes;
	}

//returns the notes from the meeting
	@Override
	public String getNotes(){
		return notes;
	}	
}