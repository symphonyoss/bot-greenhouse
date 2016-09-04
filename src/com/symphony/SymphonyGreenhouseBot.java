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

import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.User;
import com.symphony.clients.greenhouse.GreenhouseClient;
import com.symphony.clients.greenhouse.IGreenhouseClient;
import com.symphony.clients.symphony.ISymphonyClient;
import com.symphony.clients.symphony.SymphonyClient;
import com.symphony.configurations.IConfigurationProvider;
import com.symphony.configurations.greenhouse.IGreenhouseConfigurationProvider;
import com.symphony.configurations.symphony.ISymphonyConfigurationProvider;
import com.symphony.formatters.DateUtil;
import com.symphony.formatters.GreenhouseMessageMLFormatter;
import com.symphony.models.Application;
import com.symphony.models.Candidate;
import com.symphony.models.Interview;
import com.symphony.models.Interviewer;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ryan.dsouza on 7/21/16.
 *
 * The actual bot
 */

public class SymphonyGreenhouseBot implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(SymphonyGreenhouseBot.class);
  private static final String INTERVIEW_ID_TAG = "interviewId";
  private static final String BOT_TAG = "botClass";
  private static final String TRIGGER_TAG = "trigger+";

  private final Scheduler quartzScheduler;

  private final IGreenhouseClient greenhouseClient;
  private final ISymphonyClient symphonyClient;
  private final IConfigurationProvider configurationProvider;

  private final HashMap<String, User> symphonyUsers;

  public SymphonyGreenhouseBot(IGreenhouseClient greenhouseClient,
      ISymphonyClient symphonyClient,
      IConfigurationProvider configurationProvider) {
    this.greenhouseClient = greenhouseClient;
    this.symphonyClient = symphonyClient;
    this.configurationProvider = configurationProvider;
    this.symphonyUsers = new HashMap<String, User>();

    try {
      this.quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
      this.quartzScheduler.start();
    } catch (SchedulerException exception) {
      throw new RuntimeException("Error instantiating Quartz scheduler", exception);
    }

    this.greenhouseClient.authenticate();
    LOG.debug("Authenticated Greenhouse Client");

    this.symphonyClient.authenticate();
    LOG.debug("Authenticated Symphony Client");
  }

  public SymphonyGreenhouseBot(IGreenhouseConfigurationProvider greenhouseConfigurationProvider,
      ISymphonyConfigurationProvider symphonyConfigurationProvider,
      IConfigurationProvider configurationProvider) {
    this(new GreenhouseClient(greenhouseConfigurationProvider),
        new SymphonyClient(symphonyConfigurationProvider),configurationProvider);
  }

  @Override
  public void run() {
    this.checkGreenhouseForUpdatesAndPostInSymphony();
  }

  /**
   * Gets all interviews starting now and handles them (sends notifications)
   */
  private void checkGreenhouseForUpdatesAndPostInSymphony() {
    ArrayList<Interview> interviews = greenhouseClient.getScheduledInterviewsStartingAfterNow();
    for(Interview interview : interviews) {
      handleInterview(interview);
    }
  }

  /**
   * Handles either sending an interview now or adding it to a scheduler to send later
   * @param interview
   */
  private void handleInterview(Interview interview) {

    //Get our stream of users
    List<User> users = getUsersForInterview(interview);
    Stream stream = symphonyClient.getStreamWithUsers(users);

    Date timeNow = new Date();
    Date interviewDate = interview.getStartDate();

    int minutesBeforeToSendMessage = configurationProvider.minutesBeforeInterviewToSendMessage();
    int timeUntilInterview = DateUtil.getMinutesBetweenDates(timeNow, interviewDate);

    //If the interview already happened
    if(timeUntilInterview < 0) {
      return;
    }
    //If less than X minutes, send the message now
    else if(timeUntilInterview <= minutesBeforeToSendMessage + 1) {
      LOG.debug("Sending interview now " + interview);
      handleSendingMessage(interview, stream.getId());
    }
    //Otherwise, add it to the scheduler to send 30 minutes before the interview
    else {
      Date dateToSendMessageAt =
          DateUtil.getDateForMinutesBefore(interviewDate, minutesBeforeToSendMessage + 1);

      JobDetail interviewJobDetail = JobBuilder.newJob(GreenhouseMessageSender.class)
          .withIdentity(interview.getIdString(), interview.getIdString()).build();
      interviewJobDetail.getJobDataMap().put(INTERVIEW_ID_TAG, interview.getIdString());
      interviewJobDetail.getJobDataMap().put(BOT_TAG, this);

      SimpleTrigger interviewJobTrigger = TriggerBuilder.newTrigger()
          .withIdentity(TRIGGER_TAG + interview.getIdString(), interview.getIdString())
          .startAt(dateToSendMessageAt)
          .withSchedule(SimpleScheduleBuilder.simpleSchedule())
          .build();

      try {
        //If we've already scheduled a job
        if(quartzScheduler.checkExists(interviewJobDetail.getKey())) {

          //Get the job
          JobDataMap jobDataMap =
              quartzScheduler.getJobDetail(interviewJobDetail.getKey()).getJobDataMap();
          SymphonyGreenhouseBot instance = (SymphonyGreenhouseBot) jobDataMap.get(BOT_TAG);
          String interviewId = jobDataMap.getString(INTERVIEW_ID_TAG);

          if(interviewId != null && instance != null) {
            Interview oldInterview = instance.greenhouseClient.getScheduledInterview(interviewId);

            //If the start dates aren't the same, delete it so it can be replaced
            if(!oldInterview.getStartDate().equals(oldInterview)) {
              LOG.debug("Replacing " + interviewJobDetail.getKey());
              quartzScheduler.deleteJob(interviewJobDetail.getKey());
            }
          }
        }

        //Add our new job to the scheduler
        quartzScheduler.scheduleJob(interviewJobDetail, interviewJobTrigger);
        LOG.debug("Scheduling interview notification for later " +
            dateToSendMessageAt + "\t" + interview);
      } catch (SchedulerException exception) {
        LOG.error("Error scheduling job", exception);
      }
    }
  }

  /**
   * Returns the Users to notify for an interview
   * @param interview
   * @return
   */
  private List<User> getUsersForInterview(Interview interview) {
    List<User> users = new ArrayList<>();

    for(Interviewer interviewer : interview.getInterviewers()) {
      String email = interviewer.getEmail();
      User symphonyUser = this.symphonyUsers.get(email);
      if(symphonyUser == null) {
        symphonyUser = symphonyClient.getUserForEmailAddress(email);
        this.symphonyUsers.put(email, symphonyUser);
        //users.add(symphonyUser);
      }
    }

    //TODO: Remove - just for testing
    users.add(symphonyClient.getUserForEmailAddress("christiane@symphony.com"));
    users.add(symphonyClient.getUserForEmailAddress("ryan.dsouza@symphony.com"));

    return users;
  }

  /**
   * Gets the Application and Candidate for the Interview, sends to Symphony right away
   * @param interview
   * @param streamId
   */
  private void handleSendingMessage(Interview interview, String streamId) {

    //Greenhouse Application
    Application application =
        greenhouseClient.getApplication(String.valueOf(interview.getApplicationId()));

    //Greenhouse Candidate
    Candidate candidate =
        greenhouseClient.getCandidate(String.valueOf(application.getCandidateId()));

    //Formatter for ML
    GreenhouseMessageMLFormatter greenhouseMessageMLFormatter =
        new GreenhouseMessageMLFormatter(interview, candidate, application);

    symphonyClient.sendMessage(streamId, greenhouseMessageMLFormatter.getMessageML());
  }

  /**
   * Job for sending a message regarding an interview at a later time
   */
  public static class GreenhouseMessageSender implements Job {

    public GreenhouseMessageSender() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
      SymphonyGreenhouseBot instance = (SymphonyGreenhouseBot) jobDataMap.get(BOT_TAG);
      String interviewId = jobDataMap.getString(INTERVIEW_ID_TAG);

      if(interviewId != null && instance != null) {
        Interview interview = instance.greenhouseClient.getScheduledInterview(interviewId);
        instance.handleInterview(interview);
      } else {
        instance.LOG.error("Interview id or instance is null: " + jobDataMap);
      }
    }
  }
}