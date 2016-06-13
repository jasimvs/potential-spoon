You are provided with hotels database in CSV (Comma Separated Values) format.

We need you to implement HTTP service, according to the API requirements described below. You may use any language or platform that you like: C#/Java/Scala/etc.

    1) RateLimit: API calls need to be rate limited (request per 10 seconds) based on API Key provided in each http call.
       i)  On exceeding the limit, api key must be suspended for next 5 minutes. 
       ii) Api key can have different rate limit set, in this case from configuration, and if not present there must be a global rate limit applied.
    2) Search hotels by CityId
    3) Provide optional sorting of the result by Price (both ASC and DESC order).

# Hotels Service #

## Build & Run ##

```sh
$ cd Hotels_Service
$ ./sbt
> jetty:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.
