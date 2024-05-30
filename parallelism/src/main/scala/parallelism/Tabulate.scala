package parallelism

import scala.reflect.ClassTag
import scala.collection.parallel.mutable.ParArray
import scala.collection.parallel.CollectionConverters.*

extension (a: Array.type)
  def seqTabulate[A: ClassTag](n: Int)(f: Int => A): Array[A] =
    (0 until n).toArray.map(i => f(i))

extension (p: ParArray.type) {
  def parTabulate[A: ClassTag](n: Int)(f: Int => A): ParArray[A] =
    (0 until n).toArray.par.map(i => f(i))
}

extension [A](seq: Array[A])
  def zipWith[B, C: ClassTag](f: (A, B) => C)(other: Array[B]): Array[C] =
    val minSize = math.min(seq.length, other.length)
    ParArray.parTabulate(minSize)(i => f(seq(i), other(i))).toArray

def vectorAdd(a: Array[Int], b: Array[Int]) =
  a.zipWith((l: Int, r: Int) => l + r)(b)
