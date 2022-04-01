package com.project.miclima.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.project.miclima.R
import com.project.miclima.model.InfoWeather
import com.project.miclima.service.WeatherService
import com.project.miclima.viewmodel.LocationViewModel
import com.project.miclima.viewmodel.RestEngine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrincipalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrincipalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var v: View? = null
    private lateinit var tvDireccion : TextView
    private lateinit var tvFecha : TextView
    private lateinit var tvEstado : TextView
    private lateinit var tvMin : TextView
    private lateinit var tvMax : TextView
    private lateinit var tvActual : TextView
    private lateinit var tvSensacion : TextView
    private lateinit var layout_f : LinearLayout
    private lateinit var imageView: ImageView

    private lateinit var btnIdioma: Button

    private lateinit var p: ProgressBar

    private val sdf = SimpleDateFormat("dd MMM yyyy HH:mm")
    private var idioma = "es"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_principal, container, false)

        tvDireccion = v!!.findViewById(R.id.tvDireccion)
        tvFecha = v!!.findViewById(R.id.tvFecha)
        tvEstado = v!!.findViewById(R.id.tvEstado)
        tvMin = v!!.findViewById(R.id.tvMin)
        tvMax = v!!.findViewById(R.id.tvMax)
        tvActual = v!!.findViewById(R.id.tvActual)
        tvSensacion = v!!.findViewById(R.id.tvSensacion)
        layout_f = v!!.findViewById(R.id.layout_f_principal)
        imageView = v!!.findViewById(R.id.imageView)

        val fecha: Date = java.util.Date()
        val fecha_text = sdf.format(fecha)
        val hora = fecha_text.substring(13, 15).toInt()

        if(hora in 7..18){
            imageView.setImageResource(R.drawable.sol)

        }else{
            imageView.setImageResource(R.drawable.luna)
        }

        btnIdioma = v!!.findViewById(R.id.btnIdioma)
        btnIdioma.setOnClickListener(View.OnClickListener {
            if(idioma == "es"){
                btnIdioma.text = "IDIOMA ACTUAl: INGLÉS"
                idioma = "en"
            }else{
                btnIdioma.text = "IDIOMA ACTUAl: ESPAÑOL"
                idioma = "es"
            }
            iniciarServicio()

        })

        iniciarServicio()

        return v
    }

    private fun callService(lat: Double, lon: Double, lang: String){
        val API_KEY = "48dd9c49e7be1fbc29082cc4195ef3a0"
        try {
            p = v!!.findViewById(R.id.progressBar1)
            p.visibility = View.VISIBLE

            val weatherService: WeatherService =
                RestEngine.getRestEngine().create(WeatherService::class.java)
            val callback: Call<InfoWeather> =
                weatherService.getWeather(lat, lon, API_KEY, lang, "metric")
            callback.enqueue(object : Callback<InfoWeather> {

                override fun onFailure(call: Call<InfoWeather>, t: Throwable) {
                    p.visibility = View.INVISIBLE
                }
                override fun onResponse(
                    call: Call<InfoWeather>,
                    response: Response<InfoWeather>
                ) {
                    inicializar(response.body())
                    p.visibility = View.INVISIBLE
                }
            }
            )

        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            p.visibility = View.INVISIBLE
        }
    }

    private fun inicializar(infoWeather: InfoWeather?){
        if(infoWeather != null){
            tvDireccion.text = infoWeather.name
            val fecha: Date = java.util.Date()
            val fecha_text = sdf.format(fecha)
            tvFecha.text =  fecha_text
            tvEstado.text = infoWeather.weather[0].description
            tvMin.text = "Min:" + infoWeather.main.temp_min.toString() + "°"
            tvMax.text ="Max: " + infoWeather.main.temp_max.toString() + "°"
            tvActual.text = infoWeather.main.temp.toString() + "°"
            tvSensacion.text = "Sensación: " + infoWeather.main.feels_like.toString() + "°"

            val hora = fecha_text.substring(13, 15).toInt()
            if(hora in 7..18){
                imageView.setImageResource(R.drawable.sol)

            }else{
                imageView.setImageResource(R.drawable.luna)
            }
        }

    }

    fun iniciarServicio(){

        val context: Context = v!!.context
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocalizationPermission()
        }

        val data = ArrayList<Double>()
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callService(location.latitude, location.longitude, idioma)
            }
        }
    }

    private fun requestLocalizationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity().parent,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1000
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                iniciarServicio()
            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PrincipalFragment.
         */
// TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrincipalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}