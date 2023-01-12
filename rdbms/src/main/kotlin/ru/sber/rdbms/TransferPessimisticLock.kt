package ru.sber.rdbms

import java.sql.Connection
import java.sql.SQLException

class TransferPessimisticLock(val connection: Connection)  {
    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        val (id1, id2) = if (accountId1 > accountId2) arrayOf(accountId2, accountId1) else arrayOf(accountId1, accountId2)
        connection.use { conn ->
            val autoCommit = conn.autoCommit
            try {
                conn.autoCommit = false
                val prepareStatement1 = conn.prepareStatement("select * from account1 where id = ? for update")
                prepareStatement1.use { statement ->
                    statement.setLong(1, id1)
                    statement.addBatch()
                    statement.setLong(1, id2)
                    statement.addBatch()
                    statement.executeBatch()
                }
                val prepareStatement2 = conn.prepareStatement("update account1 set amount = amount + ? where id = ?")
                prepareStatement2.use { statement ->
                    statement.setLong(1, -amount)
                    statement.setLong(2, id1)
                    statement.addBatch()
                    statement.setLong(1, amount)
                    statement.setLong(2, id2)
                    statement.addBatch()
                    statement.executeBatch()
                }
                conn.commit()
            } catch (exception: SQLException) {
                println(exception.message)
                conn.rollback()
            } finally {
                conn.autoCommit = autoCommit
            }
        }
    }
}
