
import com.github.jasimvs.hotelsWebServer._
import com.typesafe.config.{ConfigFactory, Config}
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    val conf: Config = ConfigFactory.load.getConfig("hotelsservice.app")
    val configService: ConfigService = new ConfigService(conf)
    val rateLimiterService: RateLimiterService = new RateLimiterService(configService)
    val dataLoader: DataLoader = new CsvDataLoader()
    val hotelsService: HotelsService = new DefaultHotelsService(configService, rateLimiterService, dataLoader)

    context.mount(new HotelsServlet(hotelsService), "/*")
  }
}
