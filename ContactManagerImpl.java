import java.util.*;
import java.io.*;
import java.net.URL;

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

		//if there are no notes in the txt document for this contact, this prevents an exception by initialising the notes
		if(contactArray[2].length() == 0){
			contactArray[2] = "";
		}
		
		Contact newContact = new ContactImpl(Integer.parseInt(contactArray[0]), contactArray[1], contactArray[2]);
		
		contacts.add(newContact);

	}

//Prints the list of contacts
	private void printContacts(){
		for (Contact contact: contacts){
			int id = contact.getId();
			String name = contact.getName();
			String notes = contact.getNotes();
			System.out.println(id + ", " + name + ", " + notes);
			
		}
	}

//gets the list of contacts
	private Set<Contact> getContacts(){
		return contacts;
	}

//main method
	public static void main(String[] args){
		ContactManagerImpl jos = new ContactManagerImpl("/Users/Jo/Documents/contacts.txt");
		jos.getData();
		jos.printContacts();
	}

//adds contacts to Set<Contact>
	private void addContactToSet(){
		
	}




}