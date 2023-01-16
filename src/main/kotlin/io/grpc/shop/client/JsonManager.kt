package io.grpc.shop.client

import com.google.rpc.Code
import com.google.rpc.Status
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import io.grpc.shop.PointsReply
import io.grpc.shop.SalesReply

object JsonManager {

    fun getJsonStringData(jsonString: String, jsonParam: String): String {
        val stringData = JsonPath.parse(jsonString)?.read<String>(jsonParam)
        if (stringData != null) {
            return stringData
        } else {
            throw throwStatusException(jsonParam)
        }
    }

    fun getJsonDoubleData(jsonString: String, jsonParam: String): Double {
        val stringData = JsonPath.parse(jsonString)?.read<Double>(jsonParam)
        if (stringData != null) {
            return stringData
        } else {
            throw throwStatusException(jsonParam)
        }
    }

    fun createGetPointsReplyJson(response: PointsReply): String {
        return "{\n" +
                "\"finalPrice\": \"${response.finalPrice}\",\n" +
                "\"points\": ${response.points},\n}"
    }

    fun createGetSalesReplyJson(response: SalesReply): String {
        var jsonReply = "{\n" +
                "\t\t\"sales\": [  \n"
        response.listOfSalesList.forEach {
            jsonReply += "\t\t\t{\n" +
                    "\t\t\t\t\"datetime\": \"${it.datetime}\",\n" +
                    "\t\t\t\t\"sales\": \"${it.sales}\",\n" +
                    "\t\t\t\t\"points\": ${it.points},\n" +
                    "\t\t\t}"
        }
        jsonReply += "]\n}"
        return jsonReply
    }


    private fun throwStatusException(jsonParam: String): StatusException {
        val errorStatus = Status.newBuilder()
            .setCode(Code.FAILED_PRECONDITION_VALUE)
            .setMessage("The parameter $jsonParam is not in the request")
            .build()
        return StatusProto.toStatusException(errorStatus)
    }

}