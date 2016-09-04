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

import com.symphony.formatters.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ryan.dsouza on 7/20/16.
 *
 * Represents an entire interview on Greenhouse
 * Includes the interview's interviewers, organizers, etc.
 */

public class Interview {

  private static final Logger LOGGER = LoggerFactory.getLogger(Interview.class);

  private Date updatedAt;
  private Date startDate;
  private Date endDate;
  private Date createdAt;

  private ArrayList<Interviewer> interviewers;
  private Organizer organizer;
  private InterviewType interviewType;

  private String location;
  private Status status;

  private int id;
  private int applicationId;

  public enum Status {
    TO_BE_SCHEDULED,
    SCHEDULED,
    AWAITING_FEEDBACK,
    COMPLETE,
    SKIPPED,
    COLLECT_FEEDBACK,
    TO_BE_SENT,
    SENT,
    RECEIVED,
    OTHER
  }

  public Interview(JSONObject object) {

    this.updatedAt = DateUtil.getDateFromEpochString(object.getString("updated_at"));
    this.startDate = DateUtil.getDateFromEpochString(object.getJSONObject("start").getString("date_time"));
    this.createdAt = DateUtil.getDateFromEpochString(object.getString("created_at"));
    this.endDate = DateUtil.getDateFromEpochString(object.getJSONObject("end").getString("date_time"));

    this.interviewers = new ArrayList<Interviewer>();
    JSONArray interviewers = object.getJSONArray("interviewers");
    for(int i = 0; i < interviewers.length(); i++) {
      try {
        Interviewer interviewer = new Interviewer(interviewers.getJSONObject(i));
        this.interviewers.add(interviewer);
      }
      catch (JSONException exception) {
        //Null interviewer - no idea why it happens occasionally
        LOGGER.info("Null interviewer: ", exception);
      }
    }

    this.organizer = new Organizer(object.getJSONObject("organizer"));
    this.interviewType = new InterviewType(object.getJSONObject("interview"));

    this.location = getStringFromObject(object, "location");
    this.status = getStatus(getStringFromObject(object, "status"));

    this.id = getIntFromObject(object, "id");
    this.applicationId = getIntFromObject(object, "application_id");
  }

  private int getIntFromObject(JSONObject object, String key) {
    String string = getStringFromObject(object, key);
    if(string != null) {
      try {
        return Integer.parseInt(string);
      }
      catch (Exception e) {
        LOGGER.info("Error parsing number", e);
      }
    }
    return -1;
  }

  private String getStringFromObject(JSONObject object, String key) {
    Object field = object.get(key);
    if(field == null) {
      return null;
    }
    return field.toString();
  }

  public static Status getStatus(String status) {

    if(status == null) {
      return Status.OTHER;
    }

    status = status.toLowerCase();
    if(status.equals("to_be_scheduled")) {
      return Status.TO_BE_SCHEDULED;
    } else if(status.equals("scheduled")) {
      return Status.SCHEDULED;
    } else if(status.equals("awaiting_feedback")) {
      return Status.AWAITING_FEEDBACK;
    } else if(status.equals("complete")) {
      return Status.COMPLETE;
    } else if(status.equals("skipped")) {
      return Status.SKIPPED;
    } else if(status.equals("collect_feedback")) {
      return Status.COLLECT_FEEDBACK;
    } else if(status.equals("to_be_sent")) {
      return Status.TO_BE_SENT;
    } else if(status.equals("sent")) {
      return Status.SENT;
    } else if(status.equals("received")) {
      return Status.RECEIVED;
    } else {
      return Status.OTHER;
    }
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public ArrayList<Interviewer> getInterviewers() {
    return interviewers;
  }

  public void setInterviewers(ArrayList<Interviewer> interviewers) {
    this.interviewers = interviewers;
  }

  public Organizer getOrganizer() {
    return organizer;
  }

  public void setOrganizer(Organizer organizer) {
    this.organizer = organizer;
  }

  public InterviewType getInterviewType() {
    return interviewType;
  }

  public void setInterviewType(InterviewType interviewType) {
    this.interviewType = interviewType;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void setStatus(String status) {
    this.status = getStatus(status);
  }

  public int getId() {
    return id;
  }

  public String getIdString() {
    return String.valueOf(id);
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(int applicationId) {
    this.applicationId = applicationId;
  }

  @Override
  public String toString() {
    return "Interview{" +
        "updatedAt=" + updatedAt +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", createdAt=" + createdAt +
        ", interviewers=" + interviewers +
        ", organizer=" + organizer +
        ", interviewType=" + interviewType +
        ", location='" + location + '\'' +
        ", status=" + status +
        ", id=" + id +
        ", applicationId=" + applicationId +
        '}';
  }
}
