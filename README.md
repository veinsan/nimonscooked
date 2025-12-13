# 🥘 Gastropath: Travelers of the Secret Stack

> **Perpaduan unik antara estetika visual HD-2D ala *Octopath Traveler* dan kekacauan dapur kooperatif ala *Overcooked*.**

![Game Banner](https://via.placeholder.com/800x200?text=Gastropath+Gameplay+Banner) ## 📖 Tentang Proyek

**Gastropath: Travelers of the Secret Stack** adalah game simulasi memasak manajemen waktu yang dibangun menggunakan **Java** dan **LibGDX**. 

Game ini menggabungkan tekanan tinggi dalam mengelola dapur restoran dengan gaya visual "Secret Stack" yang unik. Pemain mengendalikan para *traveler* (koki) yang harus bekerja sama menyiapkan bahan, memasak hidangan, dan menyajikan pesanan sebelum waktu habis, sambil menjaga kebersihan piring di tengah kekacauan.

## ✨ Fitur Utama

* **🎨 Visual Inspired by Octopath:** Menggunakan teknik rendering 2.5D dengan shader atmosferik, pencahayaan dinamis, dan *depth-sorting* untuk menciptakan tampilan visual yang menawan.
* **🔥 Gameplay Inspired by Overcooked:**
    * **Multi-Chef System:** Tukar kendali antara dua karakter secara instan untuk manajemen tugas yang efisien.
    * **Siklus Memasak Lengkap:** Potong bahan (Chopping) → Masak (Cooking) → Rakit (Assembly) → Sajikan (Serving) → Cuci Piring (Washing).
* **⚠️ Mekanik Resiko:** Bahan makanan memiliki siklus status: *Raw* → *Chopped* → *Cooking* → *Cooked*... dan jika dibiarkan terlalu lama, akan menjadi **Burnt** (Gosong)!
* **🍽️ Manajemen Piring:** Piring kotor akan menumpuk setelah makanan disajikan. Pemain wajib mencuci piring kotor di *Washing Station* sebelum bisa digunakan kembali.
* **📜 Sistem Pesanan:** Pesanan pelanggan muncul secara dinamis dengan batas waktu. Ketepatan dan kecepatan adalah kunci skor tinggi.

## 🎮 Kontrol

| Aksi | Tombol (Keyboard) | Deskripsi |
| :--- | :---: | :--- |
| **Gerak** | `W` `A` `S` `D` / Panah | Menggerakkan karakter traveler |
| **Interaksi** | `V` / `E` | Memotong, Mencuci, Mengambil, atau Menaruh |
| **Dash** | `Shift` + Arah | Lari cepat (Cooldown 2 detik) |
| **Ganti Karakter** | `X` / `Tab` | Tukar kendali antara Traveler A dan Traveler B |
| **Lempar Item** | `F` / `K` | Melempar bahan ke rekan atau meja seberang |
| **Jatuhkan Item** | `Q` | Menaruh item di lantai (jika meja penuh) |
| **Ambil dr Lantai** | `G` | Mengambil item yang terjatuh |
| **Pause** | `Esc` | Jeda permainan |

## 🍔 Menu Resep

Para Traveler harus menguasai resep-resep "Secret Stack":

1.  **Classic Burger:** Roti + Daging Matang
2.  **Cheese Burger:** Roti + Daging Matang + Keju
3.  **Salad Segar:** Selada + Tomat (Potong)
4.  **BLT Burger:** Roti + Daging Matang + Selada + Tomat
5.  **Deluxe Burger:** Roti + Daging + Keju + Selada + Tomat

## 🛠️ Arsitektur Teknis

Dikembangkan dengan prinsip *Clean Code* dan *Object-Oriented Programming*:

* **Core Engine:** LibGDX (Java).
* **Pola Desain:** Menggunakan **MVC (Model-View-Controller)** untuk memisahkan logika permainan, rendering visual, dan input pemain.
* **Manajemen Entitas:** Sistem entitas kustom untuk menangani *collision*, status interaksi, dan pergerakan grid-based.
* **Shader & Rendering:** Implementasi `ShaderManager` kustom untuk efek visual (vignette, lighting) dan `WorldRenderer` yang menangani Z-sorting untuk efek kedalaman 2.5D.
* **Multithreading:** Logika memasak (`CookingThread`) berjalan secara asinkron untuk simulasi waktu nyata yang akurat.

---

**Gastropath: Travelers of the Secret Stack**
*Cook, Serve, and Survive the Stack.*