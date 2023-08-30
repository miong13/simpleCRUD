package com.miongapps.simplecrud

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView


class UsersAdapter(private val userList : ArrayList<UsersDataClass>, clickListener: ClickListener) :
    RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var clickListener : ClickListener = clickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]
        if(currentItem.photo.equals("null")){
            println("Empty Photo!")
            holder.titleImage.setImageResource(currentItem.titleImage)
        }else{
            val decodedString: ByteArray = Base64.decode(currentItem.photo, Base64.DEFAULT)
            println(currentItem.photo);
            println(decodedString)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            println(decodedString.size)
            println(decodedByte);
            holder.titleImage.setImageBitmap(decodedByte)
        }

        holder.txtUserID.text = currentItem.userId
        holder.txtHeading.text = currentItem.fullname
        holder.txtEmail.text = currentItem.email

        holder.itemView.setOnClickListener{
//            Toast.makeText(this, "CLICK!", Toast.LENGTH_LONG)
            println("CLICK! $currentItem" )
            clickListener.clickedItem(currentItem, position)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    interface ClickListener {
        fun clickedItem(currentItem: UsersDataClass, position: Int)
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val titleImage : ShapeableImageView = itemView.findViewById(R.id.title_image)
//        val titleImage : ImageView = itemView.findViewById(R.id.title_image)
        val txtHeading : TextView = itemView.findViewById(R.id.tvHeading)
        val txtEmail : TextView = itemView.findViewById(R.id.tvEmail)
        val txtUserID : TextView = itemView.findViewById(R.id.tvUserId)
    }

}


