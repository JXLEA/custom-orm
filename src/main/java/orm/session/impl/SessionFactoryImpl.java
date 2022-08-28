package orm.session.impl;

import lombok.RequiredArgsConstructor;
import orm.session.Session;
import orm.session.SessionFactory;

import javax.sql.DataSource;

@RequiredArgsConstructor
public class SessionFactoryImpl implements SessionFactory {

    private final DataSource dataSource;

    @Override
    public Session openSession() {
        return new SessionImpl(dataSource);
    }
}
