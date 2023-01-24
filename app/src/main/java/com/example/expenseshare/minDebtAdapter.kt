package com.example.expenseshare

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.minimum_debt.view.*

class minDebtAdapter(private val debtList: MutableList<minDebtClass>): RecyclerView.Adapter<minDebtAdapter.ExampleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.minimum_debt,
        parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = debtList[position]
        holder.fromText.text = currentItem.fromMember_Item.name
        holder.toText.text = currentItem.toMember_Item.name
        holder.amount.text = String.format("%.2f", currentItem.Debt)
    }

    override fun getItemCount(): Int = debtList.size

    class ExampleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val fromText = itemView.fromText
        val toText = itemView.toText
        val amount = itemView.amount
    }
}