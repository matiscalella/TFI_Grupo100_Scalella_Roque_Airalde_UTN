
package main;

import java.util.Scanner;
import service.MascotaServiceImpl;
import service.MicrochipServiceImpl;

public class AppMenu {
    // ---- Atributo final de scanner, es el unico lector de consola
        private final Scanner scanner; 
    // ---- Clase que ejecuta las acciones del programa
    private final MenuHandler menuHandler; 
    // ---- Bandera para controlar flujo del programa
    private boolean running; 

    // ---- Constructor
    public AppMenu() {
        
        this.scanner = new Scanner(System.in);
        // ----- Inicializacion de los servicios (cada servicio tiene su acceso al DAO)
        MascotaServiceImpl mascotaService = new MascotaServiceImpl();
        MicrochipServiceImpl microchipService = new MicrochipServiceImpl();
        // ---- Se instancia el manejador del menu y se inyectan sus dependencias
        this.menuHandler = new MenuHandler(scanner, mascotaService, microchipService);
        this.running = true;
    }
    
    public void run() {
        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());
                processOption(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
        scanner.close();
    }
    
    private void processOption(int opcion) {
        switch (opcion) {
            case 1 -> menuHandler.gestionarMascotas();
            case 2 -> menuHandler.gestionarMicrochips();
            case 3 -> menuHandler.mostrarVistaMascotasConMicrochip();
            case 4 -> menuHandler.gestionarTransacciones();
            case 0 -> {
                System.out.println("Saliendo del sistema...");
                running = false;
            }
            default -> System.out.println("Opción no válida.");
        }
    }

}
