package com.example.expenseshare

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable

class AddGroupActivity : AppCompatActivity() {

    private lateinit var memberRecyclerView: RecyclerView
    private lateinit var addedMembers: MutableList<Member_Item>
    private lateinit var name: String
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)
        memberRecyclerView = findViewById(R.id.addedGroupMembersRecyclerView)
        memberRecyclerView.setHasFixedSize(true)

        val intentToProfileActivity: Intent = getIntent()
        email = intentToProfileActivity.getStringExtra("email").toString()
        name = intentToProfileActivity.getStringExtra("name").toString()

        addedMembers = mutableListOf<Member_Item>()

        findViewById<Button>(R.id.backButton).setOnClickListener() {
            val intentToHomeActivity: Intent = Intent(this, HomeActivity::class.java)
            intentToHomeActivity.putExtra("name", name)
            intentToHomeActivity.putExtra("email", email)
            startActivity(intentToHomeActivity)
        }

        findViewById<Button>(R.id.addGroupMember).setOnClickListener(){
            addMember()
        }

        findViewById<Button>(R.id.createGroupButton).setOnClickListener(){
            createGroup()
        }

        Log.i(addedMembers.size.toString(), "The val of addedMembers.size is")
        memberRecyclerView.adapter = GroupMemberAdapter(addedMembers)

    }

    private fun createGroup() {
        Log.i("IN createGroup", "The val of new Group is")
        val groupName = findViewById<EditText>(R.id.groupNameEditText).getText().toString()
        val newTransactionList: MutableList<Transaction_Item> = mutableListOf<Transaction_Item>()
        val newGroup = Group_Item(groupName, addedMembers, newTransactionList)

        val intentWithNewGroupToHome = Intent(this, HomeActivity::class.java)
        intentWithNewGroupToHome.putExtra("newGroup", newGroup as Serializable)
        intentWithNewGroupToHome.putExtra("name", name)
        intentWithNewGroupToHome.putExtra("email", email)

        Log.i(addedMembers.size.toString(), "The val of addedMembers.size is")
        Log.i(newGroup.toString(), "The val of new Group is")
        Log.i(RESULT_OK.toString(), "The val of result_OK")
        setResult(RESULT_OK, intentWithNewGroupToHome)
        finish()
    }

    private fun addMember() {
        val nameEntered = findViewById<EditText>(R.id.memberNameEditText).getText().toString()
        val emailEntered = findViewById<EditText>(R.id.memberEmailEditText).getText().toString()
        if(nameEntered.isNullOrEmpty() || emailEntered.isNullOrEmpty()) {
            Toast.makeText(this, "Fill both the entries", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            addedMembers.add(Member_Item(nameEntered, emailEntered))
            Toast.makeText(this, "Added member", Toast.LENGTH_SHORT).show()
            Log.i(addedMembers.size.toString(), "The val of # members is")
            memberRecyclerView.adapter = GroupMemberAdapter(addedMembers)
            return
        }
    }
}