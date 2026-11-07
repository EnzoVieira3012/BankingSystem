package br.com.banco.exception;

/**
 * Exceção checked lançada quando um valor numérico é inválido (negativo, zero quando deveria ser positivo, etc).
 * Usada em validações de depósito, saque, limite, taxa de rendimento.
 * Guarda operação e valor para debug.
 */
public class ValorInvalidoException extends Exception {

    private final double valor;
    private final String operacao;

    public ValorInvalidoException(String operacao, double valor) {
        super(String.format("Valor inválido para %s: %.2f (deve ser > 0)", operacao, valor));
        this.operacao = operacao;
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public String getOperacao() {
        return operacao;
    }
}