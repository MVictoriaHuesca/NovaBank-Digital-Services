CREATE TABLE IF NOT EXISTS clients (
    id             SERIAL          PRIMARY KEY,
    name           VARCHAR(100)    NOT NULL,
    surname        VARCHAR(150)    NOT NULL,
    dni            VARCHAR(20)     NOT NULL UNIQUE,
    email          VARCHAR(150)    NOT NULL UNIQUE,
    phone          VARCHAR(20)     NOT NULL UNIQUE,
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
    id             SERIAL          PRIMARY KEY,
    account_number VARCHAR(34)     NOT NULL UNIQUE,
    client_id      INT             NOT NULL,
    balance        NUMERIC(15, 2)  NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_accounts_client FOREIGN KEY (client_id) REFERENCES clients (id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id             SERIAL          PRIMARY KEY,
    account_id     INT             NOT NULL,
    type           VARCHAR(50)     NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL', 'OUTGOING_TRANSFER', 'INCOMING_TRANSFER')),
    amount         NUMERIC(15, 2)  NOT NULL CHECK (amount > 0),
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_account FOREIGN KEY (account_id) REFERENCES accounts (id)
);
