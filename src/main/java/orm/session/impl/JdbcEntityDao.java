package orm.session.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import orm.collection.LazyList;
import orm.session.cache.EntityKey;
import orm.util.EntityUtil;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static orm.util.EntityUtil.getEntityCollectionElementType;
import static orm.util.EntityUtil.getFieldByName;
import static orm.util.EntityUtil.getId;
import static orm.util.EntityUtil.getIdField;
import static orm.util.EntityUtil.getRelatedEntityField;
import static orm.util.EntityUtil.resolveColumnName;
import static orm.util.EntityUtil.resolveTableName;

@RequiredArgsConstructor
public class JdbcEntityDao {

    private final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    private final DataSource dataSource;

    private boolean open = true;

    private Map<EntityKey<?>, Object> entityCache = new HashMap<>();
    private Map<EntityKey<?>, Object[]> entitySnapshots = new HashMap<>();

    @SneakyThrows
    private <T> T findOneBy(Class<T> type, Field field, Object value) {
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(type);
            var columnName = resolveColumnName(field);
            var selectSql = String.format(SELECT_FROM_TABLE_BY_COLUMN, tableName, columnName);
            try (var statement = connection.prepareStatement(selectSql)) {
                statement.setObject(1, value);
                System.out.println(statement);
                var resultSet = statement.executeQuery();
                resultSet.next();
                return createEntityFromResultSet(type, resultSet);
            }
        }
    }

    @SneakyThrows
    private <T> T createEntityFromResultSet(Class<T> type, ResultSet resultSet) {
        var entity = type.getDeclaredConstructor().newInstance();
        for (var field : type.getDeclaredFields()) {
            field.setAccessible(Boolean.TRUE);
            if (EntityUtil.isRegularField(field)) {
                var columnName = resolveColumnName(field);
                var columnValue = resultSet.getObject(columnName);
                field.set(entity, columnValue);
            } else if (EntityUtil.isEntityField(field)) {
                var relatedEntityType = field.getType();
                var joinColumnName = resolveColumnName(field);
                var joinColumnValue = resultSet.getObject(joinColumnName);
                var relatedEntityIdField = getIdField(type);
                var relatedEntity = findOneBy(relatedEntityType, relatedEntityIdField, joinColumnValue);
                field.set(entity, relatedEntity);
            } else if (EntityUtil.isEntityCollectionField(field)) {
                var relatedEntityType = getEntityCollectionElementType(field);
                var relatedEntityField = getRelatedEntityField(type, relatedEntityType);
                var entityId = getId(entity);
                var entityCollection = new LazyList<T>(() -> findAllBy(relatedEntityType, relatedEntityField, entityId));
                field.set(entity, entityCollection);
            }
        }
        return cache(entity);
    }

    private <T> T cache(T entity) {
        var entityKey = EntityKey.of(entity);
        return (T) entityCache.computeIfAbsent(entityKey, key -> entity);
    }

    @SneakyThrows
    private <T> List<T> findAllBy(Class<T> type, Field field, Object value) {
        var result = new ArrayList<T>();
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(type);
            var columnName = resolveColumnName(field);
            var selectSql = String.format(SELECT_FROM_TABLE_BY_COLUMN, tableName, columnName);
            try (var statement = connection.prepareStatement(selectSql)) {
                statement.setObject(1, value);
                var resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result.add(createEntityFromResultSet(type, resultSet));
                }
            }
        }
        return result;
    }

    private boolean isOpen() {
        return open;
    }

    private void verifyIsSessionOpen() {
        if (!isOpen()) {
            throw new RuntimeException("Session has been already closed.");
        }
    }

    public <T> T findById(Class<T> type, Object id) {
        verifyIsSessionOpen();
        var cachedKey = EntityKey.of(type, id);
        var entity = entityCache.get(cachedKey);
        if (Objects.isNull(entity)) {
            var idField = getIdField(type);
            entity = findOneBy(type, idField, id);
        }
        return (T) entity;
    }

    public <T> List<T> findAllBy(Class<T> type, String columnName, Object value) {
        verifyIsSessionOpen();
        var field = getFieldByName(type, columnName);
        return findAllBy(type, field, value);
    }

    public void close() {
        verifySnapshots();
        this.open = false;
        entityCache.clear();
    }

    /**
      aka dirty checking,
      TODO complete also Actions engine, like (Update, Create, Delete - Actions) on flush/commit/close
      snapshot should contains data per one single session and upload data to DB during flush/commit/close
     */
    @SneakyThrows
    private void verifySnapshots() {
        for (var entityEntry : entityCache.entrySet()) {
            var entity = entityEntry.getValue();
            var snapshot = entitySnapshots.get(entityEntry.getKey());
            var entityFields = EntityUtil.getColumnFields(entity.getClass());
            for (int i = 0; i < entityFields.length; i++) {
                var entityField = entityFields[i];
                entityField.setAccessible(Boolean.TRUE);
                var entityFieldValue = entityField.get(entity);
                var snapshotFieldValue = snapshot[i];
                if (!Objects.equals(entityFieldValue, snapshotFieldValue)) {
                    System.out.println("Entity field value '" + entityFieldValue + "' has been modified and should be updated on " + snapshotFieldValue);
                }
            }

        }
    }
}
