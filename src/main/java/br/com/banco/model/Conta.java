package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Conta {

    protected double saldo;
    private final int numero;
    private final int agencia;
    private final LocalDate dataCriacao;
    private final LocalDateTime timestampCriacao;

    // Static: contador total de contas criadas
    private static int contadorTotalContas = 0;

    // Bloco static de inicialização (executado uma vez ao carregar classe)
    static {
        contadorTotalContas = 0;
        System.out.println("[Conta] Classe carregada - contador zerado");
    }

    public Conta(int numero, int agencia) {
        this.numero = numero;
        this.agencia = agencia;
        this.saldo = 0.0;
        this.dataCriacao = LocalDate.now();
        this.timestampCriacao = LocalDateTime.now();
        contadorTotalContas++; // Incrementa a cada nova conta
    }

    // Getter static
    public static int getContadorTotalContas() {
        return contadorTotalContas;
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }
        this.saldo += valor;
    }

    public abstract void sacar(double valor) throws SaldoInsuficienteException;

    public void renderJuros() {
        // Implementado apenas em ContaPoupanca
    }

    // Getters
    public double getSaldo() {
        return saldo;
    }

    public int getNumero() {
        return numero;
    }

    public int getAgencia() {
        return agencia;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getTimestampCriacao() {
        return timestampCriacao;
    }

    public String getDataCriacaoFormatada() {
        return dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getTimestampCriacaoFormatado() {
        return timestampCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("Conta{numero=%d, agencia=%d, saldo=%.2f, criada=%s}",
                numero, agencia, saldo, getDataCriacaoFormatada());
    }
}