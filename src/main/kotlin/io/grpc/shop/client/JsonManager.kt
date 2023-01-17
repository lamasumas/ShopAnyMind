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

    fun getJsonTimeStampData(jsonString: String, jsonParam: String): String {
        val stringTimestamp = getJsonStringData(jsonString, jsonParam)
        val regex = Regex(
            pattern = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?",
            options = setOf(RegexOption.IGNORE_CASE)
        )
        if (regex.matches(stringTimestamp)) {
            return stringTimestamp
        } else {
            throw throwStatusException(
                jsonParam,
                "Timestamp is the wrong format, it should be like this 2022-09-01T00:00:00Z"
            )
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


    private fun throwStatusException(
        jsonParam: String,
        message: String = "The parameter $jsonParam is missing or is not correct type in the request"
    ): StatusException {
        val errorStatus = Status.newBuilder()
            .setCode(Code.FAILED_PRECONDITION_VALUE)
            .setMessage(message)
            .build()
        return StatusProto.toStatusException(errorStatus)
    }

}