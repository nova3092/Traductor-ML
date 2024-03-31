package com.manolovargas.traductor_ml

import android.app.Dialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.manolovargas.traductor_ml.Modelo.Idioma
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var  Et_Idioma_Origen: EditText
    private lateinit var  Tv_Idioma_Destino: TextView
    private lateinit var  Btn_Elegir_Idioma: MaterialButton
    private lateinit var  Btn_Idioma_Elegido: MaterialButton
    private lateinit var  Btn_Traducir: MaterialButton


    private var  IdiomaArrayList: ArrayList<Idioma>? = null

    companion object {
        private const val REGISTRO = "Mis_registros"
    }

private var codigo_idioma_origen= "es"
private var titulo_idioma_origen= "Español"

private var codigo_idioma_destino= "en"
private var titulo_idioma_destino= "Ingles"

    private lateinit var translateOptions: TranslatorOptions
    private  lateinit var translator: Translator
    private lateinit var progressDialog: ProgressDialog

    private var tex_idioma_origen= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InicializarVistas()
        IdiomasDisponibles()

        Btn_Elegir_Idioma.setOnClickListener {
            //Toast.makeText(this, "Idioma elegido", Toast.LENGTH_SHORT).show()

            ElegirIdiomaOrigen()
        }

        Btn_Idioma_Elegido.setOnClickListener {
            //Toast.makeText(this, "Idioma elegido", Toast.LENGTH_SHORT).show()

            ElegirIdiomaDestino()
        }

        Btn_Traducir.setOnClickListener {
            //Toast.makeText(this, "Traducido", Toast.LENGTH_SHORT).show()

            ValidarDatos()
        }
    }


    private fun InicializarVistas(){

        Et_Idioma_Origen = findViewById(R.id.Et_Idioma_Origen)
        Tv_Idioma_Destino = findViewById(R.id.Tv_Idioma_Destino)
        Btn_Elegir_Idioma = findViewById(R.id.Btn_Elegir_Idioma)
        Btn_Idioma_Elegido = findViewById(R.id.Btn_Idioma_Elegido)
        Btn_Traducir = findViewById(R.id.Btn_Traducir)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setMessage("Traduciendo...")
        progressDialog.setCancelable(false)

    }

    private fun IdiomasDisponibles(){

        IdiomaArrayList=ArrayList<Idioma>()

        val ListaCodigoIdioma= TranslateLanguage.getAllLanguages()

        //es-español

        for (codigo_lenguaje in ListaCodigoIdioma){

            val titulo_lenguaje= Locale(codigo_lenguaje).displayLanguage

           // Log.d(REGISTRO, "IdiomasDisponibles: codigo_lenguaje $titulo_lenguaje")
            //Log.d(REGISTRO, "IdiomasDisponibles: codigo_lenguaje $codigo_lenguaje")




            val modeloIdioma= Idioma(codigo_lenguaje,titulo_lenguaje)

            IdiomaArrayList!!.add(modeloIdioma)
        }

    }

    private fun ElegirIdiomaOrigen() {

        val popupMenu = PopupMenu(this, Btn_Elegir_Idioma)

        for (i in IdiomaArrayList!!.indices) {
            popupMenu.menu.add(Menu.NONE, i, i, IdiomaArrayList!![i].titulo_idioma)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener{ menuItem ->
            val position = menuItem.itemId

            codigo_idioma_origen= IdiomaArrayList!![position].codigo_idioma
            titulo_idioma_origen= IdiomaArrayList!![position].titulo_idioma

            Btn_Elegir_Idioma.text = titulo_idioma_origen
            Et_Idioma_Origen.hint = " Ingrese texto en $titulo_idioma_origen "

            Log.d(REGISTRO, "ElegirIdiomaOrigen: codigo_idioma_origen $codigo_idioma_origen")
            Log.d(REGISTRO, "ElegirIdiomaOrigen: titulo_idioma_origen $titulo_idioma_origen")


            false
        }

    }

    private fun ElegirIdiomaDestino() {

        val popupMenu = PopupMenu(this, Btn_Idioma_Elegido)

        for (i in IdiomaArrayList!!.indices) {
            popupMenu.menu.add(Menu.NONE, i, i, IdiomaArrayList!![i].titulo_idioma)
        }

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val position = menuItem.itemId

            codigo_idioma_destino = IdiomaArrayList!![position].codigo_idioma
            titulo_idioma_destino = IdiomaArrayList!![position].titulo_idioma

            Btn_Idioma_Elegido.text = titulo_idioma_destino


            Log.d(REGISTRO, "ElegirIdiomaDestino: codigo_idioma_destino $codigo_idioma_destino")
            Log.d(REGISTRO, "ElegirIdiomaDestino : titulo_idioma_destino $titulo_idioma_destino")


            false
        }

    }

    private fun ValidarDatos() {
        tex_idioma_origen = Et_Idioma_Origen.text.toString().trim()

        if (tex_idioma_origen.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese texto", Toast.LENGTH_SHORT).show()
        } else {
            TraducirTexto()
        }
    }

    private fun TraducirTexto() {

        progressDialog.show()
        progressDialog.setMessage("Procesando")

        translateOptions = TranslatorOptions.Builder()
            .setSourceLanguage(codigo_idioma_origen)
            .setTargetLanguage(codigo_idioma_destino)
            .build()

        translator = Translation.getClient(translateOptions)

        val downloadCondition = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downloadCondition)
            .addOnSuccessListener {
                Log.d(REGISTRO, "El paquete de traduccion  se descargo existosamente")
                progressDialog.setMessage("Traduciendo")

                translator.translate(tex_idioma_origen)

                    .addOnSuccessListener { texto_traducido ->

                        Log.d(REGISTRO, "texto_traducido $texto_traducido")
                        progressDialog.dismiss()
                        Tv_Idioma_Destino.text = texto_traducido


                    }
            }

            .addOnFailureListener { e ->

                Toast.makeText(applicationContext, "$e", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }


    }

    private fun MostraDialogo() {


        val Btn_entendido: Button
        val dialog = Dialog(this@MainActivity)

        dialog.setContentView(R.layout.custom_dialog)
        Btn_entendido = dialog.findViewById(R.id.Btn_entendido)

        Btn_entendido.setOnClickListener {

            dialog.dismiss()
        }


        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

    }


    //Creamos el menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.mi_menu, menu)
        return true
    }


    //Seleccioamos un elemento del mundo
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Menu_Limpiar_texto -> {

                Et_Idioma_Origen.setText("")
                Et_Idioma_Origen.hint = " Ingrese texto "
                Tv_Idioma_Destino.text = "Traduccion"


                true
            }
            R.id.Menu_Informacion -> {
                MostraDialogo()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
