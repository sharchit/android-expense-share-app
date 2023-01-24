package com.example.expenseshare

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type

class HomeActivity : AppCompatActivity(), GroupAdapter.OnGroupClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var groupRecyclerView: RecyclerView
    private lateinit var groupsCreated: MutableList<Group_Item>
    private lateinit var getGroup_Item: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("The val in", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loadData()

        groupRecyclerView = findViewById(R.id.addedGroupsRecyclerView)
        groupRecyclerView.setHasFixedSize(true)
//        auth = FirebaseAuth.getInstance()
        var email: String = "JohnDoe@mail.com"
        var name: String = "John doe"


        if(savedInstanceState == null) {
            email = intent.getStringExtra("email").toString()
            name = intent.getStringExtra("name").toString()
            Log.i(email, "The val at saved Inst")
            Log.i(name, "The val at saved Inst")
        }
        Log.i(email, "The val at start")
        Log.i(name, "The val at start")

        Log.i(groupsCreated.size.toString(), "The val of groupsCreated.size is")
        groupRecyclerView.adapter = GroupAdapter(groupsCreated, this)

        getGroup_Item = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            Log.i("The val in getGroupItem", result.resultCode.toString())
            if(result.resultCode == RESULT_OK){
                Log.i("The val in RESULT_OK", "aa")
                Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show()

                val newGroup: Group_Item = result.data?.getSerializableExtra("newGroup") as Group_Item
                Log.i("The val of new Group", newGroup.toString())
                groupsCreated.add(newGroup)

                Log.i("The val of Created:", groupsCreated.size.toString())
                groupRecyclerView.adapter = GroupAdapter(groupsCreated, this)
            }
            if(result.resultCode == RESULT_CANCELED){
                Log.i("The val in RESULT_CAN", "bb")
                Toast.makeText(this, "Group error", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.addGroupButton).setOnClickListener()
        {
            val intentToAddGroup: Intent = Intent(this, AddGroupActivity::class.java)
            intentToAddGroup.putExtra("email", email.toString())
            intentToAddGroup.putExtra("name", name)
            getGroup_Item.launch(intentToAddGroup)
        }
        findViewById<Button>(R.id.profileButton).setOnClickListener()
        {
            Log.i(name, "The val of name")
            Log.i(email, "The val of email")
            val intentToProfileActivity: Intent = Intent(this, ProfileActivity::class.java)
            intentToProfileActivity.putExtra("email", email.toString())
            intentToProfileActivity.putExtra("name", name.toString())
            startActivity(intentToProfileActivity)
        }
        findViewById<Button>(R.id.aboutButton).setOnClickListener()
        {
            val intentToAboutActivity: Intent = Intent(this, AboutActivity::class.java)
            intentToAboutActivity.putExtra("email", email.toString())
            intentToAboutActivity.putExtra("name", name.toString())
            startActivity(intentToAboutActivity)
        }
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    private fun saveData() {
        var sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        var gson: Gson = Gson()
        var json = gson.toJson(groupsCreated)
        editor.putString("group list", json)
        editor.apply()
    }

    private fun loadData() {
        var sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        var gson: Gson = Gson()
        var json = sharedPreferences.getString("group list", mutableListOf<Group_Item>().toString())
        val type = object : TypeToken<MutableList<Group_Item>>() {}.type
        groupsCreated = Gson().fromJson(json, type)

        if(groupsCreated.isEmpty()) {
            groupsCreated = mutableListOf<Group_Item>()
        }
    }
    override fun onGroupClick(position: Int) {
        Toast.makeText(this, "Item $position clicked", Toast.LENGTH_SHORT).show()
        val intentToGroupActivity = Intent(this, GroupActivity::class.java)
        intentToGroupActivity.putExtra("position", position.toString())
        intentToGroupActivity.putExtra("groupsCreated", groupsCreated as Serializable)

        startActivity(intentToGroupActivity)
    }
}