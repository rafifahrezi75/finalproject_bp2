
# 🏕️ Sistem Informasi Penyewaan Alat Camping & Outdoor

Sistem ini dirancang untuk mengelola proses penyewaan alat camping dan outdoor secara digital. Pengguna dapat melihat alat yang tersedia, menyewa alat dengan sistem stack, melakukan pengembalian alat, serta menghitung total tagihan secara otomatis. Admin dapat menyetujui penyewaan melalui sistem antrian (queue) dan mengelola pengembalian serta laporan penyewaan.

---

## ✨ Fitur Utama

- **Login Role-based** (Admin dan Pelanggan)
- **Manajemen Alat & Kategori**
- **Penyewaan dengan Stack (pilihan alat & undo)**
- **Persetujuan Penyewaan oleh Admin dengan Queue**

---

## ⚙️ Cara Menjalankan

### 1. Kompilasi Semua File
```bash
javac -d bin -cp "lib/mysql-connector-j-9.3.0.jar" -sourcepath . user/*.java connection/*.java kategori/*.java alat/*.java penyewaan/*.java detail_penyewaan/*.java tagihan_penyewaan/*.java pengembalian/*.java report/*.java Main.java
```

### 2. Jalankan Program
```bash
java -cp "bin;lib/mysql-connector-j-9.3.0.jar" Main
```

> 💡 *Pastikan file `mysql-connector-j-9.3.0.jar` berada di folder `lib`.*

---

## 🧩 Struktur Data yang Digunakan

| Struktur Data | Kegunaan |
|---------------|----------|
| `Stack<Alat>` | Menyimpan alat yang dipilih pelanggan secara LIFO. Mendukung fitur **Undo** alat terakhir. |
| `Queue<Penyewaan>` | Menyimpan antrian penyewaan yang menunggu persetujuan admin. FIFO. |

---

## 🧾 Stuktur Database (MySQL)

### Tabel:
- `user(id_user, role, nama, password, no_hp, alamat)`
- `kategori(id_kategori, nama_kategori)`
- `alat(id_alat, id_kategori, nama_alat, stok, harga_perhari)`
- `penyewaan(id_penyewaan, id_user, tgl_sewa, tgl_rencana_menyewa, tgl_kembali, barang_jaminan, status)`
- `detail_penyewaan(id_detail_penyewaan, id_penyewaan, id_alat, jumlah_barang)`
- `pengembalian(id_pengembalian, id_penyewaan, tgl_dikembalikan, status_bayar)`
- `denda(id_denda, id_pengembalian, nilai, keterangan)`

---

## 🧪 Simulasi Penggunaan

### 👤 Pelanggan
1. Login
2. Melihat alat tersedia
3. Memilih alat (Stack + Undo)
4. Mengisi data sewa (tanggal, jaminan)
5. Transaksi disimpan → status: `Belum_Disetujui`

### 🛠️ Admin
1. Login
2. Melihat antrian penyewaan (Queue)
3. Menyetujui penyewaan → status: `Disetujui`
4. Melihat data pengembalian

### 🔁 Pengembalian
1. Admin pilih penyewaan yang belum selesai
2. Input tanggal pengembalian
3. Jika terlambat → trigger membuat denda
4. Penyewaan selesai, stok diperbarui

---

## 🧠 Catatan Tambahan

- Semua class DAO menangani query database dan menjaga pemisahan logika bisnis.
- Class *Method.java seperti `PenyewaanMethod`, `PengembalianMethod` menangani interaksi dan validasi input pengguna.
- Struktur data Stack dan Queue diimplementasikan manual (`Node<T>`).

---

## 📌 Lisensi

Proyek ini dibuat sebagai tugas akhir untuk mata kuliah **Bahasa Pemrograman 2** dan tidak dimaksudkan untuk keperluan komersial.

---
