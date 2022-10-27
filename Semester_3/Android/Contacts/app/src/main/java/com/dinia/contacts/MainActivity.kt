package com.dinia.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Contact(val name: String, val phoneNumber: String)

class ContactViewHolder(root: View) : RecyclerView.ViewHolder(root) {
    val item : LinearLayout = root.findViewById(R.id.item)
    private val nameView: TextView = root.findViewById(R.id.name)
    private val phoneView : TextView = root.findViewById(R.id.phone)

    fun bind(contact: Contact) {
        nameView.text = contact.name
        phoneView.text = contact.phoneNumber
    }
}

class ContactAdapter(
    private val contacts: List<Contact>,
    private val onClick: (Contact) -> Unit
): RecyclerView.Adapter<ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item, parent, false
            )
        )

        holder.item.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }

        return holder
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) = holder.bind(contacts[position])

    override fun getItemCount(): Int = contacts.size
}

@SuppressLint("Range")
fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        null, null, null, null)
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME)) ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.NUMBER)) ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}

class MainActivity : AppCompatActivity() {
    private lateinit var myRecyclerView : RecyclerView
    private val viewManager = LinearLayoutManager(this)

    companion object {
        const val READ_CONTACT_REQUEST = 0
    }

    private fun setLayout(contactList : List<Contact>) {
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = ContactAdapter(contactList) {
                val sendIntent = Intent(Intent.ACTION_DIAL,
                    Uri.parse("tel:${it.phoneNumber}"))
                startActivity(sendIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myRecyclerView = findViewById(R.id.myRecyclerView)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED) {
            setLayout(fetchAllContacts())
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                READ_CONTACT_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setLayout(fetchAllContacts())
        } else {
            setLayout(emptyList())
        }
    }
}
