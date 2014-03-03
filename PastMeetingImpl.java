import java.util.Calendar;
import java.util.Set;

public class PastMeetingImpl extends MeetingImpl{
	private String notes = "";

public PastMeetingImpl(String notes){
		super();
		this.notes = notes;
	}

//returns the notes from the meeting
public String getNotes(){
	return notes;
}	

	

}