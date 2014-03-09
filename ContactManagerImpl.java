import java.util.*;
import java.io.*;
import java.lang.String;
import java.util.Calendar;
import java.lang.Comparable;


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
		try{
			File file = new File(FILENAME);
			out = new PrintWriter(new FileWriter(file, false));
			for(Contact contact : contacts){
				String line = "c," + contact.getId() + "," + contact.getName() + "," + contact.getNotes() + "\r\n";
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
				
				createContact(line);

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
	private void createContact(String line){
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

/*******************CONTACTS********************************/

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

//Prints the list of contacts
	private void printContacts(){
		for (Contact contact: contacts){	
			System.out.println(contact.toString());
		}
	}

//Prints the list of contacts in the Set contactsWithString
	private void printContacts(Set<Contact> contactsWithString){
		for (Contact contact: contactsWithString){	
			System.out.println(contact.toString());
		}
	}


//creates a new contact and adds them to the set
	@Override
	public void addNewContact(String name, String notes){
		ContactImpl newContact = new ContactImpl(getNewContactId(),name, notes);
		addContactToSet(newContact);
	}

//adds contacts to Set<Contact>
	private void addContactToSet(Contact contact){
		contacts.add(contact);
	}

//gets a contact whos name contains a particular string
	@Override
	public Set<Contact> getContacts(String name){
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

//returns a contact by ID
	private Contact getContact(int id){
		for(Contact contact : contacts){
			if(contact.getId() == id){
				return contact;
			}
		}
		return null;
	}

//returns a set of contacts corresponding to the ID's entered
	@Override
	public Set<Contact> getContacts(int... ids){
		Set<Contact> contactsWithId = new HashSet<Contact>();
		for (Contact contact : contacts){
			int numOfIds = ids.length;
			for(int count = 0; count < numOfIds; count++){
				if(ids[count] == contact.getId()){
					contactsWithId.add(contact);
				}
			}
		}
		return contactsWithId;
	}

/********************MEETINGS*********************/	


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


//Returns the list of future meetings scheduled with this contact in chronological order
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






 	

	

    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) throws NullPointerException {
        return null;
    }

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
        return null;
    }



//Prints the list of meetings
	private void printMeetings(){
		for (Meeting meeting: meetings){	
			System.out.println(meeting.toString());
		}
	}








//main method
	public static void main(String[] args){
		ContactManagerImpl jos = new ContactManagerImpl();

		//ContactManagerImpl jos = new ContactManagerImpl();
		//jos.getData();
		//jos.printContacts();
		//System.out.println(jos.getFileName());

		/**ContactImpl newContact = new ContactImpl(jos.getNewContactId(),"Andy"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactId(),"Bill"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactId(),"Callum"," notes.");
		jos.addContactToSet(newContact);
		System.out.println("Printing before writing.....");
		jos.printContacts();
		jos.flush();
		System.out.println("Have written the contacts to file.....");
		jos.printContacts();
		jos.getData();
		System.out.println("Have now got the data back from the file.....");
		jos.printContacts();
		System.out.println("Adding new contacts to the set, writing to file......");
		newContact = new ContactImpl(jos.getNewContactId(),"Jake"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactId(),"Liam"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactId(),"Pablo"," notes.");
		jos.addContactToSet(newContact);
		jos.flush();
		jos.printContacts();
		System.out.println("Getting data again.....");
		jos.getData();
		jos.printContacts();
		System.out.println("Writing data to file again.....");
		jos.flush();
		System.out.println("Printing contacts as in the set....");
		jos.addNewContact("Newton", "Notes");
		jos.printContacts();
		jos.getContact(6).addNotes("Has this added??");
		System.out.println("Printing contacts with the string l....");
		jos.printContacts(jos.getContacts("l"));
		System.out.println("Checking they are still in Set contacts....");
		jos.printContacts();
		jos.flush();*/

		/**newContact = new ContactImpl("Will","Wills notes.");
		jos.addContactToSet(newContact);
		System.out.println("Testing to see whether Will has a unique ID....");
		jos.printContacts();
		jos.writeContactToFile(newContact);
		System.out.println("Will should be written to the file and then we have him back again....");
		jos.getData();
		jos.printContacts();*/
		//System.out.println("Should print contacts 1, 2, and 5!");
		//jos.printContacts(jos.getContacts(1,2,5));
		//System.out.println("Checking they are still in Set contacts....");
		//jos.printContacts();

		//Calendar calendar = new GregorianCalendar();
		Calendar date = new GregorianCalendar(2014, 10, 10, 10, 00);
		Calendar dateOne = new GregorianCalendar(2016, 10, 10, 10, 00);
		Calendar dateTwo = new GregorianCalendar(2015, 10, 10, 10, 00);
    	/*System.out.println(calendar.getTime());
    	date.set(2015, 0, 21);
    	System.out.println(date.getTime());
    	if(calendar.getTime().before(date.getTime())){
    		System.out.println("Date is before!!");
    	}*/
    	//Calendar calendarNow = Calendar.getInstance();
    	//System.out.println(calendarNow.getTime());
    	/*now.set(2020, 2, 20, 12, 00);
    	System.out.println(now.getTime());*/

    	//System.out.println("Printing a list of meetings.....");
    	//Calendar dateOfMeeting = new GregorianCalendar(2014, 2, 6, 21, 24);
    	//jos.addFutureMeeting(jos.getContacts(1,2,3), dateOfMeeting);
    	//System.out.println(jos.getMeeting(1));
    	Calendar anotherMeeting = new GregorianCalendar(2013, 11, 20, 12, 00);
    	//jos.printContacts();
    	int id = jos.addFutureMeeting(jos.getContacts(5,2,3), date);
    	jos.addFutureMeeting(jos.getContacts(2,3), dateOne);
    	jos.addFutureMeeting(jos.getContacts(5,2), dateTwo);
    	//System.out.println(id);
    	//jos.printMeetings();
    	//System.out.println(jos.getPastMeeting(1));
    	//System.out.println(jos.getPastMeeting(1).getNotes());
    	//jos.printMeetings();
    	//jos.printMeetings();
    	//System.out.println("adding past meeting.....");
    	String string = "More notes!!!";
    	
    	
    	Set<Contact> josContacts = new HashSet<Contact>();
    	Contact numForty = new ContactImpl(40, "Jo", "Hello");
    	//josContacts.add(numForty);
    	josContacts.add(jos.getContact(2));
    	jos.addNewPastMeeting(josContacts, anotherMeeting, string);
    	
    	
    	//System.out.println(jos.containsContact(jos.contacts, numForty));
    	//jos.contactsExistIn(josContacts);

    	//jos.printMeetings();
    	
    	//jos.addMeetingNotes(2, "notes" );
    	//jos.printMeetings();
    	List<Meeting> josList = jos.getFutureMeetingList(jos.getContact(2));
    	for(Meeting meetingInList : josList){
    		System.out.println(meetingInList.toString());
    	}
    	System.out.println("All meetings......");
    	jos.printMeetings();
    	


    	
    	
    	//Set<Contact> newSet = new HashSet<Contact>();
		//newSet.add(jos.getContact(1));
    	//ContactImpl newContact = new ContactImpl(jos.getNewContactId(),"test"," notes.");
    	//newSet.add(newContact);

    	//System.out.println(jos.contactExists(newSet));
    	//jos.printMeetings();
    	//System.out.println(jos.getMeeting(3));
    	//jos.getPastMeeting(3).getNotes();
    	//System.out.println("Printing a list of meetings.....");
    	//jos.printMeetings();
    		
    	
    	//String notesForMeeting = "Will this work???";
    	//Meeting pastMeeting = new PastMeetingImpl();
    	//jos.meetings.add(pastMeeting); 
    	/*jos.printMeetings();
    	System.out.println(jos.getPastMeeting(2));*/

    	//problem with changing from a future to a past and then printing getPastMeeting - cos thinks its still a future meeting??
    	

		

		

	}




}