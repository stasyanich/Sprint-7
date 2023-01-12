package ru.sber.rdbms

import mu.KotlinLogging
import java.sql.Connection
import java.sql.SQLException

class TransferConstraint(val connection: Connection) {
    private val LOG = KotlinLogging.logger {}
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.autoCommit = false
        connection.use { conn ->
            try {
                conn.prepareStatement(
                    """
            update account1 set amount = amount - ? where id = ?;
            update account1 set amount = amount + ? where id = ?;
            """
                )
                    .use { statement ->
                        statement.setLong(1, amount)
                        statement.setLong(2, accountId1)
                        statement.setLong(3, amount)
                        statement.setLong(4, accountId2)
                        statement.executeUpdate()
                        LOG.info { "Success transfer with $amount from account_id: $accountId1 to account_id: $accountId2" }
                    }
                conn.commit()
            } catch (sqlException: SQLException) {
                LOG.error { "Fail transfer with $amount from account_id: $accountId1 to account_id: $accountId2\nerror:${sqlException.message}" }
                conn.rollback()
            }
        }
    }
}
