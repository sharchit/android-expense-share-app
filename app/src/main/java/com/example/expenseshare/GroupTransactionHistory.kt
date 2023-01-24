package com.example.expenseshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.io.Serializable

class GroupTransactionHistory : AppCompatActivity() {
    var position: Int = 0
    private lateinit var groups: MutableList<Group_Item>
    private lateinit var transactions: MutableList<Transaction_Item>
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var getTransaction_Item: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_transaction_history)

        val intentFromGroup = getIntent()
        position = intentFromGroup.getStringExtra("position").toString().toInt()
        groups = intentFromGroup.getSerializableExtra("groups") as MutableList<Group_Item>

        transactions = groups[position].transactionList
        Log.i("Data from intent", transactions.toString())
        Log.i("Data from intent", position.toString())
        Log.i("Data from intent", groups.toString())

        transactionRecyclerView = findViewById(R.id.TransactionsRecyclerView)
        transactionRecyclerView.adapter =transactionHistoryAdapter(transactions)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this)
        transactionRecyclerView.setHasFixedSize(true)

        findViewById<Button>(R.id.backButton).setOnClickListener() {
            val intentToGroup = Intent(this, GroupActivity::class.java)
            intentToGroup.putExtra("position", position.toString())
            intentToGroup.putExtra("groupsCreated", groups as Serializable)
            saveData()
            startActivity(intentToGroup)
        }
    }
    private fun saveData() {
        var sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        var gson: Gson = Gson()
        var json = gson.toJson(groups)
        editor.putString("group list", json)
        editor.apply()
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }
}