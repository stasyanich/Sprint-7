package ru.sber.rdbms

import mu.KotlinLogging
import java.sql.Connection
import java.sql.SQLException

class TransferOptimisticLock(val connection: Connection) {
    private val LOG = KotlinLogging.logger {}

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val accountId1Version = getVersion(connection, accountId1)
                val accountId2Version = getVersion(connection, accountId2)
                transfer(connection, - amount, accountId1, accountId1Version)
                transfer(connection, + amount, accountId2, accountId2Version)
                conn.commit()
            } catch (sqlException: SQLException) {
                LOG.error { "Fail transfer with $amount from account_id: $accountId1 to account_id: $accountId2\nerror: ${sqlException.message}" }
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }

    private fun getVersion(connection: Connection, accountId: Long): Long {
        connection.prepareStatement("SELECT version FROM account1 WHERE id= ?;")
            .use { statement ->
                statement.setLong(1, accountId)
                statement.executeQuery()
                    .use {
                        it.next()
                        return it.getLong("version")
                    }
            }
    }

    private fun transfer(connection: Connection, amount: Long, accountId: Long, version: Long){
        connection.prepareStatement("UPDATE account1 SET amount = amount + ?, version = version + 1 WHERE id = ? AND version = ?;")
            .use { statement ->
                statement.setLong(1, amount)
                statement.setLong(2, accountId)
                statement.setLong(3, version)

                if(statement.executeUpdate() == 0) {
                    throw SQLException("Concurrent update")
                }
            }
    }
}
