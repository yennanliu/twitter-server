package com.twitter.server.lint

import com.twitter.concurrent.Scheduler
import com.twitter.conversions.DurationOps._
import org.mockito.Mockito._
import org.scalatest.{FunSuite, Matchers}
import org.scalatestplus.mockito.MockitoSugar

class SchedulerBlockingRuleTest extends FunSuite with Matchers with MockitoSugar {

  test("low amount of blocking is not an issue") {
    val cutoff = 10.seconds
    val scheduler = mock[Scheduler]
    when(scheduler.blockingTimeNanos)
      .thenReturn((cutoff - 1.second).inNanoseconds)

    val rule = SchedulerBlockingRule(scheduler, cutoff)
    val issues = rule()
    assert(Nil == issues)
  }

  test("high amount of blocking is an issue") {
    val cutoff = 10.seconds
    val scheduler = mock[Scheduler]
    val blockingAmount = cutoff + 1.second
    when(scheduler.blockingTimeNanos)
      .thenReturn(blockingAmount.inNanoseconds)

    val rule = SchedulerBlockingRule(scheduler, cutoff)
    val issues = rule()
    assert(1 == issues.size)

    issues.head.details should include(blockingAmount.inMillis.toString)
  }

}
