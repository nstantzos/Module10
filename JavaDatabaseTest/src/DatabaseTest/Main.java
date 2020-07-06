package DatabaseTest;

import java.sql.*;
import java.util.ArrayList;

public class Main 
{

    public static void main(String[] args) 
    {
    	//Initialize person objects
        Person person = new Person("Rick","Springfield", 70, 11111111,123456789);
        Person person1 = new Person("Bob", "Dole", 93, 22222222,123456780);
        Person person2 = new Person("Eric", "Clapton", 78, 33333333,123456781);
        Person person3 = new Person("Danny", "Devito", 71, 44444444,1234567892);
        Person person4 = new Person("Billy", "Joel", 70, 55555555,1234567893);
        Person person5 = new Person("David", "Donnington", 22, 66666666,1234567894);

        //Create table within database
        //Insertion of multiple person objects into the database
        CreateTable();
        //Call method to insert person objects into table inside of database
        InsertPerson(person);
        InsertPerson(person1);
        InsertPerson(person2);
        InsertPerson(person3);
        InsertPerson(person4);
        InsertPerson(person5);

        //Call method to select person with ID of 4 (the 4th row in the database)
        System.out.println(SelectPerson(4));
        
        //Loop through all people in the person array list FindAllPeople method
        for(Person p: FindAllPeople())
        {
            System.out.println(p);
        }
        
        //Delete Danny Devito from the table
        DeletePerson("Danny", "Devito");

        //Print out table again after row has been deleted to prove that row was deleted
        for(Person p: FindAllPeople())
        {
            System.out.println(p);
        }

    }

    //Method for deleting person from the table based on the last name
    public static Connection DeletePerson(String firstName, String lastName)
    {
    	//Initialize connection and statement to null
        Connection connection = null;
        Statement statement = null;

        try 
        {
            connection = GetConnection();
            connection.setAutoCommit(false);

            //Create and execute SQL statement
            statement = connection.createStatement();
            //SQL statement to delete specific rows of people based on last name
            String sql = "DELETE from PersonalData where FIRSTNAME='" + firstName + "' AND LASTNAME='" + lastName + "';";
            statement.executeUpdate(sql);
            connection.commit();
            connection.close();

            System.out.println(firstName + " " + lastName + " was deleted :(");
        } 
        catch ( Exception e ) 
        {
            System.out.println(e);
            //System.out.println("I entered the delete person catch block");
            connection = null;
        }
        return connection;


    }

    //Method that selects all rows in the table and returns them as a person object
    public static ArrayList<Person> FindAllPeople()
    {
        Connection connection = null;
        Statement statement = null;
        //Make an array list full of person objects
        ArrayList<Person> person = new ArrayList<>();

        try 
        {
            connection = GetConnection();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            //Query all rows of data from person table
            ResultSet rs = statement.executeQuery("SELECT * FROM PersonalData;");
            while ( rs.next() ) 
            {
            	//Add each row to person array list
                person.add(new Person(rs.getString("firstname"), rs.getString("lastname"), rs.getInt("age"), rs.getInt("creditcard"),rs.getInt("ssn")));
            }
            rs.close();
            statement.close();
            connection.close();

            System.out.println("Find all people was done.");
        } 
        catch ( Exception e ) 
        {
            System.out.println(e);
            person = null;
        }
        return person;

    }

    //Method to select and output a person object from a row contained in the database
    public static Person SelectPerson(int id)
    {
        Connection connection = null;
        Statement statement = null;
        Person person = new Person();
        try 
        {
            connection = GetConnection();
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM PersonalData where ID=" + id + ";");
            //While loop checks the result set for final row conditions: if the cursor is after the final row, the loop stops
            //Retrieve data from database and populate person object with retrieved data
            while ( rs.next() ) 
            {
            	//Retrieve data from table to use to set properties of person object
                person.setFirstName(rs.getString("firstname"));
                person.setLastName(rs.getString("lastname"));
                person.setAge(rs.getInt("age"));
                person.setCreditCard(rs.getInt("creditcard"));
                person.setSsn(rs.getInt("ssn"));
            }
            rs.close();
            statement.close();
            connection.close();

            System.out.println("(Select Person " + id + ") done successfully");
        } 
        catch ( Exception e ) 
        {
            System.out.println(e);
            person = null;
        }
        //Return person object
        return person;

    }
    
    //Method for inserting a person object into the database.
    public static Connection InsertPerson(Person person)
    {
        Connection connection = null;
        Statement statement = null;
        try 
        {
            connection = GetConnection();
            //This setting allows SQL statements to be grouped instead of individual transactions
            connection.setAutoCommit(false);

            statement = connection.createStatement();
            //Construct SQL string to insert person object into table
            String sql = "INSERT INTO PersonalData (FIRSTNAME,LASTNAME,AGE,CREDITCARD,SSN) " +
                    "VALUES ("+
                          "'" + person.getFirstName() + "'," +
                          "'" + person.getLastName() + "'," +
                                person.getAge() + "," +
                                person.getCreditCard() + "," +
                                person.getSsn() + " );";
            statement.executeUpdate(sql);

            statement.close();
            //Commit the SQL statement group before closing
            connection.commit();
            connection.close();

            System.out.println("(Insert Person " + person.getFirstName() + " " + person.getLastName() + ") done successfully");
        } 
        catch ( Exception e ) 
        {
            System.out.println(e);
            connection = null;
        }

            return connection;
    }

    //Method to create a table within the database
    public static Connection CreateTable()
    {
        Connection connection = null;
        Statement statement = null;
        try 
        {
            connection = GetConnection();
            
            statement = connection.createStatement();
            //Drop the table in the database before creating it to make sure nothing is overwritten or duplicated (
			// This will blow up if the table 'PersonalData' doesn't already exist
            statement.execute("DROP TABLE IF EXISTS PersonalData");
			//Construct SQL string to initialize table with columns and expected value types
            String sql = "CREATE TABLE PersonalData " +
                         "(ID INTEGER PRIMARY KEY        AUTOINCREMENT, " +
                         "FIRSTNAME           CHAR(30)   NOT NULL, " +
                         "LASTNAME            CHAR(30)   NOT NULL, " +
                         "AGE                 INT                , " +
                         "CREDITCARD          BIGINT             , " +
                         "SSN                 BIGINT             ) ";
            statement.executeUpdate(sql);
            statement.close();
            connection.close();

            System.out.println("Table created successfully");
        }
        catch (Exception e)
        {
            System.out.println(e);
            connection = null;
        }

        return connection;
    }

    //Method to open a connection to the database. 
    public static Connection GetConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/NickS/Nicks Stuff/School stuff/Valencia/CEN 3024C 33719/Repos/Module10/Module10/JavaDatabaseTest/sqlite/db/MyDatabase.db");
            System.out.println("Opened database successfully");
        }
        catch (Exception e)
        {
            System.out.println(e);
            connection = null;
        }

        return connection;
    }


}