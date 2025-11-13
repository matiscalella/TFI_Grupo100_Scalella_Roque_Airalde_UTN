
package dao;

import config.DatabaseConnection;
import java.sql.SQLException;
import java.sql.ResultSet;
import entities.Microchip;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class MicrochipDAO implements GenericDAO<Microchip>{

    @Override
    public void crear(Microchip microchip) throws SQLException  {
        String sql = "INSERT INTO microchips (eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
        ps.setBoolean(1, microchip.isEliminado());
        ps.setString(2, microchip.getCodigo());
        // ---- Validacion de fecha de nacimiento para que acepte un valor null
        if (microchip.getFechaImplantacion()!= null) {
            ps.setDate(3, java.sql.Date.valueOf(microchip.getFechaImplantacion()));
        } else {
            ps.setNull(3, java.sql.Types.DATE);
        }
        ps.setString(4, microchip.getVeterinaria());
        ps.setString(5, microchip.getObservaciones());
        ps.setLong(6, microchip.getIdMascota());
        //  ---- Ejecucion de la sentencia
        ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new SQLException("Error al insertar microchip: " + e.getMessage(), e);
        }
        
    }

    @Override
    public Microchip leer(Long id) throws SQLException  {
        Microchip microchip = null;
        String sql = "SELECT id, eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id FROM microchips WHERE id = ? AND eliminado = false";
        try  (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                microchip = new Microchip();
                microchip.setId(rs.getLong("id"));
                microchip.setEliminado(rs.getBoolean("eliminado"));
                microchip.setCodigo(rs.getString("codigo"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_implantacion"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    microchip.setFechaImplantacion(fechaSql.toLocalDate());
                } else {
                    microchip.setFechaImplantacion(null);
                }
                microchip.setVeterinaria(rs.getString("veterinaria"));
                microchip.setObservaciones(rs.getString("observaciones"));
                microchip.setIdMascota(rs.getLong("mascota_id"));
            }  
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los datos del microchip con ID " + id +": " + e.getMessage(), e);
        }
        return microchip; 
    }

    @Override
    public List<Microchip> leerTodos() throws SQLException  {
        
        List<Microchip> listaDeMicrochips= new ArrayList<>();
        
        String sql = "SELECT id, eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id FROM microchips WHERE eliminado = false";
           
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
        
            while (rs.next()) {
                Microchip microchip = new Microchip();
                microchip.setId(rs.getLong("id"));
                microchip.setEliminado(rs.getBoolean("eliminado"));
                microchip.setCodigo(rs.getString("codigo"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_implantacion"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    microchip.setFechaImplantacion(fechaSql.toLocalDate());
                } else {
                    microchip.setFechaImplantacion(null);
                }
                microchip.setVeterinaria(rs.getString("veterinaria"));
                microchip.setObservaciones(rs.getString("observaciones"));
                microchip.setIdMascota(rs.getLong("mascota_id"));
                listaDeMicrochips.add(microchip);      
            } 
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los microchips: " + e.getMessage(), e);
        }
        return listaDeMicrochips;
    }

    @Override
    public void actualizar(Microchip microchip) throws SQLException  {
        
        String sql = "UPDATE microchips SET eliminado = ?, codigo = ?, fecha_implantacion = ?, veterinaria = ?, observaciones = ?, mascota_id = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setBoolean(1, microchip.isEliminado());
            ps.setString(2, microchip.getCodigo());
            // ---- Validacion de fecha de implantacion para que acepte un valor null
            if (microchip.getFechaImplantacion() != null) {
                ps.setDate(3, java.sql.Date.valueOf(microchip.getFechaImplantacion()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, microchip.getVeterinaria());
            ps.setString(5, microchip.getObservaciones());
            ps.setLong(6, microchip.getIdMascota());
            ps.setLong(7, microchip.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al actualizar los datos: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Long id) throws SQLException  {
        String sql = "UPDATE microchips SET eliminado = true WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al intentar eliminar el microchip: " + e.getMessage(), e);
        }
    }
    
/* ---- METODOS SOBRECARGADOS

    ---- Metodos con Connection externa como parametro 
*/
    public void crear(Microchip microchip, Connection conn) throws SQLException  {
        String sql = "INSERT INTO microchips (eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
        ps.setBoolean(1, microchip.isEliminado());
        ps.setString(2, microchip.getCodigo());
        // ---- Validacion de fecha de nacimiento para que acepte un valor null
        if (microchip.getFechaImplantacion()!= null) {
            ps.setDate(3, java.sql.Date.valueOf(microchip.getFechaImplantacion()));
        } else {
            ps.setNull(3, java.sql.Types.DATE);
        }
        ps.setString(4, microchip.getVeterinaria());
        ps.setString(5, microchip.getObservaciones());
        ps.setLong(6, microchip.getIdMascota());
        //  ---- Ejecucion de la sentencia
        ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al insertar el microchip con código: " + microchip.getCodigo(), e);
        }
    }
    
    public Microchip leer(Long id, Connection conn) throws SQLException  {
        Microchip microchip = null;
        String sql = "SELECT id, eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id FROM microchips WHERE id = ? AND eliminado = false";
        try  (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                microchip = new Microchip();
                microchip.setId(rs.getLong("id"));
                microchip.setEliminado(rs.getBoolean("eliminado"));
                microchip.setCodigo(rs.getString("codigo"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_implantacion"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    microchip.setFechaImplantacion(fechaSql.toLocalDate());
                } else {
                    microchip.setFechaImplantacion(null);
                }
                microchip.setVeterinaria(rs.getString("veterinaria"));
                microchip.setObservaciones(rs.getString("observaciones"));
                microchip.setIdMascota(rs.getLong("mascota_id"));
            }  
        } catch (SQLException e) {
            throw new SQLException("Error al leer el microchip con ID: " + id, e);
        }
        return microchip; 
    }

    public List<Microchip> leerTodos(Connection conn) throws SQLException  {
        
        List<Microchip> listaDeMicrochips= new ArrayList<>();
        
        String sql = "SELECT id, eliminado, codigo, fecha_implantacion, veterinaria, observaciones, mascota_id FROM microchips WHERE eliminado = false";
           
        try (PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()) {
        
            while (rs.next()) {
                Microchip microchip = new Microchip();
                microchip.setId(rs.getLong("id"));
                microchip.setEliminado(rs.getBoolean("eliminado"));
                microchip.setCodigo(rs.getString("codigo"));
                // ----- Validacion para el caso en que la fecha sea null
                java.sql.Date fechaSql = rs.getDate("fecha_implantacion"); // Variable temporal obligatoria
                if (fechaSql != null) {
                    microchip.setFechaImplantacion(fechaSql.toLocalDate());
                } else {
                    microchip.setFechaImplantacion(null);
                }
                microchip.setVeterinaria(rs.getString("veterinaria"));
                microchip.setObservaciones(rs.getString("observaciones"));
                microchip.setIdMascota(rs.getLong("mascota_id"));
                listaDeMicrochips.add(microchip);      
            } 
        } catch (SQLException e) {
            throw new SQLException("Ocurrio un error al leer los microchips: " + e.getMessage(), e);
        }
        return listaDeMicrochips;
    }

    public void actualizar(Microchip microchip, Connection conn) throws SQLException  {
        
        String sql = "UPDATE microchips SET eliminado = ?, codigo = ?, fecha_implantacion = ?, veterinaria = ?, observaciones = ?, mascota_id = ? WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setBoolean(1, microchip.isEliminado());
            ps.setString(2, microchip.getCodigo());
            // ---- Validacion de fecha de implantacion para que acepte un valor null
            if (microchip.getFechaImplantacion() != null) {
                ps.setDate(3, java.sql.Date.valueOf(microchip.getFechaImplantacion()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, microchip.getVeterinaria());
            ps.setString(5, microchip.getObservaciones());
            ps.setLong(6, microchip.getIdMascota());
            ps.setLong(7, microchip.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar el microchip con ID: " + microchip.getId(), e);
        }
    }

    public void eliminar(Long id, Connection conn) throws SQLException  {
        String sql = "UPDATE microchips SET eliminado = true WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar el microchip con ID: " + id, e);
        }
    }
}