package orm.session.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import orm.session.cache.EntityKey;
import orm.util.EntityUtil;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static orm.util.EntityUtil.*;

@RequiredArgsConstructor
public class JdbcEntityDao {

    private final String SELECT_FROM_TABLE_BY_COLUMN = "select * from %s where %s = ?";
    private final DataSource dataSource;

    private boolean open = true;

    private Map<EntityKey<?>, Object> entityCache = new ConcurrentHashMap<>();

    @SneakyThrows
    private <T> T findOneBy(Class<T> type, Field field, Object value) {
        try (var connection = dataSource.getConnection()) {
            var tableName = resolveTableName(type);
            var columnName = resolveColumnName(field);
            var selectSql = String.format(SELECT_FROM_TABLE_BY_COLUMN, tableName, columnName);
            try (var statement = connection.prepareStatement(selectSql)) {
                statement.setObject(1, value);
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
            String columnName = EntityUtil.resolveColumnName(field);
            var columnValue = resultSet.getObject(columnName);
            field.setAccessible(Boolean.TRUE);
            field.set(entity, columnValue);
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
        this.open = false;
        entityCache.clear();
    }
}
