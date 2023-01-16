package io.grpc.shop

import java.sql.DriverManager
import java.text.SimpleDateFormat

class DatabaseManager {
    data class Sale(val datetime: String, val sales: Double, val points: Int)

    private val jdbcUrl = "jdbc:postgresql://localhost:5432/shop"
    fun getData(salesRequest: SalesRequest): MutableList<Sale> {
        val connection = DriverManager.getConnection(jdbcUrl, "postgres", "")
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
        val query = connection.prepareStatement(
            "SELECT * " +
                "FROM sales_history " +
                "where datetime between " +
                    "TO_TIMESTAMP(?, 'yyyy-MM-ddThh:MI:ssZ') " +
                    "and  TO_TIMESTAMP(?,'yyyy-MM-ddThh:MI:ssZ');")
        query.setString(1,salesRequest.startDateTime)
        query.setString(2, salesRequest.endDateTime)
        val result = query.executeQuery()
        val resultList = mutableListOf<Sale>()
        while (result.next()) {
            resultList.add(
                Sale(
                    datetime = sdf.format(result.getTimestamp("datetime")),
                    sales = result.getDouble("sales"),
                    points = result.getInt("points")
                )
            )
        }
        return resultList
    }
}