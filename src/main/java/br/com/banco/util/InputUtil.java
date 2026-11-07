package br.com.banco.util;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputUtil {
    public static int lerInteiro(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int valor = scanner.nextInt();
                scanner.nextLine(); // limpar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
                scanner.nextLine(); // limpar buffer
            }
        }
    }

    public static double lerDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double valor = scanner.nextDouble();
                scanner.nextLine(); // limpar buffer
                return valor;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número decimal.");
                scanner.nextLine(); // limpar buffer
            }
        }
    }

    public static String lerString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}