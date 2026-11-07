package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;

public class ContaCorrente extends Conta {
    private double limite;

    public ContaCorrente(int numero, int agencia, double limite) {
        super(numero, agencia);
        this.limite = limite;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new SaldoInsuficienteException("Valor de saque deve ser positivo");
        }
        double disponivel = this.saldo + this.limite;
        if (valor > disponivel) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente. Disponível: %.2f (saldo: %.2f + limite: %.2f)",
                    disponivel, this.saldo, this.limite)
            );
        }
        this.saldo -= valor;
    }

    public double getLimite() {
        return limite;
    }

    @Override
    public String toString() {
        return String.format("ContaCorrente{numero=%d, agencia=%d, saldo=%.2f, limite=%.2f}",
            numero, agencia, saldo, limite);
    }
}