package com.basarsoft.firebasekotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment


class ProfilResmiFragment : DialogFragment() {


    lateinit var tvGaleridenSec : TextView
    lateinit var tvKameradanSec : TextView

    interface onProfilResimListener {

        fun getResimYolu(resimPath : Uri?)
        fun getResimBitmap(bitmap: Bitmap)

    }

    lateinit var myProfilResimListener : onProfilResimListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment


        var view = inflater.inflate(R.layout.fragment_profil_resmi, container, false)

        tvGaleridenSec = view.findViewById(R.id.tvPpFragmentGaleridenSec)
        tvKameradanSec = view.findViewById(R.id.tvPpFragmentYeniFoto)


        tvGaleridenSec.setOnClickListener{

            var intent =  Intent(Intent.ACTION_GET_CONTENT)

            intent.type = "image/*"

            startActivityForResult(intent , 100)

        }

        tvKameradanSec.setOnClickListener {

            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent , 200)

        }


        return view
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//galeriden resim seçiliyor
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null){

            var galeridenSecilenResimYolu = data.data
            myProfilResimListener.getResimYolu(galeridenSecilenResimYolu)
            dismiss()

        }
        //kameradan resim çekiliyor
        else if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {

            var kameradanCekilenResim : Bitmap
            kameradanCekilenResim = data.extras?.get("data") as Bitmap
            myProfilResimListener.getResimBitmap(kameradanCekilenResim)
            dismiss()


        }



    }


    override fun onAttach(context: Context) {

        myProfilResimListener = activity as onProfilResimListener

        super.onAttach(context)
    }

}