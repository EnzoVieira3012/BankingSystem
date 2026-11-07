-- Schema para testes de integração
CREATE TABLE IF NOT EXISTS conta (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    agencia INTEGER NOT NULL,
    saldo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    tipo VARCHAR(20) NOT NULL,
    limite NUMERIC(15,2),
    taxa_rendimento NUMERIC(5,4)
);

CREATE INDEX IF NOT EXISTS idx_conta_numero ON conta(numero);