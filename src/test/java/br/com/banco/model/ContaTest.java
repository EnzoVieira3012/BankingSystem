package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    private ContaCorrente cc;
    private ContaPoupanca cp;

    @BeforeEach
    void setUp() throws ValorInvalidoException {
        cc = new ContaCorrente(1234, 1, 500.0);
        cp = new ContaPoupanca(5678, 1, 0.01);
    }

    @Nested
    @DisplayName("ContaCorrente - cheque especial")
    class ContaCorrenteTest {

        @Test
        @DisplayName("Depositar valor positivo aumenta saldo")
        void depositarPositivo() {
            cc.depositar(100);
            assertEquals(100, cc.getSaldo());
        }

        @Test
        @DisplayName("Depositar valor negativo lança IllegalArgumentException")
        void depositarNegativo() {
            assertThrows(IllegalArgumentException.class, () -> cc.depositar(-10));
        }

        @Test
        @DisplayName("Sacar dentro do saldo + limite funciona")
        void sacarComLimite() {
            cc.depositar(100);
            assertDoesNotThrow(() -> cc.sacar(550)); // 100 saldo + 500 limite
            assertEquals(-450, cc.getSaldo());
        }

        @Test
        @DisplayName("Sacar além do saldo + limite lança SaldoInsuficienteException")
        void sacarSemLimite() {
            cc.depositar(100);
            assertThrows(SaldoInsuficienteException.class, () -> cc.sacar(601));
            assertEquals(100, cc.getSaldo()); // saldo inalterado
        }

        @Test
        @DisplayName("getLimite retorna valor correto")
        void getLimite() {
            assertEquals(500, cc.getLimite());
        }

        @Test
        @DisplayName("toString contém numero, agencia, saldo")
        void toStringContemCampos() {
            String s = cc.toString();
            assertTrue(s.contains("1234"));
            assertTrue(s.contains("1"));
            assertTrue(s.contains("0.00"));
        }
    }

    @Nested
    @DisplayName("ContaPoupanca - sem cheque especial")
    class ContaPoupancaTest {

        @Test
        @DisplayName("Depositar e sacar funciona")
        void depositarSacar() {
            cp.depositar(200);
            assertDoesNotThrow(() -> cp.sacar(100));
            assertEquals(100, cp.getSaldo());
        }

        @Test
        @DisplayName("Sacar além do saldo lança SaldoInsuficienteException")
        void sacarInsuficiente() {
            cp.depositar(50);
            assertThrows(SaldoInsuficienteException.class, () -> cp.sacar(100));
            assertEquals(50, cp.getSaldo());
        }

        @Test
        @DisplayName("renderJuros aplica taxa ao saldo")
        void renderJuros() {
            cp.depositar(1000);
            cp.renderJuros();
            assertEquals(1010, cp.getSaldo(), 0.01); // 1% de 1000 = 10
        }

        @Test
        @DisplayName("getTaxaRendimento retorna valor correto")
        void getTaxaRendimento() {
            assertEquals(0.01, cp.getTaxaRendimento());
        }

        @Test
        @DisplayName("toString contém numero, agencia, saldo")
        void toStringContemCampos() {
            String s = cp.toString();
            assertTrue(s.contains("5678"));
            assertTrue(s.contains("1"));
            assertTrue(s.contains("0.00"));
        }
    }

    @Nested
    @DisplayName("Conta base - abstract")
    class ContaBaseTest {

        @Test
        @DisplayName("Construtor inicializa saldo=0")
        void construtorSaldoZero() {
            ContaCorrente c = new ContaCorrente(9999, 1, 0);
            assertEquals(0, c.getSaldo());
        }

        @Test
        @DisplayName("Numero e agencia são imutáveis (final)")
        void camposFinais() {
            assertEquals(1234, cc.getNumero());
            assertEquals(1, cc.getAgencia());
        }
    }
}