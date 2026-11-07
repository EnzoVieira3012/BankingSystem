package br.com.banco.model;

import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.exception.ValorInvalidoException;

public class ContaCorrente extends Conta {
    private double limite;
    private final String titular;

    public ContaCorrente(int numero, int agencia, double limite) throws ValorInvalidoException {
        this(numero, agencia, "Titular " + numero, limite);
    }

    public ContaCorrente(int numero, int agencia, String titular, double limite) throws ValorInvalidoException {
        super(numero, agencia);
        if (limite < 0) {
            throw new ValorInvalidoException("limite", limite);
        }
        this.limite = limite;
        this.titular = titular;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new ValorInvalidoException("saque", valor);
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

    public String getTitular() {
        return titular;
    }

    @Override
    public TipoConta getTipo() {
        return TipoConta.CORRENTE;
    }

    @Override
    public String toString() {
        return String.format("ContaCorrente{numero=%d, agencia=%d, titular=%s, saldo=%.2f, limite=%.2f, criada=%s}",
            getNumero(), getAgencia(), titular, saldo, limite, getDataCriacaoFormatada());
    }
}