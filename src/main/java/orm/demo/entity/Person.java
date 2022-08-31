package orm.demo.entity;

import lombok.Data;
import lombok.ToString;
import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.OneToMany;
import orm.annotation.Table;

import java.util.List;

@Data
@Table("persons")
@ToString(exclude = "notes")
public class Person {

    @Id
    private int id;

    @Column(value = "first_name")
    private String firstName;

    @Column(value = "last_name")
    private String lastName;

    @OneToMany
    private List<Note> notes;

}