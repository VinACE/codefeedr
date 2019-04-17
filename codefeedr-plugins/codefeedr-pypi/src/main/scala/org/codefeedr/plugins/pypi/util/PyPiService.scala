package org.codefeedr.plugins.pypi.util

import org.codefeedr.plugins.pypi.protocol.Protocol.PyPiProject
import org.codefeedr.stages.utilities.HttpRequester
import org.json4s.{DefaultFormats, Formats}
import org.json4s.ext.JavaTimeSerializers
import scalaj.http.{Http, HttpRequest}
import org.json4s.jackson.JsonMethods.parse
import org.json4s.Extraction._

class PyPiService extends Serializable {

  lazy implicit val formats: Formats = DefaultFormats ++ JavaTimeSerializers.all
  private val url = "https://pypi.org/pypi/"

  def getProject(projectName: String): Option[PyPiProject] = {
    val projectEndPoint = projectName + "/json"

    val rawProject = getProjectRaw(projectEndPoint)

    if (rawProject.isEmpty) return None

    val json = parse(rawProject.get)
    extractOpt[PyPiProject](json)
  }

  def getProjectRaw(endpoint: String): Option[String] = {
    val response = try {
      val request = Http(url + endpoint).headers(getHeaders)
      new HttpRequester().retrieveResponse(request)
    } catch {
      case _: Throwable => return None
    }

    Some(response.body)
  }

  /** Add a user-agent with contact details. */
  def getHeaders: List[(String, String)] =
    ("User-Agent", "CodeFeedr-PyPi/1.0 Contact: zorgdragerw@gmail.com") :: Nil

}
