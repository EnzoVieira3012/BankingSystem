# Sistema Bancário Console

Projeto integrador Java consolando os 3 cursos do Pensar Cursos (41 aulas total):
- **Fase 1**: POO Básico (Códigos - 15 aulas)
- **Fase 2**: Intermediário (12 aulas)
- **Fase 3**: Avançado (14 aulas)

## Tecnologias

- Java 17+ LTS
- Maven 3.6+
- JUnit 5
- Testcontainers (PostgreSQL)
- GitHub Actions CI/CD
- JaCoCo Code Coverage
- Maven Surefire/Failsafe Reports

## Estrutura do Projeto

```
src/main/java/br/com/banco/
├── model/          # Conta (abstract), ContaCorrente, ContaPoupanca, TipoConta
├── service/        # BancoService - regras de negócio
├── util/           # InputUtil, InputUtilSeguro, ContaGenerator
├── exception/      # Exceções customizadas (SaldoInsuficienteException, ContaNaoEncontradaException, ValorInvalidoException, ExceptionChainHandler)
└── persistence/    # ExtratoWriter, BackupContas
```

```
src/test/java/br/com/banco/
├── *.java          # Testes unitários (63 testes)
├── integration/    # Testes de integração Testcontainers
│   ├── AbstractIntegrationTest.java
│   ├── ContaIntegrationTest.java
│   └── BancoServiceIntegrationTest.java
└── SuiteTest.java  # @Suite agregando todos os testes
```

## Como Executar

```bash
# Compilar
mvn clean compile

# Executar aplicação
mvn exec:java

# Testes unitários (perfil local - default)
mvn test

# Testes unitários + integração + relatórios (perfil CI)
mvn verify -Pci

# Relatório de cobertura standalone
mvn jacoco:report
```

## Perfis Maven

| Perfil | Descrição | Testes | Relatórios |
|--------|-----------|--------|------------|
| `local` (default) | Desenvolvimento local | Unitários apenas (`-ea`) | Desabilitados |
| `ci` | Pipeline CI/CD | Unitários + Integração | Surefire HTML, Failsafe HTML, JaCoCo (check 70%/60%) |

## Fases do Projeto

### Fase 1 - POO Básico
- [x] Conta (classe abstrata)
- [x] ContaCorrente extends Conta
- [x] ContaPoupanca extends Conta
- [x] Menu interativo com Scanner
- [x] Random para geração de números
- [x] Arrays para armazenamento

### Fase 2 - Intermediário
- [x] List<Conta> polimórfica (ArrayList)
- [x] Static contadorTotalContas + bloco static
- [x] Enum TipoConta {CORRENTE, POUPANCA}
- [x] Exceções customizadas checked (SaldoInsuficienteException, ContaNaoEncontradaException, ValorInvalidoException)
- [x] ExceptionChainHandler (pipeline unificado)
- [x] Persistência em arquivo (ExtratoWriter, BackupContas)
- [x] Assertions Design by Contract (-ea)
- [x] LocalDate + DateTimeFormatter timestamps
- [x] InputUtilSeguro (NumberFormatException handling)
- [x] Titular em ContaCorrente/ContaPoupanca + validação ValorInvalidoException

### Fase 3 - Avançado
- [x] JUnit 5 @Suite (@SelectClasses)
- [x] Testcontainers PostgreSQL (AbstractIntegrationTest)
- [x] ContaIntegrationTest (JDBC CRUD)
- [x] BancoServiceIntegrationTest (service layer integration)
- [x] Maven profiles (local/ci)
- [x] GitHub Actions CI/CD (.github/workflows/ci.yml)
- [x] JaCoCo coverage (prepare-agent, report, check 70% INSTRUCTION / 60% BRANCH)
- [x] maven-surefire-report-plugin HTML reports
- [x] Quality Gate (testes + coverage thresholds)

## Git Workflow

- `main` - branch principal (protegida)
- `develop` - branch de desenvolvimento
- `feature/*` - branches por funcionalidade

Fluxo: `feature/*` → `develop` → `main`

## CI/CD Pipeline

O pipeline executa em push/PR para `main` e `develop`:

1. **Build & Test** (ubuntu-latest)
   - PostgreSQL 16-alpine service container
   - Maven cache
   - `mvn clean compile test -Plocal` (unitários)
   - `mvn verify -Pci` (unitários + integração + relatórios)
   - Upload artifacts: Surefire, Failsafe, JaCoCo, Maven Site

2. **Quality Gate**
   - Verifica falhas em testes unitários e integração
   - Verifica thresholds JaCoCo (70% INSTRUCTION, 60% BRANCH)
   - Comenta PR com coverage summary

## Relatórios Gerados

| Relatório | Local | Descrição |
|-----------|-------|-----------|
| Surefire HTML | `target/site/surefire-report.html` | Testes unitários |
| Failsafe HTML | `target/site/failsafe-report.html` | Testes de integração |
| JaCoCo HTML | `target/site/jacoco/index.html` | Cobertura de código |
| JaCoCo XML | `target/site/jacoco/jacoco.xml` | Para CI/CD |

## Licença

Projeto educacional - Pensar Cursos