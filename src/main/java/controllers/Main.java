package controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bethecoder.ascii_table.ASCIITable;
import com.bethecoder.ascii_table.impl.CollectionASCIITableAware;
import com.bethecoder.ascii_table.spec.IASCIITableAware;
import com.google.common.base.Optional;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import models.Activity;
import models.User;
import utils.BinarySerializer;
import utils.JSONSerializer;
import utils.Serializer;
import utils.XMLSerializer;

public class Main {

	PacemakerAPI paceAPI;

	public Main() throws Exception
	{
		//XML Serializer
		//File  datastore = new File("datastore.xml");
		//Serializer serializer = new XMLSerializer(datastore);

		//JSON Serializer
		//File  datastore = new File("datastore.json");
		//Serializer serializer = new JSONSerializer(datastore);

		//Binary Serializer
	    File  datastore = new File("datastore.xml");
	    Serializer serializer = new XMLSerializer(datastore);

		paceAPI = new PacemakerAPI(serializer);
		if (datastore.isFile())
		{
			paceAPI.load();
		}
	}

	@Command(description = "Create a new User")
	public void createUser(@Param(name = "first name") String firstName, @Param(name = "last name") String lastName,
			@Param(name = "email") String email, @Param(name = "password") String password) {
		paceAPI.createUser(firstName, lastName, email, password);
	}

	@Command(description = "Get a Users details")
	public void getUser(@Param(name = "email") String email) {
		User user = paceAPI.getUserByEmail(email);
		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<User>(Arrays.asList(user), "firstname", "lastname", "email"); 
		ASCIITable.getInstance().printTable(asciiTableAware);
	}

	@Command(description = "Get by User Id")
	public void getUserByID(@Param(name = "id") String id) {
		User user = paceAPI.getUser(id);
		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<User>(Arrays.asList(user), "firstname", "lastname", "email"); 
		ASCIITable.getInstance().printTable(asciiTableAware);
	}

	
	@Command(description="Get all users details")
	public void getUsers ()
	{
		List<User> userList = new ArrayList<User> (paceAPI.getUsers());
		
		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<User>(userList, "email", "firstname", "lastname"); 
		ASCIITable.getInstance().printTable(asciiTableAware);
	}

	@Command(description = "Delete a User")
	public void deleteUser(@Param(name = "email") String email) {
		User user = paceAPI.getUserByEmail(email);

		paceAPI.deleteUser(user.id);
	}

	@Command(description = "Add an activity")
	public void addActivity(@Param(name = "user-id") String id, @Param(name = "type") String type,
			@Param(name = "location") String location, @Param(name = "distance") double distance) {
		Optional<User> user = Optional.fromNullable(paceAPI.getUser(id));
		if (user.isPresent()) {
			paceAPI.createActivity(id, type, location, distance);
		}
	}
	
	@Command(description = "List Activities for user")
	public void listActivitiesForUser(@Param(name = "user-id") String id) {
		
		Collection<Activity> activites = paceAPI.getActivities(id);
		List<Activity> activityList = new ArrayList<Activity> (activites);
		System.out.println("acts created : "+activityList.size());
		IASCIITableAware asciiTableAware = new CollectionASCIITableAware<Activity>(activityList,
				 "id",
				 "type", "location", "distance");
				 System.out.println(ASCIITable.getInstance().getTable(asciiTableAware)); 
				 
		System.out.println("type \t"+ "location \t"+ "distance");
		System.out.println("-------------------------------------------");
		for (Iterator iterator = activityList.iterator(); iterator.hasNext();) {
			Activity activity = (Activity) iterator.next();
			System.out.println( activity.type+"\t"+activity.location+ "\t\t"+activity.distance);
		}
		
	}

	@Command(description = "Add Location to an activity")
	public void addLocation(@Param(name = "activity-id") String id, @Param(name = "latitude") float latitude,
			@Param(name = "longitude") float longitude) {
		Optional<Activity> activity = Optional.fromNullable(paceAPI.getActivity(id));
		if (activity.isPresent()) {
			paceAPI.addLocation(activity.get().id, latitude, longitude);
		}
	}
	
	@Command(description = "load")
	public void load() throws Exception
	{
		paceAPI.load();
	}
	
	@Command(description = "store")
	public void store() throws Exception 
	{
		paceAPI.store();
	}
	
	@Command(description = "change file format")
	public void changeFileFormat() throws Exception
	{

		paceAPI.load();
		if (paceAPI.getSerializer() instanceof XMLSerializer) 
		{
			paceAPI.setSerializer(new JSONSerializer(new File("datastore.json")));
			paceAPI.store();
			new File("datastore.xml").delete();
			return;
		}
		if (paceAPI.getSerializer() instanceof JSONSerializer) 
		{
			paceAPI.setSerializer(new XMLSerializer(new File("datastore.xml")));
			paceAPI.store();
			new File("datastore.json").delete();
			return;
		}
		
	}
	

	public static void main(String[] args) throws Exception {
		Main main = new Main();

		Shell shell = ShellFactory.createConsoleShell("pm", "Welcome to pacemaker-console - ?help for instructions", main);
		shell.commandLoop();

		main.paceAPI.store();
	}
}
