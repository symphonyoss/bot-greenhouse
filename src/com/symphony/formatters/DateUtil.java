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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by ryan.dsouza on 7/20/16.
 *
 * Simple utility class for handling date formatting
 */

public class DateUtil {
  private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

  public static final SimpleDateFormat epochFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  public static final SimpleDateFormat messageDayFormatter = new SimpleDateFormat("EEE MMM dd");
  public static final SimpleDateFormat messageTimeFormatter = new SimpleDateFormat("HH:mm zzz");

  public static Date getDateFromEpochString(String date){
    try {
      epochFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
      return epochFormatter.parse(date);
    }
    catch(Exception e) {
      LOGGER.error("Error parsing date", e);
      return new Date();
    }
  }

  public static String getEpochStringFromDate(Date date) {
    return epochFormatter.format(date);
  }

  public static int getMinutesBetweenDates(Date firstDate, Date secondDate) {
    long differenceInMillis = secondDate.getTime() - firstDate.getTime();
    return (int) TimeUnit.MILLISECONDS.toMinutes(differenceInMillis);
  }

  public static Date getDateForMinutesBefore(Date date, int minutesBefore) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.MINUTE, -1 * minutesBefore);
    return calendar.getTime();
  }

  public static String getMessageFormattedTime(Date date) {
    return messageTimeFormatter.format(date);
  }

  public static String getMessageFormattedDate(Date date) {
    return messageDayFormatter.format(date) + " at " + messageTimeFormatter.format(date);
  }

  public static Date addDays(Date date, int days) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, days);
    return cal.getTime();
  }

  public static Date addDaysToToday(int days) {
    return addDays(new Date(), days);
  }
}