package orm.session.cache;

import static orm.util.EntityUtil.getId;

public record EntityKey<T>(Class<T> type, Object id) {
    public static <T> EntityKey<T> of(Class<T> type, Object id) {
        return new EntityKey<>(type, id);
    }

    public static <T> EntityKey<T> of(T entity) {
        var entityId = getId(entity);
        var entityType = entity.getClass();
        return new EntityKey(entityType, entityId);
    }
}
