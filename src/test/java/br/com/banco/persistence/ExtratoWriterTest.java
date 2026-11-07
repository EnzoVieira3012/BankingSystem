package br.com.banco.persistence;

import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

class ExtratoWriterTest {

    private static final String TEST_FILE = "test_extrato.txt";

    @AfterEach
    void cleanup() {
        File f = new File(TEST_FILE);
        if (f.exists()) f.delete();
    }

    @Nested
    @DisplayName("gravarExtrato(Conta, String)")
    class GravarTest {

        @Test
        @DisplayName("Grava extrato ContaCorrente")
        void gravarContaCorrente() throws IOException, ValorInvalidoException {
            ContaCorrente cc = new ContaCorrente(1234, 1, 500);
            cc.depositar(100);

            ExtratoWriter.gravarExtrato(cc, TEST_FILE);

            File f = new File(TEST_FILE);
            assertTrue(f.exists());
            String content = Files.readString(f.toPath());
            assertTrue(content.contains("EXTRATO BANCÁRIO"));
            assertTrue(content.contains("1234"));
            assertTrue(content.contains("100.00"));
            assertTrue(content.contains("Limite: 500.00"));
            assertTrue(content.contains("Disponível: 600.00"));
        }

        @Test
        @DisplayName("Grava extrato ContaPoupanca")
        void gravarContaPoupanca() throws IOException, ValorInvalidoException {
            ContaPoupanca cp = new ContaPoupanca(5678, 1, 0.01);
            cp.depositar(1000);

            ExtratoWriter.gravarExtrato(cp, TEST_FILE);

            String content = Files.readString(new File(TEST_FILE).toPath());
            assertTrue(content.contains("5678"));
            assertTrue(content.contains("1000.00"));
            assertTrue(content.contains("Taxa Rendimento: 1.0%"));
        }
    }

    @Nested
    @DisplayName("gravarExtrato(Conta, String, boolean) - append")
    class AppendTest {

        @Test
        @DisplayName("Append adiciona ao final")
        void append() throws IOException {
            ContaCorrente cc = new ContaCorrente(1234, 1, 500);
            cc.depositar(100);

            ExtratoWriter.gravarExtrato(cc, TEST_FILE, false);
            ExtratoWriter.gravarExtrato(cc, TEST_FILE, true);

            String content = Files.readString(new File(TEST_FILE).toPath());
            // Deve ter dois extratos
            int count = content.split("===").length - 1;
            assertEquals(2, count);
        }
    }
}