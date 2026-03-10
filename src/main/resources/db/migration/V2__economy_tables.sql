-- Table for storing accounts
CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts" (
    "uuid" BINARY(16) NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "balance" DECIMAL(19, 4) NOT NULL DEFAULT 0,
    PRIMARY KEY ("uuid")
);