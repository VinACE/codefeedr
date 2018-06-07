/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.codefeedr.plugins.cratesio


import org.codefeedr.plugins.cratesio.CargoProtocol.CrateInfo
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import scalaj.http.{Http, HttpResponse}

import scala.util.{Failure, Success, Try}

/**
  * A set of methods to query the crates.io API for a single package information
  */
class CratesIOAPIService {

  val BASE_URL = "https://crates.io/api/v1/crates/"

  implicit val defaultFormats = DefaultFormats

  def crateInfo(crateName: String) : CrateInfo =
    parseRespose(crateAPIRequest(crateName).body)

  def crateAPIRequest(crate: String): HttpResponse[String] =
    Http(BASE_URL + crate).
        timeout(connTimeoutMs = 10000, readTimeoutMs = 15000).
        asString

  def parseRespose(crateInfoJSON: String): CrateInfo =
    parse(crateInfoJSON).extract[CrateInfo]

  def extactCrateFromCommitMsg(msg: String) : Try[String] =
    try {
      Success(msg.split("`")(1).split("#")(0))
    } catch {
      case e: Exception => Failure(e)
    }
}