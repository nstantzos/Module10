package DatabaseTest;

public class Person 
{
    private long ssn;
    private String firstName;
    private String lastName;
    private int age;
    private long creditCard;

    public Person(String firstName, String lastName, int age, long creditCard, long ssn) 
    {
        this.ssn = ssn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.creditCard = creditCard;
    }

    public Person() 
    {
    }

    public long getSsn() 
    {
        return ssn;
    }

    public void setSsn(long ssn) 
    {
        this.ssn = ssn;
    }

    public String getFirstName() 
    {
        return firstName;
    }

    public void setFirstName(String firstName) 
    {
        this.firstName = firstName;
    }

    public String getLastName() 
    {
        return lastName;
    }

    public void setLastName(String lastName) 
    {
        this.lastName = lastName;
    }

    public int getAge() 
    {
        return age;
    }

    public void setAge(int age) 
    {
        this.age = age;
    }

    public long getCreditCard() 
    {
        return creditCard;
    }

    public void setCreditCard(long creditCard) 
    {
        this.creditCard = creditCard;
    }

    //Override of built in equals method. Check if object passed is an instance of Person, then compare ssn
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        return getSsn() == person.getSsn();

    }

    @Override
    public int hashCode() 
    {
    	//Exclusive or (XOR) bit shifted by 32
        return (int) (getSsn() ^ (getSsn() >>> 32));
    }

    @Override
    public String toString() 
    {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", creditCard=" + creditCard +
                ", ssn=" + ssn +
                '}';
    }
}