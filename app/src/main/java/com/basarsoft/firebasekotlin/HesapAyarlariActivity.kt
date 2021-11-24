package com.basarsoft.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*
import kotlinx.android.synthetic.main.fragment_sifremi_unuttum.*

class HesapAyarlariActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        etHesapAyarlariKullaniciAdi.setText(kullanici.displayName.toString())
        // etHesapAyarlariSifre.setText(kullanici.email.toString())

        btnSifreGonder.setOnClickListener {
            if (etEmailTekrarGonder.text.toString().isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici.email.toString())
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            Toast.makeText(
                                this@HesapAyarlariActivity,
                                "Şifre Yenileme Maili Gönderildi",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            Toast.makeText(
                                this@HesapAyarlariActivity,
                                "Hta oluştu" + task.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

            } else {
                Toast.makeText(
                    this@HesapAyarlariActivity,
                    "Lütfen Boş Alanları Doldurunuz",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnDegisiklikleriKaydet.setOnClickListener {


            if (etHesapAyarlariKullaniciAdi.text.toString()
                    .isNotEmpty()
            ) {

                if (!etHesapAyarlariKullaniciAdi.text.toString()
                        .equals(kullanici.displayName.toString())
                ) {

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etHesapAyarlariKullaniciAdi.text.toString())
                        .build()
                    kullanici.updateProfile(bilgileriGuncelle)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@HesapAyarlariActivity,
                                    "Değişiklikler Yapıldı",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }

            } else {
                Toast.makeText(
                    this@HesapAyarlariActivity,
                    "Boş Alanları Doldurunuz ",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        btnSifreveyaMailGuncelle.setOnClickListener {
            if (etHesapAyarlariSifre.text.toString().isNotEmpty()) {

                var credential = EmailAuthProvider.getCredential(
                    kullanici.email.toString(),
                    etHesapAyarlariSifre.text.toString()
                )
                kullanici.reauthenticate(credential)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            guncelleLayout.visibility = View.VISIBLE

                            btnHesapAyarlariMailiGuncelle.setOnClickListener {

                                mailAdresiniGuncelle()
                            }

                            btnHesapAyarlariSifreyiGuncelle.setOnClickListener {

                                sifreBilgisiniGuncelle()
                            }

                        } else {

                            Toast.makeText(
                                this@HesapAyarlariActivity,
                                "Şuanki Şifrenizi Yanlış Girdiniz",
                                Toast.LENGTH_LONG
                            ).show()
                            guncelleLayout.visibility = View.INVISIBLE


                        }

                    }

            } else {
                Toast.makeText(
                    this@HesapAyarlariActivity,
                    "Güncellemeler İçin Gerekli Şifrenizi Giriniz",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun sifreBilgisiniGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if (kullanici != null) {

            kullanici.updatePassword(etHesapAyarlariSifreGuncelleme.text.toString())
                .addOnCompleteListener { task ->
                    Toast.makeText(
                        this@HesapAyarlariActivity,
                        "Şifreniz Başarılı Bir Şekilde Güncellenmiştir",
                        Toast.LENGTH_LONG
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                    loginSayfasinaYönlendir()
                }
        }
    }

    private fun mailAdresiniGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if (kullanici != null) {


            FirebaseAuth.getInstance()
                .fetchSignInMethodsForEmail(etHesapAyarlariMailGuncelleme.text.toString())

                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        if (task.getResult()?.signInMethods?.size == 1) {

                            Toast.makeText(
                                this@HesapAyarlariActivity,
                                "Girmiş Olduğunuz E-mail Adresi kullanımdadır",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            kullanici.updateEmail(etHesapAyarlariMailGuncelleme.text.toString())
                                .addOnCompleteListener { task ->
                                    Toast.makeText(
                                        this@HesapAyarlariActivity,
                                        "Mailiniz Başarılı Bir Şekilde Güncellenmiştir",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginSayfasinaYönlendir()
                                }
                        }
                    } else {


                    }
                }
        }
    }

    fun loginSayfasinaYönlendir() {

        var intent = Intent(this@HesapAyarlariActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


}