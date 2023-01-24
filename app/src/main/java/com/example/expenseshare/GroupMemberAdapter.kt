package com.example.expenseshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupMemberAdapter(private val memberList: MutableList<Member_Item>):
    RecyclerView.Adapter<GroupMemberAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.added_member, parent,
        false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentMember = memberList[position]
        holder.name.text = currentMember.name
        holder.email.text = currentMember.email
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.nameOfMember)
        val email: TextView = itemView.findViewById(R.id.emailOfMember)
    }
}