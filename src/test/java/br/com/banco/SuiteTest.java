package br.com.banco;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    ContaTest.class,
    BancoServiceTest.class,
    BancoServiceIntermediarioTest.class,
    ExceptionChainHandlerTest.class,
    InputUtilSeguroTest.class,
    ExtratoWriterTest.class,
    BackupContasTest.class
})
public class SuiteTest {
    // Suite vazia - apenas agrega classes
}