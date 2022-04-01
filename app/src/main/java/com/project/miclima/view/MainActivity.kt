package com.project.miclima.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.project.miclima.R
import com.project.miclima.viewmodel.LocationViewModel
import com.project.miclima.viewmodel.NetworkConnectionViewModel
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var tvConexion : TextView
    private lateinit var layout_f : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvConexion = findViewById(R.id.tvConexion)
        layout_f = findViewById(R.id.linear_layout_base)

        checkPermisos()

        val networkConnection = NetworkConnectionViewModel(applicationContext)
        networkConnection.observe(this, Observer {
            if (it) {
                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show()
                tvConexion.visibility = View.INVISIBLE
            } else {
                Toast.makeText(this, "Sin Conexión", Toast.LENGTH_SHORT).show()
                tvConexion.visibility = View.VISIBLE
            }
        })

        val transaccion = supportFragmentManager.beginTransaction()
        val fragment = PrincipalFragment()
        transaccion.replace(R.id.container_main, fragment)
        //transaccion.addToBackStack(null)
        transaccion.commit()

        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //localizacion()
    }

    override fun onResume() {
        super.onResume()
        localizacion()
    }

    private fun localizacion(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocalizationPermission()
        }

        try {
            val data = LocationViewModel.getLocation(this)
        }catch (e:Exception){
            Toast.makeText(this, "Necesita activar la ubicación y aceptar los permisos", Toast.LENGTH_LONG).show()
        }

    }

    private fun checkPermisos() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocalizationPermission()
        }
    }

    private fun requestLocalizationPermission() {
        ActivityCompat.requestPermissions(
            this,
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
                requestLocalizationPermission()
            }
        }
    }
}