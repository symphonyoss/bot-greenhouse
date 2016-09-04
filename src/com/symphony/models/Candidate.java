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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ryan.dsouza on 7/21/16.
 *
 * Represents a candidate on Greenhouse
 */

public class Candidate {

  private static final Logger LOGGER = LoggerFactory.getLogger(Interview.class);

  private int id;
  private String firstName;
  private String lastName;
  private String company;
  private String title;
  private Date createdAt;
  private String photoURL;
  private ArrayList<Tuple<String, String>> phoneNumbers;
  private ArrayList<Tuple<String, String>> emailAddresses;
  private ArrayList<Tuple<String, String>> websites;

  public Candidate(JSONObject object) {
    this.id = getIntFromObject(object, "id");
    this.firstName = getStringFromObject(object, "first_name");
    this.lastName = getStringFromObject(object, "last_name");
    this.company = getStringFromObject(object, "company");
    this.title = getStringFromObject(object, "title");
    this.createdAt = DateUtil.getDateFromEpochString(getStringFromObject(object, "created_at"));
    this.photoURL = getStringFromObject(object, "photo_url");

    this.phoneNumbers = getTupleFromArray(object.getJSONArray("phone_numbers"));
    this.emailAddresses = getTupleFromArray(object.getJSONArray("email_addresses"));
    this.websites = getTupleFromArray(object.getJSONArray("website_addresses"));
  }

  private static ArrayList<Tuple<String, String>> getTupleFromArray(JSONArray array) {
    ArrayList<Tuple<String, String>> tupleArrayList = new ArrayList<>();

    for(int i = 0; i < array.length(); i++) {
      JSONObject object = array.getJSONObject(i);

      String field1 = getStringFromObject(object, "value");
      String field2 = getStringFromObject(object, "type");
      Tuple<String, String> tuple = new Tuple<>(field1, field2);
      tupleArrayList.add(tuple);
    }
    return tupleArrayList;
  }

  private static String getStringFromObject(JSONObject object, String key) {
    Object field = object.get(key);
    if(field == null) {
      return null;
    }
    return field.toString();
  }

  private static int getIntFromObject(JSONObject object, String key) {
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

  public static Logger getLOGGER() {
    return LOGGER;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getPhotoURL() {
    return photoURL;
  }

  public void setPhotoURL(String photoURL) {
    this.photoURL = photoURL;
  }

  public ArrayList<Tuple<String, String>> getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(
      ArrayList<Tuple<String, String>> phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

  public ArrayList<Tuple<String, String>> getEmailAddresses() {
    return emailAddresses;
  }

  public void setEmailAddresses(
      ArrayList<Tuple<String, String>> emailAddresses) {
    this.emailAddresses = emailAddresses;
  }

  public ArrayList<Tuple<String, String>> getWebsites() {
    return websites;
  }

  public void setWebsites(
      ArrayList<Tuple<String, String>> websites) {
    this.websites = websites;
  }

  @Override
  public String toString() {
    return "Candidate{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", company='" + company + '\'' +
        ", title='" + title + '\'' +
        ", createdAt=" + createdAt +
        ", photoURL='" + photoURL + '\'' +
        ", phoneNumbers=" + phoneNumbers +
        ", emailAddresses=" + emailAddresses +
        ", websites=" + websites +
        '}';
  }
}
