package orm.demo;

import org.postgresql.ds.PGSimpleDataSource;
import orm.demo.entity.Note;
import orm.demo.entity.Person;
import orm.session.SessionFactory;
import orm.session.impl.SessionFactoryImpl;

import javax.sql.DataSource;

public class Demo {

    public static void main(String[] args) {
        SessionFactory sessionFactory = new SessionFactoryImpl(initializeDataSource());
        var session = sessionFactory.openSession();

        var person = session.findById(Person.class, 4);
        System.out.println(person);


        person.getNotes().forEach(System.out::println);

        person.setFirstName("John");
        session.close();
    }

    private static DataSource initializeDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        return dataSource;
    }
}
