package co.edu.uniquindio.read_now.config;

import co.edu.uniquindio.read_now.audit.AuditJdbcContext;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Envuelve el {@link javax.sql.DataSource} para ejecutar {@code SET @audit_usuario} al obtener la conexión
 * y limpiar la variable al devolverla al pool.
 */
public class AuditingDataSource extends DelegatingDataSource {

    public AuditingDataSource(javax.sql.DataSource target) {
        setTargetDataSource(target);
        afterPropertiesSet();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return wrapConnection(super.getConnection());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return wrapConnection(super.getConnection(username, password));
    }

    private static Connection wrapConnection(Connection raw) throws SQLException {
        applySessionUser(raw);
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] {Connection.class},
                (proxy, method, args) -> {
                    if ("close".equals(method.getName()) && (args == null || args.length == 0)) {
                        try (Statement st = raw.createStatement()) {
                            st.execute("SET @audit_usuario = NULL");
                        } catch (SQLException ignored) {
                            // conexión ya inválida o pool
                        }
                    }
                    return method.invoke(raw, args);
                });
    }

    private static void applySessionUser(Connection raw) throws SQLException {
        String u = AuditJdbcContext.getUsuario();
        if (u != null && !u.isBlank()) {
            try (PreparedStatement ps = raw.prepareStatement("SET @audit_usuario = ?")) {
                ps.setString(1, u);
                ps.execute();
            }
        } else {
            try (Statement st = raw.createStatement()) {
                st.execute("SET @audit_usuario = 'SISTEMA'");
            }
        }
    }
}
