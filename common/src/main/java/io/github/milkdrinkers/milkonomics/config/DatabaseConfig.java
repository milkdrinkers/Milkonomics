package io.github.milkdrinkers.milkonomics.config;

import io.github.milkdrinkers.milkonomics.config.common.VersionedConfig;
import io.github.milkdrinkers.milkonomics.config.exception.ConfigValidationException;
import io.github.milkdrinkers.milkonomics.database.handler.DatabaseType;
import io.github.milkdrinkers.milkonomics.messaging.broker.BrokerType;
import org.spongepowered.configurate.interfaces.meta.Exclude;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class DatabaseConfig implements VersionedConfig {
    @Comment("Do not change this value!")
    public int configVersion = 1;

    @Override
    @Exclude
    public int configVersion() {
        return configVersion;
    }

    @Override
    @Exclude
    public Map<Integer, ConfigurationTransformation> migrations() {
        return Map.of();
    }

    @Override
    @Exclude
    public void validate() throws ConfigValidationException {
        if (messaging.pollingInterval >= messaging.cleanupInterval) {
            throw new ConfigValidationException(
                "messaging.polling-interval (" + messaging.pollingInterval + "ms) " +
                    "must be less than messaging.cleanup-interval (" + messaging.cleanupInterval + "ms)"
            );
        }
    }

    @Comment("Database Settings")
    public Database database = new Database();

    @ConfigSerializable
    public static class Database {
        @Comment("Available types: \"sqlite\", \"h2\", \"mysql\", \"mariadb\"")
        public DatabaseType type = DatabaseType.SQLITE;
        public String tablePrefix = "milkonomics_";

        @Comment("Authentication")
        public String host = "localhost";
        public int port = 3306;
        public String database = "database_name";
        public String username = "root";
        public String password = "";

        @Comment("Advanced settings (Please don't touch unless you know what you're doing!)")
        public Advanced advanced = new Advanced();

        @ConfigSerializable
        public static class Advanced {
            @Comment("Should we try to repair broken migrations to the database")
            public boolean repair = false;

            @Comment("Configure the HikariCP connection pool")
            public ConnectionPool connectionPool = new ConnectionPool();

            @ConfigSerializable
            public static class ConnectionPool {
                public int maxpoolsize = 10;
                public int minidle = 10;
                public long maxlifetime = 180000;
                public long keepalivetime = 60000;
                public long connectiontimeout = 20000;
            }

            @Comment("A list of connection parameters, you can include more by adding them on a new line")
            public Map<String, Object> connectionProperties = defualtValues();

            private Map<String, Object> defualtValues() {
                final Map<String, Object> map = new HashMap<>();
                map.put("useSSL", false);
                map.put("cachePrepStmts", true);
                map.put("prepStmtCacheSize", 250);
                map.put("prepStmtCacheSqlLimit", 2048);
                map.put("useServerPrepStmts", true);
                map.put("useLocalSessionState", true);
                map.put("rewriteBatchedStatements", true);
                map.put("cacheResultSetMetadata", true);
                map.put("cacheServerConfiguration", true);
                map.put("elideSetAutoCommits", true);
                map.put("maintainTimeStats", false);
                return map;
            }
        }
    }

    @Comment("Message Broker Settings")
    public Messaging messaging = new Messaging();

    @ConfigSerializable
    public static class Messaging {
        @Comment("Enable or disable the message broker\nThis is only required if you are running the plugin on a server network")
        public boolean enabled = false;

        @Comment("How often to poll for new messages (in milliseconds)\nMust be less than cleanup-interval, ideally 1/3 or less")
        public int pollingInterval = 1000;

        @Comment("How often to clean up old messages (in milliseconds)\nMust be at least 3x the polling-interval")
        public int cleanupInterval = 30000;

        @Comment("Available broker types: \"database\", \"plugin\", \"redis\", \"rabbitmq\", \"nats\"")
        public BrokerType type = BrokerType.DATABASE;

        @Comment("Connection settings (redis, rabbitmq, nats)")
        public String address = "localhost:6379";
        public String username = "";
        public String password = "";

        @Comment("Advanced broker configuration")
        public Advanced advanced = new Advanced();

        @ConfigSerializable
        public static class Advanced {
            @Comment("Authentication method: \"password\", \"certificate\", \"token\"")
            public String authMethod = "password";

            @Comment("Token authentication (JWT, API keys, Redis AUTH tokens)")
            public String authToken = "";

            @Comment("SSL/TLS configuration")
            public SSL ssl = new SSL();

            @ConfigSerializable
            public static class SSL {
                public boolean enabled = false;

                @Comment("Client certificate (.crt, .pem)")
                public String certPath = "";

                @Comment("Private key (.key, .pem)")
                public String keyPath = "";

                @Comment("Certificate Authority (.crt, .pem)")
                public String caPath = "";

                public boolean verifyServerCert = true;
                public boolean verifyHostname = true;
            }

            @Comment("RabbitMQ-specific settings")
            public RabbitMQ rabbitmq = new RabbitMQ();

            @ConfigSerializable
            public static class RabbitMQ {
                public String virtualHost = "/";
            }

            @Comment("NATS-specific settings")
            public Nats nats = new Nats();

            @ConfigSerializable
            public static class Nats {
                @Comment("NKey seed file for cryptographic auth")
                public String nkeySeedPath = "";

                @Comment("JWT token file")
                public String jwtFilePath = "";

                @Comment("Combined JWT + NKey credentials file")
                public String credentialsPath = "";
            }
        }
    }
}