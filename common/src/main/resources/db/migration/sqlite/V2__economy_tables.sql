-- Table for storing accounts
CREATE TABLE IF NOT EXISTS "${tablePrefix}accounts" (
    "uuid" BLOB NOT NULL PRIMARY KEY,
    "name" TEXT NOT NULL DEFAULT "",
    "balance" DECIMAL(19, 4) NOT NULL DEFAULT 0
);