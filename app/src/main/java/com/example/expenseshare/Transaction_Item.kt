package com.example.expenseshare


import java.io.Serializable
import java.util.*

data class Transaction_Item(val fromMember:Member_Item, val toMember: Member_Item,
val amountOfTransaction: Double, val dateOfTransaction: String, val reasonForTransaction: String):Serializable
{

}
