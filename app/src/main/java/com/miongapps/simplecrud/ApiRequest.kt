package com.miongapps.simplecrud

import android.text.Editable
import com.github.kittinunf.fuel.Fuel
import org.json.JSONArray
import org.json.JSONObject

class ApiRequest {
    private val server_url : String = "https://simplecrud.markramosonline.com"

    fun getListEmployees(results : (JSONArray) -> Unit){
        println("GET EMPLOYEES")
        val endPointURL = "/api/listemployee"
        Fuel.get( "$server_url$endPointURL")
            .responseString { request, response, result ->
//                println("Loading List Employee")
                if (result != null) {
                    val json = result.get()
                    val jsonObject = JSONArray(json)
//                    println("List JSON")
//                    println(jsonObject.getJSONObject(0).get("full_name") as String)
                    results(jsonObject)
                }
            }
    }

    fun loginEmployee(uname: Editable, pword: Editable, complete : (String) -> Unit){
        val endPointURL = "/api/signin"
        val URLwithParams = server_url + endPointURL + "?email=$uname&pword=$pword"
        var return_result = null
        Fuel.get("$server_url$endPointURL", listOf("email" to "$uname", "pword" to "$pword"))
            .responseString { request, response, result ->
                println("Result GET: " + result)
                if (result != null) {
                    val json = result.get()
                    complete(JSONObject(json).get("message").toString())
                }
            }
    }

    fun registerEmployee(employeeDataClass: UsersDataClass, complete: (String) -> Unit){
        val endPointURL = "/api/addemployee"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        Fuel.get("$server_url$endPointURL", listOf("email" to "$email", "fullname" to "$fullname" , "tel" to "123123123", "pword" to "password!"))
            .responseString { request, response, result ->
                println("Result Register: " + result)
                if (result != null) {
                    val json = result.get()
//                    println(JSONObject(json).get("message"))
                    complete(JSONObject(json).get("message").toString())
                }
            }
    }

    fun updateEmployee(employeeDataClass: UsersDataClass, complete: (String) -> Unit){
        val endPointURL = "/api/updateemployee"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        val userId = employeeDataClass.userId
        Fuel.get("$server_url$endPointURL", listOf("id" to  "$userId", "email" to "$email", "fullname" to "$fullname" , "tel" to "123123123", "pword" to "password!"))
            .responseString { request, response, result ->
                println("Result Update: " + result)
                if (result != null) {
                    val json = result.get()
                    complete(JSONObject(json).get("message").toString())
                }
            }
    }

    fun deleteEmployee(employeeDataClass: UsersDataClass, complete: (String) -> Unit){
        val endPointURL = "/api/deleteemployee"
        val userId = employeeDataClass.userId
        Fuel.get("$server_url$endPointURL", listOf("id" to  "$userId"))
            .responseString { request, response, result ->
                println("Result Delete: " + result)
                if (result != null) {
                    val json = result.get()
                    complete(JSONObject(json).get("message").toString())
                }
            }
    }
}