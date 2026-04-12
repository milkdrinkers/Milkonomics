-- Table for storing accounts
CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts" (
    "uuid" BLOB NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL DEFAULT "",
    "accepting_payments" TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts_balance" (
    "account_uuid" BLOB NOT NULL,
    "name" TEXT NOT NULL,
    "balance" DECIMAL(19,4) NOT NULL DEFAULT 0,
    PRIMARY KEY ("account_uuid", "name"),
    FOREIGN KEY ("account_uuid") REFERENCES "${tablePrefix}accounts" ("uuid") ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS "idx_${tablePrefix}accounts_balance_name_balance"
    ON "${tablePrefix}accounts_balance" ("name", "balance" DESC);