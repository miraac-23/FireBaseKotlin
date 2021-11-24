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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class OnayMailTekrarGonderFragment : DialogFragment() {

    lateinit var emailEdittext: EditText
    lateinit var sifreEdittext: EditText
    lateinit var mContext: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_diaolg, container, false)


        emailEdittext = view.findViewById(R.id.etDialogMail)
        sifreEdittext = view.findViewById(R.id.etDialogSifre)
        mContext = requireActivity()

        var btnIptal = view.findViewById<Button>(R.id.btnDialogIptal)

        btnIptal.setOnClickListener {

            dialog?.dismiss()

        }

        var btnGonder = view.findViewById<Button>(R.id.btnDialogGonder)

        btnGonder.setOnClickListener {

            if (emailEdittext.text.toString().isNotEmpty() && sifreEdittext.text.toString()
                    .isNotEmpty()
            ) {

                girisYapVeOnayMailiniTekrarGonder(
                    emailEdittext.text.toString(),
                    sifreEdittext.text.toString()
                )

            } else {
                Toast.makeText(mContext, "Verilen Boşlukları Doldurunuz", Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    private fun girisYapVeOnayMailiniTekrarGonder(email: String, sifre: String) {


        var credential = EmailAuthProvider.getCredential(email, sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    onayMailiniTekrarGonder()
                    dialog?.dismiss()

                } else {

                    Toast.makeText(mContext, "Girilen Mail veya Sifre Hatalı", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun onayMailiniTekrarGonder() {

        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici != null) {

            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {

                        if (p0.isSuccessful) {
                            Toast.makeText(
                                mContext,
                                "Mail Kutunuzu Kontrol Ediniz , Gelen Maile Onay Veriniz ...",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                mContext,
                                "Mail Gönderilirken Hata Oluştu " + p0.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                })
        }
    }

}