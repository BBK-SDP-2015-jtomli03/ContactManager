import java.util.*
import java.io.*

public class ContactManagerImpl{
	private List<Meeting> meetings = new ArrayList<Meeting>();
	private Set<Contact> contacts = new HashSet<Contact>();

	public ContactManagerImpl(List<Meeting> meetings, Set<Contact> contacts){
		this.meetings = meetings;
		this.contacts = contacts;
	}

	public ContactManagerImpl(){

	}

//adds contacts to Set<Contact>
	private void addContactToSet(){
		
	}

//gets the data from the file and transfers it to list<Meeting> and Set<contact>
	private void getData(){
		File file = new File("contacts.csv.numbers");
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null)
			{
				//write code here to split etc
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

//Once a line is read from the file this method splits the different variables and creates an object of type Contact
	private void createContact(String line){
		String[] contactArray = line.split(",");
		if (contactArray[2].length() == 0)
		{
			contactArray[2] = "";
		}
		
		ContactImpl newContact = new ContactImpl(Integer.parseInt(contactArray[0]), contactArray[1], contactArray[2]);
		

		int contactsid = newContact.getId();
		System.out.println(contactsid);
		
	}

//main method
	public static void main(String[] args){
		ContactManagerImpl jos = new ContactManagerImpl();
		jos.getData();
	}

	


















}