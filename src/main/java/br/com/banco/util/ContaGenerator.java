package br.com.banco.util;

import java.util.Random;

public class ContaGenerator {
    private static final Random random = new Random();
    private static final int AGENCIA_PADRAO = 1;

    public static int gerarNumeroConta() {
        return 1000 + random.nextInt(9000); // 1000-9999
    }

    public static int getAgenciaPadrao() {
        return AGENCIA_PADRAO;
    }
}