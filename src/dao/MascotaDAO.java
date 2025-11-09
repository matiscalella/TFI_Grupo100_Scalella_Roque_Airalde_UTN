
package dao;

// ---- Imports para trabajar con operaciones CRUD
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import config.DatabaseConnection;
import entities.Mascota;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO implements GenericDAO<Mascota> {

    @Override
    public void crear(Mascota mascota) throws SQLException  {
        
        // ----- Declaracion de la sentencia SQL
        String sql = "INSERT INTO mascotas (eliminado, nombre, especie, raza, fecha_nacimiento, duenio) VALUES (?, ?, ?, ?, ?, ?)";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            // ----- Reemplazo de los campos '?'
            ps.setBoolean(1, mascota.isEliminado());
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            // ---- Validacion de fecha de nacimiento para que acepte un valor null
            if (mascota.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(mascota.getFechaNacimiento()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, mascota.getDuenio());
            
            // ----- Execucion de la sentencia
            ps.executeUpdate();
        
        // ----- Manejo de errores
        } catch (SQLException e) {
            throw new SQLException("Error al insertar mascota: " + e.getMessage(), e);
        }
    }

    @Override
    public Mascota leer(Long id) throws SQLException  {
        
        // ---- Inicializacion del objeto mascota que retorna al final
        Mascota mascota = null; 
        
        // ----- Declaracion de la sentencia SQL
        String sql = "SELECT id, eliminado, nombre, especie, raza, fecha_nacimiento, duenio FROM mascotas WHERE id = ? AND eliminado = false";

        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            // ---- Ejecucion de la consulta
            ResultSet rs = ps.executeQuery();
            // ---- Construccion del objeto mascota
            if (rs.next()) {
                mascota = new Mascota();
                mascota.setId(rs.getLong("id"));
                mascota.setEliminado(rs.getBoolean("eliminado"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setRaza(rs.getString("raza"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_nacimiento"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    mascota.setFechaNacimiento(fechaSql.toLocalDate());
                } else {
                    mascota.setFechaNacimiento(null);
                }
                mascota.setDuenio(rs.getString("duenio"));       
            }
            
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los datos: " + e.getMessage());
        }
        return mascota;
        
    }

    @Override
    public List<Mascota> leerTodos() throws SQLException  {
        
        // ---- Inicializacion de la lista vacia que retorna al final
        List<Mascota> listaDeMascotas = new ArrayList<>();
        
        // ----- Sentencia SQL
        String sql = "SELECT id, eliminado, nombre, especie, raza, fecha_nacimiento, duenio FROM mascotas WHERE eliminado = false";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (Connection conn = DatabaseConnection.getConnection(); // Crear la conexion
            PreparedStatement ps = conn.prepareStatement(sql); // Preparar la sentencia
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()){
                Mascota mascota = new Mascota();
                mascota.setId(rs.getLong("id"));
                mascota.setEliminado(rs.getBoolean("eliminado"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setRaza(rs.getString("raza"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_nacimiento"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    mascota.setFechaNacimiento(fechaSql.toLocalDate());
                } else {
                    mascota.setFechaNacimiento(null);
                }
                mascota.setDuenio(rs.getString("duenio"));
                listaDeMascotas.add(mascota);
            }
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los datos: " + e.getMessage());
        }
        return listaDeMascotas; 
    }

    @Override
    public void actualizar(Mascota mascota) throws SQLException  {
        // ----- Declaracion de la sentencia SQL
        String sql = "UPDATE mascotas SET eliminado = ?, nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, duenio = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setBoolean(1, mascota.isEliminado());
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            // ---- Validacion de fecha de nacimiento para que acepte un valor null
            if (mascota.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(mascota.getFechaNacimiento()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, mascota.getDuenio());
            ps.setLong(7, mascota.getId());
            ps.executeUpdate();

        // ----- Manejo de errores
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar los datos de la mascota con ID: " + mascota.getId(), e);
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException  {
        // ----- Declaracion de la sentencia SQL
        String sql = "UPDATE mascotas SET eliminado = true WHERE id = ?";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ps.executeUpdate();
        // ----- Manejo de errores
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al intentar eliminar la mascota: " + e.getMessage());
        }
    }
    
    // ---- Consulta combinada mediante LEFT JOIN a través de la vista
    public void leerVistaMascotasConMicrochip() throws SQLException  {
        
        String sql = "SELECT * FROM vista_mascotas_con_microchip";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.printf("Mascota: %s (%s) -- Dueño: %s -- Chip: %s -- Veterinaria: %s%n",
                    rs.getString("nombre"),
                    rs.getString("especie"),
                    rs.getString("duenio"),
                    rs.getString("codigo"),
                    rs.getString("veterinaria"));
            }
        } catch (SQLException e) {
            throw new SQLException("Error al leer la vista de mascotas con microchip: " + e.getMessage());
        }
    }

    
/* ---- METODOS SOBRECARGADOS

    ---- Metodos con Connection externa como parametro (se eliminan los catch para delegar los errores a la capa superior)
*/
    

    public void crear(Mascota mascota, Connection conn) throws SQLException  {
        
        // ----- Declaracion de la sentencia SQL
        String sql = "INSERT INTO mascotas (eliminado, nombre, especie, raza, fecha_nacimiento, duenio) VALUES (?, ?, ?, ?, ?, ?)";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            // ----- Reemplazo de los campos '?'
            ps.setBoolean(1, mascota.isEliminado());
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            // ---- Validacion de fecha de nacimiento para que acepte un valor null
            if (mascota.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(mascota.getFechaNacimiento()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, mascota.getDuenio());
            
            // ----- Execucion de la sentencia
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al insertar la mascota con nombre: " + mascota.getNombre(), e);
        }
    }
    
    public Mascota leer(Long id, Connection conn) throws SQLException  {
        
        // ---- Inicializacion del objeto mascota que retorna al final
        Mascota mascota = null; 
        
        // ----- Declaracion de la sentencia SQL
        String sql = "SELECT id, eliminado, nombre, especie, raza, fecha_nacimiento, duenio FROM mascotas WHERE id = ? AND eliminado = false";

        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            // ---- Ejecucion de la consulta
            ResultSet rs = ps.executeQuery();
            // ---- Construccion del objeto mascota
            if (rs.next()) {
                mascota = new Mascota();
                mascota.setId(rs.getLong("id"));
                mascota.setEliminado(rs.getBoolean("eliminado"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setRaza(rs.getString("raza"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_nacimiento"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    mascota.setFechaNacimiento(fechaSql.toLocalDate());
                } else {
                    mascota.setFechaNacimiento(null);
                }
                mascota.setDuenio(rs.getString("duenio"));
            }
        } catch (SQLException e) {
            throw new SQLException("Error al leer la mascota con ID: " + id, e);
        }

        return mascota;   
    }
    
    public List<Mascota> leerTodos(Connection conn) throws SQLException  {
        
        // ---- Inicializacion de la lista vacia que retorna al final
        List<Mascota> listaDeMascotas = new ArrayList<>();
        
        // ----- Sentencia SQL
        String sql = "SELECT id, eliminado, nombre, especie, raza, fecha_nacimiento, duenio FROM mascotas WHERE eliminado = false";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (PreparedStatement ps = conn.prepareStatement(sql); // Preparar la sentencia
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()){
                Mascota mascota = new Mascota();
                mascota.setId(rs.getLong("id"));
                mascota.setEliminado(rs.getBoolean("eliminado"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setRaza(rs.getString("raza"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_nacimiento"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    mascota.setFechaNacimiento(fechaSql.toLocalDate());
                } else {
                    mascota.setFechaNacimiento(null);
                }
                mascota.setDuenio(rs.getString("duenio"));
                listaDeMascotas.add(mascota);
            }
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los datos: " + e.getMessage());
        }
        return listaDeMascotas; 
    }
    
    public void actualizar(Mascota mascota, Connection conn) throws SQLException  {
        // ----- Declaracion de la sentencia SQL
        String sql = "UPDATE mascotas SET eliminado = ?, nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ?, duenio = ? WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setBoolean(1, mascota.isEliminado());
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            // ---- Validacion de fecha de nacimiento para que acepte un valor null
            if (mascota.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(mascota.getFechaNacimiento()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, mascota.getDuenio());
            ps.setLong(7, mascota.getId());
            
        // ----- Manejo de errores
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar la mascota con ID: " + mascota.getId(), e);
        }

    }
    
    public void eliminar(Long id, Connection conn) throws SQLException  {
        // ----- Declaracion de la sentencia SQL
        String sql = "UPDATE mascotas SET eliminado = true WHERE id = ?";
        
        //  ----- Inicio del metodo con try-with-resources para cerrar la conexion automaticamente
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar la mascota con ID: " + id, e);
        }
    }  
}
