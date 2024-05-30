package parallelism

import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.mutable.ParArray

object ParenthesesBalancing:
  // start isBalancedRecursive
  def isBalancedRecursive(str: List[Char]): Boolean =
    def helper(str: List[Char], acc: Int): Boolean =
      str match
        case Nil => acc == 0
        case head :: next =>
          if acc < 0 then false
          else if head == '(' then helper(next, acc + 1)
          else if head == ')' then helper(next, acc - 1)
          else helper(next, acc)
    helper(str, 0)
  // end isBalancedRecursive

  // start isBalancedFold
  def isBalancedFold(str: List[Char]): Boolean =
    val result = str.foldLeft(0)((z, elem) =>
      if z < 0 then -1
      else
        elem match
          case '(' => z + 1
          case ')' => z - 1
          case _   => z
    )
    result == 0
  // end isBalancedFold

  // start isBalancedParSimple
  def isBalancedParSimple(str: List[Char]): Boolean =
    val foldingFunction: (Int, Char) => Int = ??? // your folding function

    val numOpen = str.par.aggregate(0)(foldingFunction, _ + _)

    (numOpen == 0)
  // end isBalancedParSimple

  // start isBalancedPar
  def isBalancedPar(str: List[Char]): Boolean =
    val seqOp: ((Int, Int), Char) => (Int, Int) =
      (acc, elem) =>
        if elem == '(' then (acc._1 + 1, acc._2)
        else if elem == ')' then
          if acc._1 > 0 then (acc._1 - 1, acc._2) else (acc._1, acc._2 + 1)
        else acc

    val combOp: ((Int, Int), (Int, Int)) => (Int, Int) = (accL, accR) =>
      (accL._1 - accR._1, accL._2 - accR._2)

    str.par.aggregate((0, 0))(seqOp, combOp) == (0, 0)

  // end isBalancedPar
