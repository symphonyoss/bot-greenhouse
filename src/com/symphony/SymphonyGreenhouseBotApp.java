/*
 *
 *
 * Copyright 2016 Symphony Communication Services, LLC
 *
 * Licensed to Symphony Communication Services, LLC under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.symphony;

import com.symphony.clients.greenhouse.GreenhouseClient;
import com.symphony.clients.symphony.ISymphonyClient;
import com.symphony.clients.symphony.SymphonyClient;
import com.symphony.configurations.IConfigurationProvider;
import com.symphony.configurations.SimpleConfigurationProvider;
import com.symphony.configurations.greenhouse.GreenhouseConfigurationProvider;
import com.symphony.configurations.greenhouse.IGreenhouseConfigurationProvider;
import com.symphony.configurations.symphony.SymphonyConfigurationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by ryan.dsouza on 7/22/16.
 *
 * Main class
 */

public class SymphonyGreenhouseBotApp {

  private final Logger LOG = LoggerFactory.getLogger(SymphonyGreenhouseBot.class);

  public static void main(String[] ryan) {

    IGreenhouseConfigurationProvider greenhouseConfigurationProvider = new GreenhouseConfigurationProvider();
    GreenhouseClient greenhouseClient = new GreenhouseClient(greenhouseConfigurationProvider);

    SymphonyConfigurationProvider symphonyConfigurationProvider = new SymphonyConfigurationProvider();
    ISymphonyClient symphonyClient = new SymphonyClient(symphonyConfigurationProvider);

    IConfigurationProvider configurationProvider = new SimpleConfigurationProvider();

    SymphonyGreenhouseBot greenhouseBot = new SymphonyGreenhouseBot(greenhouseClient, symphonyClient, configurationProvider);

    Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(greenhouseBot, 0,
        configurationProvider.minutesBeforeInterviewToSendMessage(), TimeUnit.MINUTES);
  }
}