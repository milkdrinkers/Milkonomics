CREATE TABLE IF NOT EXISTS playerdata (
    "uuid" BINARY(16) NOT NULL,
    "accepting_payments" BOOLEAN DEFAULT TRUE NOT NULL,
    PRIMARY KEY ("uuid")
);