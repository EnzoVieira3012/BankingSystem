package br.com.banco.integration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.sql.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ContaIntegrationTest extends AbstractIntegrationTest {

    private static Connection conn;

    @BeforeAll
    static void setup() throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl(), username(), password());
        conn.setAutoCommit(false);
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
    void deveInserirContaCorrente() throws SQLException {
        String sql = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1234);
            ps.setInt(2, 1);
            ps.setBigDecimal(3, new BigDecimal("100.00"));
            ps.setString(4, "CORRENTE");
            ps.setBigDecimal(5, new BigDecimal("500.00"));
            assertEquals(1, ps.executeUpdate());
        }
    }

    @Test
    @Order(2)
    void deveBuscarContaPorNumero() throws SQLException {
        // Insert first
        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (1234, 1, 100.00, 'CORRENTE', 500.00)";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(insert);
        }

        String select = "SELECT * FROM conta WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(select)) {
            ps.setInt(1, 1234);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(1234, rs.getInt("numero"));
                assertEquals(1, rs.getInt("agencia"));
                assertEquals(new BigDecimal("100.00"), rs.getBigDecimal("saldo"));
                assertEquals("CORRENTE", rs.getString("tipo"));
                assertEquals(new BigDecimal("500.00"), rs.getBigDecimal("limite"));
            }
        }
    }

    @Test
    @Order(3)
    void deveAtualizarSaldo() throws SQLException {
        String insert = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (1234, 1, 100.00, 'CORRENTE', 500.00)";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(insert);
        }

        String update = "UPDATE conta SET saldo = ? WHERE numero = ?";
        try (PreparedStatement ps = conn.prepareStatement(update)) {
            ps.setBigDecimal(1, new BigDecimal("200.00"));
            ps.setInt(2, 1234);
            assertEquals(1, ps.executeUpdate());
        }

        String select = "SELECT saldo FROM conta WHERE numero = 1234";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(select)) {
            assertTrue(rs.next());
            assertEquals(new BigDecimal("200.00"), rs.getBigDecimal("saldo"));
        }
    }

    @Test
    @Order(4)
    void deveInserirContaPoupanca() throws SQLException {
        String sql = "INSERT INTO conta (numero, agencia, saldo, tipo, taxa_rendimento) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 5678);
            ps.setInt(2, 1);
            ps.setBigDecimal(3, new BigDecimal("1000.00"));
            ps.setString(4, "POUPANCA");
            ps.setBigDecimal(5, new BigDecimal("0.01"));
            assertEquals(1, ps.executeUpdate());
        }
    }

    @Test
    @Order(5)
    void deveListarTodasContas() throws SQLException {
        String insert1 = "INSERT INTO conta (numero, agencia, saldo, tipo, limite) VALUES (1234, 1, 100.00, 'CORRENTE', 500.00)";
        String insert2 = "INSERT INTO conta (numero, agencia, saldo, tipo, taxa_rendimento) VALUES (5678, 1, 1000.00, 'POUPANCA', 0.01)";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(insert1);
            st.executeUpdate(insert2);
        }

        String select = "SELECT COUNT(*) FROM conta";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(select)) {
            assertTrue(rs.next());
            assertEquals(2, rs.getInt(1));
        }
    }
}