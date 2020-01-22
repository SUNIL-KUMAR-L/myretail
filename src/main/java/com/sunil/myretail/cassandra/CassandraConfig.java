package com.sunil.myretail.cassandra;

import com.datastax.driver.core.*;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;

import com.datastax.driver.mapping.MappingManager;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Configuration for a Cassandra connection. Allows specification of a list of nodes in the Cassandra cluster,
 * along with a connection port.
 *
 * This class maintains a singleton Cassandra Cluster instance, and is unsuited to use when connecting to multiple
 * Cassandra clusters within the same application. Cluster creation is thread-safe.
 *
 * Any Session objects created by this configuration are NOT managed by the configuration and must be handled by
 * their creator. Likewise the Cluster instance should be shutdown by the parent application.
 */

@Slf4j
public class CassandraConfig {

        private static Cluster cluster = null;

        private static final Object object = new Object();

        private List<String> nodes;

        private String keyspaceName = null;

        private String datacenter = null;

        private boolean ssl = false;

        private ConsistencyLevel consistencyLevel = ConsistencyLevel.LOCAL_QUORUM;

        private String username = null;

        private String password = null;

        private boolean enableQueryLogging = false;

        private static Session session = null;

        private Integer port = null;

        /** set up the cassandra values from the environment
         *
         * @param environment
         */
        public CassandraConfig(final Environment environment) {

            String addresses = environment.getProperty("cassandra.contactpoints");
            if (StringUtils.isEmpty(addresses)) {
                    log.error("cassandra.contactpoints is not set");
                    throw new RuntimeException("cassandra.contactpoints is not set");
            }

            this.nodes = parseNodeList(addresses);

            String portString = environment.getProperty("cassandra.port");
            if (!StringUtils.isEmpty(portString)) {
                this.port = Integer.parseInt(portString);
            }

            String keyspace = environment.getProperty("cassandra.keyspace");
            if(!StringUtils.isEmpty(keyspace)) {
                log.info("cassandra.keyspace {}", keyspace);
                this.keyspaceName = keyspace;
            } else {
                log.info("cassandra.keyspace is not set");
            }

            String username = environment.getProperty("cassandra.username");
            if(!StringUtils.isEmpty(username)) {
                this.username = username;
            } else {
                log.warn("cassandra.username is not set");
            }

            String password = environment.getProperty("cassandra.password");
            if(!StringUtils.isEmpty(password)) {
                this.password = password;
            }  else {
                log.warn("cassandra.password is not set");
            }

            String sslString = environment.getProperty("cassandra.ssl");
            if(!StringUtils.isEmpty(sslString) && Boolean.parseBoolean(sslString)) {
                log.info("cassandra ssl=true");
                this.ssl = true;
            } else {
                log.info("cassandra ssl=false");
            }

            String datacenter = environment.getProperty("cassandra.datacenter");
            if(!StringUtils.isEmpty(datacenter)) {
                log.info("cassandra.datacenter " + datacenter);
                this.datacenter = datacenter;
            } else {
                log.warn("cassandra.datacenter is not set");
            }

            String consistency = environment.getProperty("cassandra.consistency");
            if(!StringUtils.isEmpty(consistency)) {
                log.info("cassandra.consistency level {}", consistency);
                this.consistencyLevel = ConsistencyLevel.valueOf(consistency);
            } else {
                log.info("cassandra.consistency level to default {}", this.consistencyLevel);
            }

            String logQueries = environment.getProperty("cassandra.logQueries");
            if(!StringUtils.isEmpty(logQueries) && Boolean.parseBoolean(logQueries)) {
                log.info("cassandra.logQueries=true");
                this.enableQueryLogging = true;
            } else {
                log.info("cassandra.logQueries=false");
            }

        }

        /**
         * The address list format is each host names separated by semi-colons.
         *
         * @param addresses The address list string
         * @return The parsed list of addresses
         */
        protected static List<String> parseNodeList(final String addresses) {
            final List<String> nodes = new ArrayList<>();
            if (addresses != null) {
                Collections.addAll(nodes, addresses.split(";"));
            }
            return nodes;
        }

        /**
         * Close the connection to the cluster.
         */
        @PreDestroy
        public void shutdown() {
            log.info("Shutdown Cassandra...");
            if(session !=null && !session.isClosed()){
                log.info("Closing Cassandra session...");
                session.close();
            }
            if (cluster != null && !cluster.isClosed()) {
                log.info("Closing Cassandra cluster...");
                cluster.close();
            }
        }

        /**
         * Create a new Cluster connection if none exists, otherwise provide the currently-established cluster connection.
         *
         * @return The Cluster connection.
         */
        private Cluster cluster() {
            if (cluster == null) {
                synchronized (object) {
                    if (cluster == null) {
                        log.info("Creating new Cassandra cluster...");
                        Cluster.Builder builder = new Cluster.Builder();
                        for (final String node : nodes) {
                            builder.addContactPoint(node);
                        }
                        if (port != null) {
                            builder.withPort(port);
                        }
                        if(username != null || password != null) {
                            builder.withCredentials(username, password);
                        }
                        if(ssl) {
                            builder.withSSL();
                        }
                        if(!StringUtils.isEmpty(datacenter)) {
                            builder.withLoadBalancingPolicy(
                                    new TokenAwarePolicy(
                                            DCAwareRoundRobinPolicy.builder()
                                                    .withLocalDc(datacenter)
                                                    .build()));
                        }

                        SocketOptions socketOptions = new SocketOptions();
                        socketOptions.setReadTimeoutMillis(5000);
                        socketOptions.setKeepAlive(true);
                        builder.withSocketOptions(socketOptions);

                        QueryOptions queryOptions = new QueryOptions();
                        queryOptions.setConsistencyLevel(consistencyLevel);
                        builder.withQueryOptions(queryOptions);

                        builder.withoutJMXReporting();

                        builder.withoutMetrics();

                        cluster = builder.build();

                        if(enableQueryLogging) {
                            QueryLogger.Builder qBuilder = new QueryLogger.Builder();
                            cluster.register(qBuilder.withConstantThreshold(100L).build());
                        }

                    }
                }
            }

            return cluster;
        }

        /**
         * in case session is null, Create a new Session else return same session
         * @return The Session
         */
        public Session session() {
            if(null == session){
                session = buildSession();
            }
            return session;
        }

        /**
         * Create a new Session for configured keyspace
         *
         * @return The Session
         */
        private Session buildSession() {
            try {
                log.info("Creating new Cassandra session... keyspace={}", this.keyspaceName);
                return cluster().connect(this.keyspaceName);
            } catch(NoHostAvailableException e) {
                log.error("Failed to connect to Cassandra instance.", e);
                log.info("Connection errors: " + Arrays.toString(e.getErrors().entrySet().toArray()));
                throw new RuntimeException("couldn't connect to Cassandra, shutting down.  SSL: " + (ssl ? "enabled" : "disabled"), e);
            } catch(Exception e) {
                log.error("Failed to connect to Cassandra instance.", e);
                throw new RuntimeException("Couldn't connect to Cassandra, shutting down.  SSL: " + (ssl ? "enabled" : "disabled"), e);
            }
        }

        public MappingManager mappingManager(){
            return new MappingManager(session());
        }
}
