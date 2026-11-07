package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;

public class ContaPoupanca extends Conta {
    private double taxaRendimento;

    public ContaPoupanca(int numero, int agencia, double taxaRendimento) {
        super(numero, agencia);
        this.taxaRendimento = taxaRendimento;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new SaldoInsuficienteException("Valor de saque deve ser positivo");
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

    @Override
    public String toString() {
        return String.format("ContaPoupanca{numero=%d, agencia=%d, saldo=%.2f, taxaRendimento=%.4f}",
            numero, agencia, saldo, taxaRendimento);
    }
}