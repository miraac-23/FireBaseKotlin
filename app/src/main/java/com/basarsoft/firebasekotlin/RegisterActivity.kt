package com.basarsoft.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnSave.setOnClickListener {

            if (etEmail.text.isNotEmpty() && etPassword.text.isNotEmpty() && etPassword2.text.isNotEmpty()) {

                if (etPassword.text.toString().equals(etPassword2.text.toString())) {

                    yeniUyeKayit(etEmail.text.toString(), etPassword.text.toString())

                } else {
                    Toast.makeText(this, "Girilem Şifreleri Kontrol Ediniz...", Toast.LENGTH_LONG)
                        .show()
                }
            } else {

                Toast.makeText(this, "Boş Alanalrı Doldurunuz ", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun yeniUyeKayit(mail: String, sifre: String) {

        progressBarGoster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {

                override fun onComplete(p0: Task<AuthResult>) {

                    if (p0.isSuccessful) {
                        progressBarGizle()
                        onayMailiGonder()

                        var veriTabaninaEklenecekKullanici = Kullanici()
                        //xxxx@gmail.com   @ işaretine kadar olan alanı alır
                        veriTabaninaEklenecekKullanici.isim = etEmail.text.toString().substring(0,etEmail.text.toString().indexOf("@"))
                        veriTabaninaEklenecekKullanici.kullanici_id = FirebaseAuth.getInstance().currentUser?.uid
                        veriTabaninaEklenecekKullanici.profil_resmi = ""
                        veriTabaninaEklenecekKullanici.telefon = "123"
                        veriTabaninaEklenecekKullanici.seviye = "1"

                        FirebaseDatabase.getInstance().reference
                            .child("kullanici")
                            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .setValue(veriTabaninaEklenecekKullanici ).addOnCompleteListener { task->

                                if (task.isSuccessful){ Toast.makeText(this@RegisterActivity, "Üye Kaydedildi " + FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_LONG).show()
                                    FirebaseAuth.getInstance().signOut()
                                    loginSayfasinaYönlendir()
                                }
                            }

                            FirebaseAuth.getInstance().signOut()

                        etEmail.text.clear()
                        etPassword.text.clear()
                        etPassword2.text.clear()

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Üye Kaydetme İşlemi Başarısız ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
    }



    private fun onayMailiGonder() {

        var kullanici = FirebaseAuth.getInstance().currentUser

        if (kullanici != null) {

            kullanici.sendEmailVerification()
                .addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {

                        if (p0.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Mail Kutunuzu Kontrol Ediniz , Gelen Maile Onay Veriniz ...",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Mail Onayı Yapılırken Hata Oluştu " + p0.exception?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }
    }

    private fun progressBarGoster() {
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBar.visibility = View.INVISIBLE
    }

    private fun loginSayfasinaYönlendir() {

        var intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }
}




