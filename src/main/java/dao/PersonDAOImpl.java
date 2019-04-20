package dao;

import model.Person;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.Executor;

public class PersonDAOImpl implements PersonDAO {
    static Logger logger = Logger.getLogger(PersonDAO.class);
    private Connection connection;
    private String jdbcUrl;
    private String jdbcUsername;
    private String jdbcPassword;

    private final String GET_PERSON_ID="SELECT * FROM person Where personId=?";
    private final String SAVE_PERSON="INSERT INTO person(personId,personName,personSurname) VALUES(?,?,?)";
    private final String UPDATE_PERSON="UPDATE person SET personName=?,personSurname=? WHERE personId=? ";
    private final String DELETE_USER="  DELETE FROM person WHERE personId=?";

    public PersonDAOImpl() {
        setConnection();
    }//yapici metod ile PersonDAOImpl oluşturulduğu zaman setConnection calışır bağlantı kuruluyor


    private void setConnection() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = ClassLoader.getSystemClassLoader().getResourceAsStream("database.properties");
            prop.load(input);
        } catch (IOException io) {
           logger.warn("input oluşturmada hata meydana geldi  HATA : "+io);
        }

        jdbcUrl = prop.getProperty("jdbc.url");
        jdbcUsername = prop.getProperty("jdbc.username");
        jdbcPassword = prop.getProperty("jdbc.password");


        Properties properties = new Properties();
        properties.setProperty("user", jdbcUsername);
        properties.setProperty("password", jdbcPassword);
        properties.setProperty("useUnicode", "yes");
        properties.setProperty("characterEncoding", "UTF-8");
        properties.setProperty("serverTimezone", "UTC");
        properties.setProperty("autoReconnect", "true");
        properties.setProperty("useSSL", "false");

        try {
            connection = DriverManager.getConnection(jdbcUrl, properties);
            logger.info("Veritababı bağlantısı kurulmuştur");
        } catch (Exception e) {
            logger.error("Veritababı bağlantısı kurulamamıştır  bir hata meydana gelmiştir  HATA : "+e);
        }
    }//Veri tabanı bağlantısı kuruluyor

    @Override
    public Person getPersonById(int personId) {
        try {
            ResultSet resultSet=executeQuery(GET_PERSON_ID,personId);
            while (resultSet.next()){

               int id=resultSet.getInt(personId);
               String name= resultSet.getString("personName");
               String surname= resultSet.getString("personSurname");
               Person person=new Person(id,name,surname);
               logger.info("Veritabanından veri çekilmiştir");
            return person;

            }
        } catch (Exception e) {
            logger.error("Veritabanından veri çekilirken hata oluştmuştur   HATA : "+e);
        }
        return null;
    }

    @Override
    public void updatePerson(Person person) {
        try {
           execute(UPDATE_PERSON,person.getIsim(),person.getSoyisim(),person.getId());

            logger.info("Person güncellenmiştir");
        } catch (Exception e) {
            logger.error("Güncelleme yapılırken hata oluştmuştur   HATA : "+e);
        }
    }

    @Override
    public void savePerson(Person person) {
        try {
            execute(SAVE_PERSON,person.getId(),person.getIsim(),person.getSoyisim());
            logger.info("Veritabanına kayıt yapılmıştır");
        } catch (Exception e) {
            logger.error("Veritabanına kayıt yapılırken hata oluştmuştur   HATA : "+e);
        }
    }

    @Override
    public void deletePerson(int personId) {
        try {
            execute(DELETE_USER,personId);
            logger.info(personId+" numaralı Person silinmistir");
        } catch (Exception e) {
            logger.error("silme işleminde hata meydana gelmiştir   HATA : "+e);
        }
    }

    private void execute(String sql, Object... queryParameters){
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
           int index=1;
            if(queryParameters!=null){

               // queryParameters.length(n-> ps.setObject(index++,n));
                for(Object paramater:queryParameters){
                    ps.setObject(index++,paramater);
                }
            }
            ps.execute();

        } catch (Exception e) {
            logger.error("execute functionunda hata meydana geldi  HATA :"+e);
        }
    }

    private ResultSet executeQuery(String sql, Object... queryParameters) {
        try {

            PreparedStatement ps=connection.prepareStatement(sql);

          int index=1;
           if(queryParameters!=null){
                for(Object paramater:queryParameters){
                    ps.setObject(index++,paramater);
                }
            }
           ResultSet resultSet=ps.executeQuery();
           return resultSet;
        } catch (Exception e) {
            logger.error("ResultSet functionunda hata meydana geldi  HATA :"+e);
        }
        return null;
    }
}