package orm.session;

import java.util.List;

public interface Session {

    <T> T findById(Class<T> type, Object id);

    <T> List<T> findAllBy(Class<T> type, String name, Object value);

    void close();
}
