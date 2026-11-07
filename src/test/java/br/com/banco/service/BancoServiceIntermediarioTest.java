package br.com.banco.service;

import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.ContaNaoEncontradaException;
import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.exception.ValorInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BancoServiceIntermediarioTest {

    private BancoService service;

    @BeforeEach
    void setUp() {
        service = new BancoService();
    }

    @Nested
    @DisplayName("Validação de entrada - ValorInvalidoException")
    class ValidacaoTest {

        @Test
        @DisplayName("criarContaCorrente limite negativo lança ValorInvalidoException")
        void limiteNegativo() {
            assertThrows(ValorInvalidoException.class,
                () -> service.criarContaCorrente(-100));
        }

        @Test
        @DisplayName("criarContaPoupanca taxa negativa lança ValorInvalidoException")
        void taxaNegativa() {
            assertThrows(ValorInvalidoException.class,
                () -> service.criarContaPoupanca(-0.01));
        }

        @Test
        @DisplayName("depositar valor <= 0 lança ValorInvalidoException")
        void depositarInvalido() {
            ContaCorrente cc = service.criarContaCorrente(500);
            assertThrows(ValorInvalidoException.class,
                () -> service.depositar(cc.getNumero(), cc.getAgencia(), 0));
            assertThrows(ValorInvalidoException.class,
                () -> service.depositar(cc.getNumero(), cc.getAgencia(), -10));
        }

        @Test
        @DisplayName("sacar valor <= 0 lança ValorInvalidoException")
        void sacarInvalido() {
            ContaCorrente cc = service.criarContaCorrente(500);
            assertThrows(ValorInvalidoException.class,
                () -> service.sacar(cc.getNumero(), cc.getAgencia(), 0));
        }
    }

    @Nested
    @DisplayName("Conta não encontrada - ContaNaoEncontradaException")
    class NaoEncontradaTest {

        @Test
        @DisplayName("buscarConta inexistente retorna null")
        void buscarInexistente() {
            assertNull(service.buscarConta(9999, 1));
        }

        @Test
        @DisplayName("depositar inexistente lança ContaNaoEncontradaException")
        void depositarInexistente() {
            assertThrows(ContaNaoEncontradaException.class,
                () -> service.depositar(9999, 1, 100));
        }

        @Test
        @DisplayName("sacar inexistente lança ContaNaoEncontradaException")
        void sacarInexistente() {
            assertThrows(ContaNaoEncontradaException.class,
                () -> service.sacar(9999, 1, 100));
        }

        @Test
        @DisplayName("Exception guarda numero e agencia")
        void exceptionCampos() {
            ContaNaoEncontradaException e = assertThrows(ContaNaoEncontradaException.class,
                () -> service.buscarConta(1234, 5));
            assertEquals(1234, e.getNumero());
            assertEquals(5, e.getAgencia());
        }
    }

    @Nested
    @DisplayName("Assert invariantes (rodar com -ea)")
    class AssertTest {

        @Test
        @DisplayName("depositar assert: saldo aumenta")
        void assertDepositar() {
            ContaCorrente cc = service.criarContaCorrente(500);
            service.depositar(cc.getNumero(), cc.getAgencia(), 100);
            assertEquals(100, cc.getSaldo());
        }

        @Test
        @DisplayName("sacar assert: saldo diminui")
        void assertSacar() throws SaldoInsuficienteException {
            ContaCorrente cc = service.criarContaCorrente(500);
            cc.depositar(200);
            service.sacar(cc.getNumero(), cc.getAgencia(), 150);
            assertEquals(50, cc.getSaldo());
        }

        @Test
        @DisplayName("renderJuros assert: saldo não diminui")
        void assertJuros() {
            ContaPoupanca cp = service.criarContaPoupanca(0.01);
            cp.depositar(1000);
            service.renderJurosPoupanca(cp.getNumero(), cp.getAgencia());
            assertTrue(cp.getSaldo() >= 1000);
        }
    }

    @Nested
    @DisplayName("Polimorfismo List<Conta>")
    class PolimorfismoTest {

        @Test
        @DisplayName("Lista mistura ContaCorrente e ContaPoupanca")
        void listaMista() {
            service.criarContaCorrente(500);
            service.criarContaPoupanca(0.01);
            service.criarContaCorrente(1000);

            var lista = service.listarContas();
            assertEquals(3, lista.size());

            long ccCount = lista.stream().filter(c -> c instanceof ContaCorrente).count();
            long cpCount = lista.stream().filter(c -> c instanceof ContaPoupanca).count();
            assertEquals(2, ccCount);
            assertEquals(1, cpCount);
        }

        @Test
        @DisplayName("Cópia defensiva: clear na lista não afeta service")
        void copiaDefensiva() {
            service.criarContaCorrente(500);
            var lista = service.listarContas();
            lista.clear();
            assertEquals(1, service.listarContas().size());
        }
    }
}