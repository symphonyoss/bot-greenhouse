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

package com.symphony.configurations.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by ryan.dsouza on 7/22/16.
 *
 * Defines specifics needed for configuring the Symphony Client
 */

public abstract class ISymphonyConfigurationProvider {

  protected static final Logger LOG = LoggerFactory.getLogger(ISymphonyConfigurationProvider.class);

  /**
   * Name of certificate file for authentication with Symphony
   */
  public abstract File getCertificateFile();

  /**
   * Password of keystore for authentication with Symphony
   */
  public abstract String getSymphonyKeystorePassword();

  /**
   * Type of keystore for authentication with Symphony (e.g. pkcs12)
   */
  public abstract String getSymphonyKeystoreType();

  /**
   * Symphony base URL
   */
  public abstract String getSymphonyBaseURL();


  public String getKeystorePassword() {
    return this.getSymphonyKeystorePassword();
  }

  public String getKeystoreType() {
    return this.getSymphonyKeystoreType();
  }

  public String getBaseURL() {
    return this.getSymphonyBaseURL();
  }

  public String getPodBasePath() {
    return this.getBaseURL() + "/pod";
  }

  public String getAgentBasePath() {
    return this.getBaseURL() + "/agent";
  }

  public String getSBEBasePath() {
    return this.getBaseURL() + "/sessionauth";
  }

  public String getKeyManagerBasePath() {
    return this.getBaseURL() + "/keyauth";
  }
}