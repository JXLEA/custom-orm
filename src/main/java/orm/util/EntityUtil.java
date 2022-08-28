package orm.util;

import lombok.SneakyThrows;
import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EntityUtil {

    public static String resolveColumnName(Field field) {
        return Optional.ofNullable(field.getAnnotation(Column.class))
                .map(Column::value)
                .orElse(field.getName());
    }

    public static <T> Field getIdField(Class<T> type) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find a field marked with @Id in class " + type));
    }

    public static <T> String resolveTableName(Class<T> type) {
        return Optional.ofNullable(type.getAnnotation(Table.class)).map(Table::value)
                .orElseThrow(() -> new RuntimeException("Entity type not marked as @Table " + type));
    }

    public static <T> Field getFieldByName(Class<T> type, String fieldName) {
        return Arrays.stream(type.getDeclaredFields())
                .filter(field -> field.getName().equalsIgnoreCase(fieldName))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find a field with such name '" + fieldName + "'"));
    }

    @SneakyThrows
    public static <T> Object getEntityId(T entity) {
        var entityClass = entity.getClass();
        var idField = getIdField(entityClass);
        idField.setAccessible(Boolean.TRUE);
        return idField.get(entity);
    }
}
