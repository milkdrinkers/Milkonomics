CREATE TABLE IF NOT EXISTS playerdata (
    "uuid" BLOB NOT NULL,
    "accepting_payments" BOOLEAN DEFAULT TRUE NOT NULL,
    PRIMARY KEY ("uuid")
);