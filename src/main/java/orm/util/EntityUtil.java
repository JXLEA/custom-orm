package orm.util;

import lombok.SneakyThrows;
import orm.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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

    public static <T> Field getRelatedEntityField(Class<?> fromEntity, Class<T> toEntity) {
        return Arrays.stream(toEntity.getDeclaredFields())
                .filter(field -> field.getType().equals(fromEntity))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Can not find a related fields in " + toEntity + " for " + fromEntity));
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
    public static <T> Object getId(T entity) {
        var entityClass = entity.getClass();
        var idField = getIdField(entityClass);
        idField.setAccessible(Boolean.TRUE);
        return idField.get(entity);
    }

    public static boolean isRegularField(Field field) {
        return !isEntityField(field) && !isEntityCollectionField(field);
    }

    public static boolean isEntityField(Field field) {
        return field.isAnnotationPresent(ManyToOne.class);
    }

    public static boolean isEntityCollectionField(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    public static Class<?> getEntityCollectionElementType(Field field) {
        var parameterizedType = (ParameterizedType) field.getGenericType();
        var typeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>)typeArguments[0];
    }

    public static Field[] getColumnFields(Class<?> entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> !isEntityCollectionField(field))
                .toArray(Field[]::new);
    }
}
