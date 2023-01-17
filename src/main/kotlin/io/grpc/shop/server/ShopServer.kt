package io.grpc.shop.server

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.shop.*
import io.grpc.shop.server.dao.SalesDAO

class ShopServer(val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(ShopService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class ShopService : ShopGrpcKt.ShopCoroutineImplBase() {
        /**
         * Remote procedure for getting the points and the final price
         */
        override suspend fun getPoints(request: PointsRequest): PointsReply {
            return Calculator.calculate(request)
        }

        /**
         * Remote procedure that gets all the sales between two timestamps and returns it as a list
         */
        override suspend fun getSales(request: SalesRequest): SalesReply {
            val salesList = SalesDAO().getSalesBetweenDates(request.startDateTime, request.endDateTime)
            return SalesReply.newBuilder()
                .addAllListOfSales(salesList).build()
        }
    }
}

/**
 * Main function that will launch the server
 */
fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = ShopServer(port)
    server.start()
    server.blockUntilShutdown()
}
