
package main;

import java.util.Scanner;

public class MenuDisplay {

    public static void mostrarMenuPrincipal() {
        
        System.out.println("\n ------ MENU PRINCIPAL ------ ");
        System.out.println("[1] Gestionar MASCOTAS");
        System.out.println("[2] Gestionar MICROCHIPS");
        System.out.println("[3] Mostrar mascotas con microchips (vista)");
        System.out.println("[4] Transacciones");
        System.out.println("[0] Salir");
        System.out.print("Seleccione una opción: ");
        
    }
    
    public static void mostrarMenuMascotas(){
        
        System.out.println("\n ------ GESTION DE MASCOTAS ------ ");
        System.out.println("[1] Crear mascota");
        System.out.println("[2] Listar mascota");
        System.out.println("[3] Buscar mascota por ID");
        System.out.println("[4] Actualizar mascota");
        System.out.println("[5] Eliminar mascota");
        System.out.println("[6] Buscar mascota por nombre");
        System.out.println("[0] Volver al menu principal");
        System.out.println("------ --------------------- ------ ");
        System.out.print("Seleccione una opción: ");
    }
    
    public static void mostrarMenuMicrochips(){
        System.out.println("\n ------ GESTION DE MICROCHIPS ------ ");
        System.out.println("[1] Crear microchip");
        System.out.println("[2] Listar microchips");
        System.out.println("[3] Buscar microchip por ID");
        System.out.println("[4] Actualizar microchip");
        System.out.println("[5] Eliminar microchip");
        System.out.println("[6] Buscar microchip por código");
        System.out.println("[0] Volver al menu principal");
        System.out.println("------ --------------------- ------ ");
        System.out.print("Seleccione una opción: ");
    }
    
    public static void mostrarMenuTransacciones(){
        System.out.println("\n ------ TRANSACCIONES ------ ");
        System.out.println("[1] Crear mascota y microchip (Transacción ACID)");
        System.out.println("[2] Actualizar mascota y microchip");
        System.out.println("[3] Eliminar mascota y microchip asociado");
        System.out.println("[0] Volver al menu principal");
        System.out.println("------ --------------------- ------ ");
        System.out.print("Seleccione una opción: ");
    }
    
    
    // ---- Método auxiliar para leer opciones de manera segura
    public static int leerOpcion(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingrese un número: ");
            }
        }
    }
}
