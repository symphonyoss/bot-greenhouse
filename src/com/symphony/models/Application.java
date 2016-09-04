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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryan.dsouza on 7/21/16.
 *
 * Represents an application on Greenhouse
 */

public class Application {

  private int id;
  private int candidateId;
  private List<String> jobs;

  public Application(JSONObject object) {
    this.id = object.getInt("id");
    this.candidateId = object.getInt("candidate_id");

    this.jobs = new ArrayList<>();
    JSONArray jobs = object.getJSONArray("jobs");
    for(int i = 0; i < jobs.length(); i++) {
      JSONObject job = jobs.getJSONObject(i);
      String jobName = job.getString("name");
      this.jobs.add(jobName);
    }
  }

  public int getId() {
    return id;
  }

  public int getCandidateId() {
    return candidateId;
  }

  public List<String> getJobs() {
    return jobs;
  }

  @Override
  public String toString() {
    return "Application{" +
        "id=" + id +
        ", candidateId=" + candidateId +
        ", jobs=" + jobs +
        '}';
  }
}
