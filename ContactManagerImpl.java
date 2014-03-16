import java.util.*;
import java.io.*;
import java.text.*;
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
		if(!new File(FILENAME).createNewFile()){
			getData();
		}  
	}catch (IOException ioe) {
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
				line = "c|" + contact.getId() + "|" + contact.getName() + "|" + contact.getNotes() + "\r\n";
				out.write(line);
			}
			for(Meeting meeting : meetings){	
				line = "m|"  + meeting.getId() + "|" + getDateAsString(meeting) + "|" + getMeetingNotes(meeting) + "|" +  contactIdsToString(meeting) + "\r\n";
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

//add a future meeting
	@Override
	public int addFutureMeeting(Set<Contact> contacts, Calendar date) throws IllegalArgumentException{
		if(date.getTime().before(currentTime.getTime())){
			throw new IllegalArgumentException("Either the meeting date is in the past, or a contact is not in your contacts list.");
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
		else if(meeting.getDate().getTime().after(currentTime.getTime())) throw new IllegalArgumentException("This is a future meeting, not a past meeting.");{
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
		else if(meeting.getDate().getTime().before(currentTime.getTime())) throw new IllegalArgumentException("This is a past meeting, not a future meeting.");{
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
        	throw new IllegalArgumentException("This contact does not exist in your contacts.");
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
        	throw new IllegalArgumentException("This contact does not exist in your contacts.");
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
			throw new NullPointerException("Either your contacts, date, or notes are null.");
		}
		else if (contacts.isEmpty()){
			throw new IllegalArgumentException("Your list of contacts is empty.");
		}
		else if(!contactsExistIn(contacts)) throw new IllegalArgumentException("This contact does not exist in your contacts.");{
			PastMeeting pastMeeting = new PastMeetingImpl(getNewMeetingId(), date, contacts, text);
			meetings.add(pastMeeting);
		}
	}

//adds notes to past meeting
	@Override
    public void addMeetingNotes(int id, String text) throws IllegalArgumentException, NullPointerException, IllegalStateException{
    	Meeting meeting = getMeeting(id);
    	if(text == null){
    		throw new NullPointerException("The notes are null.");
    	}
    	else if(meeting == null){
    		throw new IllegalArgumentException("This meeting does not exist.");
    	}
		else if(meeting.getDate().getTime().after(currentTime.getTime())) throw new IllegalStateException("This meeting is a future meeting, not a past meeting.");{
			PastMeetingImpl pastMeeting = (PastMeetingImpl) meeting;
			pastMeeting.setNotes(text);	
		}
    }

//creates a new contact and adds them to the set
	@Override
	public void addNewContact(String name, String notes) throws NullPointerException{
		if(name == null || notes == null){
			throw new NullPointerException("Either the name or notes are null.");
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

/**
*Gets the data from the txt file, creates the contact and meeting objects, and adds them to List<Meeting> and Set<Contact>
*/
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

/**
*Closes the data source
*
*@param Reader the reader used to read the txt file
*/
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

/**
*Once a line is read from the file this method creates an object of type Contact 
*or type Meeting and adds it to the list of meetings or set of contacts.
*
*@param String the line read from the txt file
*/
	private void createObject(String line){
		String[] contactArray = mySplit(line, "|");
		String contact = "c";
		String meeting = "m";
		if(contactArray[3].length() == 0){
			contactArray[3] = "";
		}
		if(contactArray[0].equals(contact)){
			Contact newContact = new ContactImpl(Integer.parseInt(contactArray[1]), contactArray[2], contactArray[3]);
			addContact(newContact);
		}
		if(contactArray[0].equals(meeting)){
			Calendar meetingCalendar = getCalendar(contactArray);
         	if(meetingCalendar.getTime().before(currentTime.getTime())){
         		Meeting pastMeeting = new PastMeetingImpl(Integer.parseInt(contactArray[1]), meetingCalendar, getContactsInMeeting(contactArray), contactArray[3]);
         		addMeeting(pastMeeting);
         	}
         	else{
         		Meeting futureMeeting = new FutureMeetingImpl(Integer.parseInt(contactArray[1]), meetingCalendar, getContactsInMeeting(contactArray));
         		addMeeting(futureMeeting);
         	}
		}
	}

/**
*Converts a meetings Calendar date into a string for storing in the txt file
*
*@param Meeting the meeting for which the date is to be converted
*@return String the calendar date in string format
*/
	private String getDateAsString(Meeting meeting){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = sdf.format(meeting.getDate().getTime());
		return dateString;
	}

/**
*Gets the notes from a past meeting, and initialises notes for a future meeting to allow all meetings 
*to be conveniently written to the txt file
*
*@param Meeting the meeting for which the notes are being written to txt file
*@return String the meeting notes or an empty string if the meeting is a future meeting
*/
	private String getMeetingNotes(Meeting meeting){
		String meetingNotes = " ";
			if (meeting.getDate().getTime().before(currentTime.getTime())){
				PastMeeting pastMeeting = (PastMeeting) meeting;
				meetingNotes = pastMeeting.getNotes();
			}
		return meetingNotes;
	}

/**
*Returns a String of contact ids that were in a particular meeting so that they can be stored in a txt file
*
*@param Meeting the meeting being saved to txt file
*@return String a string of all the contacts id's that attended the meeting
*/
	private String contactIdsToString(Meeting meeting){
		String result = "";
		int numOfContacts = 0;
		Set<Contact> contactsInMeeting = meeting.getContacts();
		for(Contact contact : contactsInMeeting){
			numOfContacts ++;
			result = result + contact.getId() + "|";
		}
		result = numOfContacts + "|" + result;
		return result;
	}

/**
*Adds a meeting object to the meetings list
*
*@param Meeting the meeting to be added to the meeting list
*/
	private void addMeeting(Meeting meeting){
		if(meetings.isEmpty()){
			meetings.add(meeting);
		}
		else if(!containsMeeting(meetings, meeting)){
			meetings.add(meeting);
		}
	}

/**
*Converts a date and time in String format, to a Calendar date and time
*
*@param String[] the array in which the String format of the date/time is currently stored
* NOTE; this will only work if the date/time String is at position [2] in the array.
*@return Calendar the date/time of a meeting in calendar format
*/
	private Calendar getCalendar(String[] contactArray){
		Calendar meetingCalendar = Calendar.getInstance();
		try{String dateAsString = contactArray[2];
         	DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         	Date stringToDate = (Date)formatDate.parse(dateAsString);
       		meetingCalendar.setTime(stringToDate);
       	} catch (ParseException e){
       		System.out.println("ParseException" + e);
       	}
       	return meetingCalendar;
    }

/**
*Gets the contacts in a meeting from the array created from reading the txt document and returns them as contact objects in a set
* NOTE; this will only work if the contacts start at position [5] in the array. Note; position [4] in the array gives us the number 
*of contacts stored in the array to allow us to know how many there are that need to be added to the return set.
*
*@param String[] the array in which the id's of the contacts for a meeting are currently stored
*@return Set<Contact> a set of contacts that attended the meeting
*/
	private Set<Contact> getContactsInMeeting(String[] contactArray){
		Set<Contact> contactsInMeeting = new HashSet<Contact>();
		int numberOfContacts = Integer.parseInt(contactArray[4]) + 4;
		for(int count = 5; count <= numberOfContacts; count++){
			int contactId = Integer.parseInt(contactArray[count]);
			contactsInMeeting.add(getContact(contactId));
		}
		return contactsInMeeting;
	}

/**
*Adds a contact to the ContactManagerImpl set of contacts
*
*@param Contact the contact to be added
*/
	private void addContact(Contact contact){
		if(contacts.isEmpty()){
			contacts.add(contact);
		}
		else if(!containsContact(contacts, contact)){
			contacts.add(contact);
		}
	}

/**
*Returns a unique meeting ID
*
*@return int a unique meeting ID
*/
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

/**
* Returns a unique contact ID
*
* @return int a unique contact ID
*/
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

/**
* Returns all the meetings on a particular date in chronological order
*
* @param Calendar the date for which we want to get all scheduled meetings
* @return List<MeetingImpl> a list of meetings on a particular date in chronological order by time
*/
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

/**
* Returns all the meetings with a particular contact in chronological order
*
* @param Contact the contact for which we want to view all meetings we have scheduled with them
* @return List<MeetingImpl> the meetings scheduled with the contact in chronological order
*/
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

/**
* Checks to see whether a list of meetings contains a particular meeting (used to check if the meetings list in
* ContactManagerImpl contains a particular meeting)
* 
* @param List<Meeting> the list of meetings to be checked to see if it contains a perticular meeting
* @param Meeting the meeting we are checking to see is in the list of meetings
* @return boolean true if the meetings list contains the meeting, and false if not
*/
	private boolean containsMeeting(List<Meeting> meetingList, Meeting newMeeting){
		for(Meeting meetingInMeetings : meetingList){
					if(newMeeting.getId() == meetingInMeetings.getId()){
						return true;
					}
		}
		return false;
	}

/**
* Returns a contact by their contact ID
*
* @param int the contacts ID
* @return Contact the contact corresponding to the contact ID
*/
	private Contact getContact(int id){
		for(Contact contact : contacts){
			if(contact.getId() == id){
				return contact;
			}
		}
		return null;
	}

/**
* Checks to see whether a set of contacts contain a particular contact (used to check whether ContactManagerImpl contains
* a particular contact in the contacts list)
*
* @param Set<Contact> the contacts list to be checked
* @return boolean true if the list contains the contact, and false if not
*/
	private boolean containsContact(Set<Contact> contactList, Contact newContact){
		for(Contact contactInContacts : contactList){
					if(newContact.getId() == contactInContacts.getId()){
						return true;
					}
		}
		return false;
	}

/**
* Checks whether a contact exists in a given set of contacts
* 
* @param Set<Contact> the set of contacts to be checked 
* @return boolean true if the set of contacts contains the contact, false if not
*/
	private boolean contactsExistIn(Set<Contact> contacts){
		for (Contact contactInContacts : contacts){
				if (!containsContact(this.contacts, contactInContacts)) {
					return false;
				}
			}
		return true;
	}

/**
* Adds contacts to the ContactManagerImpl contacts set
*
* @param Contact the contact to be added
*/
	private void addContactToSet(Contact contact){
		contacts.add(contact);
	}

/**
* Splits a string into an array using a regex as a marker for where the string should be split
*
* @param String the string to be split
* @param String the regex to be used to identify at which points to split the string
* @return String [] an array containing the split string 
*/
	private String[] mySplit(String string, String regex){
    	Vector<String> result = new Vector<String>();
    	int start = 0;
    	int position = string.indexOf(regex);
    	while (position >= start){
        	if(position>start){
            	result.add(string.substring(start,position));
        	}
        	start = position + regex.length();
        	position = string.indexOf(regex,start); 
    	}
    	if (start<string.length()){
        	result.add(string.substring(start));
    	}
    	String[] array = result.toArray(new String[0]);
   		return array;
	}
	
}