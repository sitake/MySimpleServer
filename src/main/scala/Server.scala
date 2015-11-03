/**
 * Created by kaneko on 2015/11/01.
 */

import java.nio.file.{Paths,Files}

import com.twitter.finagle.httpx.service.RoutingService
import com.twitter.finagle.{Httpx, Service}
import com.twitter.finagle.httpx
import com.twitter.util.{Await, Future}
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._

object Server extends App {
  def service(path:String) = new Service[httpx.Request, httpx.Response] {
    def apply(req: httpx.Request): Future[httpx.Response] ={
      val response = httpx.Response()
      val url = getClass.getResource(path)

      response.contentString = scala.io.Source.fromURL(url).mkString
      Future.value(
        response
      )
    }
  }



  val routingService = RoutingService.byPath {
    case "/get/app/comments.json" => new Service[httpx.Request, httpx.Response] {
      def apply(req: httpx.Request): Future[httpx.Response] = {
        val response = httpx.Response()
        val url = Paths.get("Documents/MyWork/MySimpleServer/target/scala-2.11/classes/app/comments.json").toUri.toURL
        response.contentString = scala.io.Source.fromURL(url).mkString
        Future.value(
          response
        )
      }
    }
    case  "/post/app/comments.json" => new Service[httpx.Request, httpx.Response] {
      def apply(req: httpx.Request): Future[httpx.Response] = {
        val response = httpx.Response()
        val url = Paths.get("Documents/MyWork/MySimpleServer/target/scala-2.11/classes/app/comments.json").toUri.toURL
        val comments = parse(url.openStream)
        val newComments = comments ++ ("author" -> req.getParam("author")) ~ ("text" -> req.getParam("text"))
        val json = compact(render(newComments))
        Files.write(Paths.get(url.toURI),json.getBytes())
        response.contentString = json
        Future.value(
          response
        )
      }
    }
    case path => service(path)
  }


  val server = Httpx.serve(":8080", routingService)
  Await.ready(server)
}
