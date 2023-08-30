package com.miongapps.simplecrud

import android.text.Editable
import com.github.kittinunf.fuel.Fuel
import org.json.JSONArray
import org.json.JSONObject

class ApiRequest {
    private val server_url : String = "https://simplecrud.markramosonline.com"

    fun getListEmployees(returnResult : (JSONArray) -> Unit){
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
                    returnResult(jsonObject)
                }
            }
    }

    fun loginEmployee(uname: Editable, pword: Editable, returnResult : (JSONObject) -> Unit){
        val endPointURL = "/api/signin"
        val URLwithParams = server_url + endPointURL + "?email=$uname&pword=$pword"
        var return_result = null
        Fuel.get("$server_url$endPointURL", listOf("email" to "$uname", "pword" to "$pword"))
            .responseString { request, response, result ->
                println("Result GET: " + result)
                if (result != null) {
                    val json = result.get()
                    val obj = JSONObject(json)
                    returnResult(obj.getJSONObject("message"))
                }
            }
    }

    fun registerEmployee(employeeDataClass: UsersDataClass, returnResult: (String) -> Unit){
        val endPointURL = "/api/addemployee"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        Fuel.get("$server_url$endPointURL", listOf("email" to "$email", "fullname" to "$fullname" , "tel" to "123123123", "pword" to "password!"))
            .responseString { request, response, result ->
                println("Result Register: " + result)
                if (result != null) {
                    val json = result.get()
//                    println(JSONObject(json).get("message"))
                    returnResult(JSONObject(json).get("message").toString())
                }
            }
    }

    fun updateEmployee(employeeDataClass: UsersDataClass, returnResult: (String) -> Unit){
        val endPointURL = "/api/updateemployee"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        val userId = employeeDataClass.userId
        Fuel.get("$server_url$endPointURL", listOf("id" to  "$userId", "email" to "$email", "fullname" to "$fullname" , "tel" to "123123123", "pword" to "password!"))
            .responseString { request, response, result ->
                println("Result Update: " + result)
                if (result != null) {
                    val json = result.get()
                    returnResult(JSONObject(json).get("message").toString())
                }
            }
    }

    fun updateEmployeeWithPhoto(employeeDataClass: UsersDataClass, password: String, returnResult: (String) -> Unit){
        val endPointURL = "/api/updateemployeewp"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        val userId = employeeDataClass.userId
        val photo = employeeDataClass.photo
        Fuel.post("$server_url$endPointURL", listOf("id" to  "$userId", "email" to "$email", "fullname" to "$fullname" , "photo" to "$photo", "pword" to "$password"))
            .responseString { request, response, result ->
                println("Result Update: " + result)
                if (result != null) {
                    val json = result.get()
                    returnResult(JSONObject(json).get("message").toString())
                }
            }
    }

    fun deleteEmployee(employeeDataClass: UsersDataClass, returnResult: (String) -> Unit){
        val endPointURL = "/api/deleteemployee"
        val userId = employeeDataClass.userId
        Fuel.get("$server_url$endPointURL", listOf("id" to  "$userId"))
            .responseString { request, response, result ->
                println("Result Delete: " + result)
                if (result != null) {
                    val json = result.get()
                    returnResult(JSONObject(json).get("message").toString())
                }
            }
    }

    fun registerEmployeeWithPhoto(employeeDataClass: UsersDataClass, password: String, returnResult: (String) -> Unit){
        val endPointURL = "/api/register"
        val email = employeeDataClass.email
        val fullname = employeeDataClass.fullname
        val base64string = employeeDataClass.photo
        val pword = password
        print("B64: $base64string")
        Fuel.post("$server_url$endPointURL", listOf("email" to "$email", "fullname" to "$fullname" , "photo" to "$base64string", "pword" to "$pword"))
            .responseString { request, response, result ->
                println("Result Register: " + result)
                if (result != null) {
                    val json = result.get()
//                    println(JSONObject(json).get("message"))
                    returnResult(JSONObject(json).get("message").toString())
                }
            }
    }
}