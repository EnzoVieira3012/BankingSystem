package br.com.banco.util;

import java.util.Scanner;

/**
 * Utilitário robusto de entrada console com tratamento NumberFormatException.
 * Substitui InputUtil na Fase 2 - usa parseInt/parseDouble em vez de Scanner.nextXxx.
 */
public final class InputUtilSeguro {

    private static final Scanner scanner = new Scanner(System.in);

    private InputUtilSeguro() {
        // Utility class
    }

    /**
     * Lê inteiro com validação NumberFormatException.
     * Loop até entrada válida.
     */
    public static int lerInteiroSeguro(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro válido.");
            }
        }
    }

    /**
     * Lê double com validação NumberFormatException.
     * Aceita vírgula ou ponto decimal.
     */
    public static double lerDoubleSeguro(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número decimal válido (ex: 100.50).");
            }
        }
    }

    /**
     * Lê string não vazia.
     */
    public static String lerStringSegura(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim();
            if (!linha.isEmpty()) {
                return linha;
            }
            System.out.println("Entrada não pode ser vazia.");
        }
    }
}