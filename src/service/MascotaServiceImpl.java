
package service;

import config.TransactionManager;
import entities.Mascota;
import entities.Microchip;
import dao.MascotaDAO;
import dao.MicrochipDAO;
import java.util.List;
import java.sql.Connection;

public class MascotaServiceImpl implements GenericService<Mascota> {

    private final MascotaDAO mascotaDAO;
    private final MicrochipDAO microchipDAO;
    
    public MascotaServiceImpl(MascotaDAO mascotaDAO) {
        if (mascotaDAO == null) {
            throw new IllegalArgumentException("MascotaDAO no puede ser null");
        }
        this.mascotaDAO = mascotaDAO;
        this.microchipDAO = new MicrochipDAO();
    }
    
    public MascotaServiceImpl() {
        this.mascotaDAO = new MascotaDAO();
        this.microchipDAO = new MicrochipDAO();
    }
    
    @Override
    public void insertar(Mascota mascota) throws Exception {
        // ---- Validar que el objeto no sea nulo
        mascotaNull(mascota);
        // ---- Metodo para validar los datos de la mascota
        validateMascota(mascota);
        // ---- Llamada al DAO para insertar los datos en la BD
        mascotaDAO.crear(mascota);
        
    }

    @Override
    public void actualizar(Mascota mascota) throws Exception {
        // ---- Validar que el objeto no sea nulo
        mascotaNull(mascota);
        // ----- Validacion del id (que sea valido: mayor a cero)
        if (mascota.getId() == null || mascota.getId() <= 0 ) {
            throw new IllegalArgumentException("El ID de la mascota debe ser válido (mayor a cero) para actualizarla.");
        }
        // ----- Validacion de los datos NOT NULL de la mascota
        validateMascota(mascota);
        // ----- Validacion de que la mascota exista en la BD antes de actualizarla.
        Mascota existente = mascotaDAO.leer(mascota.getId());
        if (existente == null) {
            throw new IllegalArgumentException("No se encontró una mascota con el ID especificado: " + mascota.getId());
        }
        mascotaDAO.actualizar(mascota);
        
    }

    @Override
    public void eliminar(Long id) throws Exception {
        // ---- Validacion de id
        validateId(id);
        // ---- Validacion de que la mascota a eliminar exista en la BD
        Mascota existente = mascotaDAO.leer(id);
        if (existente == null) {
            throw new IllegalArgumentException("No se ha encontrado una mascota con id " + id);
        }
        mascotaDAO.eliminar(id);
        
    }

    @Override
    public Mascota getById(Long id) throws Exception {
        // ---- Validacion de id
        validateId(id);
        // ---- Verificar existen de la mascota en la BD
        Mascota mascota = mascotaDAO.leer(id);
        if (mascota == null) {
            throw new IllegalArgumentException("No se encontró ninguna mascota con el ID especificado: " + id);
        }
        return mascota;
    }

    @Override
    public List<Mascota> getAll() throws Exception {
        // ---- Validar que el metodo mascotaDAO.leerTodos() NO retorne una lista vacia.
        List<Mascota> mascotas = mascotaDAO.leerTodos();
        if (mascotas == null || mascotas.isEmpty()) {
            throw new IllegalArgumentException("No hay mascotas registradas en el sistema.");
        }
        return mascotas;
    }
    
// ---- Metodos Transaccionales
    // ---- Insertar una Mascota y un Microchip asociados (al mismo tiempo)
    public void insertarConMicrochip(Mascota mascota, Microchip microchip) throws Exception {
        
        TransactionManager tx = new TransactionManager();
        
        try {
            
            // Iniciar transaccion
            tx.beginTransaction();
            
            // Crear la mascota
            mascotaDAO.crear(mascota, tx.getConnection());// Crear mascota, pasando como parametro la conexion
            
            // Recuperar ID autogenerado
            Long idMascota = getGeneratedId(tx.getConnection());
            microchip.setIdMascota(idMascota);
            
            // Crear Microchip
            microchipDAO.crear(microchip, tx.getConnection());
            
            tx.commit(); // Mensaje por consola desde TransactionManager
        } catch (Exception e) {
            tx.rollback(); // Deshace la operacion
            throw e; // Propaga el error a la capa superior 
        } finally {
            tx.close(); // Cierra la conexion
        }
        
    
    }
    
    // ---- Actualizar la informacion de una Mascota y su microchip juntos
    public void actualizarConMicrochip(Mascota mascota, Microchip microchip) throws Exception {
        
        TransactionManager tx = new TransactionManager();
        
        try {
            tx.beginTransaction();
            mascotaDAO.actualizar(mascota, tx.getConnection());
            microchipDAO.actualizar(microchip, tx.getConnection());
            
            tx.commit();
            System.out.println("Mascota y microchip actualizados correctamente.");
        
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            tx.close();
        }
    }
    
    // ---- Eliminar mascota y microchip asociado
    public void eliminarConMicrochip(Long idMascota, Long idMicrochip) throws Exception {
        
        TransactionManager tx = new TransactionManager();
        
        try {
            tx.beginTransaction();

            mascotaDAO.eliminar(idMascota, tx.getConnection());
            microchipDAO.eliminar(idMicrochip, tx.getConnection());

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            tx.close();
        }
    }
    
// ---- Metodos auxiliares
    
    // ---- Validacion NULL para mascota
    public void mascotaNull(Mascota mascota){
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula.");
        }
    }
    
    // ---- Validacion de campos NOT NULL (.trim().isEmpty() elimina espacios en blanco)
    public void validateMascota(Mascota mascota){
        
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío.");
        }

        if (mascota.getEspecie() == null || mascota.getEspecie().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificarse la especie de la mascota.");
        }

        if (mascota.getDuenio() == null || mascota.getDuenio().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificarse el nombre del dueño.");
        }
        // ---- Validacion de ingreso de fecha de nacimiento futura
        if (mascota.getFechaNacimiento() != null && mascota.getFechaNacimiento().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser posterior a hoy.");
        }
    }
    
    // ---- Validacion de ID
    public void validateId(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID de la mascota debe ser válido (mayor a cero).");
        }
    }
    
    // ---- Recuperar ID generado
    private Long getGeneratedId(Connection conn) throws Exception {
        Long id = null;
        String sql = "SELECT LAST_INSERT_ID()";

        try (var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            if (rs.next()) {
                id = rs.getLong(1);
            }
        }
        return id;
    }
    
    public MascotaDAO getMascotaDAO() {
        return mascotaDAO;
    }
    
    
}
