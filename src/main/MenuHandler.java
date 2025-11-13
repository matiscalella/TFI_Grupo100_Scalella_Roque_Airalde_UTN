
package main;

import entities.Mascota;
import entities.Microchip;
import java.util.List;
import java.util.Scanner;
import service.MascotaServiceImpl;
import service.MicrochipServiceImpl;

public class MenuHandler {
    private final Scanner scanner;
    private final MascotaServiceImpl mascotaService;
    private final MicrochipServiceImpl microchipService;

    public MenuHandler(Scanner scanner, MascotaServiceImpl mascotaService, MicrochipServiceImpl microchipService) {
        this.scanner = scanner;
        this.mascotaService = mascotaService;
        this.microchipService = microchipService;
    }
// --------------------- MENUS ---------------------
    public void gestionarMascotas() {
        boolean volver = false; // Inicializa la variable para volver al menu anterior en false
        while (!volver) {
            // ---- Muestra el menu para gestionar mascotas
            MenuDisplay.mostrarMenuMascotas();
            // ---- El usuario ingresa una opcion
            int opcion = MenuDisplay.leerOpcion(scanner);
            switch (opcion) {
                case 1 -> crearMascota();
                case 2 -> listarMascotas();
                case 3 -> buscarMascotaPorId();
                case 4 -> actualizarMascota();
                case 5 -> eliminarMascota();
                case 6 -> buscarMascotaPorNombre();
                case 7 -> crearMascotaConMicrochipTransaccion();
                case 0 -> volver = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    public void gestionarMicrochips() {
        boolean volver = false; // Inicializa la variable para volver al menu anterior en false
        while (!volver) {
            
            // ---- Muestra el menu para gestionar microchips
            MenuDisplay.mostrarMenuMicrochips();
            // ---- El usuario ingresa una opcion
            int opcion = MenuDisplay.leerOpcion(scanner);
            switch (opcion) {
                case 1 -> crearMicrochip();
                case 2 -> listarMicrochips();
                case 3 -> buscarMicrochipPorId();
                case 4 -> actualizarMicrochip();
                case 5 -> eliminarMicrochip();
                case 6 -> buscarMicrochipPorCodigo();
                // ---- Si el usuario ingresa 0, regresa al menu anterior
                case 0 -> volver = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    public void mostrarVistaMascotasConMicrochip() {
        // ---- MascotaServiceImpl no expone el DAO, por lo que se usa un getter
        try {
                mascotaService.getMascotaDAO().leerVistaMascotasConMicrochip();
            } catch (Exception e) {
                System.err.println("Error al mostrar vista: " + e.getMessage());
            }
    }

    public void gestionarTransacciones(){
        boolean volver = false; // Inicializa la variable para volver al menu anterior en false
        while (!volver) {
            
            // ---- Muestra el menu para gestionar microchips
            MenuDisplay.mostrarMenuTransacciones();
            // ---- El usuario ingresa una opcion
            int opcion = MenuDisplay.leerOpcion(scanner);
            switch (opcion) {
                case 1 -> crearMascotaConMicrochipTransaccion();
                case 2 -> actualizarMascotaYMicrochipTransaccion();
                case 3 -> eliminarMascotaYMicrochipTransaccion();
                // ---- Si el usuario ingresa 0, regresa al menu anterior
                case 0 -> volver = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

// --------------------- METODOS PARA GESTION DEL MENU MASCOTAS ---------------------
    private void crearMascota() {
        try {
            
            // ---- Se le piden los valores al usuario
            System.out.println("------------- CREAR MASCOTA -------------");
            System.out.println("---- Ingrese los datos de la mascota ----");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Especie: ");
            String especie = scanner.nextLine();
            System.out.print("Raza: ");
            String raza = scanner.nextLine();
            System.out.print("Dueño: ");
            String duenio = scanner.nextLine();
            System.out.print("Fecha de nacimiento (en formato AAAA-MM-DD o vacío): ");
            String fechaStr = scanner.nextLine();
            
            // ---- Se asignan los valores a la mascota
            Mascota mascotaNueva = new Mascota();
            mascotaNueva.setEliminado(false);
            mascotaNueva.setNombre(nombre);
            mascotaNueva.setEspecie(especie);
            mascotaNueva.setRaza(raza);
            mascotaNueva.setDuenio(duenio);
            try { // ---- Validacion para el formato de la fecha, el usuario puede NO ingresar una fecha
                if (!fechaStr.isBlank()) {
                    mascotaNueva.setFechaNacimiento(java.time.LocalDate.parse(fechaStr));
                }
            } catch (Exception e) {
                System.err.println("Formato de fecha inválido (use AAAA-MM-DD): ");
                return;
            }
            
            // ---- Antes de insertar la mascota se le pregunta al usuario si quiere insertar un microchip:

            System.out.print("¿Desea registrar un microchip para esta mascota? (s/n): ");
            String respuesta = scanner.nextLine();
            
            // ---- Si responde S o s:
            if (respuesta.equalsIgnoreCase("s")) {
                Microchip microchipNuevo = new Microchip();

                System.out.println("---- Ingrese los datos del microchip ----");
                System.out.print("Código: ");
                microchipNuevo.setCodigo(scanner.nextLine().trim());
                System.out.print("Fecha de implantación (AAAA-MM-DD o vacío): ");
                String fechaStrMicrochip = scanner.nextLine();
                if (!fechaStrMicrochip.isBlank()) {
                    try {
                        microchipNuevo.setFechaImplantacion(java.time.LocalDate.parse(fechaStrMicrochip));
                    } catch (Exception e) {
                        System.err.println("Formato de fecha inválido. Use AAAA-MM-DD.");
                        return;
                    }
                }
                System.out.print("Veterinaria: ");
                microchipNuevo.setVeterinaria(scanner.nextLine().trim());
                System.out.print("Observaciones: ");
                microchipNuevo.setObservaciones(scanner.nextLine().trim());

                // ---- El ID se asigna automáticamente en la capa Service al realizar la transacción                
                // ---- Insertar ambos en una sola transaccion
                try {
                    mascotaService.insertarConMicrochip(mascotaNueva, microchipNuevo);
                } catch (Exception e) {
                    System.err.println("Error al registrar mascota con microchip: " + e.getMessage());
                }
            } else {
                // ---- Si el usuario solo quiere crear una mascota (sin microchip)
                mascotaService.insertar(mascotaNueva);
                System.out.println("Mascota insertada correctamente: " + mascotaNueva.getNombre());
            }
            
            } catch (Exception e) {
                System.err.println("Error al crear mascota: " + e.getMessage());
        }
    }

    private void listarMascotas() {
        try {
            System.out.println("---- MASCOTAS ----");
            List<Mascota> listaDeMascotas = mascotaService.getAll();
            for (Mascota mascota : listaDeMascotas) {
                System.out.println(mascota);
            }
            } catch (Exception e) {
            System.err.println("Error al listar mascotas: " + e.getMessage());
        }
    }
    
    private void buscarMascotaPorId(){
        try {
            System.out.println("---- Buscar mascota por ID ----");
            System.out.print("Ingrese el ID de la mascota: ");
            
            // ---- Ingreso del id en formato texto
            String idStr = scanner.nextLine();
            // ---- Conversion del id a Long (si falla lanza una excepcion)
            Long id = Long.valueOf(idStr);
            // ---- Llamada al Service para buscar la mascota por ID
            Mascota mascota = mascotaService.getById(id);
            System.out.println("Mascota encontrada: ");
            System.out.println(mascota);
        } catch (NumberFormatException e) {
            System.err.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.err.println("Error al buscar mascota: " + e.getMessage());
        }
    }
    
    private void actualizarMascota() {
        System.out.println("---- Actualizar mascota ----");
        try {
            
            System.out.print("Ingrese el ID de la mascota a actualizar: ");
            Long id = Long.valueOf(scanner.nextLine());
            // ----- Buscar la mascota a actualizar
            Mascota mascota = mascotaService.getById(id);
            System.out.println("Mascota encontrada. Ingrese los datos de la mascota: ");
            System.out.println("(Dejar entrada vacia (enter) para conservar valor actual)");
            // ---- Ingresar los datos nuevos
            System.out.print("Nuevo nombre [actual: " + mascota.getNombre() + "]: ");
            String nuevoNombre = scanner.nextLine();
            System.out.print("Nueva especie [actual: " + mascota.getEspecie() + "]: " );
            String nuevaEspecie = scanner.nextLine();
            System.out.print("Nueva raza [actual: " + mascota.getRaza() + "]: " );
            String nuevaRaza = scanner.nextLine();
            System.out.print("Nuevo dueño [actual: " + mascota.getDuenio() + "]: " );
            String nuevoDuenio = scanner.nextLine();
            System.out.print("Nueva fecha de nacimiento [actual: " + mascota.getFechaNacimiento() + "]: ");
            String nuevaFechaStr = scanner.nextLine();

            // ---- Asignar valores nuevos a la mascota
            // ---- (si el usuario dejo un campo en blanco, no se modifican los valores)
            if (!nuevoNombre.isBlank()) mascota.setNombre(nuevoNombre);
            if (!nuevaEspecie.isBlank()) mascota.setEspecie(nuevaEspecie);
            if (!nuevaRaza.isBlank()) mascota.setRaza(nuevaRaza);
            if (!nuevoDuenio.isBlank()) mascota.setDuenio(nuevoDuenio);
            if (!nuevaFechaStr.isBlank()) {
                try {
                    mascota.setFechaNacimiento(java.time.LocalDate.parse(nuevaFechaStr));
                } catch (Exception e) {
                    System.err.println("Formato de fecha inválido. Use AAAA-MM-DD.");
                    return;
                }
            }
            // ---- Actualizar datos en la BD (mensaje de exito desde el Service)
            System.out.println("Actualizando datos...");
            mascotaService.actualizar(mascota);
            System.out.println("La mascota con ID " + mascota.getId() + " ha sido actualizada correctamente.");
        } catch (NumberFormatException e) {
            System.err.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la mascota: " + e.getMessage());
        }
    }
    
    private void eliminarMascota() {
        System.out.println("---- Eliminar mascota ----");
        try {
            System.out.print("Ingrese el ID de la mascota a eliminar (mediante baja logica): ");
            Long id = Long.valueOf(scanner.nextLine());
            Mascota mascota = mascotaService.getById(id);
            System.out.print("Mascota encontrada: ");
            System.out.println(mascota);
            System.out.println(); // ---- Linea para claridad visual en consola
            System.out.println("¿Esta seguro de que desea eliminar a la mascota " + mascota.getNombre() + "?");
            System.out.print("Ingrese s/n: ");
            String confirma = scanner.nextLine();
            // ---- Confirmacion del usuario
            if (!confirma.equalsIgnoreCase("s")) {
                System.out.println("Operación cancelada.");
                return;
            }
            System.out.println("Eliminando mascota...");
            mascotaService.eliminar(id);
            System.out.println("Mascota con ID: " + id + " eliminada correctamente (baja lógica).");
            } catch (NumberFormatException e) {
                System.err.println("El ID debe ser un número válido.");
            } catch (Exception e) {
                System.err.println("Error al eliminar la mascota: " + e.getMessage());
            }
    }
    
    private void buscarMascotaPorNombre() {
        System.out.println("---- Buscar mascota por NOMBRE ----");
        try {
            System.out.print("Ingrese el nombre de la mascota a buscar: ");
            String nombre = scanner.nextLine().trim().toLowerCase(); // Convertimos el nombre a lower case
            // ---- Validacion de ingreso de nombre
            if (nombre.isBlank()) {
                System.out.println("Debe ingresar un nombre para realizar la búsqueda.");
                return;
            }
            List<Mascota> listaDeMascotas = mascotaService.getAll();
            boolean coincidencia = false;
            for (Mascota mascota : listaDeMascotas) {
                // ---- Usamos contains para permitir coincidencias parciales ("Lu" mostraria Luna, Luana, etc)
                if (mascota.getNombre().toLowerCase().contains(nombre)){
                    System.out.println(mascota);
                    coincidencia = true;
                }
            }
            if (!coincidencia) {
                System.out.println("No se encontraron mascotas con el nombre " + nombre);
            }   
        } catch (Exception e) {
            System.err.println("Error al buscar mascota: " + e.getMessage());
        }
    } 

// --------------------- METODOS PARA GESTION DEL MENU MICROCHIPS ---------------------

    private void crearMicrochip() {
        try {
            
            // ---- Se le piden los valores al usuario
            System.out.println("------------- CREAR MICROCHIP -------------");
            System.out.println("---- Ingrese los datos del microchip ----");
            System.out.print("Codigo: ");
            String codigo = scanner.nextLine().trim();
            System.out.print("Fecha de implantacion (en formato AAAA-MM-DD o vacío): ");
            String fechaStr = scanner.nextLine();
            System.out.print("Veterinaria: ");
            String veterinaria = scanner.nextLine().trim();
            System.out.print("Observaciones: ");
            String observaciones = scanner.nextLine().trim();
            System.out.print("Ingrese el ID de la mascota asociada al microchip: ");
            Long idMascota = Long.valueOf(scanner.nextLine());
            
            
            // ---- Se crea y asignan los valores al microchip
            Microchip microchipNuevo = new Microchip();
            microchipNuevo.setEliminado(false);
            microchipNuevo.setCodigo(codigo);
            microchipNuevo.setVeterinaria(veterinaria);
            microchipNuevo.setObservaciones(observaciones);
            microchipNuevo.setIdMascota(idMascota);
            try { // ---- Validacion para el formato de la fecha, el usuario puede NO ingresar una fecha
                if (!fechaStr.isBlank()) {
                    microchipNuevo.setFechaImplantacion(java.time.LocalDate.parse(fechaStr));
                }
            } catch (Exception e) {
                    System.err.println("Formato de fecha inválido (use AAAA-MM-DD): ");
                return;
            }
            
            // ---- Se llama al Service para validar los datos.
            microchipService.insertar(microchipNuevo);
            System.out.println("Microchip " + microchipNuevo.getCodigo() + " registrado correctamente para mascota ID " + microchipNuevo.getIdMascota());
            
            // ---- Manejo de errores
            } catch (NumberFormatException e) {
                System.err.println("El ID de la mascota debe ser un número válido.");
            } catch (IllegalArgumentException e){
                System.err.println("Error de validación: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al crear microchip: " + e.getMessage());
            }
    }

    private void listarMicrochips() {
        try {
            System.out.println("---- MICROCHIPS ----");
            List<Microchip> listaDeMicrochips = microchipService.getAll();
            // ---- Si no hay microchips en sistema el service muestra un mensaje
            // ---- Lista de microchips con contador para mejora visual
            int contador = 1;
            for (Microchip microchip : listaDeMicrochips) {
                System.out.println(contador++ + ". " + microchip);
            }
            } catch (Exception e) {
            System.err.println("Error al listar microchips: " + e.getMessage());
        }
    }

    private void buscarMicrochipPorId() {
        try {
            System.out.println("---- Buscar microchip por ID ----");
            System.out.print("Ingrese el ID del microchip: ");
            
            // ---- Ingreso del id en formato texto
            String idStr = scanner.nextLine();
            // ---- Conversion del id a Long (si falla lanza una excepcion)
            Long id = Long.valueOf(idStr);
            // ---- Llamada al Service para buscar la mascota por ID
            Microchip microchip = microchipService.getById(id);
            System.out.print("Microchip encontrado: ");
            System.out.println(microchip);
        } catch (NumberFormatException e) {
            System.err.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.err.println("Error al buscar el microchip: " + e.getMessage());
        }
    }

    private void actualizarMicrochip() {
        System.out.println("---- Actualizar microchip ----");
        try {
            
            System.out.print("Ingrese el ID del microchip a actualizar: ");
            Long id = Long.valueOf(scanner.nextLine());
            // ----- Buscar el microchip a actualizar
            Microchip microchip = microchipService.getById(id);
            System.out.println("Microchip encontrado. Ingrese los datos del microchip: ");
            System.out.println("(Dejar entrada vacia (enter) para conservar valor actual)");
            // ---- Ingresar los datos nuevos
            System.out.print("Codigo [actual: " + microchip.getCodigo()+ "]: ");
            String nuevoCodigo = scanner.nextLine();
            System.out.print("Nueva fecha de implantacion [actual: " + microchip.getFechaImplantacion() + "]: " );
            String nuevaFechaImplantacion = scanner.nextLine();
            System.out.print("Nueva veterinaria [actual: " + microchip.getVeterinaria()+ "]: " );
            String nuevaVeterinaria = scanner.nextLine();
            System.out.print("Nuevas observaciones [actuales: " + microchip.getObservaciones()+ "]: " );
            String nuevasObservaciones = scanner.nextLine();

            // ---- Asignar valores nuevos al microchip
            // ---- (si el usuario dejo un campo en blanco, no se modifican los valores)
            if (!nuevoCodigo.isBlank()) microchip.setCodigo(nuevoCodigo);
            if (!nuevaVeterinaria.isBlank()) microchip.setVeterinaria(nuevaVeterinaria);
            if (!nuevasObservaciones.isBlank()) microchip.setObservaciones(nuevasObservaciones);
            if (!nuevaFechaImplantacion.isBlank()) {
                try {
                    microchip.setFechaImplantacion(java.time.LocalDate.parse(nuevaFechaImplantacion));
                } catch (Exception e) {
                    System.err.println("Formato de fecha inválido. Use AAAA-MM-DD.");
                    return;
                }
            }
            // ---- Actualizar datos en la BD (mensaje de exito desde el Service)
            System.out.println("Actualizando datos del microchip...");
            microchipService.actualizar(microchip);
            System.out.println("La informacion del microchip con id: " + microchip.getId() + " se actualizo correctamente.");
        } catch (NumberFormatException e) {
            System.err.println("El ID debe ser un número válido.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el microchip: " + e.getMessage());
        }
    }

    private void eliminarMicrochip() {
        System.out.println("---- Eliminar microchip ----");
        try {
            System.out.print("Ingrese el ID del microchip a eliminar (baja lógica): ");
            Long id = Long.valueOf(scanner.nextLine());
            Microchip microchip = microchipService.getById(id);
            System.out.println("Microchip encontrado: ");
            System.out.println(microchip);
            System.out.println(); // ---- Linea para claridad visual en consola
            System.out.println("¿Esta seguro de que desea eliminar el microchip " + microchip.getCodigo() + "?");
            System.out.print("Ingrese s/n: ");
            String confirma = scanner.nextLine();
            // ---- Confirmacion del usuario
            if (!confirma.equalsIgnoreCase("s")) {
                System.out.println("Operación cancelada.");
                return;
            }
            System.out.println("Eliminando microchip ...");
            microchipService.eliminar(id);
            System.out.println("Microchip con ID " + id + " eliminado correctamente (baja lógica).");
            } catch (NumberFormatException e) {
                System.err.println("El ID debe ser un número válido.");
            } catch (Exception e) {
                System.err.println("Error al eliminar el microchip: " + e.getMessage());
            }
    }

    private void buscarMicrochipPorCodigo() {
        System.out.println("---- Buscar microchip por CODIGO ----");
        try {
            System.out.print("Ingrese el codigo del microchip: ");
            String codigo = scanner.nextLine().trim().toLowerCase(); // Convertimos el codigo a lower case
            // ---- Validacion de ingreso de codigo
            if (codigo.isBlank()) {
                System.out.println("Debe ingresar un codigo para realizar la búsqueda.");
                return;
            }
            List<Microchip> listaDeMicrochips = microchipService.getAll();
            boolean coincidencia = false;
            for (Microchip microchip : listaDeMicrochips) {
                // ---- Usamos contains para permitir coincidencias parciales
                if (microchip.getCodigo().toLowerCase().contains(codigo)){
                    System.out.println(microchip);
                    coincidencia = true;
                }
            }
            if (!coincidencia) {
                System.out.println("No se encontraron microchip con el codigo: " + codigo);
            }   
        } catch (Exception e) {
            System.err.println("Error al buscar el microchip: " + e.getMessage());
        }
    }

// --------------------- METODOS PARA GESTION DEL MENU MICROCHIPS ---------------------
    
    private void crearMascotaConMicrochipTransaccion() {
        try {
            System.out.println("---- Crear mascota y microchip [Transaccion ACID] ----");
            // ---- Ingreso de los datos de la mascota
            System.out.println("---- Ingrese los datos de la mascota ----");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Especie: ");
            String especie = scanner.nextLine();
            System.out.print("Raza: ");
            String raza = scanner.nextLine();
            System.out.print("Dueño: ");
            String duenio = scanner.nextLine();
            System.out.print("Fecha de nacimiento (en formato AAAA-MM-DD o vacío): ");
            String fechaStr = scanner.nextLine();
            // ---- Asignacion de los valores a la mascota
            Mascota mascotaNueva = new Mascota();
            mascotaNueva.setEliminado(false);
            mascotaNueva.setNombre(nombre);
            mascotaNueva.setEspecie(especie);
            mascotaNueva.setRaza(raza);
            mascotaNueva.setDuenio(duenio);
            
            try { // ---- Validacion para el formato de la fecha, el usuario puede NO ingresar una fecha
                if (!fechaStr.isBlank()) {
                    mascotaNueva.setFechaNacimiento(java.time.LocalDate.parse(fechaStr));
                }
            } catch (Exception e) {
                System.err.println("Formato de fecha inválido (use AAAA-MM-DD): ");
                return;
            }
            
            // ---- Crear microchip - Se le piden los valores al usuario
            System.out.println("------------- CREAR MICROCHIP -------------");
            System.out.println("---- Ingrese los datos del microchip ----");
            System.out.print("Codigo: ");
            String codigo = scanner.nextLine().trim();
            System.out.print("Fecha de implantacion (en formato AAAA-MM-DD o vacío): ");
            String fechaStrMicrochip = scanner.nextLine();
            System.out.print("Veterinaria: ");
            String veterinaria = scanner.nextLine().trim();
            System.out.print("Observaciones: ");
            String observaciones = scanner.nextLine().trim();
          
            
            // ---- Se asignan los valores al microchip
            Microchip microchipNuevo = new Microchip();
            microchipNuevo.setEliminado(false);
            microchipNuevo.setCodigo(codigo);
            microchipNuevo.setVeterinaria(veterinaria);
            microchipNuevo.setObservaciones(observaciones);
            try { // ---- Validacion para el formato de la fecha, el usuario puede NO ingresar una fecha
                if (!fechaStrMicrochip.isBlank()) {
                    microchipNuevo.setFechaImplantacion(java.time.LocalDate.parse(fechaStrMicrochip));
                }
            } catch (Exception e) {
                System.err.println("Formato de fecha inválido (use AAAA-MM-DD): ");
                return;
            }
            
            // ---- Inicio de la transaccion (utiliza el metodo creado en MascotaServiceImpl para transacciones)
            System.out.println("Iniciando transacción...");
            mascotaService.insertarConMicrochip(mascotaNueva, microchipNuevo);
            // ---- En caso exitoso se imprimen los resultados por consola
            System.out.println("---- Mascota y microchip creados correctamente---- ");
            System.out.println("\n---- Datos de la Mascota creada ----");
            System.out.println(mascotaNueva.toString());

            System.out.println("\n---- Datos del Microchip creado ----");
            System.out.println(microchipNuevo.toString());
  
        } catch (Exception e) {
            System.err.println("Error detectado: " + e.getMessage());
            System.err.println("🔁 Se ejecuto rollback: no se creó ninguna de las entidades.");
        }
    }
// ------------------------------------ PENDIENTES
    private void actualizarMascotaYMicrochipTransaccion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void eliminarMascotaYMicrochipTransaccion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
