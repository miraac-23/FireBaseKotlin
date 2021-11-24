package com.basarsoft.firebasekotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sifremi_unuttum.*


class SifremiUnuttumFragment : DialogFragment() {

    lateinit var emailEdittext: EditText
    lateinit var mContext: FragmentActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

       val view = inflater.inflate(R.layout.fragment_sifremi_unuttum, container, false)

        mContext = requireActivity()
        emailEdittext = view.findViewById(R.id.etEmailTekrarGonder)

        var btnSifreUnuttumIptal = view.findViewById<Button>(R.id.btnSifreUnuttumIptal)

        btnSifreUnuttumIptal.setOnClickListener {

            dialog?.dismiss()

        }

        var btnGonder = view.findViewById<Button>(R.id.btnSifreUnuttumGonder)

        btnGonder.setOnClickListener {

            if (etEmailTekrarGonder.text.toString().isNotEmpty()){
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailEdittext.text.toString())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){

                            Toast.makeText(mContext,"Şifre Yenileme Maili Gönderildi" , Toast.LENGTH_LONG).show()
                            dialog?.dismiss()

                        }else{

                            Toast.makeText(mContext,"Hta oluştu"+task.exception?.message , Toast.LENGTH_LONG).show()
                            dialog?.dismiss()
                        }
                    }

            }else{
                Toast.makeText(mContext,"Lütfen Boş Alanları Doldurunuz",Toast.LENGTH_LONG).show()
            }

        }

        return view
    }


}