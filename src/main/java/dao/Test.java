package dao;


import model.Person;

public class Test
{
    public static void main( String[] args )
    {
       PersonDAOImpl personDAO=new PersonDAOImpl();
       Person person= personDAO.getPersonById(1);
       System.out.println(person);
        //Person person1=new Person(7,"Fatih","Dmir");
        //personDAO.saveUser(person1);
       // Person person2=new Person(7,"arda","simsek");
        //personDAO.updatePerson(person2);

      //  personDAO.deletePerson(5);
    }
}
