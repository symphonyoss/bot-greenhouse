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

/**
 * Created by ryan.dsouza on 7/21/16.
 *
 * Simple object to hold two values
 */

public class Tuple<T, U> {

  private T val1;
  private T val2;

  public Tuple(T val1, T val2) {
    this.val1 = val1;
    this.val2 = val2;
  }

  public T getVal1() {
    return val1;
  }

  public void setVal1(T val1) {
    this.val1 = val1;
  }

  public T getVal2() {
    return val2;
  }

  public void setVal2(T val2) {
    this.val2 = val2;
  }

  @Override
  public String toString() {
    return "Tuple{" +
        "val1=" + val1 +
        ", val2=" + val2 +
        '}';
  }
}
