package com.example.expenseshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.transaction_history_item.view.*
import kotlinx.coroutines.currentCoroutineContext

class transactionHistoryAdapter(private val transactionList: MutableList<Transaction_Item>) : RecyclerView.Adapter<transactionHistoryAdapter.ExampleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_history_item,
        parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = transactionList[position]
        val date = String.format("%.2f on %s", currentItem.amountOfTransaction, currentItem.dateOfTransaction)

        holder.reason.text = currentItem.reasonForTransaction
        holder.from.text = currentItem.fromMember.name
        holder.to.text = currentItem.toMember.name
        holder.amount.text = date
        holder.delBtn.setOnClickListener() {
            transactionList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = transactionList.size

    class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val from: TextView = itemView.FromName
        val to: TextView = itemView.ToName
        val reason: TextView = itemView.ReasonText
        val amount: TextView = itemView.Amount
        val delBtn: Button = itemView.deleteButton
    }
}