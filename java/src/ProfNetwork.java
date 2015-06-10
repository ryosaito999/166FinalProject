/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


//Group 24
//Ryota Saito ID:861057726 rsait001
//Rachel Law  ID:861071722 rlaw001


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));

   /**
	* Creates a new instance of ProfNetwork
	*
	* @param hostname the MySQL or PostgreSQL server hostname
	* @param database the name of the database
	* @param authorisedUser the user name used to login to the database
	* @param password the user login password
	* @throws java.sql.SQLException when failed to make a connection.
	*/
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

	  System.out.print("Connecting to database...");
	  try{
		 // constructs the connection URL
		 String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
		 System.out.println ("Connection URL: " + url + "\n");

		 // obtain a physical connection
		 this._connection = DriverManager.getConnection(url, user, passwd);
		 System.out.println("Done");
	  }catch (Exception e){
		 System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
		 System.out.println("Make sure you started postgres on this machine");
		 System.exit(-1);
	  }//end catch
   }//end ProfNetwork

   /**
	* Method to execute an update SQL statement.  Update SQL instructions
	* includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	*
	* @param sql the input SQL string
	* @throws java.sql.SQLException when update failed
	*/
   public void executeUpdate (String sql) throws SQLException {
	  // creates a statement object
	  Statement stmt = this._connection.createStatement ();

	  // issues the update instruction
	  stmt.executeUpdate (sql);

	  // close the instruction
	  stmt.close ();
   }//end executeUpdate

   /**
	* Method to execute an input query SQL instruction (i.e. SELECT).  This
	* method issues the query to the DBMS and outputs the results to
	* standard out.
	*
	* @param query the input query string
	* @return the number of rows returned
	* @throws java.sql.SQLException when failed to execute the query
	*/
   public int executeQueryAndPrintResult (String query) throws SQLException {
	  // creates a statement object
	  Statement stmt = this._connection.createStatement ();

	  // issues the query instruction
	  ResultSet rs = stmt.executeQuery (query);

	  /*
	   ** obtains the metadata object for the returned result set.  The metadata
	   ** contains row and column info.
	   */
	  ResultSetMetaData rsmd = rs.getMetaData ();
	  int numCol = rsmd.getColumnCount ();
	  int rowCount = 0;

	  // iterates through the result set and output them to standard out.
	  boolean outputHeader = true;
	  while (rs.next()){
	 if(outputHeader){
		for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
		}
		System.out.println();
		outputHeader = false;
	 }
		 for (int i=1; i<=numCol; ++i)
			System.out.print (rs.getString (i) + "\t");
		 System.out.println ();
		 ++rowCount;
	  }//end while
	  stmt.close ();
	  return rowCount;
   }//end executeQuery

   /**
	* Method to execute an input query SQL instruction (i.e. SELECT).  This
	* method issues the query to the DBMS and returns the results as
	* a list of records. Each record in turn is a list of attribute values
	*
	* @param query the input query string
	* @return the query result as a list of records
	* @throws java.sql.SQLException when failed to execute the query
	*/
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
	  // creates a statement object
	  Statement stmt = this._connection.createStatement ();

	  // issues the query instruction
	  ResultSet rs = stmt.executeQuery (query);

	  /*
	   ** obtains the metadata object for the returned result set.  The metadata
	   ** contains row and column info.
	   */
	  ResultSetMetaData rsmd = rs.getMetaData ();
	  int numCol = rsmd.getColumnCount ();
	  int rowCount = 0;

	  // iterates through the result set and saves the data returned by the query.
	  boolean outputHeader = false;
	  List<List<String>> result  = new ArrayList<List<String>>();
	  while (rs.next()){
		  List<String> record = new ArrayList<String>();
		 for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
		 result.add(record);
	  }//end while
	  stmt.close ();
	  return result;
   }//end executeQueryAndReturnResult

   /**
	* Method to execute an input query SQL instruction (i.e. SELECT).  This
	* method issues the query to the DBMS and returns the number of results
	*
	* @param query the input query string
	* @return the number of rows returned
	* @throws java.sql.SQLException when failed to execute the query
	*/
   public int executeQuery (String query) throws SQLException {
	   // creates a statement object
	   Statement stmt = this._connection.createStatement ();

	   // issues the query instruction
	   ResultSet rs = stmt.executeQuery (query);

	   int rowCount = 0;

	   // iterates through the result set and count nuber of results.
	   if(rs.next()){
		  rowCount++;
	   }//end while
	   stmt.close ();
	   return rowCount;
   }

   /**
	* Method to fetch the last value from sequence. This
	* method issues the query to the DBMS and returns the current
	* value of sequence used for autogenerated keys
	*
	* @param sequence name of the DB sequence
	* @return current value of a sequence
	* @throws java.sql.SQLException when failed to execute the query
	*/
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
	* Method to close the physical connection if it is open.
	*/
   public void cleanup(){
	  try{
		 if (this._connection != null){
			this._connection.close ();
		 }//end if
	  }catch (SQLException e){
		 // ignored.
	  }//end try
   }//end cleanup


   /**
	* The main execution method
	*
	* @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	*/
   public static void main (String[] args) {
	  if (args.length != 3) {
		 System.err.println (
			"Usage: " +
			"java [-classpath <classpath>] " +
			ProfNetwork.class.getName () +
			" <dbname> <port> <user>");
		 return;
	  }//end if

	  Greeting();
	  ProfNetwork esql = null;
	  try{
		 // use postgres JDBC driver.
		 Class.forName ("org.postgresql.Driver").newInstance ();
		 // instantiate the ProfNetwork object and creates a physical
		 // connection.
		 String dbname = args[0];
		 String dbport = args[1];
		 String user = args[2];
		 esql = new ProfNetwork (dbname, dbport, user, "");


		 boolean keepon = true;
		 while(keepon) {
			// These are sample SQL statements
			System.out.println("MAIN MENU");
			System.out.println("---------");
			System.out.println("1. Create user");
			System.out.println("2. Log in");
			System.out.println("9. < EXIT");
			String authorisedUser = null;
			switch (readChoice()){
			   case 1: CreateUser(esql); break;
			   case 2: authorisedUser = LogIn(esql); break;
			   case 9: keepon = false; break;
			   default : System.out.println("Unrecognized choice!"); break;
			}//end switch
			if (authorisedUser != null) {
			  boolean usermenu = true;
			  while(usermenu) {
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("0. View Profile");
				System.out.println("1. Accept/Reject Connection Requests");
				System.out.println("2. Update Profile");
				System.out.println("3. Write a new message");
				System.out.println("4. Send Friend Request");
				System.out.println("5. Search Users");
				System.out.println("6. View all Friends");
				System.out.println("7. View All Messages");
				System.out.println("8 Log out");


				switch (readChoice()){
				   case 0: displayProfile(esql, authorisedUser);   break;                
				   case 1: AcceptRejectConnectionRequests(esql, authorisedUser); break;
				   case 2: UpdateProfile(esql, authorisedUser); break;
				   case 3: SendMsg(esql, authorisedUser); break;
				   case 4: SendRequest(esql, authorisedUser); break;
				   case 5: lookUpUser(esql, authorisedUser); break;
				   case 6: ViewFriends(esql, authorisedUser); break;
				   case 7: viewMessages(esql, authorisedUser); break;
				   case 8: usermenu = false; break;
				   default : System.out.println("Unrecognized choice!"); break;
				}
			  }
			}

		   
		 }//end while
	  }catch(Exception e) {
		 System.err.println (e.getMessage ());
	  }finally{
		 // make sure to cleanup the created table and close the connection.
		 try{
			if(esql != null) {
			   System.out.print("Disconnecting from database...");
			   esql.cleanup ();
			   System.out.println("Done\n\nBye !");
			}//end if
		 }catch (Exception e) {
			// ignored.
		 }//end try
	  }//end try
   }//end main

   public static void Greeting(){
	  System.out.println(
		 "\n\n*******************************************************\n" +
		 "              User Interface      	               \n" +
		 "*******************************************************\n");
   }//end Greeting

   /*
	* Reads the users choice given from the keyboard
	* @int
	**/


  public static int readChoice() {
	int input;
	// returns only if a correcdateOfBirtht value is given.
	do {
	 System.out.print("Please make your choice: ");
	 try { // read the integer, parse it and break.
	  input = Integer.parseInt(in.readLine());
	  break;
	 }catch (Exception e) {
	  System.out.println("Your input is invalid!");
	  continue;
	 }//end try
	}while (true);
	return input;
}//end readChoice


   /*
	* Creates a new user with privided login, passowrd and phoneNum
	* An empty block and contact list would be generated and associated with a user
	**/
   
   public static String inputNoNull(){
	   String input = "";
	   do {
		  try{
			input = in.readLine();
		 }catch (Exception e) {
		  System.out.println("Your input is invalid!");
		  continue;
		 }

		 if(input.equals("")){
			System.out.print("Do not leave this field blank: ");
		 } 
		 
	   }while(input.equals(""));

	  return input.trim();

  }
	public static String enterDate(){


	   String input = "";
	   do {
		  try{
			input = in.readLine();
		 }catch (Exception e) {
		  System.out.println("Your input is invalid!");
		  continue;
		 }

		 if(!input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")){
			System.out.print("\tInvalid date entered: ");
		 } 
		 
	   }while(!input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})"));

	  return input.trim();
	}

   public static void CreateUser(ProfNetwork esql){
	  try{
		 System.out.print("\tEnter user login: ");
		 String login = inputNoNull();
		 System.out.print("\tEnter user password: ");
		 String password = inputNoNull();
		 System.out.print("\tEnter user email: ");
		 String email = inputNoNull();
		 System.out.print("\tEnter your full name: ");
		 String name = inputNoNull();
		 System.out.print("\tEnter your birthday in MM/DD/YYYY format with slashes: ");
		 String bday = enterDate();

		 System.out.print("\tEnter your current company: ");
		 String company = inputNoNull();
		 System.out.print("\tEnter your current role: ");
		 String role = inputNoNull();

		 System.out.print("\tEnter your current location: ");
		 String location = inputNoNull();
		 System.out.print("\tEnter your current jobs start date : ");
		 String startdate = enterDate();
		 System.out.print("\tEnter your current job's end date (Type 12/30/9999 if unkown) : ");
		 String enddate = enterDate();

		 System.out.print("\tEnter the name of institution you attended: ");
		 String institue = inputNoNull();
		 System.out.print("\tEnter your major taken: ");
		 String major = inputNoNull();
		 System.out.print("\tEnter your current degree:");
		 String degree = inputNoNull();
		 System.out.print("\tEnter your college entrance date:  ");
		 String startdateCollege = enterDate();
		 System.out.print("\tEnter your graduation date: ");
		 String enddateGrad = enterDate();




		 //Creating empty contact\block lists for a user
		 String query = String.format("INSERT INTO USR (userId, password, email, name, dateOfBirth) VALUES ('%s','%s','%s','%s', '%s')", login, password, email, name, bday);
		 esql.executeUpdate(query);

		 query = String.format("INSERT INTO WORK_EXPR (userId, company, role, location, startDate, endDate) VALUES ('%s','%s','%s','%s', '%s' , '%s')", login, company, role,  location, startdate, enddate);
		 esql.executeUpdate(query);

		 query = String.format("INSERT INTO EDUCATIONAL_DETAILS (userId , instituitionName, major, degree, startdate, enddate) VALUES ('%s','%s','%s','%s', '%s', '%s' )",login, institue, major, degree ,startdateCollege, enddateGrad);
		 esql.executeUpdate(query);

		 System.out.println ("User successfully created!");
	  }catch(Exception e){
		 System.err.println (e.getMessage ());
	  }
   }//end

   /*
	* Check log in credentials for an existing user
	* @return User login or null is the user does not exist
	**/
   public static String LogIn(ProfNetwork esql){
	  try{
		 System.out.print("\tEnter user login: ");
		 String login = in.readLine();
		 System.out.print("\tEnter user password: ");
		 String password = in.readLine();

		 String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
		 int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
		 return null;
	  }catch(Exception e){
		 System.err.println (e.getMessage ());
		 return null;
	  }
   }//end

// Rest of the functions definition go in here

   

   /* Functions we need to implement */
   // User code goes here!

	/**
	   @return accepted friends list
	*/
	public static List<List<String> > FriendList(ProfNetwork esql, String authorisedUser) {

	String query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", authorisedUser, authorisedUser);
		List<List<String>> fList=new ArrayList<List<String> >();
		try{

		fList = esql.executeQueryAndReturnResult(query);

		} catch (Exception e) {
		System.err.println (e.getMessage ());
		}  

		return fList;
   }



	/**
	   @params authorisedUser is same thing as authorisedUser
	   @return accepted friends list
	*/
	  public static List<List<String> > ViewFriends(ProfNetwork esql, String authorisedUser){
		//Accepted Friends
			System.out.println("\n\tAccepted Friends");
			System.out.println("\t=========================");
		
			List<List<String> > fl = new ArrayList<List<String> >();
			fl = FriendList(esql, authorisedUser );
			for ( int i = 0; i < fl.size(); ++i){
			    System.out.print("\t");
			  System.out.println(fl.get(i).get(0));
			}
			System.out.println("");

		//Pending Friends
		System.out.println("\tPending Friends");
		System.out.println("\t=========================");
		List<List<String> > pfl = new ArrayList<List<String> >();
		String query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Request' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Request'", authorisedUser, authorisedUser);
		try {
		    pfl = esql.executeQueryAndReturnResult(query);
		} catch (Exception e) {
		    System.err.println (e.getMessage());
		}
		for ( int i = 0; i < pfl.size(); ++i){
		    System.out.print("\t");
		    System.out.println(pfl.get(i).get(0));
		}
		System.out.println("");

		//Rejected Friends
		System.out.println("\tRejected Friends");
		System.out.println("\t=========================");
		List<List<String> > rfl = new ArrayList<List<String> >();
		query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Reject' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Reject'", authorisedUser, authorisedUser);
		try {
		    rfl = esql.executeQueryAndReturnResult(query);
		} catch (Exception e) {
		    System.err.println (e.getMessage());
		}
		for ( int i = 0; i < rfl.size(); ++i){
		    System.out.print("\t");
		    System.out.println(rfl.get(i).get(0));
		}
		System.out.println("");
		
		
		return fl;
	  }

	/**
	   @params authorisedUser is same thing as authorisedUser
	   @return accepted friends list 
	*/

	public static List<List<String> > ViewFriendsSimple( ProfNetwork esql, String authorisedUser){

		System.out.println("\nCurrent Friends");
		System.out.println("=========================");
	
		List<List<String> > fl = new ArrayList<List<String> >();
		fl = FriendList(esql, authorisedUser );
		for ( int i = 0; i < fl.size(); ++i){
		  System.out.println(fl.get(i).get(0));
		}
		System.out.println("");

		return fl;

	}




	private static void updateField(ProfNetwork esql, String authorisedUser , String field, String tableName){

	  try{

		System.out.println("Enter your new " + field + ": ");
		String newMail = in.readLine();


		//System.out.println( "UPDATE USR SET " + field + " = '%s' WHERE userId = '%s'");
		String query = String.format("UPDATE " + tableName + " SET " + field + " = '%s' WHERE userId = '%s'", newMail, authorisedUser);
		int userNum = esql.executeQuery(query);

		if( userNum  > 0){
		  System.out.println("Your new " + field +  " is: " + newMail);
		}

		else{
		  System.out.println("Something went wrong!");          
		}

		return;
		 }catch (Exception e) {
		System.err.println (e.getMessage ());
	   }
	}

	private static void updateGeneralUser(ProfNetwork esql, String authorisedUser ){

		try {
		System.out.println("What would you like to change?");
		System.out.println("---------");
		System.out.println("1. Email");
		System.out.println("2. Full Name");
		System.out.println("3. Date of Birth");
		System.out.println("4. Menu");
		System.out.println("5. Password");

	   

	  switch (readChoice()){                   
		 case 1: updateField(esql, authorisedUser, "email" , "USR"); break;
		 case 2: updateField(esql, authorisedUser, "name", "USR"); break;
		 case 3: updateField(esql, authorisedUser, "birthday", "USR"); break;
		 case 4: return;
		 case 5: changePass(esql, authorisedUser);

		 default : System.out.println("Unrecognized choice!"); break;
		 }
	   }catch (Exception e) {
		System.err.println (e.getMessage ());
	  }
	}


	private static void updateWork(ProfNetwork esql, String authorisedUser ){

		try {
		System.out.println("\nWhat would you like to change?");
		System.out.println("---------");
		System.out.println("1. Company");
		System.out.println("2. Role");
		System.out.println("3. Location");
		System.out.println("4. Start Date");
		System.out.println("5. End Date");
		System.out.println("6. Menu");

		

	  switch (readChoice()){                   
		 case 1: updateField(esql, authorisedUser, "company", "WORK_EXPR"); break;
		 case 2: updateField(esql, authorisedUser, "role", "WORK_EXPR"); break;
		 case 3: updateField(esql, authorisedUser, "location", "WORK_EXPR"); break;
		 case 4: updateField(esql, authorisedUser, "startDate", "WORK_EXPR"); break;
		 case 5: updateField(esql, authorisedUser, "endDate", "WORK_EXPR"); break;
		 case 6: return;

		 default : System.out.println("Unrecognized choice!"); break;
		 }
	   }catch (Exception e) {
		System.err.println (e.getMessage ());
	  }

	}

	private static void updateEdu(ProfNetwork esql, String authorisedUser ){

		 try {
		System.out.println("\nWhat would you like to change?");
		System.out.println("---------");
		System.out.println("1. institution");
		System.out.println("2. major");
		System.out.println("3. degree");
		System.out.println("4. Start Date");
		System.out.println("5. End Date");
		System.out.println("6. Menu");

		

	  switch (readChoice()){                   
		 case 1: updateField(esql, authorisedUser, "instituitionName","EDUCATIONAL_DETAILS"); break;
		 case 2: updateField(esql, authorisedUser, "major" ,"EDUCATIONAL_DETAILS"); break;
		 case 3: updateField(esql, authorisedUser, "degree" ,"EDUCATIONAL_DETAILS"); break;
		 case 4: updateField(esql, authorisedUser, "startdate" ,"EDUCATIONAL_DETAILS"); break;
		 case 5: updateField(esql, authorisedUser, "enddate","EDUCATIONAL_DETAILS"); break;
		 case 6: return;

		 default : System.out.println("Unrecognized choice!"); break;
		 }
	   }catch (Exception e) {
		System.err.println (e.getMessage ());
	  }

	}



	public static void UpdateProfile( ProfNetwork esql, String authorisedUser){
		try{
		while(true){

		  System.out.println("\nWhat would you like to change?");
		  System.out.println("---------");
		  System.out.println("1. User Information");
		  System.out.println("2. Work Experience");
		  System.out.println("3. Educational Details");
		  System.out.println("4. Menu");
		  
			switch (readChoice()){                   
			   case 1: updateGeneralUser(esql, authorisedUser); break;
			   case 2: updateWork(esql, authorisedUser); break;
			   case 3: updateEdu(esql, authorisedUser); break;
			   case 4: return;
			   default : System.out.println("Unrecognized choice!"); break;
			 }
		 }
		}catch (Exception e) {
		System.err.println (e.getMessage ());
	   }
	} 


	public static void changePass(ProfNetwork esql, String authorisedUser){

	  String pwd;
	  String newPwd;
	  String newPwd2;
	  try{
		  System.out.println("Enter 'y' to change your password. Enter any other key to return to menu: ");
		  String confirm = in.readLine();


		  if(!confirm.equals("y") ){
			return;
		  }
		  else{
			
			  System.out.println("Enter your current password: ");
			  pwd = in.readLine();

			  String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", authorisedUser, pwd);
			  int userNum = esql.executeQuery(query);

			  if( userNum <= 0){
				System.out.println("\tIncorrect password!");
				return;
			  }

			  else{

				  System.out.println("Enter your new password ");
				  newPwd = in.readLine();
				  System.out.println("ReEnter your new password: ");
				  newPwd2 = in.readLine();

				  if( newPwd.equals(newPwd2)){
					  query = String.format("UPDATE USR SET password = '%s' WHERE userId='%s' ", newPwd, authorisedUser);
					  userNum = esql.executeQuery(query);
					  System.out.println(userNum);

					  if(userNum > 0){
						System.out.println("\tpassword changed!");
					  }
				  }
			  }
		  }
	  }catch(Exception e){
		 System.err.println (e.getMessage ());
	  }
	}


	public static void SendRequest(ProfNetwork esql, String authorisedUser){
	    try {
		String requester = authorisedUser.trim();
		String query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
		List<List<String>> result = esql.executeQueryAndReturnResult(query);
		
		List<String> tier1_friends = new ArrayList<String>();
		List<String> tier2_friends = new ArrayList<String>();
		List<String> tier3_friends = new ArrayList<String>();
		List<String> all_users = new ArrayList<String>();
		//System.out.println("1st connection friends: ");
		//Add Tier1 Friends
		for (int i=0; i<result.size(); i++) {
		    tier1_friends.add(result.get(i).get(0).trim());
		    //System.out.println(result.get(i).get(0));
		}
		//Add Tier2 Friends
		//System.out.println("2nd connection friends: ");
		for (int i=0; i<tier1_friends.size(); i++) {
		    requester = tier1_friends.get(i);
		    query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
		    result = esql.executeQueryAndReturnResult(query);
		    for (int j=0; j<result.size(); j++) {
			tier2_friends.add(result.get(j).get(0).trim());
			//System.out.println(result.get(j).get(0));
		    }
		}
		//Add Tier3 Friends
		//System.out.println("3rd connection friends: ");
		for (int i=0; i<tier2_friends.size(); i++) {
		    requester = tier2_friends.get(i);
		    query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
		    result = esql.executeQueryAndReturnResult(query);
		    for (int j=0; j<result.size(); j++) {
			tier3_friends.add(result.get(j).get(0).trim());
			//System.out.println(result.get(j).get(0));
		    }
		}
		requester = authorisedUser.trim();
		//Make all users list
		//System.out.println("All users list: ");
		query = String.format("Select userId FROM USR");
		result = esql.executeQueryAndReturnResult(query);
		for (int i=0; i<result.size(); i++) {
		    all_users.add(result.get(i).get(0).trim());
		    //System.out.println(result.get(i).get(0));
		}
		
		
		//Make valid connections list
		boolean addanyoneflag = false;
		List<String> valid_connections = new ArrayList<String>();
		if (tier1_friends.size() < 5) { // up to 5 connections, can do anyone
		    addanyoneflag = true;
		    System.out.println("You only have " + tier1_friends.size() + " friends.");
		    System.out.println("You have less than 5 friends. Add anyone you want!");
		}
		else {
		    System.out.println("Valid users to add");
		    //System.out.println("CUR AUTHROISED USER IS: " + authorisedUser);
		    for (int i=0; i<tier2_friends.size(); i++) {
			String tmpfriend = tier2_friends.get(i).trim();
			if (!tier1_friends.contains(tmpfriend) && !valid_connections.contains(tmpfriend) && !tmpfriend.trim().equals(requester)) {
			    valid_connections.add(tmpfriend);
			    System.out.println(tmpfriend);
			}
		    }
		    for (int i=0; i<tier3_friends.size(); i++) {
			String tmpfriend = tier3_friends.get(i).trim();
			if (!tier1_friends.contains(tmpfriend) && !valid_connections.contains(tmpfriend) && !tmpfriend.trim().equals(requester)) {
			    valid_connections.add(tmpfriend);
			    System.out.println(tmpfriend);
			}
		    }
		}
		
		//Pending Friends
		List<List<String> > pfl_2d = new ArrayList<List<String> >();
		List<String> pfl = new ArrayList<String>();
		query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Request' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Request'", requester, requester);
		pfl_2d = esql.executeQueryAndReturnResult(query);
		for ( int i = 0; i < pfl_2d.size(); ++i){
		    pfl.add((pfl_2d.get(i).get(0)).trim());
		    //System.out.println(pfl_2d.get(i).get(0));
		}
				
		//Rejected Friends
		List<List<String> > rfl_2d = new ArrayList<List<String> >();
		List<String> rfl = new ArrayList<String>();
		query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Reject' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Reject'", authorisedUser, authorisedUser);
		rfl_2d = esql.executeQueryAndReturnResult(query);
		for ( int i = 0; i < rfl_2d.size(); ++i){
		    rfl.add(rfl_2d.get(i).get(0).trim());
		    //System.out.println(rfl_2d.get(i).get(0));
		}
	      
		
		//Adding the connection
		System.out.println("Type in a user to add: ");
		String input = in.readLine();
		input = input.trim();
		//System.out.println("Your input was: " + input);
		//System.out.println("Requester was: " + requester);
		
		if (addanyoneflag==false) {
		    //System.out.println("Can only add from up to 3 levels of connections");
		    if (valid_connections.contains(input)) {
			if (pfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("There is already a pending friend request");
			}
			else if (rfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("The friend request was previously rejected");
			}
			else {
			    query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", requester, input);
			}
			//System.out.println("Query will be " + query);
			esql.executeUpdate(query);
			System.out.println("You have sent a friend request to " +  input);
		    }
		    else {
			System.out.println("Invalid input");
			return;
		    }
		}
		else {
		    //System.out.println("Add anyone not yourself or already friends");
		    if (!input.equals(requester) && !tier1_friends.contains(input) && all_users.contains(input)) {
			if (pfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("There is already a pending friend request");
			}
			else if (rfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("The friend request was previously rejected");
			}
			else {
			    query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", requester, input);
			}
			//System.out.println("Query will be " + query);
			esql.executeUpdate(query);
			System.out.println("You have sent a friend request to " +  input);
		    }
		    else {
			System.out.println("Invalid input");
			return;
		    }
		}
	    }catch (Exception e) {
		System.err.println (e.getMessage());
	    }
	    return;
	}


    public static void SendRequestToSpecificUser(ProfNetwork esql, String authorisedUser, String requestedUser){
	try {
	    String requester = authorisedUser.trim();
	    String query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
	    List<List<String>> result = esql.executeQueryAndReturnResult(query);
	    
	    List<String> tier1_friends = new ArrayList<String>();
	    List<String> tier2_friends = new ArrayList<String>();
	    List<String> tier3_friends = new ArrayList<String>();
	    List<String> all_users = new ArrayList<String>();
	    //System.out.println("1st connection friends: ");
	    //Add Tier1 Friends
	    for (int i=0; i<result.size(); i++) {
		tier1_friends.add(result.get(i).get(0).trim());
		//System.out.println(result.get(i).get(0));
	    }
	    //Add Tier2 Friends
	    //System.out.println("2nd connection friends: ");
	    for (int i=0; i<tier1_friends.size(); i++) {
		requester = tier1_friends.get(i);
		query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
		result = esql.executeQueryAndReturnResult(query);
		for (int j=0; j<result.size(); j++) {
		    tier2_friends.add(result.get(j).get(0).trim());
		    //System.out.println(result.get(j).get(0));
		}
	    }
	    //Add Tier3 Friends
	    //System.out.println("3rd connection friends: ");
	    for (int i=0; i<tier2_friends.size(); i++) {
		requester = tier2_friends.get(i);
		query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Accept' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Accept'", requester, requester);
		result = esql.executeQueryAndReturnResult(query);
		for (int j=0; j<result.size(); j++) {
		    tier3_friends.add(result.get(j).get(0).trim());
		    //System.out.println(result.get(j).get(0));
		}
	    }
	    requester = authorisedUser.trim();
	    //Make all users list
	    //System.out.println("All users list: ");
	    query = String.format("Select userId FROM USR");
	    result = esql.executeQueryAndReturnResult(query);
	    for (int i=0; i<result.size(); i++) {
		all_users.add(result.get(i).get(0).trim());
		//System.out.println(result.get(i).get(0));
	    }
	    
	    
	    //Make valid connections list
	    boolean addanyoneflag = false;
	    List<String> valid_connections = new ArrayList<String>();
	    if (tier1_friends.size() < 5) { // up to 5 connections, can do anyone
		addanyoneflag = true;
		//System.out.println("You only have " + tier1_friends.size() + " friends.");
		//System.out.println("You have less than 5 friends. Add anyone you want!");
	    }
	    else {
		System.out.println("Valid users to add");
		//System.out.println("CUR AUTHROISED USER IS: " + authorisedUser);
		for (int i=0; i<tier2_friends.size(); i++) {
		    String tmpfriend = tier2_friends.get(i).trim();
		    if (!tier1_friends.contains(tmpfriend) && !valid_connections.contains(tmpfriend) && !tmpfriend.trim().equals(requester)) {
			valid_connections.add(tmpfriend);
			//System.out.println(tmpfriend);
		    }
		}
		for (int i=0; i<tier3_friends.size(); i++) {
		    String tmpfriend = tier3_friends.get(i).trim();
		    if (!tier1_friends.contains(tmpfriend) && !valid_connections.contains(tmpfriend) && !tmpfriend.trim().equals(requester)) {
			valid_connections.add(tmpfriend);
			//System.out.println(tmpfriend);
		    }
		}
	    }
	    
	    //Pending Friends
	    List<List<String> > pfl_2d = new ArrayList<List<String> >();
	    List<String> pfl = new ArrayList<String>();
	    query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Request' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Request'", requester, requester);
	    pfl_2d = esql.executeQueryAndReturnResult(query);
	    for ( int i = 0; i < pfl_2d.size(); ++i){
		pfl.add((pfl_2d.get(i).get(0)).trim());
		//System.out.println(pfl_2d.get(i).get(0));
	    }
	    
	    //Rejected Friends
	    List<List<String> > rfl_2d = new ArrayList<List<String> >();
	    List<String> rfl = new ArrayList<String>();
	    query = String.format("Select C.connectionId FROM CONNECTION_USR C WHERE C.userId='%s' AND C.status='Reject' UNION Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Reject'", authorisedUser, authorisedUser);
	    rfl_2d = esql.executeQueryAndReturnResult(query);
	    for ( int i = 0; i < rfl_2d.size(); ++i){
		rfl.add(rfl_2d.get(i).get(0).trim());
		//System.out.println(rfl_2d.get(i).get(0));
	    }
	    
	    
	    //Adding the connection
	    System.out.println("Type in a user to add: ");
	    String input = requestedUser.trim();
	    
	    if (addanyoneflag==false) {
		//System.out.println("Can only add from up to 3 levels of connections");
		if (valid_connections.contains(input)) {
		    if (pfl.contains(input)) {
			query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			System.out.println("There is already a pending friend request");
		    }
		    else if (rfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("The friend request was previously rejected");
			}
			else {
			    query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", requester, input);
			}
			//System.out.println("Query will be " + query);
			esql.executeUpdate(query);
			System.out.println("You have sent a friend request to " +  input);
		    }
		    else {
			System.out.println("You are not able to send a friend request to this user");
			return;
		    }
		}
		else {
		    //System.out.println("Add anyone not yourself or already friends");
		    if (!input.equals(requester) && !tier1_friends.contains(input) && all_users.contains(input)) {
			if (pfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("There is already a pending friend request");
			}
			else if (rfl.contains(input)) {
			    query = String.format("UPDATE CONNECTION_USR SET status='Request' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", input, requester, requester, input);
			    System.out.println("The friend request was previously rejected");
			}
			else {
			    query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", requester, input);
			}
			//System.out.println("Query will be " + query);
			esql.executeUpdate(query);
			System.out.println("You have sent a friend request to " +  input);
		    }
		    else {
			System.out.println("You are not able to send a friend request to this user");
			return;
		    }
		}
	    }catch (Exception e) {
		System.err.println (e.getMessage());
	    }
    }


	public static void GoToFriend(ProfNetwork esql, String authorisedUser){
	  try{

		List<List<String>> friendsFriends = FriendList(esql, authorisedUser);
		System.out.println("Enter the name of the user you would like to look at: ");
		String requestedUser = in.readLine().trim();

		for ( int i=0 ; i < friendsFriends.size() ; ++i){

			if(friendsFriends.get(i).get(0).trim().equals(requestedUser)){
				displayProfile(esql, requestedUser);

				System.out.println("\nSelect an option: ");
				System.out.println("---------");
				System.out.println("1. Lookup a Profile on the friends list");
				System.out.println("2. Send this person a message");
				System.out.println("3. Send a connection request");
				System.out.println("4. Menu");


			  switch (readChoice()){                   
				 case 1: GoToFriend(esql, requestedUser);return;
				 case 2: NewMessage(esql, authorisedUser, requestedUser); return;
			  case 3: SendRequestToSpecificUser(esql, authorisedUser, requestedUser);return;
				 case 4: return; 
				 default : System.out.println("Unrecognized choice!"); break;
				}
				return;
			}
		}

		System.out.println("\n Requested User not found");
		return;
	}catch (Exception e) {
	    System.err.println(e.getMessage());}
	}


	public static void lookUpUser(ProfNetwork esql, String authorisedUser){
	  String requestedUser;

	  try{
		System.out.println("Enter the name of the user you would like to find: ");
		requestedUser = in.readLine();
		System.out.println("Searching....\n");

		String query = String.format("SELECT * FROM USR WHERE userId = '%s'", requestedUser);
		int userNum = esql.executeQuery(query);

		if (userNum > 0) {
		  displayProfile(esql, requestedUser);
		}

		else{
		  System.out.println("User does not exist.\n");
		  return;
		}

	  }catch(Exception e){
		 System.err.println (e.getMessage ());
		 return ;
	  }


	System.out.println("\nSelect an option: ");
	System.out.println("---------");
	System.out.println("1. Lookup a Profile on the friends list");
	System.out.println("2. Send this person a message");
	System.out.println("3. Send a connection request");
	System.out.println("4. Menu");


  switch (readChoice()){                   
	 case 1: GoToFriend(esql, requestedUser);return;
	 case 2: NewMessage(esql, authorisedUser, requestedUser); return;
  case 3: SendRequestToSpecificUser(esql, authorisedUser, requestedUser);return;
	 case 4: return; 
    default : System.out.println("Unrecognized choice!"); break;


	}
}

    public static void AcceptRejectConnectionRequests(ProfNetwork esql, String authorisedUser) {
	try {
	    String requester = authorisedUser.trim();
	    
	    System.out.println("You have pending requests from: ");
	    //Pending Friends
	    List<List<String> > pfl_2d = new ArrayList<List<String> >();
	    List<String> pfl = new ArrayList<String>();
	    String query = String.format("Select C2.userId FROM CONNECTION_USR C2 WHERE C2.connectionId='%s' AND C2.status='Request'", requester);
	    pfl_2d = esql.executeQueryAndReturnResult(query);
	    for ( int i = 0; i < pfl_2d.size(); ++i){
		//System.out.print(i + ". ");
		System.out.print("\t");
		pfl.add((pfl_2d.get(i).get(0)).trim());
		System.out.println(pfl_2d.get(i).get(0));
	    }
	    
	    System.out.println("Input the user to Accept/Reject: ");
	    String user = in.readLine();
	    user = user.trim();
	    if (pfl.contains(user)) {
		System.out.println("Accept (y)\nReject(n)");
		String input = in.readLine();
		input = input.trim();
		if (input.equals("y")) { query = String.format("UPDATE CONNECTION_USR SET status='Accept' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", user, requester, requester, user); }
		else if (input.equals("n")) { query = String.format("UPDATE CONNECTION_USR SET status='Reject' WHERE (userId='%s' AND connectionId='%s') OR (userId='%s' AND connectionId='%s')", user, requester, requester, user); }
		else { 
		    System.out.println("Invalid input"); 
		    return;
		}
		esql.executeUpdate(query);
		System.out.println("Connection has been updated");
	    }
	    else {
		System.out.println("Invalid input of userId");
		return;
	    }
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}
    }
   

	public static void displayProfile(ProfNetwork esql, String authorisedUser){
	try{
		String query = String.format("SELECT email, name , dateOfBirth FROM USR WHERE userId='%s'", authorisedUser );  
		List<List<String> > usrArray = new ArrayList<List<String> >();
		usrArray = esql.executeQueryAndReturnResult(query);
		//replaceNulls(usrArray);
		String email = usrArray.get(0).get(0).trim();
		String usr = usrArray.get(0).get(1);
		String date = usrArray.get(0).get(2);


		System.out.println( usr.trim() + "'s profie:");
		System.out.println("========================="  + "\n");

		System.out.println("Email: " + email + "\n");
		System.out.println("Birthday: " + date + "\n");


		System.out.println("Work Info:");
		System.out.println("========================="  + "\n");

		query = String.format("SELECT * FROM WORK_EXPR WHERE userId='%s'", authorisedUser );  
		usrArray = esql.executeQueryAndReturnResult(query);


		String company  = usrArray.get(0).get(1);
		String role = usrArray.get(0).get(2);
		String location = usrArray.get(0).get(3);
		String start = usrArray.get(0).get(4);
		String end = usrArray.get(0).get(5);

		System.out.println( "Current company: " + company);
		System.out.println("Role: " + role);
		System.out.println("Location: " + location);
		System.out.println("Start Date: " + start);
		System.out.println("End Date: " + end + "\n");

		query = String.format("SELECT * FROM EDUCATIONAL_DETAILS WHERE userId='%s'", authorisedUser );  
		usrArray = esql.executeQueryAndReturnResult(query);


		String institute  = usrArray.get(0).get(1);
		String major = usrArray.get(0).get(2);
		String degree = usrArray.get(0).get(3);
		String startEdu = usrArray.get(0).get(4);
		String endEdu = usrArray.get(0).get(5);

		System.out.println("Educational Details:");
		System.out.println("========================="  + "\n");

		System.out.println( "Institute: " + institute);
		System.out.println("Major: " + major);
		System.out.println("Degree: " + degree);
		System.out.println("Start Date: " + startEdu);
		System.out.println("End Date: " + endEdu + "\n");
		ViewFriendsSimple(esql, authorisedUser);
		return;

	}catch(Exception e){
	//System.err.println (e.getMessage ());
	//return ;
	}
	} 


	public static void replyInbox(ProfNetwork esql, String authorisedUser){
	try{

		System.out.println("\nType ID of message you would like to reply to:");
		String replyID= in.readLine().trim();
		List<List<String> > msgTable = new ArrayList<List<String> >();


		String query = String.format("SELECT senderId ,receiverId FROM MESSAGE WHERE receiverId='%s' AND msgId = %s " , authorisedUser, replyID);  
		int resultNum = esql.executeQuery(query);

		if(resultNum > 0){
			msgTable = esql.executeQueryAndReturnResult(query);
			NewMessage(esql, msgTable.get(0).get(1).trim() , msgTable.get(0).get(0).trim() );
			return;
		}

		else{
			System.out.println("Invalid Message ID\n");
			return;
		}

	}catch(Exception e){
			System.err.println(e.getMessage() );}

		return;
	}

	/*
	Delete Statuses:
	1 = sender delted msg
	2 = reciver deleted
	3 = both sender & reciver deleted
	*/
	public static void delInbox(ProfNetwork esql, String authorisedUser, String user){

	try{

		boolean oneSideDelted = false;

		System.out.println("\nType ID of message you would like to Delete:");
		String replyID= in.readLine().trim();
		List<List<String> > msgTable = new ArrayList<List<String> >();
		String query = "";
		if( user.equals("reciver") ){
			 query = String.format("SELECT msgId , deleteStatus FROM MESSAGE WHERE receiverId='%s' AND msgId = %s " , authorisedUser, replyID);  
		}
		else{
			 query = String.format("SELECT msgId , deleteStatus FROM MESSAGE WHERE senderId='%s' AND msgId = %s " , authorisedUser, replyID);  
		}


		msgTable = esql.executeQueryAndReturnResult(query);
		int resultNum = esql.executeQuery(query);

		if(resultNum > 0){
			if( user.equals("reciver")){

				if(msgTable.get(0).get(1).equals("0")){
					query = String.format("UPDATE MESSAGE SET deleteStatus = 2  WHERE msgId='%s'  ", msgTable.get(0).get(0) );
				}

				else {
					query = String.format("UPDATE MESSAGE SET deleteStatus = 3  WHERE msgId='%s'  ", msgTable.get(0).get(0) );					
				}
			}
			else{

				if(msgTable.get(0).get(1).equals("0") ) {
					query = String.format("UPDATE MESSAGE SET deleteStatus = 1  WHERE msgId='%s'  ", msgTable.get(0).get(0) );
				}

				else{
					query = String.format("UPDATE MESSAGE SET deleteStatus = 3  WHERE msgId='%s'  ", msgTable.get(0).get(0) );					
				}				

			}
			
			msgTable = esql.executeQueryAndReturnResult(query);
			System.out.println("Message deleted\n");

			return;
		}

		else{
			System.out.println("Invalid Message ID\n");
			return;
		}

	}catch(Exception e){
			//System.err.println(e.getMessage() );}

		return;
	}
}

	public static void seeInbox(ProfNetwork esql, String authorisedUser){

		List<List<String> > msgTable = new ArrayList<List<String> >();
	
		try{

			String query = String.format("SELECT * FROM MESSAGE WHERE receiverId='%s' AND status != 'Failed to Deliver' AND status != 'Draft' AND deleteStatus != 2 AND deleteStatus != 3" , authorisedUser );  
			
			int numResult = esql.executeQuery(query);
			if (numResult <= 0){
				System.out.println("Inbox is empty!\n");
				return;
			}

			msgTable = esql.executeQueryAndReturnResult(query);

	    	for(int i = 0; i  < msgTable.size();  i++) {

	    		List<String> msgRow = msgTable.get(i);
				System.out.println( "From "  + msgRow.get(1).trim() + ":\tMessage ID: " + msgRow.get(0).trim() + "\tSent: " +  msgRow.get(4).trim()  ); 
				System.out.println("========================================================================="  + "\n");
				System.out.println(msgRow.get(3).trim()  );
				
				//query = String.format("SELECT * FROM MESSAGE WHERE senderId='%s' " , authorisedUser,authorisedUser );  
			    query = String.format("UPDATE MESSAGE SET status = 'Read' WHERE msgId='%s'  ", msgRow.get(0) );
			    esql.executeUpdate(query);
			} 

			System.out.println("\nSelect an option: ");
			System.out.println("---------");
			System.out.println("1. Reply to a Message");
			System.out.println("2. Delete a message");
			System.out.println("3. Go Back");


			switch (readChoice()){  
				case 1: replyInbox(esql, authorisedUser);                 
				case 2: delInbox(esql, authorisedUser, "reciver" ); break;
				case 3: return; 
				default : System.out.println("Unrecognized choice!"); break;
			}

	
		}catch(Exception e){
			System.err.println(e.getMessage() );}

		return;

	}

	public static void printSentMsg( ProfNetwork esql, String authorisedUser){
		List<List<String> > msgTable = new ArrayList<List<String> >();
		System.out.println("");
		try{

		String query = String.format("SELECT * FROM MESSAGE WHERE senderId='%s' AND status != 'Failed to Deliver' AND status != 'Draft' AND deleteStatus != 1 AND deleteStatus != 3" , authorisedUser );  
		
		int numResult = esql.executeQuery(query);
		if (numResult <= 0){
			System.out.println("Inbox is empty!\n");
			return;
		}
		msgTable = esql.executeQueryAndReturnResult(query);


    	for(int i = 0; i  < msgTable.size();  i++) {

    		List<String> msgRow = msgTable.get(i);
			System.out.println( "To "  + msgRow.get(2).trim() + ": \t Status: " + msgRow.get(6).trim() + "\tSent: " + msgRow.get(4).trim() + ":\tMessage ID: " + msgRow.get(0).trim() ); 
			System.out.println("=========================================================================="  + "\n");
			System.out.println(msgRow.get(3).trim()  );
		}


		System.out.println("\nSelect an option: ");
		System.out.println("---------");
		System.out.println("1. Delete a message");
		System.out.println("2. Go Back");


		switch (readChoice()){                   
			case 1: delInbox(esql, authorisedUser, "sender" ); break;
			case 2: return; 
			default : System.out.println("Unrecognized choice!"); break;
		}


		}catch(Exception e){
			System.err.println(e.getMessage() );}

		return;

	}

	public static void viewMessages(ProfNetwork esql, String authorisedUser){
		try{

		while (true){  	

			System.out.println("\nSelect an option: ");
			System.out.println("---------");
			System.out.println("1. See your inbox");
			System.out.println("2. View all sent messages");
			System.out.println("3. Menu");

		
		  switch (readChoice()){                   
			 case 1: seeInbox(esql, authorisedUser); break;
			 case 2: printSentMsg(esql, authorisedUser); break;
			 case 3: return; 

			 default : System.out.println("Unrecognized choice!"); break;
		}
		
    	}



		}catch(Exception e){
		System.err.println(e.getMessage() );
		}
			return;
	}

	public static String getNextMsgID(ProfNetwork esql){
	List<List<String> > msgNumStringList = new ArrayList<List<String> >();
	try{

	   String query = String.format ("SELECT max(msgId) from MESSAGE ");
	   msgNumStringList = esql.executeQueryAndReturnResult(query);
	  }catch(Exception e){
		  System.err.println(e.getMessage() );
	  }

	  int maxID = Integer.parseInt(msgNumStringList.get(0).get(0));
	  maxID = maxID+1;
	  return "" + maxID;
	}

	public static void NewMessage(ProfNetwork esql, String authorisedUser, String reciver){
	  try{

	  System.out.println("Type your msg in"); 
	  System.out.println("========================="  + "\n");

	  String msg ="";
	  String line = "_";
	  while ( !line .equals("") ){
		line = in.readLine();
		msg += line;
	  }

	 String newStatus = "Sent";
	 String n = getNextMsgID(esql).trim();
	 

	 String query = String.format("INSERT INTO MESSAGE( msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ( %s,'%s','%s','%s', current_timestamp ,0, 'Sent' )", n, authorisedUser, reciver, msg);     
	 esql.executeUpdate(query);


	  }catch(Exception e){
		System.err.println(e.getMessage() );
	  }
	}

	public static boolean userExists( ProfNetwork esql, String requestedUser){
	    try{

			String query = String.format("SELECT * FROM USR WHERE userId='%s' " , requestedUser);  
			int numResult = esql.executeQuery(query);

			if( numResult > 0){
				return true;
			}

		}catch(Exception e){
			System.err.println(e.getMessage() );
		  }

			return false;
	}

	public static void SendMsg(ProfNetwork esql, String authorisedUser){

	  try{

		  System.out.println("Type the username of the user you would like to send a message to (Enter 'n' to exit) : ");
		  String requestedUser = in.readLine().trim();

		  if( requestedUser.equals("n")){
			  return;
		  }

		  else if( userExists( esql, requestedUser) ){ 

		  	NewMessage(esql, authorisedUser.trim(), requestedUser);

		  }

		  else{

		  	System.out.println("User not found in friend list");
			return;   
		}

		}catch(Exception e){
		   System.err.println(e.getMessage() );
		}

	}
}//end ProfNetwork
