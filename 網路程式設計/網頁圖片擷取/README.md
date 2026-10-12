# 網頁圖片擷取

本程式以 Java HTTP／HTTPS 連線讀取指定網頁的 HTML，解析 `<img>`、`<source>` 裡的 `src`、`data-src`、`srcset` 等屬性，下載找到的圖片。支援相對網址、絕對網址、帶查詢參數的網址，圖片以 `pic0.png`、`pic1.jpg` 等名稱儲存。

## 編譯和執行

請先用終端機切換到本資料夾：

```bash
javac -encoding UTF-8 MI3C_HWK7_412631474_01.java
java MI3C_HWK7_412631474_01 "https://tw.yahoo.com/" "images" 10
```

參數依序為 **網頁網址、下載目錄、最多成功下載的圖片數量**。若全部省略，預設嘗試 Yahoo 首頁、輸出至 `images`，最多下載 20 張。不同網站可能限制自動存取，圖片是否出現在 HTML 中也取決於網站設計。

## 可以在電腦上重現的示範

為避免依賴外部網站，可以使用本資料夾的 `示範網頁`。在**第一個終端機**、本資料夾中啟動本機 HTTP 網站：

```bash
python -m http.server 8000 --directory "示範網頁"
```

然後在**第二個終端機**，同樣切換到本資料夾後執行：

```bash
java MI3C_HWK7_412631474_01 "http://127.0.0.1:8000/index.html" "images" 10
```

正常情況會在 `images` 資料夾看到 `pic0.png` 和 `pic1.jpg`。結束 HTTP 伺服器可按 `Ctrl+C`。Python 僅用於建立示範網站，**Java 程式本身不需第三方套件**。

## 使用範圍

程式解析 HTTP 回傳的 HTML，不會執行 JavaScript；因此無法擷取必須等瀏覽器執行腳本之後才產生的圖片。每張圖片設有 20 MB 上限、連線逾時及回應類型檢查，不會將一般 HTML 誤存為圖片。
