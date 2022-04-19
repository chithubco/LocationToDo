package com.echithub.locationtodo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.echithub.locationtodo.ListFragmentDirections
import com.echithub.locationtodo.R
import com.echithub.locationtodo.data.model.Reminder
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil

class ReminderListAdapter(val reminderList: ArrayList<Reminder>):RecyclerView.Adapter<ReminderListAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

    }

//    private val diffUtil = object : DiffUtil.ItemCallback<Reminder>(){
//        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
//            return oldItem == newItem
//        }
//
//        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    private val recyclerListDiffer = AsyncListDiffer(this,diffUtil)
//
//    var reminders: List<Reminder>
//    get() = recyclerListDiffer.currentList
//    set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_row, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = reminderList[position]
        holder.itemView.findViewById<TextView>(R.id.tv_title).text = currentItem.title
        holder.itemView.findViewById<TextView>(R.id.tv_description).text = currentItem.description
        holder.itemView.findViewById<TextView>(R.id.tv_lat_long).text = "Lat : ${currentItem.latitude} , Long : ${currentItem.longitude}"

        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToDetailFragment(currentItem)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    fun setData(reminders: List<Reminder>){
        reminderList.clear()
        reminderList.addAll(reminders)
        notifyDataSetChanged()
    }
}