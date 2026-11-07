package br.com.banco.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionChainHandlerTest {

    @Nested
    @DisplayName("wrap() - preserva cause")
    class WrapTest {

        @Test
        @DisplayName("Wrap exception com cause preservada")
        void wrapPreservaCause() {
            Exception original = new IllegalArgumentException("original");
            Exception wrapped = ExceptionChainHandler.wrap(original, "wrapped",
                (msg, cause) -> new RuntimeException(msg, cause));

            assertEquals("wrapped", wrapped.getMessage());
            assertSame(original, wrapped.getCause());
        }

        @Test
        @DisplayName("Wrap com exception checked customizada")
        void wrapCheckedException() {
            Exception original = new ContaNaoEncontradaException(1234, 1);
            Exception wrapped = ExceptionChainHandler.wrap(original, "contexto",
                (msg, cause) -> new RuntimeException(msg, cause));

            assertTrue(wrapped.getMessage().contains("contexto"));
            assertSame(original, wrapped.getCause());
        }
    }

    @Nested
    @DisplayName("addSuppressed() - múltiplas exceptions")
    class SuppressedTest {

        @Test
        @DisplayName("Adiciona suppressed exceptions")
        void addSuppressed() {
            Exception base = new Exception("base");
            Exception s1 = new Exception("suppressed1");
            Exception s2 = new Exception("suppressed2");

            ExceptionChainHandler.addSuppressed(base, s1, s2);

            assertEquals(2, base.getSuppressed().length);
            assertSame(s1, base.getSuppressed()[0]);
            assertSame(s2, base.getSuppressed()[1]);
        }

        @Test
        @DisplayName("Ignora null e base duplicada")
        void ignoraNullEDuplicada() {
            Exception base = new Exception("base");
            ExceptionChainHandler.addSuppressed(base, null, base, new Exception("ok"));

            assertEquals(1, base.getSuppressed().length);
        }
    }

    @Nested
    @DisplayName("getFullStackTrace() - stack trace completo")
    class StackTraceTest {

        @Test
        @DisplayName("Inclui cause chain")
        void incluiCauseChain() {
            Exception cause = new IllegalArgumentException("cause");
            Exception effect = new RuntimeException("effect", cause);

            String trace = ExceptionChainHandler.getFullStackTrace(effect);

            assertTrue(trace.contains("effect"));
            assertTrue(trace.contains("cause"));
            assertTrue(trace.contains("Caused by:"));
        }

        @Test
        @DisplayName("Inclui suppressed")
        void incluiSuppressed() {
            Exception base = new Exception("base");
            Exception suppressed = new Exception("suppressed");
            base.addSuppressed(suppressed);

            String trace = ExceptionChainHandler.getFullStackTrace(base);

            assertTrue(trace.contains("base"));
            assertTrue(trace.contains("suppressed"));
            assertTrue(trace.contains("Suppressed:"));
        }
    }

    @Nested
    @DisplayName("log() - não lança exception")
    class LogTest {

        @Test
        @DisplayName("Log executa sem erro")
        void logNaoLanca() {
            Exception e = new Exception("test");
            assertDoesNotThrow(() -> ExceptionChainHandler.log(e));
        }
    }
}