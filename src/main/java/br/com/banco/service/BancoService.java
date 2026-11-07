package br.com.banco.service;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.exception.ContaNaoEncontradaException;
import br.com.banco.exception.ValorInvalidoException;
import br.com.banco.exception.ExceptionChainHandler;
import br.com.banco.util.ContaGenerator;
import java.util.ArrayList;
import java.util.List;

public class BancoService {

    private final List<Conta> contas = new ArrayList<>();

    // ===== Design by Contract - Invariant Checks =====
    private void checkInvariants() {
        // Invariant: lista não pode ser null
        assert contas != null : "Invariante violada: lista de contas não pode ser null";

        // Invariant: não pode haver contas duplicadas (mesmo numero + agencia)
        for (int i = 0; i < contas.size(); i++) {
            for (int j = i + 1; j < contas.size(); j++) {
                Conta c1 = contas.get(i);
                Conta c2 = contas.get(j);
                assert !(c1.getNumero() == c2.getNumero() && c1.getAgencia() == c2.getAgencia())
                    : "Invariante violada: contas duplicadas detectadas";
            }
        }

        // Invariant: saldos não negativos (exceto corrente com limite)
        for (Conta c : contas) {
            if (c instanceof ContaCorrente) {
                ContaCorrente cc = (ContaCorrente) c;
                assert cc.getSaldo() + cc.getLimite() >= 0
                    : "Invariante violada: saldo + limite negativo em conta corrente";
            } else {
                assert c.getSaldo() >= 0 : "Invariante violada: saldo negativo em conta poupança";
            }
        }
    }

    // Factory methods
    public ContaCorrente criarContaCorrente(String titular, double limite) {
        // Precondition: limite >= 0
        assert limite >= 0 : "Pré-condição violada: limite deve ser >= 0";
        assert titular != null && !titular.trim().isEmpty() : "Pré-condição violada: titular não pode ser vazio";

        int numero = ContaGenerator.gerarNumeroConta();
        int agencia = ContaGenerator.getAgenciaPadrao();
        ContaCorrente cc = new ContaCorrente(numero, agencia, titular, limite);
        contas.add(cc);

        // Postcondition: conta foi adicionada
        assert contas.contains(cc) : "Pós-condição violada: conta não adicionada à lista";
        checkInvariants();
        return cc;
    }

    public ContaPoupanca criarContaPoupanca(String titular, double taxaRendimento) {
        // Precondition: taxa > 0 e <= 1 (100%)
        assert taxaRendimento > 0 && taxaRendimento <= 1 : "Pré-condição violada: taxa deve ser entre 0 e 1";
        assert titular != null && !titular.trim().isEmpty() : "Pré-condição violada: titular não pode ser vazio";

        int numero = ContaGenerator.gerarNumeroConta();
        int agencia = ContaGenerator.getAgenciaPadrao();
        ContaPoupanca cp = new ContaPoupanca(numero, agencia, titular, taxaRendimento);
        contas.add(cp);

        // Postcondition
        assert contas.contains(cp) : "Pós-condição violada: conta não adicionada à lista";
        checkInvariants();
        return cp;
    }

    // Mantidos para compatibilidade com testes existentes
    public ContaCorrente criarContaCorrente(double limite) {
        return criarContaCorrente("Titular " + ContaGenerator.gerarNumeroConta(), limite);
    }

    public ContaPoupanca criarContaPoupanca(double taxaRendimento) {
        return criarContaPoupanca("Titular " + ContaGenerator.gerarNumeroConta(), taxaRendimento);
    }

    // Busca polimórfica
    public Conta buscarConta(int numero, int agencia) {
        checkInvariants();
        for (Conta c : contas) {
            if (c.getNumero() == numero && c.getAgencia() == agencia) {
                return c;
            }
        }
        return null;
    }

    // Operações delegando para conta (polimorfismo)
    public void depositar(int numero, int agencia, double valor) throws ContaNaoEncontradaException, ValorInvalidoException {
        checkInvariants();

        // Precondition
        assert valor > 0 : "Pré-condição violada: valor do depósito deve ser > 0";

        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new ContaNaoEncontradaException(numero, agencia);
        }

        double saldoAntes = c.getSaldo();
        c.depositar(valor);

        // Postcondition
        assert c.getSaldo() == saldoAntes + valor : "Pós-condição violada: saldo não atualizado corretamente no depósito";
        checkInvariants();
    }

    public void sacar(int numero, int agencia, double valor) throws ContaNaoEncontradaException, ValorInvalidoException, SaldoInsuficienteException {
        checkInvariants();

        // Precondition
        assert valor > 0 : "Pré-condição violada: valor do saque deve ser > 0";

        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new ContaNaoEncontradaException(numero, agencia);
        }

        double saldoAntes = c.getSaldo();
        c.sacar(valor);

        // Postcondition
        assert c.getSaldo() == saldoAntes - valor : "Pós-condição violada: saldo não atualizado corretamente no saque";
        checkInvariants();
    }

    public void renderJurosPoupanca(int numero, int agencia) throws ContaNaoEncontradaException {
        checkInvariants();

        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new ContaNaoEncontradaException(numero, agencia);
        }

        // Precondition: deve ser conta poupança
        assert c instanceof ContaPoupanca : "Pré-condição violada: renderJuros só válido para ContaPoupanca";

        double saldoAntes = c.getSaldo();
        c.renderJuros();

        // Postcondition: saldo aumentou
        assert c.getSaldo() > saldoAntes : "Pós-condição violada: juros não renderam";
        checkInvariants();
    }

    // Listagem (cópia defensiva)
    public List<Conta> listarContas() {
        checkInvariants();
        List<Conta> copia = new ArrayList<>(contas);

        // Postcondition: cópia independente
        assert copia != contas : "Pós-condição violada: retornou mesma referência da lista";
        assert copia.size() == contas.size() : "Pós-condição violada: tamanho da cópia difere do original";
        return copia;
    }

    // Extrato
    public void extrato(int numero, int agencia) throws ContaNaoEncontradaException {
        checkInvariants();

        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new ContaNaoEncontradaException(numero, agencia);
        }

        System.out.println("=== EXTRATO ===");
        System.out.println(c);
    }
}