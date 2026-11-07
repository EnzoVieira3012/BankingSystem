package br.com.banco.persistence;

import br.com.banco.model.Conta;
import br.com.banco.model.ContaCorrente;
import br.com.banco.model.ContaPoupanca;
import br.com.banco.service.BancoService;
import br.com.banco.exception.ExceptionChainHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Backup e restauração simples de contas em arquivo texto.
 * Serialização simples: tipo;numero;agencia;saldo;limiteOuTaxa
 * Usa FileWriter/FileReader com try-with-resources (Fase 2).
 */
public final class BackupContas {

    private static final String DELIM = ";";
    private static final String FILE = "backup_contas.txt";

    private BackupContas() {
        // Utility class
    }

    /**
     * Salva todas as contas do service em arquivo.
     * Formato: tipo;numero;agencia;saldo;limiteOuTaxa
     */
    public static void salvar(BancoService service) throws IOException {
        try (FileWriter fw = new FileWriter(FILE)) {
            for (Conta c : service.listarContas()) {
                String linha;
                if (c instanceof ContaCorrente) {
                    ContaCorrente cc = (ContaCorrente) c;
                    linha = "CORRENTE" + DELIM + cc.getNumero() + DELIM + cc.getAgencia() + DELIM +
                            cc.getSaldo() + DELIM + cc.getLimite();
                } else if (c instanceof ContaPoupanca) {
                    ContaPoupanca cp = (ContaPoupanca) c;
                    linha = "POUPANCA" + DELIM + cp.getNumero() + DELIM + cp.getAgencia() + DELIM +
                            cp.getSaldo() + DELIM + cp.getTaxaRendimento();
                } else {
                    continue; // tipo desconhecido
                }
                fw.write(linha + "\n");
            }
        } catch (IOException e) {
            ExceptionChainHandler.log(e);
            throw e;
        }
    }

    /**
     * Restaura contas do arquivo para o service.
     * @return lista de contas restauradas
     */
    public static List<Conta> restaurar(BancoService service) throws IOException {
        List<Conta> restauradas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(DELIM);
                if (partes.length != 5) continue;

                String tipo = partes[0];
                int numero = Integer.parseInt(partes[1]);
                int agencia = Integer.parseInt(partes[2]);
                double saldo = Double.parseDouble(partes[3]);
                double extra = Double.parseDouble(partes[4]);

                Conta c;
                if ("CORRENTE".equals(tipo)) {
                    c = new ContaCorrente(numero, agencia, extra);
                } else if ("POUPANCA".equals(tipo)) {
                    c = new ContaPoupanca(numero, agencia, extra);
                } else {
                    continue;
                }
                // Deposita para restaurar saldo (construtor zera saldo)
                c.depositar(saldo);
                service.listarContas().add(c); // acesso direto para restore
                restauradas.add(c);
            }
        } catch (IOException e) {
            ExceptionChainHandler.log(e);
            throw e;
        }
        return restauradas;
    }

    /**
     * Verifica se arquivo de backup existe.
     */
    public static boolean backupExiste() {
        java.io.File f = new java.io.File(FILE);
        return f.exists() && f.length() > 0;
    }
}