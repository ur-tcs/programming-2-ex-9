package parallelism

import collection.parallel.CollectionConverters.IterableIsParallelizable
import parallelism.common.Task.task

object FoldReduce:

  // start reduceWithFold
  extension [A](l: List[A])
    def reduceWithFold(op: (A, A) => A): A =
      l match
        case head :: next => next.foldLeft(head)(op)
        case Nil          => throw IllegalArgumentException("Empty list")

  // end reduceWithFold

  // start reducePar
  extension [A](l: List[A])
    def reducePar(op: (A, A) => A): A =
      l match
        case Nil         => throw IllegalArgumentException("Empty list")
        case head :: Nil => head
        case _ =>
          val (left, right) = l.splitAt(l.length / 2)
          op(
            List(left, right).par.map(elem => elem.reducePar(op)).toList(0),
            List(left, right).par.map(elem => elem.reducePar(op)).toList(1)
          )
  // end reducePar

  // start aggregateMapReduce
  extension [A](l: List[A])
    def aggregate[B](z: B)(seqop: (B, A) => B, combop: (B, B) => B): B =
      l match
        case l if l.length <= 2 => l.foldLeft(z)(seqop)
        case _ =>
          val half = l.length / 2
          val (left, right) = l.splitAt(half)
          val List(leftF, rightF) = List(left, right).par
              .map(elem => elem.aggregate(z)(seqop, combop))
              .toList
          combop(leftF, rightF)

    // end aggregateMapReduce

    def average(a: List[Int]): Int =
      val (sum, length) = a.aggregate((0, 0))(
        (acc, element) => (acc._1 + element, acc._2 + 1),
        (accL, accR) => (accL._1 + accR._1, accL._2 + accR._2)
      )
      sum / length

end FoldReduce
