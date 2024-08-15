CREATE TABLE IF NOT EXISTS invoice (
    id BIGSERIAL PRIMARY KEY,
    invoice_description VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    email VARCHAR(100) NOT NULL,
    card_name VARCHAR(100) NOT NULL,
    invoice_date DATE NOT NULL DEFAULT CURRENT_DATE,
    invoice_status VARCHAR(20) DEFAULT 'Pendente',
    due_date DATE NOT NULL,
    FOREIGN KEY (card_name) REFERENCES cards(card_name)
);