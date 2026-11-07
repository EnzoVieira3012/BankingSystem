# Sistema Bancário Console

Projeto integrador Java consolando os 3 cursos do Pensar Cursos (41 aulas total):
- **Fase 1**: POO Básico (Códigos - 15 aulas)
- **Fase 2**: Intermediário (12 aulas)
- **Fase 3**: Avançado (14 aulas)

## Tecnologias

- Java 17+ LTS
- Maven 3.6+
- JUnit 5
- Git

## Estrutura do Projeto

```
src/main/java/br/com/banco/
├── model/          # Conta (abstract), ContaCorrente, ContaPoupanca, TipoConta
├── service/        # BancoService - regras de negócio
├── util/           # InputUtil, ContaGenerator
├── exception/      # Exceções customizadas
└── persistence/    # ExtratoWriter, BackupContas
```

## Como Executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java

# Testes
mvn test
```

## Fases do Projeto

### Fase 1 - POO Básico
- [ ] Conta (classe abstrata)
- [ ] ContaCorrente extends Conta
- [ ] ContaPoupanca extends Conta
- [ ] Menu interativo com Scanner
- [ ] Random para geração de números
- [ ] Arrays para armazenamento

### Fase 2 - Intermediário
- [ ] List<Conta> polimórfica (ArrayList)
- [ ] Static contadorTotalContas
- [ ] Enum TipoConta {CORRENTE, POUPANCA}
- [ ] Exceções customizadas checked
- [ ] Persistência em arquivo (FileWriter)
- [ ] Assertions (Design by Contract)
- [ ] LocalDate para timestamps

### Fase 3 - Avançado
- [ ] JUnit 5 @Suite
- [ ] Testcontainers PostgreSQL
- [ ] Maven profiles (local/ci)
- [ ] GitHub Actions CI/CD
- [ ] JaCoCo coverage

## Git Workflow

- `main` - branch principal (protegida)
- `develop` - branch de desenvolvimento
- `feature/*` - branches por funcionalidade

## Licença

Projeto educacional - Pensar Cursos