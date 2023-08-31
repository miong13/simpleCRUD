package com.miongapps.simplecrud

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Base64
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView


class Dashboard : AppCompatActivity(), UsersAdapter.ClickListener {
    private lateinit var btnLogout : Button
    private lateinit var btnAddNew : Button
    var sharedPrefs : SharedPreferences? = null
    var PREFS_KEY = "pref"
    var LOGGED_IN = "loggedIn"
    var LOGGED_USERFN = "loggedUserFN"
    var LOGGED_USEREM = "loggedUserEM"
    var LOGGED_USERPH = "loggedUserPH"
    var LOGGED_USERID = "loggedUserID"
    var is_RecyclerLoaded = "loadedRecycler"
    var is_loggedIn : String? = null

    private lateinit var newRecycleView : RecyclerView
    private lateinit var newUserList : ArrayList<UsersDataClass>
    private lateinit var itemRecyclerAdapter : UsersAdapter
    lateinit var imageId : Array<Int>
    lateinit var txtHeading : Array<String>

    private lateinit var lblWelcome : TextView

    lateinit var toggle : ActionBarDrawerToggle
    private lateinit var lblUserFullName : TextView
    private lateinit var circleImgProfilePic : CircleImageView
    private lateinit var lblUserEmail : TextView
    private lateinit var btnReloadList : Button
    var userID : String? = null

    var recyclerViewState: Parcelable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        println("START DASHBOARD - CREATE")
        val drawerLayout : DrawerLayout = findViewById(R.id.dashboard_drawer)
        val navView : NavigationView =  findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPrefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        is_loggedIn = sharedPrefs!!.getString(LOGGED_IN, "").toString()
        if(is_loggedIn.equals("true")){
            getUserdata()
        }else{
            logMeOut()
//            val DashboardIntent = Intent(this, MainActivity::class.java)
//            startActivity(DashboardIntent)
//            finish()
        }
        lblWelcome = findViewById(R.id.lblWelcome)

        circleImgProfilePic = navView.getHeaderView(0).findViewById(R.id.circleImgProfilePic)
        lblUserFullName = navView.getHeaderView(0).findViewById(R.id.lblUserFullName)
        lblUserEmail = navView.getHeaderView(0).findViewById(R.id.lblUserEmail)

        btnAddNew = findViewById(R.id.btnAddNew)
        btnReloadList = findViewById(R.id.btnReloadList)
        btnLogout = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener{
              logMeOut()
        }

        btnReloadList.setOnClickListener {
            getUserdata()
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
            R.drawable.profile_5004140,
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


        lblWelcome.text = "WELCOME " + sharedPrefs!!.getString(LOGGED_USERFN, "").toString() + "!"
        lblUserFullName.text = sharedPrefs!!.getString(LOGGED_USERFN, "").toString()
        lblUserEmail.text = sharedPrefs!!.getString(LOGGED_USEREM, "").toString()
        var userPhoto  =  sharedPrefs!!.getString(LOGGED_USERPH, "").toString()
        userID = sharedPrefs!!.getString(LOGGED_USERID, "").toString()
        if(userPhoto.equals("null")){
            circleImgProfilePic.setImageResource(imageId[2])
        }else{
            val decodedString: ByteArray = Base64.decode(userPhoto, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            circleImgProfilePic.setImageBitmap(decodedByte)
        }

//        if(is_loggedIn.equals("true")){
//            //DO NOTHING
//            //getUserdata()
//        }else{
//            logMeOut()
//        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_userprofile -> {
//                    startActivity(Intent(this, EditProfileActivity::class.java))
                    editProfileData()
                }
                R.id.nav_logout -> {
                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@Dashboard)
                    alertDialog.setTitle("LOGOUT???")
                    alertDialog.setMessage("Are you sure you want to logout?")
                    alertDialog.setPositiveButton(
                        "yes"
                    ) { _, _ ->
                        // Log me out
                        logMeOut()
                    }
                    alertDialog.setNegativeButton(
                        "No"
                    ) { _, _ ->
                        // Nothing to do here
                    }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }
            }
            true
        }

    }

    override fun onStart() {
        super.onStart()
        println("START DASHBOARD - ON")
        println("START : " + is_loggedIn)
        if(is_loggedIn.equals("true")){
            println("START DASHBOARD - TRUE")
            println("START : " + is_loggedIn)
        }else{
            println("START DASHBOARD - FALSE")
            println("START : " + is_loggedIn)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
//    override fun onPause() {
//        super.onPause()
//        recyclerViewState = newRecycleView.layoutManager?.onSaveInstanceState()//save
//    }
//
//    override fun onResume() {
//        super.onResume()
//        newRecycleView.layoutManager?.onRestoreInstanceState(recyclerViewState)//restore
//    }

    private fun getUserdata(){
        Toast.makeText(this@Dashboard, "LOADING LIST...", Toast.LENGTH_SHORT).show()
        ApiRequest().getListEmployees { returnArray ->
            if(returnArray.length() > 0) {
                println(returnArray.length())
                for (i in 0..(returnArray.length()-1)) {
                    println("GET USER DATA")
                    val item = returnArray.getJSONObject(i)
                    val EmployeeID : String = item.get("user_id").toString()
                    val EmployeeName : String = item.get("full_name").toString()
                    val EmployeeEmail : String = item.get("email").toString()
                    val EmployeePhoto : String = item.get("photo").toString()
                    val users = UsersDataClass(imageId[1], EmployeeID, EmployeeName, EmployeeEmail, EmployeePhoto)
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
                                if(userID.equals(deleteItem.userId)){
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(this@Dashboard, "You cannot remove your own data!", Toast.LENGTH_LONG).show()
                                    }
                                    newUserList.removeAt(position)
                                    itemRecyclerAdapter.notifyItemRemoved(position)
                                    newUserList.add(position, deleteItem)
                                    itemRecyclerAdapter.notifyItemInserted(position)
                                }else {
                                    Toast.makeText(this@Dashboard, "Deleting...", Toast.LENGTH_SHORT).show()
                                    ApiRequest().deleteEmployee(deleteItem) { result ->
                                        if (result.equals("This record cannot be deleted!")) {
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(
                                                    this@Dashboard,
                                                    result,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            newUserList.removeAt(position)
                                            itemRecyclerAdapter.notifyItemRemoved(position)
                                            newUserList.add(position, deleteItem)
                                            itemRecyclerAdapter.notifyItemInserted(position)
                                        } else {
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(
                                                    this@Dashboard,
                                                    result,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            newUserList.removeAt(position)
                                            itemRecyclerAdapter.notifyItemRemoved(position)
                                        }


                                    }
                                }
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
        extras.putString("photo", currentItem.photo)
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
        extras.putString("photo", currentItem.photo)
        extras.putInt("position", position)
        intent.putExtras(extras)
        startActivity(intent)
    }

    private fun editProfileData(){
        val intent = Intent(this, EditProfileActivity::class.java)
        val extras = Bundle()
        extras.putInt("imageId", 0)
        extras.putString("userId", sharedPrefs!!.getString(LOGGED_USERID, "").toString())
        extras.putString("fullname", sharedPrefs!!.getString(LOGGED_USERFN, "").toString())
        extras.putString("email", sharedPrefs!!.getString(LOGGED_USEREM, "").toString())
        extras.putString("photo", sharedPrefs!!.getString(LOGGED_USERPH, "").toString())
        extras.putInt("position", 0)
        intent.putExtras(extras)
        startActivity(intent)
    }

    private fun loadDataRecycler() {
        var intent_extras = intent.extras
        if (intent.hasExtra("fullname")) {
            var employeeNameEdit = intent_extras!!.getString("fullname").toString()
            var employeeEmailEdit = intent_extras.getString("email").toString()
            var pos = intent_extras.getInt("position")
            var update_data = UsersDataClass(imageId[1], "", employeeNameEdit, employeeEmailEdit, "")

            newUserList.removeAt(pos)
            itemRecyclerAdapter.notifyItemRemoved(pos)
            newUserList.add(pos, update_data)
            itemRecyclerAdapter.notifyItemInserted(pos)

        }
    }

    private fun logMeOut(){
        println("START LOGOUT!")
//        val editor : SharedPreferences.Editor = sharedPrefs!!.edit()
//        editor.remove(LOGGED_IN).commit()
//        editor.clear().commit()
        //editor.apply()
        // REMOVING OR CLEARING PREFS DOESN'T WORK IN SOME POINT
        // WORKAROUND IS TO SET IT AS EMPTY
        val editor: SharedPreferences.Editor = sharedPrefs!!.edit()
        editor.putString(LOGGED_IN, "")
        editor.apply()
        println("START : " + is_loggedIn)

        var LogInIntent = Intent(this, MainActivity::class.java)
        startActivity(LogInIntent)
        finish()
        println("DONE LOGOUT!")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }




}
