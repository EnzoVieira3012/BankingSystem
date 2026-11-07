package br.com.banco.service;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class BancoServiceTest {

    private BancoService service;

    @BeforeEach
    void setUp() {
        service = new BancoService();
    }

    @Nested
    @DisplayName("Factory methods")
    class FactoryTest {

        @Test
        @DisplayName("criarContaCorrente adiciona à lista e retorna conta")
        void criarContaCorrente() {
            ContaCorrente cc = service.criarContaCorrente(500);
            assertNotNull(cc);
            assertEquals(500, cc.getLimite());
            assertEquals(1, service.listarContas().size());
        }

        @Test
        @DisplayName("criarContaPoupanca adiciona à lista e retorna conta")
        void criarContaPoupanca() {
            ContaPoupanca cp = service.criarContaPoupanca(0.01);
            assertNotNull(cp);
            assertEquals(0.01, cp.getTaxaRendimento());
            assertEquals(1, service.listarContas().size());
        }

        @Test
        @DisplayName("Múltiplas contas: lista cresce")
        void multiplasContas() {
            service.criarContaCorrente(100);
            service.criarContaPoupanca(0.02);
            service.criarContaCorrente(200);
            assertEquals(3, service.listarContas().size());
        }
    }

    @Nested
    @DisplayName("Busca")
    class BuscaTest {

        @Test
        @DisplayName("buscarConta encontra conta existente")
        void buscarExistente() {
            ContaCorrente cc = service.criarContaCorrente(500);
            Conta encontrada = service.buscarConta(cc.getNumero(), cc.getAgencia());
            assertNotNull(encontrada);
            assertEquals(cc.getNumero(), encontrada.getNumero());
        }

        @Test
        @DisplayName("buscarConta retorna null para inexistente")
        void buscarInexistente() {
            Conta c = service.buscarConta(9999, 1);
            assertNull(c);
        }
    }

    @Nested
    @DisplayName("Operações polimórficas")
    class OperacoesTest {

        @Test
        @DisplayName("depositar delega para conta")
        void depositar() {
            ContaCorrente cc = service.criarContaCorrente(500);
            service.depositar(cc.getNumero(), cc.getAgencia(), 100);
            assertEquals(100, cc.getSaldo());
        }

        @Test
        @DisplayName("sacar delega para conta (polimorfismo)")
        void sacar() throws SaldoInsuficienteException {
            ContaCorrente cc = service.criarContaCorrente(500);
            cc.depositar(200);
            service.sacar(cc.getNumero(), cc.getAgencia(), 150);
            assertEquals(50, cc.getSaldo());
        }

        @Test
        @DisplayName("sacar lança SaldoInsuficienteException se insuficiente")
        void sacarInsuficiente() {
            ContaCorrente cc = service.criarContaCorrente(500);
            cc.depositar(100);
            assertThrows(SaldoInsuficienteException.class,
                () -> service.sacar(cc.getNumero(), cc.getAgencia(), 700));
        }

        @Test
        @DisplayName("renderJurosPoupanca delega para ContaPoupanca")
        void renderJuros() {
            ContaPoupanca cp = service.criarContaPoupanca(0.01);
            cp.depositar(1000);
            service.renderJurosPoupanca(cp.getNumero(), cp.getAgencia());
            assertEquals(1010, cp.getSaldo(), 0.01);
        }
    }

    @Nested
    @DisplayName("Listagem e cópia defensiva")
    class ListagemTest {

        @Test
        @DisplayName("listarContas retorna cópia (modificação externa não afeta)")
        void copiaDefensiva() {
            service.criarContaCorrente(500);
            List<Conta> lista = service.listarContas();
            lista.clear(); // modifica a cópia
            assertEquals(1, service.listarContas().size()); // original intacto
        }

        @Test
        @DisplayName("listarContas vazia se nenhuma conta")
        void listaVazia() {
            assertTrue(service.listarContas().isEmpty());
        }
    }

    @Nested
    @DisplayName("Erros conta não encontrada")
    class ErroTest {

        @Test
        @DisplayName("depositar conta inexistente lança IllegalArgumentException")
        void depositarInexistente() {
            assertThrows(IllegalArgumentException.class,
                () -> service.depositar(9999, 1, 100));
        }

        @Test
        @DisplayName("sacar conta inexistente lança IllegalArgumentException")
        void sacarInexistente() {
            assertThrows(IllegalArgumentException.class,
                () -> service.sacar(9999, 1, 100));
        }

        @Test
        @DisplayName("extrato conta inexistente lança IllegalArgumentException")
        void extratoInexistente() {
            assertThrows(IllegalArgumentException.class,
                () -> service.extrato(9999, 1));
        }
    }
}