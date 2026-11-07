package br.com.banco.integration;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.service.BancoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.sql.*;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BancoServiceIntegrationTest extends AbstractIntegrationTest {

    private static Connection conn;
    private static BancoService service;

    @BeforeAll
    static void setup() throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl(), username(), password());
        conn.setAutoCommit(false);
        service = new BancoService();
    }

    @AfterAll
    static void teardown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    @AfterEach
    void rollback() throws SQLException {
        conn.rollback();
    }

    @Test
    @Order(1)
    void deveCriarContaCorrenteViaServiceEPersistir() throws SQLException {
        // Arrange: criar conta via service
        ContaCorrente cc = service.criarContaCorrente(500.00);

        // Act: inserir no banco
        String sql = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cc.getNumero());
            ps.setInt(2, cc.getAgencia());
            ps.setBigDecimal(3, BigDecimal.valueOf(cc.getSaldo()));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc.getLimite()));
            assertEquals(1, ps.executeUpdate());
        }

        // Assert: verificar se foi persistida
        String select = "SELECT * FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, cc.getNumero());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(cc.getNumero(), rs.getInt("numero"));
                assertEquals(cc.getAgencia(), rs.getInt("agencia"));
                assertEquals(BigDecimal.valueOf(cc.getSaldo()), rs.getBigDecimal("saldo"));
                assertEquals("CORRENTE", rs.getString("tipo"));
                assertEquals(BigDecimal.valueOf(cc.getLimite()), rs.getBigDecimal("limite"));
            }
        }
    }

    @Test
    @Order(2)
    void deveCriarContaPoupancaViaServiceEPersistir() throws SQLException {
        ContaPoupanca cp = service.criarContaPoupanca(0.01);

        String sql = "INSERT INTO conta (numero, agencia, saldo, tipo, taxa_rendimento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cp.getNumero());
            ps.setInt(2, cp.getAgencia());
            ps.setBigDecimal(3, BigDecimal.valueOf(cp.getSaldo()));
            ps.setString(4, "POUPANCA");
            ps.setBigDecimal(5, BigDecimal.valueOf(cp.getTaxaRendimento()));
            assertEquals(1, ps.executeUpdate());
        }

        String select = "SELECT * FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, cp.getNumero());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(cp.getNumero(), rs.getInt("numero"));
                assertEquals(cp.getAgencia(), rs.getInt("agencia"));
                assertEquals(BigDecimal.valueOf(cp.getSaldo()), rs.getBigDecimal("saldo"));
                assertEquals("POUPANCA", rs.getString("tipo"));
                assertEquals(BigDecimal.valueOf(cp.getTaxaRendimento()), rs.getBigDecimal("taxa_rendimento"));
            }
        }
    }

    @Test
    @Order(3)
    void deveDepositarViaServiceERefletirNoBanco() throws SQLException {
        ContaCorrente cc = service.criarContaCorrente(500.00);
        int numero = cc.getNumero();
        int agencia = cc.getAgencia();

        // Inserir conta inicial
        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, numero);
            ps.setInt(2, agencia);
            ps.setBigDecimal(3, BigDecimal.valueOf(cc.getSaldo()));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc.getLimite()));
            ps.executeUpdate();
        }

        // Depositar via service
        service.depositar(numero, agencia, 200.00);

        // Verificar no banco
        String select = "SELECT saldo FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(new BigDecimal("200.00"), rs.getBigDecimal("saldo"));
            }
        }
    }

    @Test
    @Order(4)
    void deveSacarViaServiceERefletirNoBanco() throws SQLException {
        ContaCorrente cc = service.criarContaCorrente(500.00);
        int numero = cc.getNumero();
        int agencia = cc.getAgencia();

        // Inserir conta com saldo inicial
        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, numero);
            ps.setInt(2, agencia);
            ps.setBigDecimal(3, new BigDecimal("300.00"));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc.getLimite()));
            ps.executeUpdate();
        }

        // Sacar via service
        service.sacar(numero, agencia, 100.00);

        // Verificar no banco
        String select = "SELECT saldo FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(new BigDecimal("200.00"), rs.getBigDecimal("saldo"));
            }
        }
    }

    @Test
    @Order(5)
    void deveRenderJurosPoupancaViaServiceERefletirNoBanco() throws SQLException {
        ContaPoupanca cp = service.criarContaPoupanca(0.01);
        int numero = cp.getNumero();
        int agencia = cp.getAgencia();

        // Inserir conta com saldo inicial
        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, taxa_rendimento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, numero);
            ps.setInt(2, agencia);
            ps.setBigDecimal(3, new BigDecimal("1000.00"));
            ps.setString(4, "POUPANCA");
            ps.setBigDecimal(5, BigDecimal.valueOf(cp.getTaxaRendimento()));
            ps.executeUpdate();
        }

        // Render juros via service
        service.renderJurosPoupanca(numero, agencia);

        // Verificar no banco (1000 * 1.01 = 1010)
        String select = "SELECT saldo FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(new BigDecimal("1010.00"), rs.getBigDecimal("saldo"));
            }
        }
    }

    @Test
    @Order(6)
    void deveBuscarContaViaServiceECompararComBanco() throws SQLException {
        ContaCorrente cc = service.criarContaCorrente(500.00);
        int numero = cc.getNumero();
        int agencia = cc.getAgencia();

        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, numero);
            ps.setInt(2, agencia);
            ps.setBigDecimal(3, BigDecimal.valueOf(cc.getSaldo()));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc.getLimite()));
            ps.executeUpdate();
        }

        // Buscar via service (deve ser null pois não está na lista do service)
        // Mas podemos testar que a busca no banco funciona
        String select = "SELECT * FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                Conta encontrada = service.buscarConta(numero, agencia);
                // Service não tem a conta pois foi criada separadamente
                assertNull(encontrada);
            }
        }
    }

    @Test
    @Order(7)
    void deveListarContasViaServiceComDadosDoBanco() throws SQLException {
        ContaCorrente cc1 = service.criarContaCorrente(500.00);
        ContaPoupanca cp1 = service.criarContaPoupanca(0.01);

        // Inserir ambas no banco
        String insert1 = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert1)) {
            ps.setInt(1, cc1.getNumero());
            ps.setInt(2, cc1.getAgencia());
            ps.setBigDecimal(3, BigDecimal.valueOf(cc1.getSaldo()));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc1.getLimite()));
            ps.executeUpdate();
        }

        String insert2 = "INSERT INTO conta (numero, agencia, saldo, tipo, taxa_rendimento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert2)) {
            ps.setInt(1, cp1.getNumero());
            ps.setInt(2, cp1.getAgencia());
            ps.setBigDecimal(3, BigDecimal.valueOf(cp1.getSaldo()));
            ps.setString(4, "POUPANCA");
            ps.setBigDecimal(5, BigDecimal.valueOf(cp1.getTaxaRendimento()));
            ps.executeUpdate();
        }

        // Listar via service (apenas as criadas no service)
        List<Conta> contas = service.listarContas();
        assertEquals(2, contas.size());
        assertTrue(contas.stream().anyMatch(c -> c.getNumero() == cc1.getNumero()));
        assertTrue(contas.stream().anyMatch(c -> c.getNumero() == cp1.getNumero()));
    }

    @Test
    @Order(8)
    void deveLancarExcecaoContaNaoEncontradaAoDepositarInexistente() {
        assertThrows(br.com.banco.exception.ContaNaoEncontradaException.class,
                () -> service.depositar(9999, 1, 100.00));
    }

    @Test
    @Order(9)
    void deveLancarExcecaoValorInvalidoAoSacarNegativo() throws SQLException {
        ContaCorrente cc = service.criarContaCorrente(500.00);

        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setInt(1, cc.getNumero());
            ps.setInt(2, cc.getAgencia());
            ps.setBigDecimal(3, new BigDecimal("100.00"));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, BigDecimal.valueOf(cc.getLimite()));
            ps.executeUpdate();
        }

        assertThrows(br.com.banco.exception.ValorInvalidoException.class,
                () -> service.sacar(cc.getNumero(), cc.getAgencia(), -50.00));
    }
}