package com.example.expenseshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TransactionDetailAdapter(private val transactionList: ArrayList<TransactionDetail>) :
    RecyclerView.Adapter
    <TransactionDetailAdapter.MyViewHolder>() {


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameField = itemView.findViewById<TextView>(R.id.nameOfMember)
        val enteredAmount = itemView.findViewById<EditText>(R.id.expenseAmount)
        val okButton = itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.OKbutton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_detail_item,
        parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = transactionList[position]
        holder.nameField.text = currentItem.nameOfMember
        holder.okButton.setOnClickListener() {
            val enteredAmount = holder.enteredAmount.text.toString().toDouble()
            if(!enteredAmount.isNaN()) currentItem.amount = enteredAmount
        }
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }


}