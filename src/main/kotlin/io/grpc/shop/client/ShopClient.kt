/*
 * Copyright 2020 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.grpc.shop.client

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import io.grpc.shop.ShopGrpcKt.ShopCoroutineStub
import io.grpc.shop.client.JsonManager.createGetPointsReplyJson
import io.grpc.shop.client.JsonManager.createGetSalesReplyJson
import io.grpc.shop.pointsRequest
import io.grpc.shop.salesRequest
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ShopClient(val channel: ManagedChannel) : Closeable {
    private val stub: ShopCoroutineStub = ShopCoroutineStub(channel)


    /**
     * Method that will execute the GetPoints remote procedure.
     */
    fun getPoints(jsonString: String) = runBlocking {

        try {
            val request = pointsRequest {
                datetime = JsonManager.getJsonTimeStampData(jsonString, "$.datetime")
                priceModifier = JsonManager.getJsonDoubleData(jsonString, "$.priceModifier")
                price = JsonManager.getJsonDoubleData(jsonString, "$.price")
                paymentMethod = JsonManager.getJsonStringData(jsonString, "$.paymentMethod")
            }

            val response = stub.getPoints(request)
            val jsonResponse = createGetPointsReplyJson(response)
            println(jsonResponse)
        } catch (e: StatusException) {
            println()
            println("{ \n error: ${e.message} \n}")
        }
    }


    /**
     * Method that will execute the GetSales remote procedure.
     */
    fun getSales(jsonString: String) = runBlocking {
        try {
            val request = salesRequest {
                startDateTime = JsonManager.getJsonTimeStampData(jsonString, "$.startDateTime")
                endDateTime = JsonManager.getJsonTimeStampData(jsonString, "$.endDateTime")
            }
            val response = stub.getSales(request)
            val jsonResponse = createGetSalesReplyJson(response)
            println()
            println(jsonResponse)
        } catch (e: StatusException) {
            println()
            println("{ \n error: ${e.message} \n}")
        }
    }


    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Main method of the shopclient that will execute both procedures
 */
fun main(args: Array<String>) {
    val isRemote = args.size == 1

    Executors.newFixedThreadPool(10).asCoroutineDispatcher().use { dispatcher ->
        val builder = if (isRemote)
            ManagedChannelBuilder.forTarget(args[0].removePrefix("https://") + ":443").useTransportSecurity()
        else
            ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext()

        ShopClient(
            builder.executor(dispatcher.asExecutor()).build()
        ).use {
            //Calls the getPoints remote procedure
            println("EXECUTING GETPOINTS PROCEDURE")
            var testClientRequest = File("PointsExample.json").readText(Charsets.UTF_8)
            println("Response received by the Server:")
            it.getPoints(testClientRequest)
            //Calls the getSales remote procedure
            println("=================================")
            println("EXECUTING GETSALES PROCEDURE")
            println("Response received by the Server:")
            testClientRequest = File("SalesExmple.json").readText(Charsets.UTF_8)
            it.getSales(testClientRequest)
        }
    }
}
