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

import java.io.File;

/**
 * Created by ryan.dsouza on 7/22/16.
 *
 * Defines specifics needed for configuring the Symphony Client
 */

public class SymphonyConfigurationProvider extends ISymphonyConfigurationProvider {

  @Override
  public String getSymphonyKeystorePassword() {
    return "cOrpB0tK3y";
  }

  @Override
  public String getSymphonyKeystoreType() {
    return "pkcs12";
  }

  @Override
  public String getSymphonyBaseURL() {
    return "https://sym-corp-stage-guse1-aha1.symphony.com:8444";
  }

  @Override
  public File getCertificateFile() {
    String fileName = "symphony-bot-user.p12";
    String classpathResource = '/' + fileName;
    super.LOG.info("attempting to load certificate file as classpath resource at " + classpathResource);
    File certificate = new File(getClass().getResource(classpathResource).getFile());
    if(!certificate.exists()) {
      throw new RuntimeException("no certificate found at " + certificate.getAbsolutePath());
    }
    return certificate;
  }
}