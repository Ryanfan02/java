import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * TCP 檔案下載 Client。保留原始作業的 Clinet 類別名稱。
 * 使用方式：java MI3C_HWK8_412631474_01_Clinet [檔名] [主機] [Port] [下載資料夾]
 * 例如：java MI3C_HWK8_412631474_01_Clinet test1.txt 127.0.0.1 5555 downloads
 */
public class MI3C_HWK8_412631474_01_Clinet {
    public static void main(String[] args) throws Exception {
        String fileName = args.length > 0 ? args[0] : "test1.txt";
        String host = args.length > 1 ? args[1] : "127.0.0.1";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 5555;
        String downloadDir = args.length > 3 ? args[3] : "downloads";

        if (fileName.isEmpty() || fileName.equals(".") || fileName.equals("..")
                || fileName.contains("/") || fileName.contains("\\") || fileName.contains(":")) {
            System.out.println("檔案名稱不合法");
            return;
        }

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 5000);
            socket.setSoTimeout(15000);

            DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream dis = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            dos.writeUTF("GetFile");
            dos.writeUTF(fileName);
            dos.flush();

            String response = dis.readUTF();
            if ("Fail".equalsIgnoreCase(response)) {
                System.out.println("下載失敗：" + dis.readUTF());
                return;
            }
            if (!"Success".equalsIgnoreCase(response)) {
                throw new IOException("伺服器回應格式錯誤：" + response);
            }

            long remaining = dis.readLong();
            if (remaining < 0) {
                throw new IOException("檔案大小不合法");
            }
            long fileSize = remaining;

            File directory = new File(downloadDir).getCanonicalFile();
            if (!directory.exists() && !directory.mkdirs()) {
                throw new IOException("無法建立下載資料夾：" + directory);
            }
            File outputFile = new File(directory, fileName).getCanonicalFile();
            if (!directory.equals(outputFile.getParentFile())) {
                throw new IOException("下載路徑不合法");
            }

            // 先寫入暫存檔，完整接收後才取代正式檔案，避免中斷時留下壞檔。
            Path temporary = Files.createTempFile(directory.toPath(), "socket-download-", ".part");
            try {
                try (OutputStream fos = new BufferedOutputStream(Files.newOutputStream(temporary))) {
                    byte[] buffer = new byte[8192];
                    while (remaining > 0) {
                        int bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                        if (bytesRead == -1) {
                            throw new EOFException("傳輸中斷，檔案尚未接收完整");
                        }
                        fos.write(buffer, 0, bytesRead); // 寫入實際從 Socket 收到的資料。
                        remaining -= bytesRead;
                    }
                }
                Files.move(temporary, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporary);
            }

            System.out.println("下載完成：" + outputFile + "（" + fileSize + " bytes）");
        }
    }
}
