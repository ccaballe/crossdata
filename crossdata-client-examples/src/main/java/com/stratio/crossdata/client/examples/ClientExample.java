/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.crossdata.client.examples;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.jfairy.Fairy;
import org.jfairy.producer.BaseProducer;
import org.jfairy.producer.person.Person;

import com.stratio.crossdata.common.exceptions.ConnectionException;
import com.stratio.crossdata.common.exceptions.ExecutionException;
import com.stratio.crossdata.common.exceptions.UnsupportedException;
import com.stratio.crossdata.common.exceptions.ValidationException;
import com.stratio.crossdata.driver.BasicDriver;
import com.stratio.crossdata.driver.DriverConnection;

public class ClientExample {
    /**
     * Class constructor.
     */
    private ClientExample() {
    }

    static final Logger LOG = Logger.getLogger(ClientExample.class);

    static final String CASSANDRA_DATASTORE_MANIFEST = "https://raw.githubusercontent.com/Stratio/stratio-connector-cassandra/master/src/main/resources/com/stratio/connector/cassandra/CassandraDataStore.xml";
    static final String CASSANDRA_CONNECTOR_MANIFEST = "https://raw.githubusercontent.com/Stratio/stratio-connector-cassandra/master/src/main/resources/com/stratio/connector/cassandra/CassandraConnector.xml";
    static final String DEEP_CONNECTOR_MANIFEST = "https://raw.githubusercontent.com/Stratio/stratio-connector-deep/maintenance/0.3/src/main/config/DeepConnector.xml";

    static final String CASSANDRA_DATASTORE_FILE = CASSANDRA_DATASTORE_MANIFEST.substring(CASSANDRA_DATASTORE_MANIFEST.lastIndexOf('/')+1);
    static final String CASSANDRA_CONNECTOR_FILE = CASSANDRA_CONNECTOR_MANIFEST.substring(CASSANDRA_CONNECTOR_MANIFEST.lastIndexOf('/')+1);
    static final String DEEP_CONNECTOR_FILE = DEEP_CONNECTOR_MANIFEST.substring(DEEP_CONNECTOR_MANIFEST.lastIndexOf('/')+1);

    static final int NUMBER_OF_ROWS = 1000;
    static final String USER_NAME = "stratio";
    static final String PASSWORD = "stratio";

    public static void main(String[] args) {

        /*downloadManifest(
                CASSANDRA_DATASTORE_MANIFEST,
                CASSANDRA_DATASTORE_FILE);
        downloadManifest(
                CASSANDRA_CONNECTOR_MANIFEST,
                CASSANDRA_CONNECTOR_FILE);
        downloadManifest(
                DEEP_CONNECTOR_MANIFEST,
                DEEP_CONNECTOR_FILE);
        */

        BasicDriver basicDriver = new BasicDriver();

        DriverConnection xdConnection = null;

        try {
            xdConnection = basicDriver.connect(USER_NAME, PASSWORD);
            LOG.info("Connected to Crossdata Server");

            // RESET SERVER DATA
            xdConnection.cleanMetadata();
            LOG.info("Server data cleaned");

            // ADD CASSANDRA DATASTORE MANIFEST
            /*CrossdataManifest manifest = ManifestUtils.parseFromXmlToManifest(
                                CrossdataManifest.TYPE_DATASTORE,
                                CASSANDRA_DATASTORE_FILE);
            xdConnection.addManifest(manifest);
            LOG.info("Datastore manifest added.");
            */

            // ATTACH CLUSTER
            xdConnection.executeQuery("ATTACH CLUSTER cassandra_prod ON DATASTORE Cassandra WITH OPTIONS " +
                                "{'Hosts': '[127.0.0.1]', 'Port': 9042};");
            LOG.info("Cluster attached.");

            // ADD STRATIO CASSANDRA CONNECTOR MANIFEST
            /*
            manifest = ManifestUtils.parseFromXmlToManifest(
                                CrossdataManifest.TYPE_CONNECTOR,
                                CASSANDRA_CONNECTOR_FILE);
            xdConnection.addManifest(manifest);
            LOG.info("Stratio Cassandra Connector manifest added.");

            // ADD STRATIO DEEP CONNECTOR MANIFEST
            manifest = ManifestUtils.parseFromXmlToManifest(
                                CrossdataManifest.TYPE_CONNECTOR,
                                DEEP_CONNECTOR_FILE);
            xdConnection.addManifest(manifest);
            LOG.info("Stratio Deep Connector manifest added.");
            */


            // ATTACH STRATIO CASSANDRA CONNECTOR
            xdConnection.executeQuery("ATTACH CONNECTOR CassandraConnector TO cassandra_prod WITH OPTIONS " +
                                "{'DefaultLimit': '1000'};");
            LOG.info("Stratio Cassandra connector attached.");

            // ATTACH STRATIO DEEP CONNECTOR
            xdConnection.executeQuery("ATTACH CONNECTOR DeepConnector TO cassandra_prod WITH OPTIONS {};");
            LOG.info("Stratio Deep connector attached.");

            // CREATE CATALOG
            xdConnection.executeQuery("CREATE CATALOG catalogTest;");
            LOG.info("Catalog created.");

            // USE
            xdConnection.setCurrentCatalog("catalogTest");

            // CREATE TABLE 1
            xdConnection.executeQuery("CREATE TABLE tableTest ON CLUSTER cassandra_prod " +
                                "(id int PRIMARY KEY, serial int, name text, rating double, email text);");
            LOG.info("Table 1 created.");

            // CREATE TABLE 2
            xdConnection.executeQuery("CREATE TABLE tableTest2 ON CLUSTER cassandra_prod " +
                                "(id int PRIMARY KEY, lastname text, age int, company text);");
            LOG.info("Table 2 created.");

            // USE
            xdConnection.setCurrentCatalog("catalogTest");

            // INSERT RANDOM DATA
            Fairy fairy = Fairy.create();
            BaseProducer baseProducer = fairy.baseProducer();
            StringBuilder sb;
            for(int i = 1; i<NUMBER_OF_ROWS+1; i++){
                Person person = fairy.person();

                // INSERT INTO FIRST TABLE
                sb = new StringBuilder("INSERT INTO tableTest(id, serial, name, rating, email) VALUES (");
                sb.append(i).append(", ");
                sb.append(generateSerial(baseProducer)).append(", ");
                sb.append(generateName(person)).append(", ");
                sb.append(generateRating(baseProducer)).append(", ");
                sb.append(generateEmail(person));
                sb.append(");");

                xdConnection.executeQuery(sb.toString());
                LOG.info("Row for first table inserted.");

                // INSERT INTO SECOND TABLE
                sb = new StringBuilder("INSERT INTO tableTest2(id, lastname, age, company) VALUES (");
                sb.append(generateInt(baseProducer, NUMBER_OF_ROWS)).append(", ");
                sb.append(generateLastName(person)).append(", ");
                sb.append(generateAge(person)).append(", ");
                sb.append(generateCompany(person));
                sb.append(");");

                xdConnection.executeQuery(sb.toString());
                LOG.info("Row for second table inserted.");

            }
            // CREATE DEFAULT INDEX
            xdConnection.executeQuery("CREATE INDEX indexTest ON tableTest(name);");
            LOG.info("Default index created.");

            // CREATE FULL TEXT INDEX
            xdConnection.executeQuery("CREATE FULL_TEXT INDEX myIndex ON tableTest(email);");
            LOG.info("Full text index created.");

            // CLOSE CONNECTION
            basicDriver.disconnect();
            LOG.info("Connection closed");
        } catch (ConnectionException | ValidationException | ExecutionException | UnsupportedException  ex) {
            LOG.error(ex);
        }
        // CLOSE DRIVER
        basicDriver.close();
    }



    private static void downloadManifest(String url, String output) {
        URL link = null;
        try {
            link = new URL(url);
        } catch (MalformedURLException ex) {
            LOG.error(ex);
        }
        assert link != null;

        InputStream in = null;
        try {
            in = new BufferedInputStream(link.openStream());
        } catch (IOException ex) {
            LOG.error(ex);
        }
        assert in != null;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        try {
            while (-1!=(n=in.read(buf))){
                out.write(buf, 0, n);
            }
            out.close();
        } catch (IOException ex) {
            LOG.error(ex);
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(output);
        } catch (FileNotFoundException e) {
            LOG.error(e);
        }
        try {
            in.close();
            byte[] response = out.toByteArray();


            fos.write(response);
            fos.close();
        } catch (IOException ex) {
            LOG.error(ex);
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }
    }

    private static String generateCompany(Person person) {
        return "'" + person.getCompany().name() + "'";
    }

    private static int generateAge(Person person) {
        return person.age();
    }

    private static String generateLastName(Person person) {
        return "'" + person.lastName() + "'";
    }

    private static int generateInt(BaseProducer baseProducer, int nRows) {
        return baseProducer.randomBetween(1, nRows*8);
    }

    private static int generateSerial(BaseProducer baseProducer) {
        return baseProducer.randomBetween(1, Integer.MAX_VALUE-1);
    }

    private static String generateName(Person person) {
        return "'" + person.username() + "'";
    }

    private static double generateRating(BaseProducer baseProducer) {
        double rating = baseProducer.randomBetween(0.0, 10.0);
        DecimalFormat f = new DecimalFormat("##.##");
        return Double.parseDouble(f.format(rating));
    }

    private static String generateEmail(Person person) {
        return "'" + person.email() + "'";
    }

}
