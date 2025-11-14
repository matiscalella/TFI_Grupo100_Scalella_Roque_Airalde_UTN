
package config;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private Connection conn;
    
    // ----- Inicia una transaccion (obtiene la conexion y set de AutoCommit en false)
     public void beginTransaction() throws SQLException {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
    }
    // ----- Confirma los cambios
    public void commit() throws SQLException {
       if (conn != null) {
           conn.commit();
       } else {
           throw new SQLException("Error al hacer commit.");
       }
    }
    // ----- Revierte los cambios ante fallos
    public void rollback() throws SQLException {
        if (conn != null) {
                conn.rollback();
        } else {
            throw new SQLException("Error al hacer rollback: conexion nula o no se inicio la transaccion.");
        }
        
    }
    // ----- Cierra la conexion manualmente
    public void close() throws SQLException {
        if (conn != null) {
            if (!conn.getAutoCommit()) {
                conn.setAutoCommit(true); // Restaura el modo por defecto
            }
            conn.close();
        } else {
            throw new SQLException("No se puede cerrar la conexión: ya está cerrada o nunca se abrió.");
        }
    }
    // ----- Metodo getter para acceder a la conexion
    public Connection getConnection() {
        return conn;
    }
}
