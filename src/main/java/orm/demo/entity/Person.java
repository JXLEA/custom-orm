package orm.demo.entity;

import lombok.Data;
import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

@Data
@Table("persons")
public class Person {

    @Id
    private int id;

    @Column(value = "first_name")
    private String firstName;

    @Column(value = "last_name")
    private String lastName;
}
