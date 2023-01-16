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
        override suspend fun getPoints(request: PointsRequest): PointsReply {
            //calculate points and reply
            return Calculator.calculate(request)
        }

        override suspend fun getSales(request: SalesRequest): SalesReply {
            val salesList = SalesDAO().getSalesBetweenDates(request.startDateTime, request.endDateTime)
            return SalesReply.newBuilder()
                .addAllListOfSales(salesList).build()
        }
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = ShopServer(port)
    server.start()
    server.blockUntilShutdown()
}
