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

package io.grpc.shop

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusException
import io.grpc.shop.ShopGrpcKt.ShopCoroutineStub
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ShopClient(val channel: ManagedChannel) : Closeable {
    private val stub: ShopCoroutineStub = ShopCoroutineStub(channel)

    fun getPoints(jsonString: String) = runBlocking {

        try {
            val request = pointsRequest {
                datetime = JsonManager.getJsonStringData(jsonString, "$.datetime")
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


    fun getSales(jsonString: String) = runBlocking {
        try{
            val request = salesRequest {
                startDateTime = JsonManager.getJsonStringData(jsonString, "$.startDateTime")
                endDateTime = JsonManager.getJsonStringData(jsonString, "$.endDateTime")
            }
            val response = stub.getSales(request)
            val jsonResponse = createGetSalesReplyJson(response)
            println()
            println(jsonResponse)
        }catch (e: StatusException) {
            println()
            println("{ \n error: ${e.message} \n}")
        }
    }


    private fun createGetPointsReplyJson(response: PointsReply): String {
        return "{\n" +
                "\"finalPrice\": \"${response.finalPrice}\",\n" +
                "\"points\": ${response.points},\n}"
    }

    private fun createGetSalesReplyJson(response: SalesReply): String {
        var jsonReply = "{\n" +
                "\"sales\": [  \n"
        jsonReply += "]\n}"
        return jsonReply
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

/**
 * Greeter, uses first argument as name to greet if present;
 * greets "world" otherwise.
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
           // val testClientRequest = args.singleOrNull() ?: ("{\n" +
            //         "\"price\": \"100.00\",\n" +
            //         "\"priceModifier\": 0.95,\n" +
            //       "\"paymentMethod\": \"MASTERCARD\",\n" +
            //      "\"datetime\": \"2022-09-01T00:00:00Z\"}")
            // it.getPoints(testClientRequest)

            val testClientRequest = args.singleOrNull() ?: ("{\n" +
                             "\"startDateTime\" : \"2022-10-02T10:09:00Z\",\n" +
                             "\"endDateTime\": \"2022-10-02T10:15:01Z\"}" )
            it.getSales(testClientRequest)
        }
    }
}
