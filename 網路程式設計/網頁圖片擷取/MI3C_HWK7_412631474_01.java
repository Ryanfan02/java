import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 擷取 HTML 中 img/source 標籤內的圖片網址，並下載圖片。
 * 使用方式：java MI3C_HWK7_412631474_01 [網頁網址] [下載資料夾] [最多下載張數]
 * 例如：java MI3C_HWK7_412631474_01 https://example.com images 10
 * 不使用第三方套件；JavaScript 執行後才出現的圖片不在處理範圍內。
 */
public class MI3C_HWK7_412631474_01 {
    private static final int MAX_HTML_CHARACTERS = 5_000_000;
    private static final long MAX_IMAGE_BYTES = 20L * 1024 * 1024;
    private static final Pattern IMAGE_TAG = Pattern.compile("(?is)<(?:img|source)\\b[^>]*>");
    private static final Pattern IMAGE_ATTRIBUTE = Pattern.compile(
            "(?i)(?:^|\\s)(src|data-src|data-original|data-lazy-src|srcset|data-srcset)\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))");

    public static void main(String[] args) throws Exception {
        String pageUrl = args.length > 0 ? args[0] : "https://tw.yahoo.com/";
        String outputDir = args.length > 1 ? args[1] : "images";
        int maxCount = args.length > 2 ? Integer.parseInt(args[2]) : 20;
        if (maxCount < 1 || maxCount > 200) {
            throw new IllegalArgumentException("最多下載張數必須介於 1 到 200");
        }
        findWeb(pageUrl, new File(outputDir), maxCount);
    }

    private static void findWeb(String pageUrl, File outputDir, int maxCount) throws Exception {
        URI pageUri = new URI(pageUrl);
        if (!isHttp(pageUri)) {
            throw new IllegalArgumentException("僅支援 http:// 或 https:// 網頁");
        }
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("無法建立圖片資料夾：" + outputDir);
        }

        System.out.println("讀取網頁：" + pageUri);
        HttpURLConnection pageConnection = openConnection(pageUri);
        String html;
        try {
            checkStatus(pageConnection);
            String contentType = pageConnection.getContentType();
            if (contentType != null && !contentType.toLowerCase().contains("text/html")) {
                throw new IOException("來源不是 HTML：" + contentType);
            }
            Charset encoding = findCharset(contentType);
            try (Reader reader = new BufferedReader(
                    new InputStreamReader(pageConnection.getInputStream(), encoding))) {
                StringBuilder builder = new StringBuilder();
                char[] block = new char[4096];
                int n;
                while ((n = reader.read(block)) != -1) {
                    if (builder.length() + n > MAX_HTML_CHARACTERS) {
                        throw new IOException("網頁內容過大，已停止讀取");
                    }
                    builder.append(block, 0, n);
                }
                html = builder.toString();
            }
        } finally {
            pageConnection.disconnect();
        }

        LinkedHashSet<URI> imageUrls = findImageUrls(html, pageUri);
        System.out.println("找到 " + imageUrls.size() + " 個候選圖片網址");
        int count = 0;
        int failed = 0;
        for (URI imageUri : imageUrls) {
            if (count >= maxCount) {
                break;
            }
            try {
                File downloaded = downloadImage(imageUri, outputDir, count);
                System.out.println("下載完成：" + downloaded.getName() + " <- " + imageUri);
                count++;
            } catch (IOException e) {
                failed++;
                System.out.println("略過下載失敗的圖片：" + imageUri + "（" + e.getMessage() + "）");
            }
        }
        System.out.println("完成：成功 " + count + " 張，失敗／略過 " + failed + " 張，儲存位置："
                + outputDir.getCanonicalPath());
    }

    private static LinkedHashSet<URI> findImageUrls(String html, URI pageUri) {
        LinkedHashSet<URI> result = new LinkedHashSet<>();
        Matcher tagMatcher = IMAGE_TAG.matcher(html);
        while (tagMatcher.find()) {
            Matcher attribute = IMAGE_ATTRIBUTE.matcher(tagMatcher.group());
            while (attribute.find()) {
                String name = attribute.group(1).toLowerCase();
                String value = attribute.group(2);
                if (value == null) value = attribute.group(3);
                if (value == null) value = attribute.group(4);
                if (value == null) continue;
                value = value.replace("&amp;", "&").replace("&#38;", "&").trim();

                if (name.endsWith("srcset")) {
                    for (String item : value.split(",")) {
                        String candidate = item.trim().split("\\s+")[0];
                        addUrl(result, pageUri, candidate);
                    }
                } else {
                    addUrl(result, pageUri, value);
                }
            }
        }
        return result;
    }

    private static void addUrl(LinkedHashSet<URI> result, URI base, String candidate) {
        if (candidate.isEmpty() || candidate.startsWith("#")) return;
        try {
            URI uri = base.resolve(new URI(candidate));
            if (isHttp(uri)) result.add(uri);
        } catch (IllegalArgumentException | URISyntaxException e) {
            // 忽略空值、不合法網址、data: 或 javascript: 等非 HTTP 圖片來源。
        }
    }

    private static boolean isHttp(URI uri) {
        String scheme = uri.getScheme();
        return scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"));
    }

    private static HttpURLConnection openConnection(URI uri) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(12000);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Java Image Example)");
        connection.setRequestProperty("Accept", "image/avif,image/webp,image/*,*/*;q=0.8");
        return connection;
    }

    private static void checkStatus(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        if (status < 200 || status >= 300) {
            throw new IOException("HTTP 狀態：" + status);
        }
    }

    private static Charset findCharset(String contentType) {
        if (contentType != null) {
            Matcher m = Pattern.compile("(?i)charset=([\\w-]+)").matcher(contentType);
            if (m.find()) {
                try {
                    return Charset.forName(m.group(1));
                } catch (Exception ignored) {
                    // 未提供有效的編碼名稱時採用 UTF-8。
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    private static File downloadImage(URI imageUri, File outputDir, int count) throws IOException {
        HttpURLConnection connection = openConnection(imageUri);
        Path temporary = null;
        try {
            checkStatus(connection);
            String type = connection.getContentType();
            if (type == null || !type.toLowerCase().startsWith("image/")) {
                throw new IOException("回傳內容不是圖片：" + type);
            }
            String ext = extensionFromType(type);
            File result = new File(outputDir, "pic" + count + ext);
            temporary = Files.createTempFile(outputDir.toPath(), "download-", ".part");
            long total = 0;
            try (InputStream dis = new BufferedInputStream(connection.getInputStream());
                 OutputStream fos = new BufferedOutputStream(Files.newOutputStream(temporary))) {
                byte[] b = new byte[8192];
                int len;
                while ((len = dis.read(b)) != -1) {
                    total += len;
                    if (total > MAX_IMAGE_BYTES) {
                        throw new IOException("圖片超過 20 MB 上限");
                    }
                    fos.write(b, 0, len);
                }
            }
            Files.move(temporary, result.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return result;
        } finally {
            if (temporary != null) Files.deleteIfExists(temporary);
            connection.disconnect();
        }
    }

    private static String extensionFromType(String contentType) {
        String type = contentType.toLowerCase().split(";")[0].trim();
        if (type.equals("image/jpeg") || type.equals("image/pjpeg")) return ".jpg";
        if (type.equals("image/png")) return ".png";
        if (type.equals("image/gif")) return ".gif";
        if (type.equals("image/webp")) return ".webp";
        if (type.equals("image/svg+xml")) return ".svg";
        if (type.equals("image/avif")) return ".avif";
        if (type.equals("image/bmp")) return ".bmp";
        return ".img";
    }
}
