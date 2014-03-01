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
}