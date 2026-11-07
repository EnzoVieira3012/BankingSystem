package br.com.banco.persistence;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.service.BancoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

class BackupContasTest {

    private static final String TEST_BACKUP_FILE = "backup_contas.txt";

    @AfterEach
    void cleanup() {
        File f = new File(TEST_BACKUP_FILE);
        if (f.exists()) f.delete();
    }

    @Nested
    @DisplayName("salvar() - serialização")
    class SalvarTest {

        @Test
        @DisplayName("Salva contas correntes e poupança")
        void salvarContas() throws IOException {
            BancoService service = new BancoService();
            ContaCorrente cc = service.criarContaCorrente(500);
            cc.depositar(200);
            ContaPoupanca cp = service.criarContaPoupanca(0.01);
            cp.depositar(1000);

            BackupContas.salvar(service);

            File f = new File(TEST_BACKUP_FILE);
            assertTrue(f.exists());
            assertTrue(f.length() > 0);

            String content = java.nio.file.Files.readString(f.toPath());
            assertTrue(content.contains("CORRENTE"));
            assertTrue(content.contains("POUPANCA"));
            assertTrue(content.contains(String.valueOf(cc.getNumero())));
            assertTrue(content.contains(String.valueOf(cp.getNumero())));
        }

        @Test
        @DisplayName("Salvar lista vazia cria arquivo vazio")
        void salvarVazio() throws IOException {
            BancoService service = new BancoService();

            BackupContas.salvar(service);

            File f = new File(TEST_BACKUP_FILE);
            assertTrue(f.exists());
        }
    }

    @Nested
    @DisplayName("restaurar() - deserialização")
    class RestaurarTest {

        @Test
        @DisplayName("Restaura contas do arquivo")
        void restaurarContas() throws IOException {
            // Primeiro salva
            BancoService serviceOrig = new BancoService();
            ContaCorrente cc = serviceOrig.criarContaCorrente(500);
            cc.depositar(200);
            ContaPoupanca cp = serviceOrig.criarContaPoupanca(0.01);
            cp.depositar(1000);
            BackupContas.salvar(serviceOrig);

            // Depois restaura em novo service
            BancoService serviceNovo = new BancoService();
            List<Conta> restauradas = BackupContas.restaurar(serviceNovo);

            assertEquals(2, restauradas.size());
            assertEquals(2, serviceNovo.listarContas().size());

            // Verifica dados restaurados
            Conta ccRest = serviceNovo.buscarConta(cc.getNumero(), cc.getAgencia());
            assertNotNull(ccRest);
            assertEquals(cc.getSaldo(), ccRest.getSaldo(), 0.01);
            assertTrue(ccRest instanceof ContaCorrente);
            assertEquals(((ContaCorrente) cc).getLimite(), ((ContaCorrente) ccRest).getLimite(), 0.01);

            Conta cpRest = serviceNovo.buscarConta(cp.getNumero(), cp.getAgencia());
            assertNotNull(cpRest);
            assertEquals(cp.getSaldo(), cpRest.getSaldo(), 0.01);
            assertTrue(cpRest instanceof ContaPoupanca);
            assertEquals(((ContaPoupanca) cp).getTaxaRendimento(), ((ContaPoupanca) cpRest).getTaxaRendimento(), 0.0001);
        }
    }

    @Nested
    @DisplayName("backupExiste()")
    class ExisteTest {

        @Test
        @DisplayName("Retorna true se arquivo existe e não vazio")
        void existeTrue() throws IOException {
            BancoService service = new BancoService();
            service.criarContaCorrente(500);
            BackupContas.salvar(service);

            assertTrue(BackupContas.backupExiste());
        }

        @Test
        @DisplayName("Retorna false se arquivo não existe")
        void existeFalse() {
            File f = new File(TEST_BACKUP_FILE);
            if (f.exists()) f.delete();

            assertFalse(BackupContas.backupExiste());
        }
    }
}