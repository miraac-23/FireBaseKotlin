package com.basarsoft.firebasekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var myAuthListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initMyAuthListener()

        tvKayitOl.setOnClickListener {

            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        tvOnayMailiGonder.setOnClickListener {

            var dialogGoster = OnayMailTekrarGonderFragment()
            dialogGoster.show(supportFragmentManager, "dialoggoster")
        }

     tvSifremiUnuttum.setOnClickListener{
         var dialogGoster = SifremiUnuttumFragment()
         dialogGoster.show(supportFragmentManager,"dialogGoster")
     }


        btnGiris.setOnClickListener {

            if (etMail.text.isNotEmpty() && etSifre.text.isNotEmpty()) {
                progressBarGoster()

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(etMail.text.toString(), etSifre.text.toString())
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                        override fun onComplete(p0: Task<AuthResult>) {

                            if (p0.isSuccessful) {
                                progressBarGizle()
                                if (!p0.result?.user!!.isEmailVerified) {
                                    FirebaseAuth.getInstance().signOut()
                                }

                            } else {

                                progressBarGizle()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Hatalı Giriş : " + p0.exception?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    })

            } else {
                Toast.makeText(this@LoginActivity, "Boş Alanları Doldurunuz ", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun progressBarGoster() {
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle() {
        progressBarLogin.visibility = View.INVISIBLE
    }

    private fun initMyAuthListener() {


        myAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if (kullanici != null) {
                    if (kullanici.isEmailVerified) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Mail Onayı Yapılmıştır Başarılı Şekilde Giriş Yapabilirsiniz ",
                            Toast.LENGTH_LONG
                        ).show()
                        var intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        Toast.makeText(
                            this@LoginActivity,
                            "Mail Aderesinizi Onayladıktan Sonra Giriş Yapınız ",
                            Toast.LENGTH_LONG
                        ).show()
                        // FirebaseAuth.getInstance().signOut()

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        FirebaseAuth.getInstance().addAuthStateListener(myAuthListener)
    }

    override fun onStop() {
        super.onStop()

        FirebaseAuth.getInstance().removeAuthStateListener(myAuthListener)
    }

}