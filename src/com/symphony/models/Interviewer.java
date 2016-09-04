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

import org.json.JSONObject;

/**
 * Created by ryan.dsouza on 7/20/16.
 *
 * Represents an Interviewer on Greenhouse
 */

public class Interviewer {

  private String scorecardId;
  private String name;
  private int id;
  private String email;

  public Interviewer(JSONObject object) {

    this.scorecardId = getStringFromObject(object, "scorecard_id");
    this.name = getStringFromObject(object, "name");
    this.email = getStringFromObject(object, "email");

    String id = getStringFromObject(object, "id");
    if(id != null) {
      try {
        this.id = Integer.parseInt(id);
      }
      catch (Exception exception) {
        this.id = -1;
      }
    }
    else {
      this.id = -1;
    }
  }

  private String getStringFromObject(JSONObject object, String key) {
    Object field = object.get(key);
    if(field == null) {
      return null;
    }
    return field.toString();
  }

  public String getScorecardId() {
    return scorecardId;
  }

  public void setScorecardId(String scorecardId) {
    this.scorecardId = scorecardId;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return "Interviewer{" +
        "scorecardId='" + scorecardId + '\'' +
        ", name='" + name + '\'' +
        ", id=" + id +
        ", email='" + email + '\'' +
        '}';
  }
}