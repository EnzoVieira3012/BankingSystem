package br.com.banco.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utilitário unificado para exception chaining (cause/suppressed).
 * Centraliza logging e rethrow com causa preservada.
 */
public final class ExceptionChainHandler {

    private ExceptionChainHandler() {
        // Utility class
    }

    /**
     * Wraps uma exception em outra preservando a causa original.
     * @param cause exception original
     * @param message mensagem contextual
     * @param <T> tipo da nova exception (deve ter construtor String, Throwable)
     * @return nova exception com cause setada
     */
    public static <T extends Exception> T wrap(Throwable cause, String message, ExceptionFactory<T> factory) {
        T wrapped = factory.create(message, cause);
        return wrapped;
    }

    /**
     * Adiciona exceptions suppressed a uma exception base.
     * Útil quando múltiplas falhas ocorrem (ex: backup parcial).
     */
    public static void addSuppressed(Exception base, Exception... suppressed) {
        for (Exception e : suppressed) {
            if (e != null && e != base) {
                base.addSuppressed(e);
            }
        }
    }

    /**
     * Extrai stack trace completo incluindo causes e suppressed.
     */
    public static String getFullStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        // Suppressed exceptions
        for (Throwable suppressed : t.getSuppressed()) {
            pw.append("\nSuppressed: ");
            suppressed.printStackTrace(pw);
        }
        // Cause chain
        Throwable cause = t.getCause();
        while (cause != null) {
            pw.append("\nCaused by: ");
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        return sw.toString();
    }

    /**
     * Loga exception completa no System.err.
     */
    public static void log(Throwable t) {
        System.err.println(getFullStackTrace(t));
    }

    @FunctionalInterface
    public interface ExceptionFactory<T extends Exception> {
        T create(String message, Throwable cause);
    }
}