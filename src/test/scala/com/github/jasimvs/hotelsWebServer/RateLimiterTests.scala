package com.github.jasimvs.hotelsWebServer

import org.scalatest.{ParallelTestExecution, Matchers, WordSpec}
import org.scalatest.mock.EasyMockSugar
/**
 * Created by jsulaiman on 6/14/2016.
 */
class RateLimiterTests extends WordSpec with Matchers with ParallelTestExecution {

  "A RateLimiter" should {
    " not issue a token when request exceeds rate limit" in {
      val rateLimiter = RateLimiter("001", 3, 2, 1)
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe false
      rateLimiter.getToken() shouldBe false
    }
  }

  "A RateLimiter" should  {
    " issue tokens when suspension is revoked" in {
      val rateLimiter = RateLimiter("002", 3, 2, 1)
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe false
      Thread.sleep(61 * 1000)

      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe true
      rateLimiter.getToken() shouldBe false
    }
  }

  "A RateLimiter" should {
    " should throw exception on illegal arguments" in {
      intercept[IllegalArgumentException] {
        RateLimiter("003", 0, 2, 1)
      }
      intercept[IllegalArgumentException] {
        RateLimiter("003", 3, 0, 1)
      }
      intercept[IllegalArgumentException] {
        RateLimiter("003", 3, 2, -1)
      }
    }
  }
}

//// this test takes too much time!
//class RateLimiterTest2 extends WordSpec with Matchers with ParallelTestExecution {
//  "A RateLimiter" should {
//    " issue tokens when not exceeding rate limit" in {
//      val rateLimiter = RateLimiter("004", 3, 2, 1)
//      rateLimiter.getToken() shouldBe true
//      Thread.sleep(60 * 1000)
//      rateLimiter.getToken() shouldBe true
//      Thread.sleep(30 * 1000)
//      rateLimiter.getToken() shouldBe true
//      Thread.sleep(30 * 1000)
//      rateLimiter.getToken() shouldBe true
//      Thread.sleep(60 * 1000)
//      rateLimiter.getToken() shouldBe true
//      Thread.sleep(30 * 1000)
//      rateLimiter.getToken() shouldBe true
//    }
//  }
//}
