package orm.demo.entity;

import lombok.Data;
import orm.annotation.Id;
import orm.annotation.Table;

@Data
@Table("notes")
public class Note {

    @Id
    private int id;

    private int author;

    private String note;
}
