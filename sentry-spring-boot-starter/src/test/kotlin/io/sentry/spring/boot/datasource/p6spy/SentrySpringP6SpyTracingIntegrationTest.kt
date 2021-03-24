package io.sentry.spring.boot.datasource.p6spy

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.sentry.ITransportFactory
import io.sentry.spring.boot.datasource.dsproxy.SentryDsProxyAutoConfiguration
import io.sentry.test.checkTransaction
import io.sentry.transport.ITransport
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RunWith(SpringRunner::class)
@SpringBootTest(
    classes = [TracingApp::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["sentry.dsn=http://key@localhost/proj", "debug=true","sentry.enable-tracing=true", "sentry.traces-sample-rate=1.0"]
)
class SentrySpringP6SpyTracingIntegrationTest {

    @MockBean
    lateinit var transportFactory: ITransportFactory

    @LocalServerPort
    lateinit var port: Integer

    @Test
    fun `attaches span from database query call to transaction`() {
        val restTemplate = TestRestTemplate()
        val transport = mock<ITransport>()
        whenever(transportFactory.create(any(), any())).thenReturn(transport)

        restTemplate.getForEntity("http://localhost:$port/p6spy", String::class.java)

        await.untilAsserted {
            verify(transport, atLeastOnce()).send(checkTransaction { transaction ->
                assertThat(transaction.spans).hasSize(1)
                val span = transaction.spans.first()
                assertThat(span.op).isEqualTo("db.query")
                assertThat(span.description).isEqualTo(TracingController.QUERY)
            }, any())
        }
    }
}

@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class, SentryDsProxyAutoConfiguration::class])
@SpringBootConfiguration
@Import(TracingController::class)
open class TracingApp

@RestController
class TracingController(private val jdbcTemplate: JdbcTemplate) {
    companion object {
        val QUERY = "select count(*) from INFORMATION_SCHEMA.SYSTEM_USERS where 1=0"
    }

    @GetMapping("/p6spy")
    fun dsProxy() {
        jdbcTemplate.queryForObject(QUERY, Long::class.java)
    }
}
