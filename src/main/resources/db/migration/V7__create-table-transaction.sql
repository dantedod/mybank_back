CREATE TABLE
    IF NOT EXISTS transactions (
        id BIGSERIAL PRIMARY KEY,
        account_id BIGINT NOT NULL,
        card_id BIGINT NOT NULL,
        transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        amount DOUBLE PRECISION NOT NULL,
        payment_description TEXT,
        FOREIGN KEY (account_id) REFERENCES account (id),
        FOREIGN KEY (card_id) REFERENCES cards (id)
    );