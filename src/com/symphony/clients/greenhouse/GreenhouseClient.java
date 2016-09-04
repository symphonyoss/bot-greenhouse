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

package com.symphony.clients.greenhouse;

import com.symphony.configurations.greenhouse.IGreenhouseConfigurationProvider;
import com.symphony.formatters.DateUtil;
import com.symphony.models.Application;
import com.symphony.models.Candidate;
import com.symphony.models.Interview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Created by ryan.dsouza on 7/19/16.
 *
 * Interacts with the Greenhouse API
 */

public final class GreenhouseClient implements IGreenhouseClient {

  private static final Logger LOG = LoggerFactory.getLogger(GreenhouseClient.class);

  private static final String BASE_URL = "https://harvest.greenhouse.io/v1/";
  private static final int MAX_RETURN = 500;

  private final IGreenhouseConfigurationProvider configurationProvider;
  private final String apiCredentialEncoded;

  public GreenhouseClient(IGreenhouseConfigurationProvider configurationProvider) {
    this.configurationProvider = configurationProvider;
    this.apiCredentialEncoded = getApiCredentialEncoded();
  }

  /**
   * Authenticates the Greenhouse client
   */
  @Override
  public void authenticate() {

    String fullURL = getApplicationsURL() + "?limit=2";
    JSONObject result = makeGetRequest(fullURL);
    JSONArray resultArray = result.getJSONArray("array");

    if(resultArray.length() < 1) {
      throw new RuntimeException("Greenhouse client not authenticated");
    }
  }

  /**
   * Returns a full application given the applicationId
   * @param applicationId
   * @return
   */
  @Override
  public Application getApplication(String applicationId) {
    String fullURL = getApplicationURL() + applicationId;
    JSONObject object = makeGetRequest(fullURL);
    return new Application(object);
  }

  /**
   * Returns a full Candidate given the candidateId
   * @param candidateId
   * @return
   */
  @Override
  public Candidate getCandidate(String candidateId) {
    String fullURL = getCandidateURL() + candidateId;
    JSONObject object = makeGetRequest(fullURL);
    return new Candidate(object);
  }

  /**
   * Returns interview given the interviewId
   * @param interviewId
   * @return
   */
  @Override
  public Interview getScheduledInterview(String interviewId) {
    String fullURL = getScheduledInterviewsURL() + "/" + interviewId;
    JSONObject object = makeGetRequest(fullURL);
    return new Interview(object);
  }

  /**
   * Returns interviews created after the date
   * @param date
   * @return
   */
  @Override
  public ArrayList<Interview> getScheduledInterviewsCreatedAfter(Date date) {
    String parameter = "created_after=" + DateUtil.getEpochStringFromDate(date);
    return getScheduledInterviews(parameter);
  }

  /**
   * Returns interviews updated after the date
   * @param date
   * @return
   */
  @Override
  public ArrayList<Interview> getScheduledInterviewsUpdatedAfter(Date date) {
    String parameter = "updated_after=" + DateUtil.getEpochStringFromDate(date);
    return getScheduledInterviews(parameter);
  }

  /**
   * Returns interviews starting after the date
   * @param date
   * @return
   */
  @Override
  public ArrayList<Interview> getScheduledInterviewsStartingAfter(Date date) {
    String parameter = "starts_after=" + DateUtil.getEpochStringFromDate(date);
    return getScheduledInterviews(parameter);
  }

  /**
   * Return interviews starting after right now
   * @return
   */
  @Override
  public ArrayList<Interview> getScheduledInterviewsStartingAfterNow() {
    return this.getScheduledInterviewsStartingAfter(new Date());
  }

  /**
   * Returns all scheduled interviews
   * @return
   */
  @Override
  public ArrayList<Interview> getScheduledInterviews() {
    return getScheduledInterviews(null);
  }

  /**
   * Private helper method for getting interviews
   * @param parameter
   * @return
   */
  private ArrayList<Interview> getScheduledInterviews(String parameter) {
    String url = getScheduledInterviewsURL();

    ArrayList<Interview> interviews = new ArrayList<Interview>();
    JSONObject result = makeGetRequest(url, parameter);
    JSONArray resultArray = result.getJSONArray("array");

    for(int i = 0; i < resultArray.length(); i++) {
      Interview interview = new Interview(resultArray.getJSONObject(i));
      System.out.println(resultArray.getJSONObject(i));
      interviews.add(interview);
    }

    return interviews;
  }

  /**
   * Private helper method for getting the JSON response for a GET request
   * @param fullURL
   * @return
   */
  private JSONObject makeGetRequest(String fullURL) {

    LOG.debug("Making GET request to: " + fullURL);
    String responseContent = ClientBuilder.newClient()
        .target(fullURL)
        .request(MediaType.APPLICATION_JSON)
        .header("Authorization", "Basic " + this.apiCredentialEncoded)
        .get(String.class);

    try {
      //Response is always an array
      JSONArray jsonArray = new JSONArray(responseContent);
      JSONObject response;

      //If there is only one object in the array, get that object and return it
      if(jsonArray.length() == 1) {
        response = jsonArray.getJSONObject(0);
      }
      //If it's an actual array, put it in a JSON object
      else {
        response = new JSONObject();
        response.put("array", jsonArray);
      }
      return response;
    } catch (JSONException e) {
      try {
        JSONObject response = new JSONObject(responseContent);
        return response;
      }
      catch (JSONException err) {
        LOG.error("Error converting to object", err);
      }
    }
    return null;
  }

  /**
   * Private helper method for getting the JSON response of a GET request
   * @param url
   * @param parameters
   * @return
   */
  private JSONObject makeGetRequest(String url, String parameters) {
    String fullURL = url + "?per_page=" + MAX_RETURN;
    if(parameters != null && !parameters.isEmpty()) {
      fullURL += "&" + parameters;
    }

    return makeGetRequest(fullURL);
  }

  /**
   * Helper method for encoding the API Credentials
   * @return
   */
  private String getApiCredentialEncoded() {
    String apiCredential = this.configurationProvider.getApiToken() + ":";
    final byte[] apiCredentialBytes = apiCredential.getBytes(StandardCharsets.UTF_8);
    return Base64.getEncoder().encodeToString(apiCredentialBytes);
  }

  private String getDepartments() {
    return BASE_URL + "departments";
  }

  private String getCandidateURL() {
    return BASE_URL + "candidates/";
  }

  private String getApplicationURL() {
    return BASE_URL + "applications/";
  }

  private String getApplicationsURL() {
    return BASE_URL + "applications";
  }

  private String getScheduledInterviewsURL() {
    return BASE_URL + "scheduled_interviews";
  }

}