import java.io.*;
import java.net.*;

/**
 * TCP 檔案伺服器。
 * 使用方式：java MI3C_HWK8_412631474_01_Server [分享資料夾] [Port] [監聽IP]
 * 例如：java MI3C_HWK8_412631474_01_Server shared 5555 127.0.0.1
 */
public class MI3C_HWK8_412631474_01_Server {
    public static void main(String[] args) throws Exception {
        String pubDir = args.length > 0 ? args[0] : "shared";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5555;
        String bindHost = args.length > 2 ? args[2] : "127.0.0.1";

        File directory = new File(pubDir).getCanonicalFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("無法建立分享資料夾：" + directory);
        }
        if (!directory.isDirectory()) {
            throw new IOException("分享路徑不是資料夾：" + directory);
        }

        try (ServerSocket serverSocket = new ServerSocket(
                port, 50, InetAddress.getByName(bindHost))) {
            System.out.println("TCP Socket 檔案伺服器已啟動");
            System.out.println("監聽：" + bindHost + ":" + serverSocket.getLocalPort());
            System.out.println("分享資料夾：" + directory);
            System.out.println("按 Ctrl+C 可停止伺服器");

            while (true) {
                try (Socket client = serverSocket.accept()) {
                    client.setSoTimeout(15000);
                    handleClient(client, directory);
                } catch (IOException e) {
                    // 某位 Client 發生錯誤時，伺服器仍可接收下一個連線。
                    System.out.println("連線處理失敗：" + e.getMessage());
                }
            }
        }
    }

    private static void handleClient(Socket client, File directory) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(client.getInputStream()));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

        String command = dis.readUTF();
        if (!"GetFile".equalsIgnoreCase(command)) {
            sendFail(dos, "不支援的指令");
            return;
        }

        String fileName = dis.readUTF();
        // 只允許下載分享資料夾內的直接檔案，避免 ../ 等路徑穿越。
        if (fileName.isEmpty() || fileName.equals(".") || fileName.equals("..")
                || fileName.contains("/") || fileName.contains("\\") || fileName.contains(":")) {
            sendFail(dos, "檔案名稱不合法");
            return;
        }

        File file = new File(directory, fileName).getCanonicalFile();
        if (!directory.equals(file.getParentFile()) || !file.isFile() || !file.canRead()) {
            sendFail(dos, "檔案不存在或無法讀取：" + fileName);
            return;
        }

        long length = file.length();
        dos.writeUTF("Success");
        dos.writeLong(length);  // 真正的檔案位元組長度，而不是檔案數量。

        try (InputStream fileInput = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInput.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }
        dos.flush();
        System.out.println("傳送完成：" + fileName + "（" + length + " bytes）");
    }

    private static void sendFail(DataOutputStream dos, String reason) throws IOException {
        dos.writeUTF("Fail");
        dos.writeUTF(reason);
        dos.flush();
        System.out.println("拒絕請求：" + reason);
    }
}
