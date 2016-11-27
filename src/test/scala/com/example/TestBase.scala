package com.example

import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Suite, WordSpecLike}

trait TestBase
  extends WordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
    { this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    system.shutdown()
    system.awaitTermination()
  }
}
