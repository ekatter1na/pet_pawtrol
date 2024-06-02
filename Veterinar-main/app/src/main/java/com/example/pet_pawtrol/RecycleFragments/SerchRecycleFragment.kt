package com.example.pet_pawtrol.RecycleFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pet_pawtrol.Entity.Veterinars
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.MainDb
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.adapters.SearchAdapter
import com.example.pet_pawtrol.adapters.SearchModel
import com.example.pet_pawtrol.databinding.FragmentSerchRecycleBinding
import com.example.pet_pawtrol.listSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import kotlin.random.Random

class SerchRecycleFragment : Fragment() {

    private lateinit var binding: FragmentSerchRecycleBinding
    private lateinit var adapter: SearchAdapter
    var list = arrayListOf<SearchModel>()
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSerchRecycleBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        Proverca()
        lifecycleScope.launchWhenStarted {
            init()
        }
    }

    suspend fun init() = coroutineScope {
            initRcView()
    }

    private fun Proverca(){
        val database = MainDb.getDb(MAIN)
        val rowCount = database.getDao().countTableRowsVeterinars()
        rowCount.observeForever { count ->
            if (count == 0) {
                lifecycleScope.launchWhenStarted {
                    setData()
                }
                return@observeForever
            }
        }
    }
    private fun getData(): LiveData<List<SearchModel>>{
        try{
        val database = MainDb.getDb(MAIN)
        val listVet = MutableLiveData<List<SearchModel>>()
        val query = database.getDao().getAllVeterinar()
        query.asLiveData().observe(viewLifecycleOwner){vetlist->
            val vetirList = ArrayList<SearchModel>()
            vetlist.forEach{ veterinars ->
                val vet = SearchModel(
                    veterinars.name,
                    veterinars.email,
                    veterinars.comment,
                    veterinars.specialization,
                    veterinars.price,
                    veterinars.urlProfile
                )
                vetirList.add(vet)
            }
            listVet.value = vetirList
        }
        return listVet
        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
            return TODO("Provide the return value")
        }
    }

    private suspend fun setData() = withContext(Dispatchers.IO) {
        try {
            val database = MainDb.getDb(MAIN)
            val url = "https://zoon.ru/penza/p-veterinar/"
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document = Jsoup.parse(response.body?.string())

                val containers = document.select("div[class=js-results-item]")
                for (container in containers) {
                    val name = container.select("div[class=specialist]")
                        .select("div[class=specialist-top-info]")
                        .select("a[class=prof-name specialist-name js-specialist-card-link js-item-url js-link]")
                        .text()
                    val email = finalGenerateEmail(name)
                    val comment = container.select("div[class=specialist]")
                        .select("div[class=specialist-photo-container]")
                        .select("div[class=specialist-mark]").select("div[class=specialist-mark]")
                        .select("span[class=stars-rating-text strong]").text()
                    val specialization = container.select("div[class=specialist]")
                        .select("div[class=specialist-top-info]")
                        .select("div[class=prof-spec-list specialist-spec-list]").select("a").text()
                    val price = generatePrice().toString()
                    val urlProfile = container.select("div[class=specialist]")
                        .select("div[class=specialist-top-info]")
                        .select("a[class=prof-name specialist-name js-specialist-card-link js-item-url js-link]")
                        .attr("href")

                    if (comment.isNotEmpty()) {
                        val commentWithoutComma = comment.replace(",", ".")
                        val commentFloat = commentWithoutComma.trim().toFloat()

                        val vet = Veterinars(
                            null,
                            name,
                            email,
                            commentFloat,
                            specialization,
                            price.toInt(),
                            urlProfile
                        )

                        database.getDao().insertVeterinar(vet)
                    } else {
                        val vet = Veterinars(
                            null,
                            name,
                            email,
                            1.0F,
                            specialization,
                            price.toInt(),
                            urlProfile
                        )
                    }
                }
            }
        }
        catch (e: Exception) {
            Toast.makeText(
                MAIN,
                "${e}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun generatePrice(): Int {
        val minValue = 300
        val maxValue = 1500
        val step = 200

        val count = (maxValue - minValue) / step + 1
        val randomIndex = Random.nextInt(count)
        val randomNumber = minValue + randomIndex * step

        return randomNumber
    }

    fun initRcView(){
        binding.rcSerch.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
        getData().observe(viewLifecycleOwner){ vlist ->
            listSearch = vlist as ArrayList<SearchModel>
            adapter.submitList(vlist)
            binding.rcSerch.adapter = adapter
        }
    }

    fun sortByPriceAscending(){
        recyclerView = MAIN.findViewById(R.id.rcSerch)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
            val sortedList = listSearch.sortedBy { it.price }
            adapter.submitList(sortedList)
            recyclerView.adapter = adapter
    }

    fun sortByPriceDescending(){
        recyclerView = MAIN.findViewById(R.id.rcSerch)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
            val sortedList = listSearch.sortedByDescending { it.price }
            adapter.submitList(sortedList)
            recyclerView.adapter = adapter
    }

    fun sortByRatingAscending(){
        recyclerView = MAIN.findViewById(R.id.rcSerch)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
            val sortedList = listSearch.sortedBy { it.otz }
            adapter.submitList(sortedList)
            recyclerView.adapter = adapter
    }


    fun sortByRatingDescending(){
        recyclerView = MAIN.findViewById(R.id.rcSerch)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
            val sortedList = listSearch.sortedByDescending { it.otz }
            adapter.submitList(sortedList)
            recyclerView.adapter = adapter
    }

    fun cancelSort(){
        recyclerView = MAIN.findViewById(R.id.rcSerch)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = SearchAdapter()
        adapter.submitList(listSearch)
        recyclerView.adapter = adapter
    }

    fun generateEmail(firstName: String, domain: String = "mail.ru"): String {
        val sanitizedFirstName = firstName.lowercase().replace(" ", "")
        return "$sanitizedFirstName@$domain"
    }

    fun finalGenerateEmail(name: String): String {
        val email = generateEmail(cyrillicToTranslit(name))
        return email
    }

    fun cyrillicToTranslit(text: String): String {
        val translitMap = mapOf(
            'а' to 'a', 'б' to 'b', 'в' to 'v', 'г' to 'g', 'д' to 'd',
            'е' to 'e', 'ё' to 'e', 'ж' to 'g', 'з' to 'z', 'и' to 'i',
            'й' to 'y', 'к' to 'k', 'л' to 'l', 'м' to 'm', 'н' to 'n',
            'о' to 'o', 'п' to 'p', 'р' to 'r', 'с' to 's', 'т' to 't',
            'у' to 'u', 'ф' to 'f', 'х' to 'h', 'ц' to 'c', 'ч' to 'h',
            'ш' to 'h', 'щ' to 'h', 'ъ' to ' ', 'ы' to 'y', 'ь' to ' ',
            'э' to 'e', 'ю' to 'y', 'я' to 'i'
        )

        return text.map { char ->
            translitMap[char.lowercaseChar()]?.let { translitChar ->
                if (char.isUpperCase()) translitChar.uppercase() else translitChar
            } ?: char
        }.joinToString("")
    }

    companion object {
        @JvmStatic
        fun newInstance() = SerchRecycleFragment()
    }
}