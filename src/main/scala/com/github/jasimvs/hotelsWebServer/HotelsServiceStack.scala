package com.github.jasimvs.hotelsWebServer

import org.scalatra._
import scalate.ScalateSupport

trait HotelsServiceStack extends ScalatraServlet with ScalateSupport {

  notFound {
    // remove content type in case it was set through an action
    contentType = null
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

  protected val tooManyRequestsError = <html>
    <head>
      <title>429 Too Many Requests</title>
    </head>
    <body>
      <h1>Too Many Requests</h1>
      <p>Please wait for a while before sending any more requests.</p>
    </body>
  </html>

  protected val forbiddenRequestError = <html>
    <head>
      <title>403 Forbidden</title>
    </head>
    <body>
      <h1>Forbidden</h1>
      <p>Access to this resource on the server is denied!</p>
    </body>
  </html>

  protected val internalServerError = <html>
    <head>
      <title>500 Internal Server Error</title>
    </head>
    <body>
      <h1>Internal Server Error</h1>
      <p>The server encountered an internal error or misconfiguration and was unable to complete your request.</p>
    </body>
  </html>

}
