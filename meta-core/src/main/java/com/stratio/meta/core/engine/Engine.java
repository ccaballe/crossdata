/*
 * Stratio Meta
 *
 * Copyright (c) 2014, Stratio, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.stratio.meta.core.engine;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.stratio.meta.core.executor.Executor;
import com.stratio.meta.core.parser.Parser;
import com.stratio.meta.core.planner.Planner;
import com.stratio.meta.core.validator.Validator;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class Engine {
    private final Parser parser;
    private final Validator validator;
    private final Planner planner;
    private final Executor executor;
    private final Session session;


    /**
     * Class logger.
     */
    private static final Logger _logger = Logger.getLogger(Engine.class.getName());

    public Engine(EngineConfig config) {

        Cluster cluster = Cluster.builder()
                .addContactPoints(config.getCassandraHosts())
                .withPort(config.getCassandraPort()).build();

        _logger.info("Connecting to Cassandra on "
                + Arrays.toString(config.getCassandraHosts()) + ":" + config.getCassandraPort());
        this.session=cluster.connect();

        parser = new Parser();
        validator = new Validator(session);
        planner = new Planner();
        executor = new Executor(session);
    }
       
    public Parser getParser() {
        return parser;
    }

    public Validator getValidator() {
        return validator;
    }

    public Planner getPlanner() {
        return planner;
    }

    public Executor getExecutor() {
        return executor;
    }

}
