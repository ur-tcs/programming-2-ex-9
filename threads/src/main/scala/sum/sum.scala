package sum

/**
  * Sequential sum
  *
  * @param n
  * @return the sum until i
  */
def sumSeq(n: Int) : Int = {
    n match
        case 0 => 0
        case _ => 1 + sumSeq(n-1)
}

/**
  * parallel sum with t threads
  *
  * @param i the final result
  * @param t the number of threads
  * @return the sum made by t threads
  */
def sumPara(n: Int, t: Int) : Int = {
    createThread
    launch
    collect
}

def sumParaAux(n: Int) : Int = {
    1 +
}