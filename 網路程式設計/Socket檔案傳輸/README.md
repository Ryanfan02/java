# TCP Socket 檔案傳輸

此練習包含 Java TCP Server 和 Client。Client 向 Server 請求指定檔案，Server 回傳是否成功、檔案長度及實際位元組資料。Client 收完指定長度的內容才完成下載。

**類別名稱 `Clinet` 是原本作業的拼字，這個版本暫時保留，方便與原始作業對照。**

## 編譯與執行

先在終端機切換到這個資料夾，執行：

```bash
javac -encoding UTF-8 MI3C_HWK8_412631474_01_Server.java MI3C_HWK8_412631474_01_Clinet.java
```

**第一個終端機：啟動 Server**

```bash
java MI3C_HWK8_412631474_01_Server
```

預設使用 `127.0.0.1:5555`，讀取本資料夾 `shared` 下的檔案。已附上 `shared/test1.txt`，可以直接下載測試。Server 保持啟動，按 `Ctrl+C` 可以停止。

**第二個終端機：執行 Client**

```bash
java MI3C_HWK8_412631474_01_Clinet
```

完成後，會在本資料夾 `downloads/test1.txt` 找到收到的檔案。

## 自訂參數

Server：

```bash
java MI3C_HWK8_412631474_01_Server shared 5555 127.0.0.1
```

參數依序為 **分享資料夾、Port、監聽 IP**。

Client：

```bash
java MI3C_HWK8_412631474_01_Clinet test1.txt 127.0.0.1 5555 downloads
```

參數依序為 **檔名、伺服器 IP、Port、下載資料夾**。

程式預設只監聽本機，若需要讓區域網路其他電腦連線，可以自行設定伺服器監聽 IP（例如 `0.0.0.0`）並使用可信任的網路環境。此練習是一般 TCP 傳輸，**不是 WebSocket，也沒有加密或使用者身分驗證**，不應直接作為公開網際網路檔案伺服器。

## 修正重點

- Server 以 `file.length()` 回傳正確的檔案位元組長度，不再誤用資料夾內的檔案數量。
- Client 會透過 `read()` 取得真正收到的位元組，不會只建立空陣列就寫入檔案。
- 使用 8192 bytes 緩衝區傳輸，能處理文字、二進位與零長度檔案。
- 檔名限制為分享資料夾中的單一檔案，拒絕路徑穿越；下載中斷會移除暫存檔。
