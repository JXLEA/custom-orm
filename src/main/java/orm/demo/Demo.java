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

        var person = session.findById(Person.class, 1);
        var person1 = session.findById(Person.class, 1);

        System.out.println(person == person1);

        var note = session.findById(Note.class, 1);
        session.close();
        System.out.println(note);

        var notes = session.findAllBy(Note.class, "author", 4);
        System.out.println(notes);
    }

    private static DataSource initializeDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        return dataSource;
    }
}
