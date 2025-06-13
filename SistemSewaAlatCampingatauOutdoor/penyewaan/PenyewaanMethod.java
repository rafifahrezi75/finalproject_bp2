package penyewaan;

import java.util.List;
import java.util.Scanner;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import alat.Alat;
import alat.AlatDAO;
import alat.AlatMethod;
import user.User;
import user.UserDAO;
import detail_penyewaan.DetailPenyewaan;
import detail_penyewaan.DetailPenyewaanDAO;
import detail_penyewaan.DetailPenyewaanMethod;
import structure.Stack;
import structure.Queue;

public class PenyewaanMethod {
    private static final Scanner scanner = new Scanner(System.in);

    public static void handleMenuPenyewaan(PenyewaanDAO penyewaanDAO, UserDAO userDAO) {
        boolean exit = false;

        while (!exit) {
        System.out.println("\n====== Menu Penyewaan ======");
        System.out.println("1. Lihat semua penyewaan");
        System.out.println("2. Kembali ke menu utama");
        System.out.print("Pilih menu: ");
        String pilihan = scanner.nextLine();

        switch (pilihan) {
            case "1":
            getAllPenyewaan(penyewaanDAO);
            break;
            case "2":
            exit = true;
            break;
            default:
            System.out.println("Pilihan tidak valid!");
        }
        }
    }

    public static void handleMenuPenyewaanPelanggan(PenyewaanDAO penyewaanDAO, User currentUser) {
        List<Penyewaan> list = penyewaanDAO.getPenyewaanByUserId(currentUser.getIdUser());

        if (list.isEmpty()) {
            System.out.println("Tidak ada data histori penyewaan.");
            return;
        }

        System.out.println("\n=== DAFTAR HISTORI SEWA ===");
        System.out.printf(
            "%-5s %-15s %-20s %-20s %-20s %-20s %-15s %-15s%n",
            "ID", "Nama User", "Tgl Sewa", "Tgl Rencana", "Tgl Kembali", "Barang Jaminan", "Status Sewa", "Status"
        );

        for (Penyewaan p : list) {
            System.out.printf(
                "%-5d %-15s %-20s %-20s %-20s %-20s %-15s %-15s%n",
                p.getIdPenyewaan(),
                p.getUser().getNama(),
                p.getTglSewa().toString(),
                p.getTglRencanaMenyewa().toString(),
                p.getTglKembali().toString(),
                p.getBarangJaminan(),
                p.getStatusSewa(),
                p.getStatus()
            );
        }

        System.out.print("\nLihat detail penyewaan? (Y/N): ");
        String jawab = scanner.nextLine();

        if (jawab.equalsIgnoreCase("Y")) {
            DetailPenyewaanMethod.lihatDetailPenyewaan();
        }
    }


    public static void getAllPenyewaan(PenyewaanDAO penyewaanDAO) {
        List<Penyewaan> list = penyewaanDAO.getAllPenyewaan();

        if (list.isEmpty()) {
        System.out.println("Tidak ada data penyewaan.");
        return;
        }

        System.out.println("\n=== DAFTAR SEMUA PENYEWAAN ===");
        System.out.printf(
            "%-5s %-15s %-20s %-20s %-20s %-20s %-15s %-15s%n",
            "ID", "Nama User", "Tgl Sewa", "Tgl Rencana", "Tgl Kembali", "Barang Jaminan", "Status Sewa", "Status"
        );

        for (Penyewaan p : list) {
            System.out.printf(
                "%-5d %-15s %-20s %-20s %-20s %-20s %-15s %-15s%n",
                p.getIdPenyewaan(),
                p.getUser().getNama(),
                p.getTglSewa().toString(),
                p.getTglRencanaMenyewa().toString(),
                p.getTglKembali().toString(),
                p.getBarangJaminan(),
                p.getStatusSewa(),
                p.getStatus()
            );
        }

        System.out.print("\nLihat detail penyewaan? (Y/N): ");
        String jawab = scanner.nextLine();

        if (jawab.equalsIgnoreCase("Y")) {
            DetailPenyewaanMethod.lihatDetailPenyewaan();
        }
    }

    //Digunakan Untuk Stack
    public static void sewaAlat(PenyewaanDAO penyewaanDAO, DetailPenyewaanDAO detailDAO, AlatDAO alatDAO, User currentUser) {
        Stack<Alat> stackAlat = new Stack<>();

        while (true) {
            // Tampilkan ulang alat yang tersedia setiap loop
            AlatMethod.tampilkanSemuaAlatReady(alatDAO);
            System.out.println("\n--- Menu Pilih Alat ---");
            System.out.println("Masukkan ID Alat untuk menambah");
            System.out.println("Ketik 0 untuk masuk ke menu konfirmasi");

            while (true) {
                System.out.print("Pilihan Anda: ");
                int id;
                try {
                    id = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Input harus berupa angka!");
                    continue;
                }

                if (id == 0) {
                    if (stackAlat.isEmpty()) {
                        System.out.println("Belum ada alat yang dipilih.");
                        break;
                    }

                    // Tampilkan alat yang dipilih
                    System.out.println("\nAlat yang telah dipilih:");
                    stackAlat.printAll();

                    // Menu konfirmasi akhir
                    while (true) {
                        System.out.println("\n--- Konfirmasi Penyewaan ---");
                        System.out.println("1. Lanjutkan proses penyewaan");
                        System.out.println("2. Batalkan alat terakhir (Undo)");
                        System.out.println("3. Kembali memilih alat");
                        System.out.print("Pilih opsi: ");
                        String pilih = scanner.nextLine();

                        switch (pilih) {
                            case "1":
                                if (stackAlat.isEmpty()) {
                                    System.out.println("Belum ada alat yang dipilih.");
                                } else {
                                    // Lanjut ke proses penyewaan
                                    gotoPenyewaan(penyewaanDAO, detailDAO, alatDAO, currentUser, stackAlat);
                                    return;
                                }
                                break;
                            case "2":
                                Alat dibatalkan = stackAlat.pop();
                                if (dibatalkan != null) {
                                    System.out.println("Alat '" + dibatalkan.getNamaAlat() + "' dibatalkan (Undo).");
                                } else {
                                    System.out.println("Tidak ada alat untuk dibatalkan.");
                                }
                                System.out.println("\nAlat yang tersisa:");
                                stackAlat.printAll();
                                break;
                            case "3":
                                System.out.println("Kembali ke pemilihan alat...");
                                break; // kembali ke daftar alat
                            default:
                                System.out.println("Input tidak valid. Pilih 1, 2, atau 3.");
                        }

                        if (pilih.equals("3")) break;
                    }

                    break; // keluar dari inner-loop dan tampilkan alat lagi
                }

                Alat alat = alatDAO.getAlatById(id);
                if (alat != null) {
                    if (alat.getStok() == 0) {
                        System.out.println("Stok alat '" + alat.getNamaAlat() + "' habis.");
                    } else if (stackAlat.containsAlatById(alat.getIdAlat())) {
                        System.out.println("Alat '" + alat.getNamaAlat() + "' sudah dipilih.");
                    } else {
                        stackAlat.push(alat);
                        System.out.println("Alat '" + alat.getNamaAlat() + "' ditambahkan ke daftar.");
                    }
                } else {
                    System.out.println("Alat tidak ditemukan.");
                }
            }
        }
    }

    //Digunakan Untuk Stack
    private static void gotoPenyewaan(PenyewaanDAO penyewaanDAO, DetailPenyewaanDAO detailDAO, AlatDAO alatDAO, User currentUser, Stack<Alat> stackAlat) {
        Timestamp tglSewa = new Timestamp(System.currentTimeMillis());

        System.out.print("Masukkan tanggal rencana menyewa (format: YYYY-MM-DD HH:MM): ");
        String input = scanner.nextLine() + ":00";

        Timestamp tglRencanaMenyewa;
        try {
            tglRencanaMenyewa = Timestamp.valueOf(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Format tanggal salah!");
            return;
        }

        System.out.print("Masukkan lama sewa (hari): ");
        int lamaSewaHari = Integer.parseInt(scanner.nextLine());

        LocalDateTime kembaliDateTime = tglRencanaMenyewa.toLocalDateTime().plusDays(lamaSewaHari);
        Timestamp tglKembali = Timestamp.valueOf(kembaliDateTime);

        System.out.print("Masukkan barang jaminan: ");
        String jaminan = scanner.nextLine();

        Penyewaan penyewaan = new Penyewaan();
        penyewaan.setUser(currentUser);
        penyewaan.setTglSewa(tglSewa);
        penyewaan.setTglRencanaMenyewa(tglRencanaMenyewa);
        penyewaan.setTglKembali(tglKembali);
        penyewaan.setBarangJaminan(jaminan);

        int idPenyewaan = penyewaanDAO.insertPenyewaan(penyewaan);
        if (idPenyewaan == -1) {
            System.out.println("Gagal menyimpan penyewaan.");
            return;
        }
        penyewaan.setIdPenyewaan(idPenyewaan);

        // Pindahkan stack ke DB
        Stack<Alat> tempStack = new Stack<>();
        while (!stackAlat.isEmpty()) {
            tempStack.push(stackAlat.pop());
        }

        while (!tempStack.isEmpty()) {
            Alat alat = tempStack.pop();
            int jumlah;
            while (true) {
                System.out.print("Masukkan jumlah untuk '" + alat.getNamaAlat() + "' (Stok: " + alat.getStok() + "): ");
                try {
                    jumlah = Integer.parseInt(scanner.nextLine());
                    if (jumlah <= 0) {
                        System.out.println("Jumlah harus lebih dari 0.");
                    } else if (jumlah > alat.getStok()) {
                        System.out.println("Jumlah melebihi stok tersedia.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input tidak valid.");
                }
            }

            DetailPenyewaan dp = new DetailPenyewaan(penyewaan, alat, jumlah);
            detailDAO.insertDetailPenyewaan(dp);
        }

        System.out.println("\nPenyewaan berhasil disimpan!");
    }

    //Digunakan Untuk Queue
    public static void setujui(PenyewaanDAO penyewaanDAO) {
        Queue<Penyewaan> queue = penyewaanDAO.getQueuePenyewaanBelumDisetujui();

        if (queue.isEmpty()) {
            System.out.println("Tidak ada penyewaan yang menunggu persetujuan.");
            return;
        }

        // Tampilkan semua antrian terlebih dahulu
        System.out.println("\n=== DAFTAR ANTRIAN PENYEWAAN YANG BELUM DISETUJUI ===");
        System.out.printf("%-5s %-15s %-20s %-20s %-15s%n",
            "ID", "Nama User", "Tgl Sewa", "Barang Jaminan", "Status");

        // Salin queue ke sementara untuk preview tanpa menghapus isinya
        Queue<Penyewaan> tempQueue = new Queue<>();
        while (!queue.isEmpty()) {
            Penyewaan p = queue.dequeue();
            System.out.printf("%-5d %-15s %-20s %-20s %-15s%n",
                p.getIdPenyewaan(),
                p.getUser().getNama(),
                p.getTglSewa().toString(),
                p.getBarangJaminan(),
                p.getStatus()
            );
            tempQueue.enqueue(p); // pindahkan ke queue sementara
        }

        // Kembalikan antrian utama
        while (!tempQueue.isEmpty()) {
            queue.enqueue(tempQueue.dequeue());
        }

        System.out.println("\n>>> PROSES KONFIRMASI SATU PER SATU <<<");

        while (!queue.isEmpty()) {
            Penyewaan p = queue.dequeue();
            System.out.println("\nID: " + p.getIdPenyewaan() +
                            " | Nama: " + p.getUser().getNama() +
                            " | Tgl Sewa: " + p.getTglSewa() +
                            " | Barang Jaminan: " + p.getBarangJaminan() +
                            " | Status: " + p.getStatus());

            System.out.print("Setujui penyewaan ini? (y/n/skip): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("y")) {
                boolean success = penyewaanDAO.setujuiPenyewaan(p.getIdPenyewaan());
                System.out.println(success ? "Disetujui." : "Gagal disetujui.");
            } else if (input.equalsIgnoreCase("skip")) {
                System.out.println("Dilewati.");
            } else {
                boolean success = penyewaanDAO.tolakPenyewaan(p.getIdPenyewaan());
                System.out.println(success ? "Ditolak." : "Gagal ditolak.");
                
            }
        }

        System.out.println("\nSemua antrian penyewaan telah diproses.");
    }

}
