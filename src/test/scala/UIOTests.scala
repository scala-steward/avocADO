package avacado.tests

import avocado.*
import scala.concurrent.duration._
import zio.*

class UIOTests extends munit.FunSuite {

  given Applicative[UIO] = new Applicative[UIO] {
    def pure[A](a: A): UIO[A] = ZIO.succeed(a)
    def zip[A, B](fa: UIO[A], fb: UIO[B]): UIO[(A, B)] = fa.zipPar(fb)
  }

  def testWithTimeLimit(name: String, maxMillis: Long)(body: => Unit): Unit = {
    test(name) {
      val start = java.lang.System.currentTimeMillis()
      body
      val end = java.lang.System.currentTimeMillis()
      assert(end - start < maxMillis)
    }
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 1", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- ZIO.succeed(1)
      } yield a
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 1)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 2", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 3", 2000) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        c <- ZIO.succeed(a + b)
        d <- wait.map(_ => 4)
      } yield c + d
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 7)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 4", 2000) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        c <- ZIO.succeed(a + b)
        d <- wait.map(_ => 4)
        e <- wait.map(Function.const(5))
      } yield c + d
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 7)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 5", 2000) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        c <- ZIO.succeed(a + b)
        d <- wait.map(_ => 4)
        e <- wait
      } yield c + d
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 7)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 6", 2000) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        c <- ZIO.succeed(a + b)
        d <- wait.map(_ => 4)
        e <- wait
        f <- wait
        g <- wait.map(Function.const(7))
      } yield c + d + g
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 14)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 7", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        _ <- wait
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }
  
  testWithTimeLimit("correctly expand a simple UIO comprehension 8", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        _ <- wait
        c <- wait.map(_ => 3)
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 9", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        _ <- wait
        a <- wait.map(_ => 1)
        b <- wait.map(_ => 2)
        _ <- wait
        c <- wait.map(_ => 3)
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 10", 2000) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        _ <- wait.map(_ => a)
        b <- wait.map(_ => 2)
        c <- wait.map(_ => 3)
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 11", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => {
          val a = 2
          a
        })
      } yield a + b
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 3)
  }

  testWithTimeLimit("correctly expand a simple UIO comprehension 12", 1200) {
    val wait = ZIO.sleep(800.millis)
    val run: UIO[Int] = ado {
      for {
        a <- wait.map(_ => 1)
        b <- wait.map(_ => {
          val a = 2
          a
        })
        c <- wait.map { a =>
          def b(): Int = 3
          b()
        }
      } yield a + b + c
    }
    val res = Unsafe.unsafe { unsafe ?=>
      zio.Runtime.default.unsafe.run(
        run
      ).getOrThrowFiberFailure()
    }
    assertEquals(res, 6)
  }

}
