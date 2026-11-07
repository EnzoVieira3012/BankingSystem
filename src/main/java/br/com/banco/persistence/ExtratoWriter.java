package br.com.banco.persistence;

import br.com.banco.model.Conta;
import br.com.banco.exception.ExceptionChainHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Escritor de extratos bancários em arquivo.
 * Usa FileWriter + BufferedWriter com try-with-resources (Fase 2).
 * Gera arquivos de extrato formatados por conta.
 */
public final class ExtratoWriter {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private ExtratoWriter() {
        // Utility class
    }

    /**
     * Grava extrato da conta em arquivo.
     * @param conta conta para extrato
     * @param arquivo caminho do arquivo (ex: "extrato_1234.txt")
     * @param append true para append, false para sobrescrever
     * @throws IOException se erro de I/O
     */
    public static void gravarExtrato(Conta conta, String arquivo, boolean append) throws IOException {
        try (FileWriter fw = new FileWriter(arquivo, append);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("=== EXTRATO BANCÁRIO ===");
            bw.newLine();
            bw.write("Data/Hora: " + LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            bw.newLine();
            bw.write("Conta: " + conta.getNumero() + "-" + conta.getAgencia());
            bw.newLine();
            bw.write("Titular: " + conta.getTitular());
            bw.newLine();
            bw.write("Saldo: " + String.format("%.2f", conta.getSaldo()));
            bw.newLine();
            bw.write("Tipo: " + conta.getTipo().getDescricao());
            bw.newLine();

            if (conta instanceof br.com.banco.model.ContaCorrente) {
                br.com.banco.model.ContaCorrente cc = (br.com.banco.model.ContaCorrente) conta;
                bw.write("Limite: " + String.format("%.2f", cc.getLimite()));
                bw.newLine();
                bw.write("Disponível: " + String.format("%.2f", conta.getSaldo() + cc.getLimite()));
                bw.newLine();
            } else if (conta instanceof br.com.banco.model.ContaPoupanca) {
                br.com.banco.model.ContaPoupanca cp = (br.com.banco.model.ContaPoupanca) conta;
                bw.write("Taxa Rendimento: " + (cp.getTaxaRendimento() * 100) + "%");
                bw.newLine();
            }

            bw.write("========================");
            bw.newLine();
            bw.newLine();
        } catch (IOException e) {
            ExceptionChainHandler.log(e);
            throw e;
        }
    }

    /**
     * Grava extrato simples (sobrescreve).
     */
    public static void gravarExtrato(Conta conta, String arquivo) throws IOException {
        gravarExtrato(conta, arquivo, false);
    }

    /**
     * Grava extrato consolidado de todas as contas.
     */
    public static void gravarExtratoConsolidado(java.util.List<Conta> contas, String arquivo) throws IOException {
        try (FileWriter fw = new FileWriter(arquivo);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write("=== EXTRATO CONSOLIDADO ===");
            bw.newLine();
            bw.write("Data/Hora: " + LocalDateTime.now().format(TIMESTAMP_FORMATTER));
            bw.newLine();
            bw.write("Total de contas: " + contas.size());
            bw.newLine();
            bw.newLine();

            double totalSaldo = 0;
            for (Conta conta : contas) {
                bw.write(String.format("%d-%d | %-20s | %-15s | R$ %10.2f",
                        conta.getNumero(), conta.getAgencia(),
                        conta.getTitular(),
                        conta.getTipo().getDescricao(),
                        conta.getSaldo()));
                bw.newLine();
                totalSaldo += conta.getSaldo();
            }

            bw.newLine();
            bw.write("------------------------");
            bw.newLine();
            bw.write("SALDO TOTAL: R$ " + String.format("%.2f", totalSaldo));
            bw.newLine();
            bw.write("========================");
            bw.newLine();
        } catch (IOException e) {
            ExceptionChainHandler.log(e);
            throw e;
        }
    }
}