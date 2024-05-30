package scalashop.common

import java.util.concurrent.*
import scala.math.sqrt
import scala.util.DynamicVariable

/** Restricts the integer into the specified range.
  */
def clamp(v: Int, min: Int, max: Int): Int =
  if v < min then min
  else if v > max then max
  else v

extension [T1, T2, T3, T4](l: Seq[(T1, T2, T3, T4)])
  /** Transforms a `Seq` of tuple4 into a tuple4 of `Seq`s. For example,
    * `Seq((0, 1, 2, 3), (4, 5, 6, 7)).unzip4 == (Seq(0, 4), Seq(1, 5), Seq(2,
    * 6), Seq(3, 7))`
    */
  def unzip4 =
    l.foldLeft((Seq.empty[T1], Seq.empty[T2], Seq.empty[T3], Seq.empty[T4]))((quadruple, elements) =>
      (
        quadruple._1 :+ elements._1,
        quadruple._2 :+ elements._2,
        quadruple._3 :+ elements._3,
        quadruple._4 :+ elements._4
      )
    )

extension [T](quadruple: (T, T, T, T))
  def map4[B](f: T => B): (B, B, B, B) = (
    (
      f(quadruple._1),
      f(quadruple._2),
      f(quadruple._3),
      f(quadruple._4),
    )
  )

val forkJoinPool = ForkJoinPool()

abstract class TaskScheduler:
  def schedule[T](body: => T): ForkJoinTask[T]
  def parallel[A, B](taskA: => A, taskB: => B): (A, B) =
    val right = task {
      taskB
    }
    val left = taskA
    (left, right.join())

class DefaultTaskScheduler extends TaskScheduler:
  def schedule[T](body: => T): ForkJoinTask[T] =
    val t = new RecursiveTask[T]:
      def compute = body
    Thread.currentThread match
      case wt: ForkJoinWorkerThread =>
        t.fork()
      case _ =>
        forkJoinPool.execute(t)
    t

val scheduler =
  DynamicVariable[TaskScheduler](DefaultTaskScheduler())

def task[T](body: => T): ForkJoinTask[T] =
  scheduler.value.schedule(body)

def parallel[A, B](taskA: => A, taskB: => B): (A, B) =
  scheduler.value.parallel(taskA, taskB)

def parallel[A, B, C, D](
    taskA: => A,
    taskB: => B,
    taskC: => C,
    taskD: => D
): (A, B, C, D) =
  val ta = task { taskA }
  val tb = task { taskB }
  val tc = task { taskC }
  val td = taskD
  (ta.join(), tb.join(), tc.join(), td)
