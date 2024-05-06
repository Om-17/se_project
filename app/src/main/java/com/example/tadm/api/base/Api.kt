package com.example.tadm.api.base


import com.example.tadm.model.PersonDetail
import com.google.gson.Gson
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

class Api
{
    companion object
    {
        class Methods
        {
            companion object
            {
                const val GET = "GET"
                const val POST = "POST"
            }
        }


        inline fun <reified TResponse : Response> get(path: String, responseType: Type): TResponse {
            val connection = createHttpConnection(path, Methods.GET)

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                val errorReader = connection.errorStream.bufferedReader()
                val error = Gson().fromJson(errorReader.readText(), ApiError::class.java)

                error.statusCode = connection.responseCode
                errorReader.close()

                connection.disconnect()
                throw error
            }

            val reader = connection.inputStream.bufferedReader()
            val response = Gson().fromJson<TResponse>(reader.readText(), responseType)
            reader.close()

            connection.disconnect()
            return response
        }
        fun <TRequest : Request, TResponse : Response>
                post(path: String, request: TRequest, responseType: Class<TResponse>): TResponse
        {
            val connection = createHttpConnection(path, Methods.POST)
            val json = Gson().toJson(request)

            val writer = connection.outputStream.bufferedWriter()
            writer.write(json)
            writer.close()




            if (connection.responseCode != 200)
            {
                val errorReader = connection.errorStream.bufferedReader()
                val error = Gson().fromJson(errorReader.readText(), ApiError::class.java) ?: ApiError()

                error.statusCode = connection.responseCode
                errorReader.close()

                connection.disconnect()
                throw error
            }

            val reader = connection.inputStream.bufferedReader()
            println(reader.readText())
            val response = Gson().fromJson(reader.readText(), responseType) ?: Response()

            response.statusCode = connection.responseCode
            reader.close()
            println("res $response")
            connection.disconnect()

            @Suppress("UNCHECKED_CAST")
            return response as TResponse
        }



        public fun createHttpConnection(path: String, method: String): HttpURLConnection
        {
            val url = URL(path)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = method
            connection.doInput = true
            connection.doOutput = method != Methods.GET
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accepts", "application/json")


            return connection
        }
    }
}