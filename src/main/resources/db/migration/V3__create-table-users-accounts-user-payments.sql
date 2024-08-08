-- Criação da tabela de usuários
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    birthDate DATE NOT NULL
);

-- Criação da tabela de contas
CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    credit_limit DOUBLE PRECISION NOT NULL,
    account_value DOUBLE PRECISION NOT NULL,
    used_limit DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (cpf) REFERENCES users(cpf)
);

-- Criação da tabela de transações
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    sender_account_id BIGINT NOT NULL,
    receiver_account_id BIGINT NOT NULL,
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    amount DOUBLE PRECISION NOT NULL,
    payment_description TEXT,
    transaction_type VARCHAR(10) NOT NULL,
    FOREIGN KEY (sender_account_id) REFERENCES account(id),
    FOREIGN KEY (receiver_account_id) REFERENCES account(id)
);