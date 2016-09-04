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

package com.symphony.formatters;

import com.symphony.models.Application;
import com.symphony.models.Candidate;
import com.symphony.models.Interview;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by ryan.dsouza on 7/25/16.
 *
 * Formats a message regarding a candidate
 */

public class GreenhouseMessageMLFormatter {

  private final Interview interview;
  private final Candidate candidate;
  private final Application application;

  public GreenhouseMessageMLFormatter(Interview interview, Candidate candidate, Application application) {
    this.interview = interview;
    this.candidate = candidate;
    this.application = application;
  }

  public MessageML getMessageML() {
    MessageML messageML = new MessageML();

    //TODO: Remove - just for testing
    messageML.addParagraph(interview.getInterviewers().get(0).getName() + ", ");

    messageML.addParagraph("You have an interview with ");
    messageML.addItalicText(candidate.getFirstName() + " " + candidate.getLastName());

    int minutesUntilInterview = DateUtil.getMinutesBetweenDates(new Date(), interview.getStartDate());

    String startTime = "";
    if(minutesUntilInterview < 60) {
      if(minutesUntilInterview == 1) {
        startTime = " in 1 minute";
      }
      else {
        startTime = " at " + DateUtil.getMessageFormattedTime(interview.getStartDate());
        //startTime = " in " + minutesUntilInterview + " minutes";
      }
    } else {
      messageML.addParagraph(" on ");
      startTime = DateUtil.getMessageFormattedDate(interview.getStartDate());
    }

    messageML.addBoldText(startTime);

    String jobs = StringUtils.join(application.getJobs());
    messageML.addParagraph(" for " + jobs);

    return messageML;
  }
}