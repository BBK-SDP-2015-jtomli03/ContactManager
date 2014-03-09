import java.util.*;
import java.io.*;
import java.lang.String;
import java.util.Calendar;
import java.lang.Comparable;
import java.text.SimpleDateFormat;


public class ContactManagerImpl implements ContactManager{
	private static final String FILENAME = "contacts.txt";
	Calendar currentTime = new GregorianCalendar();
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private Set<Contact> contacts = new HashSet<Contact>();


	public ContactManagerImpl(){
		try{
			if (!new File(FILENAME).createNewFile()){
				getData();
			}  
		} catch (IOException ioe) {
      	ioe.printStackTrace();
    	}		
	}


//Saves all data to the file
	@Override
	public void flush(){
		PrintWriter out = null;
		String line = "";
		try{
			File file = new File(FILENAME);
			out = new PrintWriter(new FileWriter(file, false));
			for(Contact contact : contacts){
				line = "c," + contact.getId() + "," + contact.getName() + "," + contact.getNotes() + "\r\n";
				out.write(line);
			}
			for(Meeting meeting : meetings){
				MeetingImpl meetingImpl = (MeetingImpl) meeting;
				line = "m," + meetingImpl.getId() + "," + meetingImpl.getDate() + "," + contactIdsToString(meetingImpl) + "," + meetingImpl.getNotes() + "\r\n";
				out.write(line);
			}
			out.flush();
		}catch (FileNotFoundException ex){
			System.out.println("Cannot write to file " + FILENAME);
		}catch (IOException ex){
			ex.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}

//returns a String of contact ids that were in a particular meeting
	private String contactIdsToString(Meeting meeting){
		String result = "";
		Set<Contact> contactsInMeeting = meeting.getContacts();
		for(Contact contact : contactsInMeeting){
				result = result + contact.getId() + ",";
		}
		return result;
	}


//gets the data from the file and transfers it to list<Meeting> and Set<Contact>
	private void getData(){

		File file = new File (FILENAME);
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null)
			{
				
				createObject(line);

			}
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("File" + file + " Does not exist.");
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			closeReader(in);
		}
	}

//Closes the data source
	private void closeReader(Reader reader){
		try
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

/**Once a line is read from the file this method splits the different variables and creates an object of type Contact and
adds it to the Hashset contacts.*/
	private void createObject(String line){
		String[] contactArray = line.split(",");
		String contact = "c";
		String meeting = "m";
		/**if there are no notes in the txt document for this contact/meeting, 
		this prevents an exception by initialising the notes/meeting*/
		if(contactArray[3].length() == 0){
			contactArray[3] = "";
		}

		if(contactArray[0].equals(contact)){
			Contact newContact = new ContactImpl(Integer.parseInt(contactArray[1]), contactArray[2], contactArray[3]);
			if(contacts.isEmpty()){
				contacts.add(newContact);
			}
			else if(!containsContact(contacts, newContact)){
				contacts.add(newContact);
			}
		}

		if(contactArray[0].equals(meeting)){

		}
	}


//add a future meeting
	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) throws IllegalArgumentException{
		if(date.getTime().before(currentTime.getTime())){
			throw new IllegalArgumentException();
		}
		else if(!contactsExistIn(contacts))throw new IllegalArgumentException();{	
		Meeting newMeeting = new FutureMeetingImpl(getNewMeetingId(), date, contacts);
		meetings.add(newMeeting);			
		return newMeeting.getId();
		}
	}


//gets a past meeting by its ID
	@Override
	public PastMeeting getPastMeeting(int id) throws IllegalArgumentException{
		Meeting meeting = getMeeting(id);
		if(meeting == null){
			return null;
		}
		else if(meeting.getDate().getTime().after(currentTime.getTime())) throw new IllegalArgumentException();{
			PastMeeting pastMeeting = (PastMeeting) meeting;
			return pastMeeting;
		}
	}


//gets a future meeting by its ID
	@Override
	public FutureMeeting getFutureMeeting(int id) throws IllegalArgumentException{
		Meeting meeting = getMeeting(id);
		if(meeting == null){
			return null;
		}
		else if(meeting.getDate().getTime().before(currentTime.getTime())) throw new IllegalArgumentException();{
			FutureMeeting futureMeeting = (FutureMeeting) meeting;
			return futureMeeting;
		}
	}


//returns a meeting by ID
	@Override
	public Meeting getMeeting(int id){
		for(Meeting meeting : meetings){
			if(meeting.getId() == id){
				return meeting;
			}
		}
		return null;
	}


//returns the list of future meetings scheduled with this contact in chronological order
 	@Override
    public List<Meeting> getFutureMeetingList(Contact contact) throws IllegalArgumentException{
        if(!containsContact(this.contacts, contact)){
        	throw new IllegalArgumentException();
        }
      	List<Meeting> returnMeetings = new ArrayList<Meeting>();
      	List<MeetingImpl> sortedMeetings = getAllMeetings(contact);
      	for(MeetingImpl meeting : sortedMeetings){
      		if(meeting.getDate().getTime().after(currentTime.getTime())){
      			returnMeetings.add(meeting);
      		}
      	}
        return returnMeetings;
    }


//returns the list of chronologically sorted meetings scheduled on this date
 	@Override
    public List<Meeting> getFutureMeetingList(Calendar date){
        
      	List<Meeting> returnMeetings = new ArrayList<Meeting>();
      	List<MeetingImpl> sortedMeetings = getAllMeetings(date);
      	for(MeetingImpl meeting : sortedMeetings){
      			returnMeetings.add(meeting);
      	}
        return returnMeetings;
    }


//returns the list of past meetings scheduled with this contact in chronological order
 	@Override
    public List<PastMeeting> getPastMeetingList(Contact contact) throws IllegalArgumentException{
        if(!containsContact(this.contacts, contact)){
        	throw new IllegalArgumentException();
        }
      	List<PastMeeting> returnMeetings = new ArrayList<PastMeeting>();
      	List<MeetingImpl> sortedMeetings = getAllMeetings(contact);
      	for(MeetingImpl meeting : sortedMeetings){
      		if(meeting.getDate().getTime().before(currentTime.getTime())){
      			PastMeetingImpl pastMeeting = (PastMeetingImpl) meeting;
      			returnMeetings.add(pastMeeting);
      		}
      	}
        return returnMeetings;
    }


//creates a new record for a meeting that has already taken place
	@Override
	public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) throws IllegalArgumentException, NullPointerException{
		if(contacts == null || date == null || text == null) {
			throw new NullPointerException();
		}
		else if (contacts.isEmpty()){
			throw new IllegalArgumentException();
		}
		else if(!contactsExistIn(contacts)) throw new IllegalArgumentException();{
			PastMeeting pastMeeting = new PastMeetingImpl(getNewMeetingId(), date, contacts, text);
			meetings.add(pastMeeting);
		}
	}


//adds notes to past meeting
	@Override
    public void addMeetingNotes(int id, String text) throws IllegalArgumentException, NullPointerException, IllegalStateException{
    	Meeting meeting = getMeeting(id);
    	if(text == null){
    		throw new NullPointerException();
    	}
    	else if(meeting == null){
    		throw new IllegalArgumentException();
    	}
		else if(meeting.getDate().getTime().after(currentTime.getTime())) throw new IllegalStateException();{
			PastMeetingImpl pastMeeting = (PastMeetingImpl) meeting;
			pastMeeting.setNotes(text);	
		}
    }

//creates a new contact and adds them to the set
	@Override
	public void addNewContact(String name, String notes) throws NullPointerException{
		if(name == null || notes == null){
			throw new NullPointerException();
		}
		ContactImpl newContact = new ContactImpl(getNewContactId(),name, notes);
		addContactToSet(newContact);
	}

//returns a set of contacts corresponding to the ID's entered
	@Override
	public Set<Contact> getContacts(int... ids) throws IllegalArgumentException{
		Set<Contact> contactsWithId = new HashSet<Contact>();
		for (Contact contact : contacts){
			int numOfIds = ids.length;
			for(int count = 0; count < numOfIds; count++){
				Contact exists = getContact(ids[count]);
				if(exists == null){
					throw new IllegalArgumentException("Contact does not exist.");
				}
				if(ids[count] == contact.getId()){
					contactsWithId.add(contact);
				}
			}
		}
		return contactsWithId;
	}

//gets a contact whos name contains a particular string
	@Override
	public Set<Contact> getContacts(String name) throws NullPointerException{
		if(name == null){
			throw new NullPointerException("name == null");
		}
		Set<Contact> contactsWithString = new HashSet<Contact>();
		int searchIndex = -1;
		for(Contact contact : contacts){
			searchIndex = contact.getName().indexOf(name);
			if(searchIndex != -1){
				contactsWithString.add(contact);
			}
		}
		return contactsWithString;
	}


//returns a unique meeting ID
	private int getNewMeetingId(){
		int max = 0;
		if (meetings.isEmpty()){
			return 1;
		}
		else{
			for(Meeting meeting : meetings){
				if (meeting.getId() > max){
					max = meeting.getId();
				}
			}
			return max + 1;
		}
	}


//returns a unique contact ID
	private int getNewContactId(){
		int max = 0;
		if (contacts.isEmpty()){
			return 1;
		}
		else{
			for(Contact contact : contacts){
				if (contact.getId() > max){
					max = contact.getId();
				}
			}
			return max + 1;
		}
	}

//returns all the meetings on a particular date in chronological order
    private List<MeetingImpl> getAllMeetings(Calendar date){
    	int dayOfYear = date.get(Calendar.DAY_OF_MONTH) + date.get(Calendar.MONTH) + date.get(Calendar.YEAR);
    	List<MeetingImpl> meetingsOnDate = new ArrayList<MeetingImpl>();
    	for(Meeting meeting : meetings){
    			Calendar dateOfThisMeeting = meeting.getDate();
    			int dayOfMeeting = dateOfThisMeeting.get(Calendar.DAY_OF_MONTH) + dateOfThisMeeting.get(Calendar.MONTH) + dateOfThisMeeting.get(Calendar.YEAR);
    			if (dayOfMeeting == dayOfYear){
    				MeetingImpl downCastMeeting = (MeetingImpl) meeting;
    				meetingsOnDate.add(downCastMeeting);
    			}	
    	}
    	Collections.sort(meetingsOnDate);
    	return meetingsOnDate;
    }

//returns all the meetings with a particular contact in chronological order
    private List<MeetingImpl> getAllMeetings(Contact contact){
    	List<MeetingImpl> meetingsWithContact = new ArrayList<MeetingImpl>();
    	for(Meeting meeting : meetings){
    		for(Contact meetingContact : meeting.getContacts()){
    			if (meetingContact.getId() == contact.getId()){
    				MeetingImpl downCastMeeting = (MeetingImpl) meeting;
    				meetingsWithContact.add(downCastMeeting);
    			}
    		}
    	}
    	Collections.sort(meetingsWithContact);
    	return meetingsWithContact;
    }

//returns a contact by ID
	private Contact getContact(int id){
		for(Contact contact : contacts){
			if(contact.getId() == id){
				return contact;
			}
		}
		return null;
	}

//checks to see whether ContactManager contains this contact in the contacts list
	private boolean containsContact(Set<Contact> contactList, Contact newContact){
		for(Contact contactInContacts : contactList){
					if(newContact.getId() == contactInContacts.getId()){
						return true;
					}
		}
		return false;
	}

//checks whether a contact exists in the given set
	private boolean contactsExistIn(Set<Contact> contacts){
		for (Contact contactInContacts : contacts){
				if (!containsContact(this.contacts, contactInContacts)) {
					return false;
				}
			}
		return true;
	}

//adds contacts to Set<Contact>
	private void addContactToSet(Contact contact){
		contacts.add(contact);
	}

}