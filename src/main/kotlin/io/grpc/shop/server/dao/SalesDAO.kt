package io.grpc.shop.server.dao

import com.google.rpc.Code
import com.google.rpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.shop.Sale
import io.grpc.shop.sale
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat

class SalesDAO {

    private val jdbcUrl = "jdbc:postgresql://localhost:5432/shop"
    fun getSalesBetweenDates(startDate: String, endDate: String): MutableList<Sale> {
        val resultList = mutableListOf<Sale>()
        try {
            val connection = DriverManager.getConnection(jdbcUrl, "postgres", "")
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
            val query = connection.prepareStatement(
                "SELECT * " +
                        "FROM sales_history " +
                        "where datetime between " +
                        "TO_TIMESTAMP(?, 'yyyy-MM-ddThh:MI:ssZ') " +
                        "and  TO_TIMESTAMP(?,'yyyy-MM-ddThh:MI:ssZ');"
            )
            query.setString(1, startDate)
            query.setString(2, endDate)
            val result = query.executeQuery()
            while (result.next()) {
                resultList.add(
                    sale {
                        datetime = sdf.format(result.getTimestamp("datetime"))
                        sales = result.getDouble("sales").toString()
                        points = result.getInt("points")
                    }
                )
            }
            connection.close()
        } catch (e: SQLException) {
            val errorStatus = Status.newBuilder()
                .setCode(Code.UNAVAILABLE.number)
                .setMessage("SQL error: The database is down or is not well builded")
                .build()
            throw StatusProto.toStatusException(errorStatus)
        }
        return resultList
    }
}