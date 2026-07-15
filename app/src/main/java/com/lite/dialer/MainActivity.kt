package com.lite.dialer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView

/**
 * تطبيق اتصال خفيف جداً.
 * لا مكتبات خارجية، لا RecyclerView، لا ViewModel، لا Compose.
 * كل الحالة بالذاكرة فقط لتفادي أي حمل إضافي على أجهزة ضعيفة.
 */
class MainActivity : Activity() {

    private var currentNumber = StringBuilder()
    private val callLog = mutableListOf<String>()

    private lateinit var numberText: TextView
    private lateinit var hintText: TextView
    private lateinit var delBtn: ImageButton
    private lateinit var displayArea: View
    private lateinit var keypad: View
    private lateinit var actionRow: View
    private lateinit var listView: ListView

    private lateinit var tabContacts: TextView
    private lateinit var tabLog: TextView
    private lateinit var tabKeypad: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberText = findViewById(R.id.numberText)
        hintText = findViewById(R.id.hintText)
        delBtn = findViewById(R.id.delBtn)
        displayArea = findViewById(R.id.displayArea)
        keypad = findViewById(R.id.keypad)
        actionRow = findViewById(R.id.actionRow)
        listView = findViewById(R.id.listView)

        tabContacts = findViewById(R.id.tabContacts)
        tabLog = findViewById(R.id.tabLog)
        tabKeypad = findViewById(R.id.tabKeypad)

        setupKeys()
        setupActions()
        setupTabs()
        renderNumber()
    }

    private fun setupKeys() {
        val keyIds = intArrayOf(
            R.id.key1, R.id.key2, R.id.key3,
            R.id.key4, R.id.key5, R.id.key6,
            R.id.key7, R.id.key8, R.id.key9,
            R.id.keyStar, R.id.key0, R.id.keyHash
        )
        for (id in keyIds) {
            val view = findViewById<TextView>(id)
            view.setOnClickListener {
                currentNumber.append(view.tag.toString())
                renderNumber()
            }
        }
    }

    private fun setupActions() {
        findViewById<ImageButton>(R.id.callBtn).setOnClickListener {
            val number = currentNumber.toString()
            if (number.isEmpty()) return@setOnClickListener
            callLog.add(0, number)
            // ACTION_DIAL يفتح تطبيق الاتصال الرسمي بالرقم جاهز، بدون أي إذن مطلوب
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            startActivity(intent)
            currentNumber.clear()
            renderNumber()
        }

        delBtn.setOnClickListener {
            if (currentNumber.isNotEmpty()) {
                currentNumber.deleteCharAt(currentNumber.length - 1)
                renderNumber()
            }
        }
        delBtn.setOnLongClickListener {
            currentNumber.clear()
            renderNumber()
            true
        }
    }

    private fun setupTabs() {
        tabContacts.setOnClickListener { showList(isLog = false) }
        tabLog.setOnClickListener { showList(isLog = true) }
        tabKeypad.setOnClickListener { showKeypad() }
    }

    private fun renderNumber() {
        numberText.text = currentNumber.toString()
        val hasNumber = currentNumber.isNotEmpty()
        hintText.visibility = if (hasNumber) View.INVISIBLE else View.VISIBLE
        delBtn.visibility = if (hasNumber) View.VISIBLE else View.INVISIBLE
    }

    private fun showKeypad() {
        setActiveTab(tabKeypad)
        listView.visibility = View.GONE
        displayArea.visibility = View.VISIBLE
        keypad.visibility = View.VISIBLE
        actionRow.visibility = View.VISIBLE
    }

    private fun showList(isLog: Boolean) {
        setActiveTab(if (isLog) tabLog else tabContacts)
        displayArea.visibility = View.GONE
        keypad.visibility = View.GONE
        actionRow.visibility = View.GONE
        listView.visibility = View.VISIBLE

        val data = if (isLog) callLog else emptyList()
        val emptyLabel = if (isLog)
            getString(R.string.empty_log) else getString(R.string.empty_contacts)

        val items = if (data.isEmpty()) listOf(emptyLabel) else data
        // Adapter نظامي بسيط بدل RecyclerView — أخف بكثير لقوائم قصيرة
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            items
        )
    }

    private fun setActiveTab(active: TextView) {
        for (tab in listOf(tabContacts, tabLog, tabKeypad)) {
            tab.setTextColor(
                if (tab === active) getColor(R.color.accent) else getColor(R.color.text_dim)
            )
        }
    }
}
