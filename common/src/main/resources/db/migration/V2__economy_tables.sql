-- Table for storing accounts
CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts" (
    "uuid" BINARY(16) NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "accepting_payments" TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY ("uuid")
);

CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts_balance" (
    "account_uuid" BINARY(16) NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "balance" DECIMAL(19, 4) NOT NULL DEFAULT 0,
    PRIMARY KEY ("account_uuid", "name"),
    FOREIGN KEY ("account_uuid") REFERENCES "${tablePrefix}accounts" ("uuid") ON DELETE CASCADE
);

CREATE INDEX "idx_${tablePrefix}accounts_balance_name_balance"
    ON "${tablePrefix}accounts_balance" ("name", "balance" DESC);