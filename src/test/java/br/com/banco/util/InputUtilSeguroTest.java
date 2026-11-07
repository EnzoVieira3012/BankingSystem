package br.com.banco.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

class InputUtilSeguroTest {

    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // Each test will set its own System.in via setInput()
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Nested
    @DisplayName("lerInteiroSeguro")
    class LerInteiroTest {

        @Test
        @DisplayName("Aceita inteiro válido")
        void inteiroValido() {
            setInput("42\n");
            int result = InputUtilSeguro.lerInteiroSeguro("Digite: ");
            assertEquals(42, result);
        }

        @Test
        @DisplayName("Rejeita não-numérico e aceita válido depois")
        void naoNumericoDepoisValido() {
            setInput("abc\n10\n");
            int result = InputUtilSeguro.lerInteiroSeguro("Digite: ");
            assertEquals(10, result);
        }

        @Test
        @DisplayName("Aceita número negativo")
        void negativo() {
            setInput("-5\n");
            int result = InputUtilSeguro.lerInteiroSeguro("Digite: ");
            assertEquals(-5, result);
        }
    }

    @Nested
    @DisplayName("lerDoubleSeguro")
    class LerDoubleTest {

        @Test
        @DisplayName("Aceita ponto decimal")
        void pontoDecimal() {
            setInput("10.50\n");
            double result = InputUtilSeguro.lerDoubleSeguro("Digite: ");
            assertEquals(10.50, result, 0.001);
        }

        @Test
        @DisplayName("Aceita vírgula decimal (BR)")
        void virgulaDecimal() {
            setInput("10,50\n");
            double result = InputUtilSeguro.lerDoubleSeguro("Digite: ");
            assertEquals(10.50, result, 0.001);
        }

        @Test
        @DisplayName("Rejeita inválido e aceita válido depois")
        void invalidoDepoisValido() {
            setInput("abc\n100.0\n");
            double result = InputUtilSeguro.lerDoubleSeguro("Digite: ");
            assertEquals(100.0, result, 0.001);
        }

        @Test
        @DisplayName("Aceita número negativo com vírgula")
        void negativoVirgula() {
            setInput("-5,25\n");
            double result = InputUtilSeguro.lerDoubleSeguro("Digite: ");
            assertEquals(-5.25, result, 0.001);
        }
    }

    @Nested
    @DisplayName("lerStringSegura")
    class LerStringTest {

        @Test
        @DisplayName("Aceita string não vazia")
        void naoVazia() {
            setInput("João\n");
            String result = InputUtilSeguro.lerStringSegura("Digite: ");
            assertEquals("João", result);
        }

        @Test
        @DisplayName("Rejeita vazia e aceita válido depois")
        void vaziaDepoisValido() {
            setInput("\nMaria\n");
            String result = InputUtilSeguro.lerStringSegura("Digite: ");
            assertEquals("Maria", result);
        }

        @Test
        @DisplayName("Trim remove espaços")
        void trim() {
            setInput("  João Silva  \n");
            String result = InputUtilSeguro.lerStringSegura("Digite: ");
            assertEquals("João Silva", result);
        }

        @Test
        @DisplayName("Rejeita apenas espaços")
        void apenasEspacos() {
            setInput("   \nPedro\n");
            String result = InputUtilSeguro.lerStringSegura("Digite: ");
            assertEquals("Pedro", result);
        }
    }
}