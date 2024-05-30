package futures.rest

import scala.collection.concurrent.TrieMap
import scala.collection.mutable.Queue
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success, Try}

import munit.Assertions.*

import sttp.client3.{Request, Response, SttpBackendOptions}
import sttp.client3.testing.SttpBackendStub
import sttp.model.StatusCode

enum Event:
  case Output(content: String)
  case Response(url: String)
  case ResponseError(url: String, err: Exception)

def testSchedule[T](
    handler: Request[?, ?] => Try[Response[String]],
    action: (HttpBackend, Window) ?=> Future[T],
    schedule: IArray[Event]
): Try[T] =
  inline def log(s: String) =
    // println(f"testSchedule: $s")
    ()

  val requests = TrieMap[String, (Request[?, ?], Promise[Response[String]])]()
  val output = Queue[String]()
  var currentLine = 0
  var currentEvent = 0
  val lock = Object()
  var started = false

  def tick(): Unit =
    lock.synchronized:
      if !started then return
      // log(f"tick currentEvent=$currentEvent")
      while !output.isEmpty do
        val line = output.dequeue()
        // log(f"consume $line")
        if currentEvent >= schedule.length then
          throw AssertionError(f"Unexpected output: $line")
        schedule(currentEvent) match
          case Event.Output(content) => assertNoDiff(line, content)
          case ev                    => throw AssertionError(f"Unexpected output: $line, before $ev")
        currentEvent += 1
      if currentEvent < schedule.length then
        schedule(currentEvent) match
          case Event.Response(url) if requests.contains(url) =>
            val (req, promise) = requests(url)
            log(f"response $url")
            promise.complete(handler(req))
            currentEvent += 1
            tick()
          case Event.ResponseError(url, err) if requests.contains(url) =>
            val (req, promise) = requests(url)
            log(f"response error $url")
            promise.failure(err)
            currentEvent += 1
            tick()
          case _ => ()

  object StubWindow extends Window:
    def println(s: String): Unit =
      synchronized(output.enqueue(s))
      log(f"output $s")
      tick()

  val backend =
    SttpBackendStub
      .asynchronousFuture
      .whenAnyRequest
      .thenRespondF(req =>
        // log(f"request: ${req.uri}")
        if requests.contains(req.uri.toString) then
          Future.failed(Exception(f"Request to ${req.uri} was already made"))
        else
          val promise = Promise[Response[String]]()
          requests.put(req.uri.toString, (req, promise))
          tick()
          promise.future
      )

  val resFuture = action(using backend, StubWindow)
  started = true
  tick()
  val res = Try(Await.result(resFuture, 500.millis))
  assertEquals(currentEvent, schedule.length, "Not enough events")
  res
