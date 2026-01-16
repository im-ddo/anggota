package com.nim2411500037.anggota

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// DATA CLASS ANGGOTA
data class AnggotaItem(
    val idAnggota: Int,
    val namaLengkap: String,
    val alamat: String,
    val fotoProfil: String?
)

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var listAdapter: AnggotaAdapter
    private lateinit var searchEditText: EditText

    private val allItems = ArrayList<AnggotaItem>()
    private var filteredItems = ArrayList<AnggotaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)
        searchEditText = findViewById(R.id.searchEditText)

        listAdapter = AnggotaAdapter(this, filteredItems)
        listView.adapter = listAdapter

        // SEARCH FILTER
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchAnggotaFromAPI()
    }

    // AMBIL DATA ANGGOTA
    private fun fetchAnggotaFromAPI() {
        Thread {
            try {
                val url = URL("http://192.168.1.18/uts_pdcs/api/anggota.php")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connect()

                val responseBody =
                    conn.inputStream.bufferedReader().use { it.readText() }

                val json = JSONObject(responseBody)
                val dataArray = json.getJSONArray("data")

                val tempList = ArrayList<AnggotaItem>()

                for (i in 0 until dataArray.length()) {
                    val o = dataArray.getJSONObject(i)

                    tempList.add(
                        AnggotaItem(
                            idAnggota = o.getInt("id_anggota"),
                            namaLengkap = o.getString("nama_lengkap"),
                            alamat = o.getString("alamat"),
                            fotoProfil = if (o.isNull("foto_profil"))
                                null
                            else
                                o.getString("foto_profil")
                        )
                    )
                }

                runOnUiThread {
                    allItems.clear()
                    allItems.addAll(tempList)

                    filteredItems.clear()
                    filteredItems.addAll(tempList)

                    listAdapter.updateList(filteredItems)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // FILTER SEARCH
    private fun filterList(query: String) {
        val filteredList = if (query.isEmpty()) {
            allItems
        } else {
            allItems.filter {
                it.namaLengkap.contains(query, true) ||
                        it.alamat.contains(query, true)
            }
        }

        filteredItems = ArrayList(filteredList)
        listAdapter.updateList(filteredItems)
    }
}
