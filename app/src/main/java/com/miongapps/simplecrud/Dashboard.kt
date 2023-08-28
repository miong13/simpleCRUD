package com.miongapps.simplecrud

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.util.*
import kotlin.collections.ArrayList
import android.content.Intent as Intent

class Dashboard : AppCompatActivity(), UsersAdapter.ClickListener {
    private lateinit var btnLogout : Button
    private lateinit var btnAddNew : Button
    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var is_loggedIn = ""

    private lateinit var newRecycleView : RecyclerView
    private lateinit var newUserList : ArrayList<UsersDataClass>
    private lateinit var itemRecyclerAdapter : UsersAdapter
    lateinit var imageId : Array<Int>
    lateinit var txtHeading : Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        btnAddNew = findViewById(R.id.btnAddNew)
        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener{
//            MainActivity().sharedPrefs!!.edit().clear().apply()
            val editor : SharedPreferences.Editor = sharedPrefs!!.edit();
            editor.clear()
            editor.apply()

            var LogInIntent = Intent(this, MainActivity::class.java)
            startActivity(LogInIntent)
            finish()
        }

        btnAddNew.setOnClickListener {
            var AddUserIntent = Intent(this, AddUserActivity::class.java)
            startActivity(AddUserIntent)
//            finish()
        }

        // TEST DATA
        imageId = arrayOf(
            R.drawable.markramos2x2,
            R.drawable.login_4557368,
            R.drawable.login_4557368,
            R.drawable.login_4557368,
            R.drawable.login_4557368,
            R.drawable.login_4557368,
            R.drawable.login_4557368,
            R.drawable.login_4557368
        )

        // TEST DATA
        txtHeading = arrayOf(
            "Mark Lyndon Ramos1",
            "Mark Lyndon Ramos2",
            "Mark Lyndon Ramos3",
            "Mark Lyndon Ramos4",
            "Mark Lyndon Ramos5",
            "Mark Lyndon Ramos6",
            "Mark Lyndon Ramos7",
            "Mark Lyndon Ramos8"
        )

        // initiate recyclerview
        newRecycleView = findViewById(R.id.recyclerView)
        newRecycleView.layoutManager = LinearLayoutManager(this)
        newRecycleView.setHasFixedSize(true)

        newUserList = arrayListOf<UsersDataClass>()
        getUserdata()
//        loadDataRecycler()
    }

    private fun getUserdata(){
        Toast.makeText(this@Dashboard, "LOADING LIST...", Toast.LENGTH_SHORT).show()
        ApiRequest().getListEmployees { returnArray ->
            if(returnArray.length() > 0) {
                println(returnArray.length());
//                val len: Int = returnArray.length()
                for (i in 0..(returnArray.length()-1)) {
                    println("GET USER DATA")
                    val item = returnArray.getJSONObject(i)
//                    println(item)
                    val EmployeeID : String = item.get("user_id").toString()
                    val EmployeeName : String = item.get("full_name").toString()
                    val EmployeeEmail : String = item.get("email").toString()
//                    println(EmployeeName)
//                    println(imageId[0])
                    val users = UsersDataClass(imageId[1], EmployeeID, EmployeeName, EmployeeEmail)
                    newUserList.add(users)
                }
                println(newUserList)
                Handler(Looper.getMainLooper()).post {
                    itemRecyclerAdapter = UsersAdapter(newUserList, this)
//                    newRecycleView.adapter = UsersAdapter(newUserList)
                    newRecycleView.adapter = itemRecyclerAdapter
                    swipeToGesture(newRecycleView)
                }
            }else{
                MainActivity().ToastAlert("Nothing to load here!")
            }
        }
//        for (i in imageId.indices){
//            val users = UsersDataClass(imageId[1], txtHeading[i])
//            newUserList.add(users)
//        }
//        println("NEW USERLIST")
//        println(newUserList)
//        newRecycleView.adapter = UsersAdapter(newUserList)
    }

    private fun swipeToGesture(newRecyclerView: RecyclerView?){
        val swipeGesture=object :SwipeGesture(this){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                var actionBtnTapped = false

                try{
                    when(direction){
                        ItemTouchHelper.LEFT->{
                            val deleteItem = newUserList[position]

                            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@Dashboard)
                            alertDialog.setTitle("DELETE???")
                            alertDialog.setMessage("Are you sure you want to delete?")
                            alertDialog.setPositiveButton(
                                "yes"
                            ) { _, _ ->
                                Toast.makeText(this@Dashboard, "Deleting...", Toast.LENGTH_SHORT).show()
                                ApiRequest().deleteEmployee(deleteItem){
                                    result ->

                                    if(result.equals("This record cannot be deleted!")){
                                        Handler(Looper.getMainLooper()).post {
                                            Toast.makeText(this@Dashboard, result, Toast.LENGTH_LONG).show()
                                        }
                                        newUserList.removeAt(position)
                                        itemRecyclerAdapter.notifyItemRemoved(position)
                                        newUserList.add(position, deleteItem)
                                        itemRecyclerAdapter.notifyItemInserted(position)
                                    }else{
                                        Handler(Looper.getMainLooper()).post {
                                            Toast.makeText(this@Dashboard, result, Toast.LENGTH_LONG).show()
                                        }
                                        newUserList.removeAt(position)
                                        itemRecyclerAdapter.notifyItemRemoved(position)
                                    }
                                }


//                                val snackbar=Snackbar.make(this@Dashboard.newRecycleView, "Item deleted!", Snackbar.LENGTH_LONG)
//                                    .addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
//                                        override fun onDismissed(
//                                            transientBottomBar: Snackbar?,
//                                            event: Int
//                                        ) {
//                                            super.onDismissed(transientBottomBar, event)
//                                        }
//
//                                        override fun onShown(transientBottomBar: Snackbar?) {
//                                            transientBottomBar?.setAction("UNDO"){
//                                                newUserList.add(position, deleteItem)
////                                            UsersAdapter(newUserList).notifyItemInserted(position)
//                                                itemRecyclerAdapter.notifyItemInserted(position)
//                                                actionBtnTapped=true
//                                            }
//                                            super.onShown(transientBottomBar)
//                                        }
//
//                                    }).apply {
//                                        animationMode= Snackbar.ANIMATION_MODE_FADE
//                                    }
//                                snackbar.setActionTextColor(
//                                    ContextCompat.getColor(
//                                        this@Dashboard,
//                                        R.color.orangeRed
//                                    )
//                                )
//
//                                snackbar.show()
                            }
                            alertDialog.setNegativeButton(
                                "No"
                            ) { _, _ ->
//                                val deleteItem = newUserList[position]
                                newUserList.removeAt(position)
                                itemRecyclerAdapter.notifyItemRemoved(position)
                                newUserList.add(position, deleteItem)
                                itemRecyclerAdapter.notifyItemInserted(position)
//                                actionBtnTapped=true
                            }
                            val alert: AlertDialog = alertDialog.create()
                            alert.setCanceledOnTouchOutside(false)
                            alert.show()
                        }

                        ItemTouchHelper.RIGHT->{
                            //EDIT INTERFACE HERE

                            val editItem = newUserList[position]
                            newUserList.removeAt(position)
                            itemRecyclerAdapter.notifyItemRemoved(position)
                            newUserList.add(position, editItem)
                            itemRecyclerAdapter.notifyItemInserted(position)
                            actionBtnTapped=true
                            editData(editItem, position)

//                            val snackbar=Snackbar.make(this@Dashboard.newRecycleView, "Edit Item!", Snackbar.LENGTH_LONG)
//                                .addCallback(object :BaseTransientBottomBar.BaseCallback<Snackbar>(){
//                                    override fun onDismissed(
//                                        transientBottomBar: Snackbar?,
//                                        event: Int
//                                    ) {
//                                        super.onDismissed(transientBottomBar, event)
//                                    }
//
//                                    override fun onShown(transientBottomBar: Snackbar?) {
//                                        transientBottomBar?.setAction("UNDO"){
//                                            newUserList.add(position, editItem)
//                                            itemRecyclerAdapter.notifyItemInserted(position)
//                                            actionBtnTapped=true
//                                        }
//                                        super.onShown(transientBottomBar)
//                                    }
//
//                                }).apply {
//                                    animationMode= Snackbar.ANIMATION_MODE_FADE
//                                }
//                            snackbar.setActionTextColor(
//                                ContextCompat.getColor(
//                                    this@Dashboard,
//                                    R.color.orangeRed
//                                )
//                            )
//
//                            snackbar.show()
                        }
                    }
                }catch (e:Exception){
                    Toast.makeText(this@Dashboard, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        val touchHelper=ItemTouchHelper(swipeGesture)

        touchHelper.attachToRecyclerView(newRecyclerView)
    }

    override fun clickedItem(currentItem: UsersDataClass, position: Int) {
        val intent = Intent(this, SelectedUserActivity::class.java)
        val extras = Bundle()
        extras.putInt("imageId", currentItem.titleImage)
        extras.putString("userId", currentItem.userId)
        extras.putString("fullname", currentItem.fullname)
        extras.putString("email", currentItem.email)
        extras.putInt("position", position)
        intent.putExtras(extras)
        startActivity(intent)
    }

    private fun editData(currentItem: UsersDataClass, position: Int){
        val intent = Intent(this, EditUserActivity::class.java)
        val extras = Bundle()
        extras.putInt("imageId", currentItem.titleImage)
        extras.putString("userId", currentItem.userId)
        extras.putString("fullname", currentItem.fullname)
        extras.putString("email", currentItem.email)
        extras.putInt("position", position)
        intent.putExtras(extras)
        startActivity(intent)
    }

    private fun loadDataRecycler() {
        var intent_extras = intent.extras
        if (intent.hasExtra("fullname")) {
            var employeeNameEdit = intent_extras!!.getString("fullname").toString()
            var employeeEmailEdit = intent_extras!!.getString("email").toString()
            var pos = intent_extras!!.getInt("position")
            var update_data = UsersDataClass(imageId[1], "", employeeNameEdit, employeeEmailEdit)

            newUserList.removeAt(pos)
            itemRecyclerAdapter.notifyItemRemoved(pos)
            newUserList.add(pos, update_data)
            itemRecyclerAdapter.notifyItemInserted(pos)

        }
    }

}
