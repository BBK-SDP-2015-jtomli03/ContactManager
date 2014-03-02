import java.util.*;
import java.io.*;


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

//writes a contact to the file
	private void writeContactToFile(ContactImpl contact){
		PrintWriter out = null;
		try{
			File file = new File(getFileName());
			out = new PrintWriter(new FileWriter(file, true));
			String line = "\n" + "c," + contact.getId() + "," + contact.getName() + "," + contact.getNotes();
			out.append(line);
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
		contacts.add(newContact);
		}

		if(contactArray[0].equals(meeting)){

		}

	}

//Prints the list of contacts
	private void printContacts(){
		for (Contact contact: contacts){	
			System.out.println(contact.toString());
		}
	}

//gets the list of contacts
	private Set<Contact> getContacts(){
		return contacts;
	}

//gets the filename
	private String getFileName(){
		return this.fileName;
	}

//adds contacts to Set<Contact>
	private void addContactToSet(Contact contact){
		contacts.add(contact);
	}

//main method
	public static void main(String[] args){
		ContactManagerImpl jos = new ContactManagerImpl("/Users/Jo/Documents/contacts.txt");
		jos.getData();
		jos.printContacts();
		//System.out.println(jos.getFileName());
		//ContactImpl contact = new ContactImpl(3,"Jon","Jons notes.");
		//jos.addContactToSet(contact);
		//jos.printContacts();
		//contact.addNotes("Has this added??");
		//jos.writeContactToFile(contact);
		

	}




}