# # Programming 2 - Exercise 9: Threads and Concurrency

Overview:
Create a thred, print its name/numer (I am thread 1, 2, 5...)
See that they can occur in various order

Make a sum sequential vs concurrent

Shared variables : message passing or shared memory

Synchronization: deadlock and rendez-vous

## Discover the Threads

Create a function that launches a thread an print its name
See that they not occurs in the right order

```Scala
// Scala code for thread creation by extending 
// the Thread class 
class MyThread extends Thread 
{ 
	override def run() 
	{ 
		// Displaying the thread that is running 
		println("Thread " + Thread.currentThread().getName() + 
										" is running.") 
	} 
} 

// Creating object 
object GFG 
{ 
	// Main method 
	def main(args: Array[String]) 
	{ 
		for (x <- 1 to 5) 
		{ 
			var th = new MyThread() 
						th.setName(x.toString()) 
							th.start() 
		} 
	} 
} 
```


## The Sum of Many

Now, you are going to make a function that compute the sum in two different way. First, let us define a sequential version of a sum up to $n$. 

```Scala
def sumSeq(n: Int) : Int = {
    n match
        case 0 => ???
        case _ => ???
}
```
This (not very smart function) add 1 $n$ times and returns the result. Complete the implementation in [threads/src/main/scala/sum/sum.scala](threads/src/main/scala/sum/sum.scala).

Now, let us do the same, with multiple threads

## Communication: Shared Memory VS. Message Passing

Let us see how threads can communicate. 

## Deadlocks and Rendez-Vous 

Launch thread, random action, then wait, decrement counter, and then resume 
