package hku.cs.cloudalbum;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Niko Feng on 11/27/2016.
 */

public class CommunicationUtils {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 10000000;
    private static final String CHARSET = "utf-8";
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";
    public static final String RESULT_FAIL = "FAIL";

    public static String uploadFile(File file, String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString();  //Randomly generate the Boundary
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //Content Type

        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //Allow InputStream
            conn.setDoOutput(true); //Allow OutputStream
            conn.setUseCaches(false);  //Not Allowed to use Caches
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", CHARSET);
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (file != null) {
                OutputStream outputSteam = conn.getOutputStream();

                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();

                int res = conn.getResponseCode();
                Log.e(TAG, "response code:" + res);
                if (res == 200) {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FAILURE;
    }

    public static String getResponseJson(String url) {
        HttpURLConnection conn = null;
        final int HTML_BUFFER_SIZE = 2 * 1024 * 1024;
        char htmlBuffer[] = new char[HTML_BUFFER_SIZE];
        try {
            URL responseURL = new URL(url);
            conn = (HttpURLConnection) responseURL.openConnection();
            conn.setInstanceFollowRedirects(true);
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));
            String HTMLSource = ReadBufferedHTML(reader, htmlBuffer, HTML_BUFFER_SIZE);
            HTMLSource = HTMLSource.substring(0, HTMLSource.lastIndexOf("}") + 1);
            reader.close();
            return HTMLSource;
        } catch (Exception e) {
            e.printStackTrace();
            return RESULT_FAIL;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String ReadBufferedHTML(BufferedReader reader, char[] htmlBuffer, int bufSz) throws java.io.IOException {
        htmlBuffer[0] = '\0';
        int offset = 0;
        do {
            int cnt = reader.read(htmlBuffer, offset, bufSz - offset);
            if (cnt > 0) {
                offset += cnt;
            } else {
                break;
            }
        } while (true);
        return new String(htmlBuffer);
    }

    public static ArrayList<VideoItem> parseJson(String JSONString) {
        ArrayList<VideoItem> videoItems = new ArrayList<VideoItem>();

        try {
            JSONObject rootJSONObj = new JSONObject(JSONString);

            JSONArray jsonArray = rootJSONObj.optJSONArray("videos");
            for (int i = 0; i < jsonArray.length(); ++i) {
                int video_id = jsonArray.getJSONObject(i).getInt("video_id");
                String video_name = jsonArray.getJSONObject(i).getString("video_name");
                String video_url = jsonArray.getJSONObject(i).getString("video_url");
                String uploadTimeStamp = jsonArray.getJSONObject(i).getString("upload_timestamp");
                VideoItem item = new VideoItem(video_id, video_name, uploadTimeStamp, video_url);
                videoItems.add(item);
            }
            return videoItems;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videoItems;
    }

    public static String httpUrlConnPost(String requestURL, String video_id, String video_name, String option) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("video_id", video_id);
        params.put("video_name", video_name);
        params.put("option", option);
        byte[] data = getRequestData(params, "utf-8").toString().getBytes();//获得请求体
        try {

            URL url = new URL(requestURL);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                     //处理服务器的响应结果
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return "err: " + e.getMessage().toString();
        }
        return "-1";
    }

    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    public static String getRequestURL(String account, String pageType) {
        String url = "http://i.cs.hku.hk/~" + account + "/php/" + pageType + ".php";
        return url;
    }
}
