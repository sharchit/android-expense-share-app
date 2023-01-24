package com.example.expenseshare

import java.io.Serializable

data class Group_Item(var groupName: String, val memberList: MutableList<Member_Item>,
                      val transactionList: MutableList<Transaction_Item>):Serializable {
    var TotalTransaction: Double = 0.0

    fun getTotalTransaction(){
        for(transaction in transactionList)
            TotalTransaction += transaction.amountOfTransaction
    }
}
