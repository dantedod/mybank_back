CREATE TABLE
    IF NOT EXISTS invoice (
        id BIGSERIAL PRIMARY KEY,
        invoice_description VARCHAR(255) NOT NULL,
        amount DOUBLE PRECISION NOT NULL,
        email VARCHAR(100) NOT NULL,
        account_id BIGSERIAL NOT NULL,
        invoice_date DATE NOT NULL DEFAULT CURRENT_DATE,
        invoice_status VARCHAR(20) DEFAULT 'Pendente',
        due_date DATE NOT NULL,
        closing_date DATE NOT NULL,
        FOREIGN KEY (account_id) REFERENCES account (id)
    );