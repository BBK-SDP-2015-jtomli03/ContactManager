import java.util.*;
import java.io.*;
import java.lang.String;


public class ContactManagerImpl{
	private String fileName = "";
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private Set<Contact> contacts = new HashSet<Contact>();


	public ContactManagerImpl(String fileName, List<Meeting> meetings, Set<Contact> contacts){
		this.fileName = fileName;
		this.meetings = meetings;
		this.contacts = contacts;
	}

//empty constructor for testing******************************************
	public ContactManagerImpl(String fileName){
		this.fileName = fileName;
	}



//returns a unique contact ID
	private int getNewContactID(){
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

//Saves all data to the file
	private void flush(){
		PrintWriter out = null;
		try{
			File file = new File(getFileName());
			out = new PrintWriter(new FileWriter(file, false));
			for(Contact contact : contacts){
				String line = "c," + contact.getId() + "," + contact.getName() + "," + contact.getNotes() + "\r\n";
				out.write(line);
			}
			out.flush();
		}catch (FileNotFoundException ex){
			System.out.println("Cannot write to file " + getFileName());
		}catch (IOException ex){
			ex.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}

/**writes a contact to the file
	private void writeContactToFile(ContactImpl contact){
		PrintWriter out = null;
		try{
			File file = new File(getFileName());
			out = new PrintWriter(new FileWriter(file, true));
			String line = "c," + contact.getId() + "," + contact.getName() + "," + contact.getNotes() + "\r\n";
			out.write(line);
		
			contacts.clear();
			out.flush();
		}catch (FileNotFoundException ex){
			System.out.println("Cannot write to file " + getFileName());
		}catch (IOException ex){
			ex.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}

	}*/




//gets the data from the file and transfers it to list<Meeting> and Set<Contact>
	private void getData(){

		File file = new File (this.fileName);
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

//checks to see if the contact is already in the set
	private boolean containsContact(Set<Contact> contactList, Contact newContact){
		for(Contact contactInContacts : contactList){
					if(newContact.getId() == contactInContacts.getId()){
						return true;
					}
		}
		return false;
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


//gets the filename
	private String getFileName(){
		return this.fileName;
	}

//creates a new contact and adds them to the set
	private void addNewContact(String name, String notes){
		ContactImpl newContact = new ContactImpl(getNewContactID(),name, notes);
		addContactToSet(newContact);
	}

//adds contacts to Set<Contact>
	private void addContactToSet(Contact contact){
		contacts.add(contact);
	}

//gets a contact whos name contains a particular string
	private Set<Contact> getContacts(String name){
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

//main method
	public static void main(String[] args){
		ContactManagerImpl jos = new ContactManagerImpl("/Users/Jo/Documents/contacts.txt");
		//jos.getData();
		//jos.printContacts();
		//System.out.println(jos.getFileName());

		ContactImpl newContact = new ContactImpl(jos.getNewContactID(),"Andy"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactID(),"Bill"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactID(),"Callum"," notes.");
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
		newContact = new ContactImpl(jos.getNewContactID(),"Jake"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactID(),"Liam"," notes.");
		jos.addContactToSet(newContact);
		newContact = new ContactImpl(jos.getNewContactID(),"Pablo"," notes.");
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

		/**newContact = new ContactImpl("Will","Wills notes.");
		jos.addContactToSet(newContact);
		System.out.println("Testing to see whether Will has a unique ID....");
		jos.printContacts();
		jos.writeContactToFile(newContact);
		System.out.println("Will should be written to the file and then we have him back again....");
		jos.getData();
		jos.printContacts();*/

		

	}




}