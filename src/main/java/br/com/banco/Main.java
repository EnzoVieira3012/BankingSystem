package br.com.banco;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.exception.SaldoInsuficienteException;
import br.com.banco.exception.ContaNaoEncontradaException;
import br.com.banco.exception.ValorInvalidoException;
import br.com.banco.service.BancoService;
import br.com.banco.persistence.ExtratoWriter;
import br.com.banco.persistence.BackupContas;
import br.com.banco.util.InputUtilSeguro;
import java.util.List;

public class Main {

    private static final BancoService service = new BancoService();
    private static final ExtratoWriter extratoWriter = new ExtratoWriter();
    private static final BackupContas backupContas = new BackupContas();

    public static void main(String[] args) {
        // Restaurar backup ao iniciar
        if (backupContas.backupExiste()) {
            backupContas.restaurar(service);
            System.out.println("Backup restaurado: " + service.listarContas().size() + " contas carregadas.");
        }

        int opcao;
        do {
            exibirMenu();
            opcao = InputUtilSeguro.lerInteiroSeguro("Escolha uma opção: ");
            processarOpcao(opcao);
        } while (opcao != 0);

        // Salvar backup ao sair
        backupContas.salvar(service);
        System.out.println("Sistema encerrado. Backup salvo. Até logo!");
    }

    private static void exibirMenu() {
        System.out.println("\n=== SISTEMA BANCÁRIO CONSOLE ===");
        System.out.println("1  - Criar Conta Corrente");
        System.out.println("2  - Criar Conta Poupança");
        System.out.println("3  - Depositar");
        System.out.println("4  - Sacar");
        System.out.println("5  - Listar Contas");
        System.out.println("6  - Extrato (console)");
        System.out.println("7  - Gerar Extrato (arquivo)");
        System.out.println("8  - Render Juros (Poupança)");
        System.out.println("9  - Backup Manual");
        System.out.println("0  - Sair");
    }

    private static void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> fluxoCriarContaCorrente();
            case 2 -> fluxoCriarContaPoupanca();
            case 3 -> fluxoDepositar();
            case 4 -> fluxoSacar();
            case 5 -> fluxoListar();
            case 6 -> fluxoExtratoConsole();
            case 7 -> fluxoGerarExtratoArquivo();
            case 8 -> fluxoRenderJuros();
            case 9 -> fluxoBackupManual();
            case 0 -> System.out.println("Encerrando...");
            default -> System.out.println("Opção inválida. Tente novamente.");
        }
    }

    private static void fluxoCriarContaCorrente() {
        System.out.println("\n--- CRIAR CONTA CORRENTE ---");
        String titular = InputUtilSeguro.lerStringSegura("Nome do titular: ");
        double limite = InputUtilSeguro.lerDoubleSeguro("Limite do cheque especial: ");
        try {
            ContaCorrente cc = service.criarContaCorrente(titular, limite);
            System.out.println("Conta criada com sucesso!");
            System.out.println(cc);
        } catch (ValorInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoCriarContaPoupanca() {
        System.out.println("\n--- CRIAR CONTA POUPANÇA ---");
        String titular = InputUtilSeguro.lerStringSegura("Nome do titular: ");
        double taxa = InputUtilSeguro.lerDoubleSeguro("Taxa de rendimento (ex: 0.01 para 1%): ");
        try {
            ContaPoupanca cp = service.criarContaPoupanca(titular, taxa);
            System.out.println("Conta criada com sucesso!");
            System.out.println(cp);
        } catch (ValorInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoDepositar() {
        System.out.println("\n--- DEPOSITAR ---");
        int numero = InputUtilSeguro.lerInteiroSeguro("Número da conta: ");
        int agencia = InputUtilSeguro.lerInteiroSeguro("Agência: ");
        double valor = InputUtilSeguro.lerDoubleSeguro("Valor do depósito: ");
        try {
            service.depositar(numero, agencia, valor);
            System.out.println("Depósito realizado com sucesso!");
            Conta c = service.buscarConta(numero, agencia);
            if (c != null) System.out.println("Novo saldo: " + c.getSaldo());
        } catch (ContaNaoEncontradaException | ValorInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoSacar() {
        System.out.println("\n--- SACAR ---");
        int numero = InputUtilSeguro.lerInteiroSeguro("Número da conta: ");
        int agencia = InputUtilSeguro.lerInteiroSeguro("Agência: ");
        double valor = InputUtilSeguro.lerDoubleSeguro("Valor do saque: ");
        try {
            service.sacar(numero, agencia, valor);
            System.out.println("Saque realizado com sucesso!");
            Conta c = service.buscarConta(numero, agencia);
            if (c != null) System.out.println("Novo saldo: " + c.getSaldo());
        } catch (ContaNaoEncontradaException | ValorInvalidoException | SaldoInsuficienteException e) {
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

    private static void fluxoExtratoConsole() {
        System.out.println("\n--- EXTRATO (CONSOLE) ---");
        int numero = InputUtilSeguro.lerInteiroSeguro("Número da conta: ");
        int agencia = InputUtilSeguro.lerInteiroSeguro("Agência: ");
        try {
            service.extrato(numero, agencia);
        } catch (ContaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoGerarExtratoArquivo() {
        System.out.println("\n--- GERAR EXTRATO (ARQUIVO) ---");
        int numero = InputUtilSeguro.lerInteiroSeguro("Número da conta: ");
        int agencia = InputUtilSeguro.lerInteiroSeguro("Agência: ");
        String arquivo = InputUtilSeguro.lerStringSegura("Nome do arquivo (ex: extrato.txt): ");
        try {
            Conta c = service.buscarConta(numero, agencia);
            if (c == null) {
                throw new ContaNaoEncontradaException(numero, agencia);
            }
            extratoWriter.gravarExtrato(c, arquivo);
            System.out.println("Extrato gravado em: " + arquivo);
        } catch (ContaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoRenderJuros() {
        System.out.println("\n--- RENDER JUROS (POUPANÇA) ---");
        int numero = InputUtilSeguro.lerInteiroSeguro("Número da conta: ");
        int agencia = InputUtilSeguro.lerInteiroSeguro("Agência: ");
        try {
            service.renderJurosPoupanca(numero, agencia);
            System.out.println("Juros rendidos com sucesso!");
            Conta c = service.buscarConta(numero, agencia);
            if (c != null) System.out.println("Novo saldo: " + c.getSaldo());
        } catch (ContaNaoEncontradaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void fluxoBackupManual() {
        System.out.println("\n--- BACKUP MANUAL ---");
        backupContas.salvar(service);
        System.out.println("Backup realizado com sucesso!");
    }
}