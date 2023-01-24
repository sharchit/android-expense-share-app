package com.example.expenseshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

    class GroupAdapter(private val groupList: MutableList<Group_Item>, private val listener: OnGroupClickListener):
        RecyclerView.Adapter<GroupAdapter.MyViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.added_group, parent,
                false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val currentGroup = groupList[position]
            holder.name.text = currentGroup.groupName.toString()
            currentGroup.getTotalTransaction()
            holder.total.text = String.format("%.2f", currentGroup.TotalTransaction)
        }

        override fun getItemCount(): Int {
            return groupList.size
        }

        inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{

            val name: TextView = itemView.findViewById(R.id.nameOfGroup)
            val total: TextView = itemView.findViewById(R.id.totalTransaction)

            init{
                itemView.setOnClickListener(this)
            }
            override fun onClick(p0: View?) {
                val position :Int = adapterPosition
                if(position != RecyclerView.NO_POSITION)
                    listener.onGroupClick(position)
            }
        }
        interface OnGroupClickListener{
            fun onGroupClick(position: Int, )
        }
    }
