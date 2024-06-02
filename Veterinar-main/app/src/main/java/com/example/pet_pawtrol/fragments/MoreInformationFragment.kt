package com.example.pet_pawtrol.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.pet_pawtrol.MAIN
import com.example.pet_pawtrol.R
import com.example.pet_pawtrol.databinding.FragmentMoreInformationBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException


class MoreInformationFragment : Fragment() {

    private lateinit var binding: FragmentMoreInformationBinding
    lateinit var bundle: Bundle

    init {
        bundle = Bundle()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoreInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val urlProfile = arguments?.getString("urlProfile", "https://zoon.ru/penza/p-veterinar/ekaterina_dmitrievna_nikogosova/")
        lifecycleScope.launchWhenStarted {
            if (urlProfile != null) {
                setData(urlProfile)
            }
        }
        binding.BackImBut.setOnClickListener {
            MAIN.navController.navigate(R.id.action_moreInformationFragment_to_searchFragment)
        }
        binding.zapBut.setOnClickListener{
            bundle.putString("veterinarName", binding.tvName.text.toString())
            Navigation.findNavController(it).navigate(R.id.action_moreInformationFragment_to_makeAnAppointmentFragment, bundle)
        }
    }
    @SuppressLint("SetTextI18n")
    private suspend fun setData(urlProfile: String) = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(urlProfile)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val document =
                    Jsoup.parse(response.body?.string()).select("div[class=prof-page-wrapper]")

                val name = document.select("div[class=prof-page__header-wrapper]")
                    .select("div[class=prof-page__header]")
                    .select("div[class=prof-about prof-page__header-info]")
                    .select("div[class=z-flex z-flex--center-y z-gap--24]").select("h1").text()
                val stag =
                    document.select("ul[class=prof-page__header-props fs-simple]").select("li")
                        .text()
                val phNumber = document.select("div[class=prof-page__header-wrapper]")
                    .select("div[class=prof-page__header]").select("a").text()
                val osenca = "Оценка: ${
                    document.select("div[class=prof-page__header-rate]")
                        .select("div[class=prof-page__header-rating]").text()
                }"
                val information =
                    document.select("div[class=service-page bg-gray js-phone-holder pd-m clearfix]")
                        .select("div[class=ss-container flexbox]").select("div[class=oh]")
                        .select("div[class=service-box-white service-block]")
                        .select("div[class=service-page-info]")
                        .select("div[class=service-description-box b0 vtop]")
                        .select("div[class=box-padding]")
                        .select("div[class=params-list params-list-default]")
                        .select("dl[class=fluid]").text()
                val price = "Цена услуг: 1000p"
                val urlImgVet = document.select("div[class=prof-page__header-wrapper]")
                    .select("div[class=prof-page__header]")
                    .select("div[class=prof-page__header-photo z-placeholder z-placeholder--60 z-skeleton]")
                    .select("img").attr("data-original")

                val clinicName =
                    document.select("div[class=service-page bg-gray js-phone-holder pd-m clearfix]")
                        .select("div[class=ss-container flexbox]").select("div[class=oh]")
                        .select("div[class=service-box-white service-block oh]")
                        .select("ul[class=list-reset service-items-medium service-items-medium-hovered btop js-results-container]")
                        .select("li[class=minicard-item js-results-item  ]")
                        .select("div[class=minicard-item__container]")
                        .select("div[class=minicard-item__info]")
                        .select("div[class=minicard-item__info]").select("h2").text()
                val clinicGrade = document.select("div[class=z-text--bold]").text()
                val clinicAdress =
                    document.select("div[class=service-page bg-gray js-phone-holder pd-m clearfix]")
                        .select("div[class=ss-container flexbox]").select("div[class=oh]")
                        .select("div[class=service-box-white service-block oh]")
                        .select("ul[class=list-reset service-items-medium service-items-medium-hovered btop js-results-container]")
                        .select("li[class=minicard-item js-results-item  ]")
                        .select("div[class=minicard-item__container]")
                        .select("div[class=minicard-item__info]")
                        .select("div[class=minicard-item__info]")
                        .select("address[class=minicard-item__address]").text()
                val clinicImg =
                    document.select("div[class=service-page bg-gray js-phone-holder pd-m clearfix]")
                        .select("div[class=ss-container flexbox]").select("div[class=oh]")
                        .select("div[class=service-box-white service-block oh]")
                        .select("ul[class=list-reset service-items-medium service-items-medium-hovered btop js-results-container]")
                        .select("li[class=minicard-item js-results-item  ]")
                        .select("div[class=minicard-item__container]")
                        .select("div[class=minicard-item__photo]")
                        .select("a[class=photo-wrapper js-item-url js-minicard-photo]")
                        .select("div[class=slider-block js-slider-block]").attr("data-photos")

                withContext(Dispatchers.Main) {
                    if (urlImgVet != "") {
                        Picasso.get().load(urlImgVet).into(binding.PhotoImgView)
                    }
                    binding.tvName.text = name
                    binding.tvStag.text = stag
                    binding.tvOtz.text = osenca
                    //binding.tvPhoneNumber.text = phNumber
                    binding.tvInformation.text = information
                    binding.tvPrice.text = price

                    if (clinicName == "") {
                        binding.workLocation.isVisible = false
                        binding.tvClinick.isVisible = false
                        binding.tvClinickComment.isVisible = false
                        binding.tvAdresss.isVisible = false
                        binding.ClinicImg.isVisible = false
                    } else {
                        if (clinicImg != "") {
                            val newclinicImg = clinicImg.substringBefore(',').replace("""["""", "")
                                .replace(""""""", "")
                            Picasso.get().load(newclinicImg).into(binding.ClinicImg)
                        }
                        binding.tvClinick.text = clinicName
                        binding.tvClinickComment.text = "Оценка: ${clinicGrade}"
                        binding.tvAdresss.text = "Адрес: г.Пенза ${clinicAdress}"
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

    companion object {

        @JvmStatic
        fun newInstance() = MoreInformationFragment()
    }
}