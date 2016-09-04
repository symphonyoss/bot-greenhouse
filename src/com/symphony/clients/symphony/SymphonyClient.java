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

package com.symphony.clients.symphony;

import com.symphony.api.agent.api.MessagesApi;
import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.auth.api.AuthenticationApi;
import com.symphony.api.auth.model.Token;
import com.symphony.api.pod.api.PresenceApi;
import com.symphony.api.pod.api.RoomMembershipApi;
import com.symphony.api.pod.api.StreamsApi;
import com.symphony.api.pod.api.UsersApi;
import com.symphony.api.pod.model.RoomSearchCriteria;
import com.symphony.api.pod.model.RoomSearchResults;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.User;
import com.symphony.api.pod.model.UserIdList;
import com.symphony.api.pod.model.V2RoomDetail;
import com.symphony.configurations.symphony.ISymphonyConfigurationProvider;
import com.symphony.formatters.MessageML;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interacts with the Symphony platform API
 */

public class SymphonyClient implements ISymphonyClient {

  private final Logger LOG = LoggerFactory.getLogger(SymphonyClient.class);

  private final AuthenticationApi sbeApi;
  private final AuthenticationApi keyManagerApi;
  private final MessagesApi messagesApi;
  private final PresenceApi presenceApi;
  private final UsersApi usersApi;
  private final StreamsApi streamsApi;
  private final RoomMembershipApi roomMembershipApi;

  private Token sessionToken;
  private Token keyManagerToken;

  public SymphonyClient(ISymphonyConfigurationProvider configurationProvider) {

    com.symphony.api.agent.client.ApiClient agentClient = new com.symphony.api.agent.client.ApiClient();
    com.symphony.api.auth.client.ApiClient keyManagerClient = new com.symphony.api.auth.client.ApiClient();
    com.symphony.api.pod.client.ApiClient podApiClient = new com.symphony.api.pod.client.ApiClient();
    com.symphony.api.auth.client.ApiClient sbeClient = new com.symphony.api.auth.client.ApiClient();

    podApiClient.setBasePath(configurationProvider.getPodBasePath());
    agentClient.setBasePath(configurationProvider.getAgentBasePath());
    sbeClient.setBasePath(configurationProvider.getSBEBasePath());
    keyManagerClient.setBasePath(configurationProvider.getKeyManagerBasePath());

    this.messagesApi = new MessagesApi(agentClient);
    this.keyManagerApi = new AuthenticationApi(keyManagerClient);
    this.sbeApi = new AuthenticationApi(sbeClient);

    this.usersApi = new UsersApi(podApiClient);
    this.presenceApi = new PresenceApi(podApiClient);
    this.streamsApi = new StreamsApi(podApiClient);
    this.roomMembershipApi = new RoomMembershipApi(podApiClient);

    File certificate = configurationProvider.getCertificateFile();
    System.setProperty("javax.net.ssl.keyStore", certificate.getAbsolutePath());
    System.setProperty("javax.net.ssl.keyStorePassword", configurationProvider.getKeystorePassword());
    System.setProperty("javax.net.ssl.keyStoreType", configurationProvider.getKeystoreType());
  }

  /**
   * Authenticates the SymphonyClient
   */
  @Override
  public void authenticate() {

    try {
      Token sessionToken = sbeApi.v1AuthenticatePost();
      if(sessionToken.getToken() != null && sessionToken.getToken().length() != 0) {
        this.sessionToken = sessionToken;
        Token keyManagerToken = keyManagerApi.v1AuthenticatePost();

        if(keyManagerToken.getToken() != null && keyManagerToken.getToken().length() != 0) {
          this.keyManagerToken = keyManagerToken;
          LOG.debug("successfully authenticated symphony client");
          return;
        }
      }
    } catch(com.symphony.api.auth.client.ApiException e) {
      throw new RuntimeException("failed to authenticate symphony client", e);
    }
    throw new RuntimeException("failed to authenticate symphony client");
  }

  /**
   * Returns a user associated with that email address
   * @param emailAddress
   * @return
   */
  @Override
  public User getUserForEmailAddress(String emailAddress) {
    try {
      User user = usersApi.v1UserGet(emailAddress, this.sessionToken.getToken(), true);
      return user;
    } catch (com.symphony.api.pod.client.ApiException e) {
      LOG.error("Could not find user: " + emailAddress, e);
      return null;
    }
  }

  /**
   * Creates or returns (if exists) a stream with this User
   * @param user
   * @return
   */
  @Override
  public Stream getStreamWithUser(User user) {
    return getStreamWithUsers(Collections.singletonList(user));
  }

  /**
   * Creates or returns (if exists) a stream with these Users
   * @param users
   * @return
   */
  @Override
  public Stream getStreamWithUsers(User... users) {
    List<User> usersList = new ArrayList<>();
    for(User user : users) {
      if(user != null) {
        usersList.add(user);
      }
    }
    return getStreamWithUsers(usersList);
  }

  /**
   * Creates or returns (if exists) a stream with these Users
   * @param users
   * @return
   */
  @Override
  public Stream getStreamWithUsers(List<User> users) {
    try {
      UserIdList userIdList = new UserIdList();
      for(User user : users) {
        if(user != null) {
          userIdList.add(user.getId());
        }
      }
      Stream stream = this.streamsApi.v1ImCreatePost(userIdList, sessionToken.getToken());
      return stream;
    } catch (com.symphony.api.pod.client.ApiException exception) {
      LOG.error("Could not create stream", exception);
      return null;
    }
  }

  /**
   * Finds rooms given the query
   * @param query
   * @return
   */
  @Override
  public List<V2RoomDetail> getRoomsForSearchQuery(String query) {
    RoomSearchCriteria searchCriteria = new RoomSearchCriteria();
    searchCriteria.setQuery(query);

    try {
      RoomSearchResults results =
          streamsApi.v2RoomSearchPost(this.sessionToken.getToken(), searchCriteria, 0, 100);

      if(results.getCount() > 0) {
        return results.getRooms();
      }
    } catch(com.symphony.api.pod.client.ApiException e) {
      throw new RuntimeException("failed while searching for room with query " + query, e);
    }
    throw new RuntimeException("no rooms found for query " + query);
  }

  /**
   * Finds the first room given the query
   * @param query
   * @return
   */
  @Override
  public V2RoomDetail getRoomForSearchQuery(String query) {
    List<V2RoomDetail> rooms = this.getRoomsForSearchQuery(query);
    return rooms.get(0);
  }

  /**
   * Sends a MessageML to the roomID
   * @param roomID
   * @param messageML
   * @return
   */
  @Override
  public V2Message sendMessage(String roomID, MessageML messageML) {
    V2MessageSubmission messageSubmission = new V2MessageSubmission();
    messageSubmission.setFormat(V2MessageSubmission.FormatEnum.MESSAGEML);
    messageSubmission.setMessage(messageML.toString());

    V2Message response = this.sendMessage(roomID, messageSubmission);
    return response;
  }

  /**
   * Sends plain text to the roomID
   * @param roomID
   * @param text
   * @return
   */
  @Override
  public V2Message sendMessage(String roomID, String text) {
    V2MessageSubmission messageSubmission = new V2MessageSubmission();
    messageSubmission.setFormat(V2MessageSubmission.FormatEnum.TEXT);
    messageSubmission.setMessage(text);

    V2Message response = this.sendMessage(roomID, messageSubmission);
    return response;
  }

  /**
   * Sends a MessageML to the room
   * @param roomDetail
   * @param messageML
   * @return
   */
  @Override
  public V2Message sendMessage(V2RoomDetail roomDetail, MessageML messageML) {
    String roomID = roomDetail.getRoomSystemInfo().getId();
    return this.sendMessage(roomID, messageML);
  }

  /**
   * Private helper method to send a message to a room
   * @param roomID
   * @param message
   * @return
   */
  private V2Message sendMessage(String roomID, V2MessageSubmission message) {

    if(message.getMessage().replaceAll(" ", "").length() == 0) {
      return null;
    }

    try {
      V2Message result = messagesApi.v2StreamSidMessageCreatePost(roomID,
          sessionToken.getToken(), keyManagerToken.getToken(), message);

      if(result != null && result.getId() != null) {
        LOG.debug("successfully sent message: " + message);
        return result;
      }
    } catch(com.symphony.api.agent.client.ApiException e) {
      throw new RuntimeException("failed while sending message: " + message, e);
    }
    throw new RuntimeException("failed while sending message: " + message);
  }
}