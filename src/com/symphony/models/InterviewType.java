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

package com.symphony.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.json.JSONObject;

/**
 * Created by ryan.dsouza on 7/20/16.
 *
 * Represents a type of interview on Greenhouse
 */

@JsonIgnoreProperties (
    ignoreUnknown = true
)
public class InterviewType {

  private String name;
  private int id;

  public InterviewType(JSONObject object) {
    this.name = object.getString("name");
    this.id = object.getInt("id");
  }

  public InterviewType() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "InterviewType{" +
        "name='" + name + '\'' +
        ", id=" + id +
        '}';
  }
}