package br.com.banco.exception;

/**
 * Exceção checked lançada quando uma conta não é encontrada no sistema.
 * Usada em operações de busca, depósito, saque e extrato.
 * Guarda número e agência para logs/debug.
 */
public class ContaNaoEncontradaException extends Exception {

    private final int numero;
    private final int agencia;

    public ContaNaoEncontradaException(int numero, int agencia) {
        super(String.format("Conta não encontrada: %d-%d", numero, agencia));
        this.numero = numero;
        this.agencia = agencia;
    }

    public ContaNaoEncontradaException(String message, int numero, int agencia) {
        super(message);
        this.numero = numero;
        this.agencia = agencia;
    }

    public int getNumero() {
        return numero;
    }

    public int getAgencia() {
        return agencia;
    }
}