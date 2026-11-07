package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;

public abstract class Conta {

    protected double saldo;
    private final int numero;
    private final int agencia;

    public Conta(int numero, int agencia) {
        this.numero = numero;
        this.agencia = agencia;
        this.saldo = 0.0;
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

    @Override
    public String toString() {
        return String.format("Conta{numero=%d, agencia=%d, saldo=%.2f}", numero, agencia, saldo);
    }
}