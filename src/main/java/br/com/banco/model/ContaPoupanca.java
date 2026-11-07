package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.exception.ValorInvalidoException;

public class ContaPoupanca extends Conta {
    private double taxaRendimento;
    private final String titular;

    public ContaPoupanca(int numero, int agencia, double taxaRendimento) throws ValorInvalidoException {
        this(numero, agencia, "Titular " + numero, taxaRendimento);
    }

    public ContaPoupanca(int numero, int agencia, String titular, double taxaRendimento) throws ValorInvalidoException {
        super(numero, agencia);
        if (taxaRendimento <= 0 || taxaRendimento > 1) {
            throw new ValorInvalidoException("taxa de rendimento", taxaRendimento);
        }
        this.taxaRendimento = taxaRendimento;
        this.titular = titular;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new ValorInvalidoException("saque", valor);
        }
        if (valor > this.saldo) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente. Saldo: %.2f", this.saldo)
            );
        }
        this.saldo -= valor;
    }

    @Override
    public void renderJuros() {
        this.saldo += this.saldo * this.taxaRendimento;
    }

    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    public String getTitular() {
        return titular;
    }

    @Override
    public TipoConta getTipo() {
        return TipoConta.POUPANCA;
    }

    @Override
    public String toString() {
        return String.format("ContaPoupanca{numero=%d, agencia=%d, titular=%s, saldo=%.2f, taxaRendimento=%.4f, criada=%s}",
            getNumero(), getAgencia(), titular, saldo, taxaRendimento, getDataCriacaoFormatada());
    }
}