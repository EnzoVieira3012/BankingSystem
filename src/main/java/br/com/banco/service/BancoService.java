package br.com.banco.service;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.util.ContaGenerator;
import java.util.ArrayList;
import java.util.List;

public class BancoService {

    private final List<Conta> contas = new ArrayList<>();

    // Factory methods
    public ContaCorrente criarContaCorrente(double limite) {
        int numero = ContaGenerator.gerarNumeroConta();
        int agencia = ContaGenerator.getAgenciaPadrao();
        ContaCorrente cc = new ContaCorrente(numero, agencia, limite);
        contas.add(cc);
        return cc;
    }

    public ContaPoupanca criarContaPoupanca(double taxaRendimento) {
        int numero = ContaGenerator.gerarNumeroConta();
        int agencia = ContaGenerator.getAgenciaPadrao();
        ContaPoupanca cp = new ContaPoupanca(numero, agencia, taxaRendimento);
        contas.add(cp);
        return cp;
    }

    // Busca polimórfica
    public Conta buscarConta(int numero, int agencia) {
        for (Conta c : contas) {
            if (c.getNumero() == numero && c.getAgencia() == agencia) {
                return c;
            }
        }
        return null;
    }

    // Operações delegando para conta (polimorfismo)
    public void depositar(int numero, int agencia, double valor) {
        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        c.depositar(valor);
    }

    public void sacar(int numero, int agencia, double valor) throws SaldoInsuficienteException {
        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        c.sacar(valor);
    }

    public void renderJurosPoupanca(int numero, int agencia) {
        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        c.renderJuros();
    }

    // Listagem (cópia defensiva)
    public List<Conta> listarContas() {
        return new ArrayList<>(contas);
    }

    // Extrato
    public void extrato(int numero, int agencia) {
        Conta c = buscarConta(numero, agencia);
        if (c == null) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        System.out.println("=== EXTRATO ===");
        System.out.println(c);
    }
}