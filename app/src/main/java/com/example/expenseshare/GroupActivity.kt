package com.example.expenseshare

import android.app.DatePickerDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.View
import android.widget.*
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_group.view.*
import java.io.Serializable
import java.lang.Integer.min
import java.lang.reflect.Member
import java.util.*
import javax.xml.datatype.DatatypeConstants.MONTHS
import kotlin.collections.ArrayList

class GroupActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var PayingMembersChipgroup: ChipGroup
    private lateinit var PaidMembersChipgroup: ChipGroup
    private lateinit var transactionRecyclerView: RecyclerView
    private lateinit var transactionDetailList: ArrayList<TransactionDetail>
    var emailContent = ""
    var position: Int = 0
    private lateinit var groups: MutableList<Group_Item>
    var date: String = ""
    val whoPaidList = mutableListOf<Member_Item>()
    val paidForList = mutableListOf<Member_Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)

        val intentFromHome = getIntent()
        position = intentFromHome.getStringExtra("position").toString().toInt()
        groups = intentFromHome.getSerializableExtra("groupsCreated") as MutableList<Group_Item>

        minimumTransactions()

        findViewById<Button>(R.id.backButton).setOnClickListener()
        {
            val intentToHomeActivity: Intent = Intent(this, HomeActivity::class.java)
            startActivity(intentToHomeActivity)
        }
        findViewById<TextView>(R.id.groupNameText).text = groups[position].groupName

        findViewById<Button>(R.id.transactionHistoryButton).setOnClickListener() {
            val intentToGroupTransactionHistory = Intent(this, GroupTransactionHistory::class.java)
            intentToGroupTransactionHistory.putExtra("position", position.toString())
            intentToGroupTransactionHistory.putExtra("groups", groups as Serializable)
            Log.i("PositionfromIntent", position.toString())
            Log.i("GroupfromIntent", groups.toString())
            startActivity(intentToGroupTransactionHistory)
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener() {
            groups.removeAt(position.toString().toInt())
            Toast.makeText(this, "Deleted, " + groups.size + " groups left.", Toast.LENGTH_SHORT).show()
            val intentToHomeActivity: Intent = Intent(this, HomeActivity::class.java)
            saveData()
            startActivity(intentToHomeActivity)
        }

        findViewById<Button>(R.id.sendMailBtn).setOnClickListener() {
            sendEmailsToMembers()
        }
        val membersInGroup = groups[position].memberList
        var membersText: String = ""
        val maxMembers = min(membersInGroup.size - 1, 9)
        for(i in 0..maxMembers){
            membersText += membersInGroup[i].name
            if(i != maxMembers)
                membersText += ", "
        }
        if(membersInGroup.size > 9) membersText += "..."
        Log.i("The val of memTxt", membersText)

        findViewById<TextView>(R.id.memberNameText).text = membersText
        PayingMembersChipgroup = findViewById<ChipGroup>(R.id.PayingMembersChipgroup)
        PaidMembersChipgroup = findViewById<ChipGroup>(R.id.PaidMembersChipgroup)

        for(member in membersInGroup)
            addToWhoPaidChip(member)
        for(member in membersInGroup)
            addToForWhoChip(member)

        findViewById<Button>(R.id.selectDateButton).setOnClickListener(){
            val c = Calendar.getInstance()
            DatePickerDialog(this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
        }
        findViewById<Button>(R.id.doneAddingButton).setOnClickListener(){
            doneAdding()
        }
        findViewById<Button>(R.id.addTransactionButton).setOnClickListener() {
            performTransaction()
        }
        transactionRecyclerView = findViewById(R.id.transactionDetailsRecyclerView)
        transactionRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        transactionRecyclerView.setHasFixedSize(true)

        transactionDetailList = arrayListOf<TransactionDetail>()
    }

    private fun sendEmailsToMembers() {
        var emailArrayList = Array<String>(groups[position].memberList.size){""}

        var current = 0
        for(member in groups[position].memberList) {
            emailArrayList[current] = member.email
            current ++
        }
        val Subject = "Expense Share: Settle Expense for %s group".format(groups[position].groupName)
        val intentToSendMail = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, emailArrayList)
            putExtra(Intent.EXTRA_SUBJECT, Subject)
            putExtra(Intent.EXTRA_TEXT, emailContent)
        }
        Log.i("Email Content", emailContent)
        Log.i("Email Address", emailArrayList.toString())
        if(intentToSendMail.resolveActivity(packageManager) != null) {
            startActivity(intentToSendMail)
        }
        else{
            Toast.makeText(this, "No Email App Found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performTransaction() {
        val totalAmount: Double = findViewById<EditText>(R.id.transactionAmountEditText).text.toString().toDouble()
        val reasonOfTransaction = findViewById<EditText>(R.id.reasonEditText).text.toString()
        val dateOfTransaction = date

        if(totalAmount.isNaN() || reasonOfTransaction.isNullOrBlank() || dateOfTransaction.isNullOrBlank()) {
            Toast.makeText(this, "One of the field is left empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if(!findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.ByRatioOrAmountSwitch).isChecked()) {
            val creditorList = mutableListOf<Member_Item>()
            var sum: Double = 0.0
            for(trans in transactionDetailList)
                sum += trans.amount
            if(sum != totalAmount) {
                Toast.makeText(this, "Numbers don't add up!", Toast.LENGTH_SHORT).show()
                return
            }
            for(x in transactionDetailList) creditorList.add(x.member)

            for(trans in transactionDetailList) {
                val mem = trans.member
                val owedAmount = trans.amount / paidForList.size
                for(paidFor in paidForList) {
                    if(mem != paidFor) {
                        val newTransaction = Transaction_Item(mem, paidFor, owedAmount, date, reasonOfTransaction)
                        groups[position].transactionList.add(newTransaction)
                    }
                }
            }
        }
        else {
            val creditorList = mutableListOf<Member_Item>()
            var sum: Double = 0.0
            for(trans in transactionDetailList)
                sum += trans.amount
            for(x in transactionDetailList) creditorList.add(x.member)

            for(trans in transactionDetailList) {
                val mem = trans.member
                val owedAmount = (trans.amount * totalAmount / sum)/paidForList.size
                for(paidFor in paidForList) {
                    if(mem != paidFor) {
                        val newTransaction = Transaction_Item(mem, paidFor, owedAmount, date, reasonOfTransaction)
                        groups[position].transactionList.add(newTransaction)
                    }
                }
            }
        }
        Log.i("Transaction_List: ", groups[position].transactionList.toString())
        findViewById<EditText>(R.id.transactionAmountEditText).text.clear()
        findViewById<EditText>(R.id.reasonEditText).text.clear()
        findViewById<androidx.cardview.widget.CardView>(R.id.fillTransactionDetailsCard).setVisibility(
            View.GONE)
        findViewById<ChipGroup>(R.id.PayingMembersChipgroup).clearCheck()
        findViewById<ChipGroup>(R.id.PaidMembersChipgroup).clearCheck()
        minimumTransactions()
    }

    private fun minimumTransactions() {
        var group = groups[position]
        var matrix = Array(group.memberList.size) {Array<Double>(group.memberList.size) {0.0} }
        var minDebtList = mutableListOf<minDebtClass>()
        for(transaction in group.transactionList) {
            val fromIndex = group.memberList.indexOf(transaction.fromMember)
            val toIndex = group.memberList.indexOf(transaction.toMember)
            if(fromIndex < toIndex)
                matrix[toIndex][fromIndex] += transaction.amountOfTransaction
            else
                matrix[fromIndex][toIndex] -= transaction.amountOfTransaction
        }


        var i = 0
        while(i < matrix.size) {
            var j = 0;
            while(j < matrix.size) {
                if(matrix[i][j] > 0) {
                    var x = minDebtClass(group.memberList[i], group.memberList[j], matrix[i][j])
                    emailContent += "\n%s owes %s %.2f\n".format(group.memberList[i].name, group.memberList[j].name, matrix[i][j])
                    minDebtList.add(x)
                }
                else if(matrix[i][j] < 0) {
                    var x = minDebtClass(group.memberList[i], group.memberList[j], -matrix[i][j])
                    emailContent += "\n%s owes %s %.2f\n".format(group.memberList[i].name, group.memberList[j].name, matrix[i][j])
                    minDebtList.add(x)
                }
                j++
            }
            i ++
        }
        val debtRecycler = findViewById<RecyclerView>(R.id.minimizedDebt)
        debtRecycler.adapter = minDebtAdapter(minDebtList)
        debtRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        debtRecycler.setHasFixedSize(true)
    }

    private fun doneAdding() {
        transactionDetailList.clear()
        whoPaidList.clear()
        paidForList.clear()
        findViewById<androidx.cardview.widget.CardView>(R.id.fillTransactionDetailsCard).setVisibility(
            View.VISIBLE)
        val listOfPayersId = PayingMembersChipgroup.getCheckedChipIds()
        val listOfPaidId = PaidMembersChipgroup.getCheckedChipIds()
        for(i in listOfPayersId) {
            val chip = findViewById<Chip>(i)
            val member = searchByName(chip.text.toString())
            val temp = TransactionDetail(member, member.name, 200.0)
            transactionDetailList.add(temp)
            whoPaidList.add(member)
        }
        for(i in listOfPaidId) {
            val chip = findViewById<Chip>(i)
            val member = searchByName(chip.text.toString())
            paidForList.add(member)
        }
        transactionRecyclerView.adapter = TransactionDetailAdapter(transactionDetailList)
    }

    private fun searchByName(name: String): Member_Item {
        for(member in groups[position].memberList) {
            if(member.name == name)
                return member
        }
        return groups[position].memberList[0]
    }


    private fun addToForWhoChip(member: Member_Item) {
        val chip = Chip(this)
        chip.text = member.name
        chip.setCheckable(true)
        PaidMembersChipgroup.addView(chip)
    }

    private fun addToWhoPaidChip(member: Member_Item) {
        val chip = Chip(this)
        chip.text = member.name
        chip.setCheckable(true)
        PayingMembersChipgroup.addView(chip)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        date = p3.toString() + "/" + p2.toString() + "/" + p1.toString()
        findViewById<Button>(R.id.selectDateButton).text = date
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