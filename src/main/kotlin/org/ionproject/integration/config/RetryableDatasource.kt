package org.ionproject.integration.config

import org.springframework.jdbc.datasource.AbstractDataSource
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import java.sql.Connection
import javax.sql.DataSource

class RetryableDatasource(
    val dataSource: DataSource
) : AbstractDataSource() {

    @Retryable(maxAttempts = 5, backoff = Backoff(multiplier = 1.3, maxDelay = 10000))
    override fun getConnection(): Connection {
        TODO("Not yet implemented")
    }

    @Retryable(maxAttempts = 5, backoff = Backoff(multiplier = 1.3, maxDelay = 10000))
    override fun getConnection(username: String?, password: String?): Connection {
        TODO("Not yet implemented")
    }
}
