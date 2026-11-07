package br.com.banco.model;

/**
 * Enum type-safe para identificar tipo de conta bancária.
 * Substitui verificações instanceof e provê descrições legíveis.
 */
public enum TipoConta {
    CORRENTE("Conta Corrente"),
    POUPANCA("Conta Poupança");

    private final String descricao;

    TipoConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}