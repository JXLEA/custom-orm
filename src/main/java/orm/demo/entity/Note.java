package orm.demo.entity;

import lombok.Data;
import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.ManyToOne;
import orm.annotation.Table;

@Data
@Table("notes")
public class Note {

    @Id
    private int id;

    @ManyToOne
    @Column(value = "author")
    private Person person;

    private String note;
}
