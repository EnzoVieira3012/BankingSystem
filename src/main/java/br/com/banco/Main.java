package br.com.banco;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.service.BancoService;
import br.com.banco.util.InputUtil;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final BancoService service = new BancoService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcao;
        do {
            exibirMenu();
            opcao = InputUtil.lerInteiro(scanner, "Escolha uma opção: ");
            processarOpcao(opcao);
        } while (opcao != 0);
        System.out.println("Sistema encerrado. Até logo!");
    }

    private static void exibirMenu() {
        System.out.println("\n=== SISTEMA BANCÁRIO CONSOLE ===");
        System.out.println("1  - Criar Conta Corrente");
        System.out.println("2  - Criar Conta Poupança");
        System.out.println("3  - Depositar");
        System.out.println("4  - Sacar");
        System.out.println("5  - Listar Contas");
        System.out.println("6  - Extrato");
        System.out.println("7  - Render Juros (Poupança)");
        System.out.println("0  - Sair");
    }

    private static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> fluxoCriarContaCorrente();
            case 2 -> fluxoCriarContaPoupanca();
            case 3 -> fluxoDepositar();
            case 4 -> fluxoSacar();
            case 5 -> fluxoListar();
            case 6 -> fluxoExtrato();
            case 7 -> fluxoRenderJuros();
            case 0 -> System.out.println("Encerrando...");
            default -> System.out.println("Opção inválida. Tente novamente.");
        }
    }

    private static void fluxoCriarContaCorrente() {
        System.out.println("\n--- CRIAR CONTA CORRENTE ---");
        double limite = InputUtil.lerDouble(scanner, "Limite do cheque especial: ");
        ContaCorrente cc = service.criarContaCorrente(limite);
        System.out.println("Conta criada com sucesso!");
        System.out.println(cc);
    }

    private static void fluxoCriarContaPoupanca() {
        System.out.println("\n--- CRIAR CONTA POUPANÇA ---");
        double taxa = InputUtil.lerDouble(scanner, "Taxa de rendimento (ex: 0.01 para 1%): ");
        ContaPoupanca cp = service.criarContaPoupanca(taxa);
        System.out.println("Conta criada com sucesso!");
        System.out.println(cp);
    }

    private static void fluxoDepositar() {
        System.out.println("\n--- DEPOSITAR ---");
        int numero = InputUtil.lerInteiro(scanner, "Número da conta: ");
        int agencia = InputUtil.lerInteiro(scanner, "Agência: ");
        double valor = InputUtil.lerDouble(scanner, "Valor do depósito: ");
        try {
            service.depositar(numero, agencia, valor);
            System.out.println("Depósito realizado com sucesso!");
            Conta c = service.buscarConta(numero, agencia);
            if (c != null) System.out.println("Novo saldo: " + c.getSaldo());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoSacar() {
        System.out.println("\n--- SACAR ---");
        int numero = InputUtil.lerInteiro(scanner, "Número da conta: ");
        int agencia = InputUtil.lerInteiro(scanner, "Agência: ");
        double valor = InputUtil.lerDouble(scanner, "Valor do saque: ");
        try {
            service.sacar(numero, agencia, valor);
            System.out.println("Saque realizado com sucesso!");
            Conta c = service.buscarConta(numero, agencia);
            if (c != null) System.out.println("Novo saldo: " + c.getSaldo());
        } catch (IllegalArgumentException | SaldoInsuficienteException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoListar() {
        System.out.println("\n--- LISTAR CONTAS ---");
        List<Conta> lista = service.listarContas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
        } else {
            for (Conta c : lista) {
                System.out.println(c);
            }
        }
    }

    private static void fluxoExtrato() {
        System.out.println("\n--- EXTRATO ---");
        int numero = InputUtil.lerInteiro(scanner, "Número da conta: ");
        int agencia = InputUtil.lerInteiro(scanner, "Agência: ");
        try {
            service.extrato(numero, agencia);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoRenderJuros() {
        System.out.println("[TODO] Implementar fluxo render juros");
    }
}