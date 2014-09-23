package com.socrata.thirdparty.metrics

import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConverters._

/**
 * Options for configuring metrics and reporters.
 * Easiest way to get started is using a Typesafe config:
 * {{{
 *   com.socrata {
 *     metrics {
 *       prefix = "com.socrata.soda.server"
 *       log-metrics = true
 *       reporting-interval = 60 s
 *       enable-graphite = true
 *       graphite-host = "my.graphite.host"
 *       graphite-port = 2003
 *     }
 *   }
 * }}}
 *
 * Then:
 * {{{
 *   val metricsOpts = MetricsOptions(ConfigFactory.load().getConfig("com.socrata.metrics"))
 * }}}
 */
case class MetricsOptions(// Should be a prefix string unique to each service
                          prefix: String = MetricsOptions.defPrefix,
                          logMetrics: Boolean = MetricsOptions.defLogMetrics,
                          enableJmx: Boolean = MetricsOptions.defEnableJmx,
                          enableGraphite: Boolean = MetricsOptions.defEnableGraphite,
                          graphiteHost: String = MetricsOptions.defGraphiteHost,
                          graphitePort: Int = MetricsOptions.defGraphitePort,
                          // How often metrics are logged / reported to statsd etc.
                          reportingIntervalSecs: Int = MetricsOptions.defReportingIntervalSecs)

object MetricsOptions {
  val defPrefix = "com.socrata.some.application"
  val defLogMetrics = false
  val defEnableJmx = true    // Expose all metrics through JMX
  val defEnableGraphite = false
  val defReportingIntervalSecs = 60
  val defGraphiteHost = "localhost"
  val defGraphitePort = 2003

  val defaultConfig = ConfigFactory.parseMap(Map(
                        "prefix" -> defPrefix,
                        "log-metrics" -> defLogMetrics,
                        "enable-jmx" -> defEnableJmx,
                        "enable-graphite" -> defEnableGraphite,
                        "graphite-host" -> defGraphiteHost,
                        "graphite-port" -> defGraphitePort,
                        "reporting-interval" -> (defReportingIntervalSecs + " s")
                      ).asJava)

  def apply(config: Config): MetricsOptions = {
    val configWithDefaults = config.withFallback(defaultConfig)
    val reportingIntervalSecs = configWithDefaults.getMilliseconds("reporting-interval") / 1000
    new MetricsOptions(configWithDefaults.getString("prefix"),
                       configWithDefaults.getBoolean("log-metrics"),
                       configWithDefaults.getBoolean("enable-jmx"),
                       configWithDefaults.getBoolean("enable-graphite"),
                       configWithDefaults.getString("graphite-host"),
                       configWithDefaults.getInt("graphite-port"),
                       reportingIntervalSecs.toInt)
  }
}
