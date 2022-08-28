package orm.session.impl;

import lombok.RequiredArgsConstructor;
import orm.session.Session;

import javax.sql.DataSource;
import java.util.List;

@RequiredArgsConstructor
public class SessionImpl implements Session {

    private final JdbcEntityDao entityDao;

    public SessionImpl(DataSource dataSource) {
        this.entityDao = new JdbcEntityDao(dataSource);
    }

    @Override
    public <T> List<T> findAllBy(Class<T> type, String field, Object value) {
        return entityDao.findAllBy(type, field, value);
    }

    @Override
    public void close() {
        entityDao.close();
    }

    @Override
    public <T> T findById(Class<T> type, Object value) {
        return entityDao.findById(type, value);
    }
}
