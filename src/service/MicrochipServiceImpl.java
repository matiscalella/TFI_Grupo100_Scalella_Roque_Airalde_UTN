
package service;

import dao.MascotaDAO;
import entities.Microchip;
import dao.MicrochipDAO;
import entities.Mascota;
import java.util.List;

public class MicrochipServiceImpl implements GenericService<Microchip> {
    
    private final MicrochipDAO microchipDAO;

    public MicrochipServiceImpl(MicrochipDAO microchipDAO) {
        if (microchipDAO == null) {
            throw new IllegalArgumentException("MicrochipDAO no puede ser null");
        }
        this.microchipDAO = microchipDAO;
    }
    
    public MicrochipServiceImpl() {
        this.microchipDAO = new MicrochipDAO();
    }

    @Override
    public void insertar(Microchip microchip) throws Exception {
        // ---- Validar que el objeto no sea null
        microchipNull(microchip);
        
        // ---- Validar los valores del objeto
        validateMicrochip(microchip);
        
        // ---- Validacion de unicidad de codigo y mascota
        List<Microchip> chips = microchipDAO.leerTodos(); 
        for (Microchip m : chips) {
            
            // ---- Compara el codigo del microchip con los existentes en la bd
            if (m.getCodigo().equalsIgnoreCase(microchip.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un microchip con el código: " + microchip.getCodigo());
            }
            // ---- Busca si el id de la mascota ya tiene un microchip asociado
            if (m.getIdMascota().equals(microchip.getIdMascota())) {
                throw new IllegalArgumentException("La mascota con ID " + microchip.getIdMascota() + " ya tiene un microchip asociado.");          
            }
        }
        // ---- Validar que la mascota exista en la BD antes de insertar
        MascotaDAO mascotaDAO = new MascotaDAO();
        Mascota mascotaAsociada = mascotaDAO.leer(microchip.getIdMascota());
        if (mascotaAsociada == null) {
            throw new IllegalArgumentException("No existe ninguna mascota con el ID " + microchip.getIdMascota() + " en el sistema.");
        }
        // ---- Insertar microchip
        microchipDAO.crear(microchip);
        
    }

    @Override
    public void actualizar(Microchip microchip) throws Exception {
        microchipNull(microchip);
        validateId(microchip.getId());
        validateMicrochip(microchip);
        // ---- Validacion de la existencia del microchip en la bd antes de actualizar
        Microchip existente = microchipDAO.leer(microchip.getId());
        if (existente == null) {
            throw new IllegalArgumentException("No se encontro un microchip con id: " + microchip.getId() + " en la base de datos.");
        }
        microchipDAO.actualizar(microchip);
        
    }

    @Override
    public void eliminar(Long id) throws Exception {
        validateId(id);
        // ---- Verificar que exista el microchip con ese id en la BD
        Microchip existente = microchipDAO.leer(id);
        if (existente == null) {
            throw new IllegalArgumentException("No se encontró ningún microchip con el ID especificado: " + id);
        }
        microchipDAO.eliminar(id);
        
    }

    @Override
    public Microchip getById(Long id) throws Exception {
        validateId(id);
        Microchip microchip = microchipDAO.leer(id);
        if (microchip == null) {
            throw new IllegalArgumentException("No se encontró ningún microchip con el ID especificado: " + id);
        }
        return microchip;
    }

    @Override
    public List<Microchip> getAll() throws Exception {
        List<Microchip> microchips = microchipDAO.leerTodos();
        if (microchips == null || microchips.isEmpty()) {
            throw new IllegalArgumentException("No hay registros de microchips en la base de datos.");
        }
        return microchips;
    }
    
// ---- Metodos auxiliares
    public void microchipNull(Microchip microchip){
        if (microchip == null) {
                throw new IllegalArgumentException("El objeto Microchip no puede ser nulo.");
        }
    }
    
    // ---- Validar datos de Microchip
    public void validateMicrochip(Microchip microchip) {
        
        // ---- Validacion del codigo del chip
        if(microchip.getCodigo() == null || microchip.getCodigo().trim().isEmpty()){
            throw new IllegalArgumentException("El codigo del chip no puede ser null ni estar vacio.");
        }
        // ---- Validacion del campo veterinaria
        if(microchip.getVeterinaria()== null || microchip.getVeterinaria().trim().isEmpty()){
            throw new IllegalArgumentException("Debe especificarse la veterinaria.");
        }
        // ---- Validacion de la fecha
        if (microchip.getFechaImplantacion() != null && microchip.getFechaImplantacion().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de implantación no puede ser posterior a hoy.");
        }
        // ---- Validacion del id de mascota asociado al microchip
        if (microchip.getIdMascota() == null || microchip.getIdMascota() <= 0) {
            throw new IllegalArgumentException("Debe asociarse un ID de mascota válido al microchip.");
        }
    }
    
    // ---- Validar el ID del Microchip
    public void validateId(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del microchip debe ser válido (mayor a cero).");
        }
}
    
}
