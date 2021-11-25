package com.basarsoft.firebasekotlin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.number.NumberFormatter.with
import android.icu.number.NumberRangeFormatter.with
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.content.AsyncTaskLoader
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_hesap_ayarlari.*
import kotlinx.android.synthetic.main.fragment_sifremi_unuttum.*

class HesapAyarlariActivity : AppCompatActivity() , ProfilResmiFragment.onProfilResimListener{

    var izinlerVerildi = false
    var galeridenGelenURI: Uri? = null
    var kameradanGelenBitmap: Bitmap? = null


    override fun getResimYolu(resimPath: Uri?) {

        galeridenGelenURI = resimPath
        Picasso.get().load(galeridenGelenURI).resize(107,106).into(imgProfilResmi)

    }

    override fun getResimBitmap(bitmap: Bitmap) {
        kameradanGelenBitmap = bitmap
        imgProfilResmi.setImageBitmap(bitmap)
    }


    inner class BackgroundResimPress : AsyncTask<Uri , Void , ByteArray?>(){

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Uri?): ByteArray? {


        }

        override fun onProgressUpdate(vararg values: Void?) {
            super.onProgressUpdate(*values)
        }
        override fun onPostExecute(result: ByteArray?) {
            super.onPostExecute(result)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap_ayarlari)

        var kullanici = FirebaseAuth.getInstance().currentUser!!


        kullaniciBilgileriniOku ()

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
        btnSifreveyaMailGuncelle.setOnClickListener {
            if (etHesapAyarlariTelefon.text.toString().isNotEmpty()) {

                var credential = EmailAuthProvider.getCredential(
                    kullanici.email.toString(),
                    etHesapAyarlariTelefon.text.toString()
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


        btnDegisiklikleriKaydet.setOnClickListener {


            if (etHesapAyarlariKullaniciAdi.text.toString().isNotEmpty()) {

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                        .setDisplayName(etHesapAyarlariKullaniciAdi.text.toString())
                        .build()
                    kullanici.updateProfile(bilgileriGuncelle)
                        .addOnCompleteListener { task ->

                            if (task.isSuccessful) {

                                FirebaseDatabase.getInstance().reference
                                    .child("kullanici")
                                    .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                                    .child("isim")
                                    .setValue(etHesapAyarlariKullaniciAdi.text.toString())

                                Toast.makeText(this@HesapAyarlariActivity, "Değişiklikler Yapıldı", Toast.LENGTH_LONG).show()
                            }
                        }

            } else {
                Toast.makeText(this@HesapAyarlariActivity, "Boş Alanları Doldurunuz ", Toast.LENGTH_LONG).show()
            }

           if (etHesapAyarlariTelefon.text.toString().isNotEmpty()) {
                   FirebaseDatabase.getInstance().reference
                       .child("kullanici")
                       .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                       .child("telefon")
                       .setValue(etHesapAyarlariTelefon.text.toString())

               }

        }

        imgProfilResmi.setOnClickListener {

            if (izinlerVerildi){
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager , "fotosec")

            }else{
                izinleriIste()
            }
        }
    }

    private fun izinleriIste() {

        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE ,
            android.Manifest.permission.CAMERA)

        if (ContextCompat.checkSelfPermission(this , izinler[0])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this , izinler[1])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this , izinler[2])== PackageManager.PERMISSION_GRANTED){

            izinlerVerildi = true

        }else{
            ActivityCompat.requestPermissions(this,izinler,150)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 150){

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED){

                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager , "fotosec")

            }else{
                Toast.makeText(this@HesapAyarlariActivity , "Uygulamaya devam edebilmek için tüm izinleri vermeniz gerekmektedir" , Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun kullaniciBilgileriniOku() {

        var referans = FirebaseDatabase.getInstance().reference
        var kullanici = FirebaseAuth.getInstance().currentUser


        etHesapAyarlariKullaniciAdi.setText(kullanici?.displayName.toString())
        etHesapAyarlariEmail.setText(kullanici?.email.toString())

        // query 1
        var sorgu = referans.child("kullanici")
            .orderByKey()
            .equalTo(kullanici?.uid)

        sorgu.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                for (singleSnapshot in snapshot.children){

                    var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    etHesapAyarlariKullaniciAdi.setText(okunanKullanici?.isim)
                    etHesapAyarlariTelefon.setText(okunanKullanici?.telefon)

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }

        })


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

                            Toast.makeText(this@HesapAyarlariActivity, "Girmiş Olduğunuz E-mail Adresi kullanımdadır", Toast.LENGTH_LONG).show()

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