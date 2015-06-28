/**
 * The MIT License
 * Copyright (c) 2014-2015 Techcable
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.techcable.npclib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class HttpUtils {

    private static JSONParser PARSER = new JSONParser();

    public static Object getJson(String rawUrl) {
        BufferedReader reader = null;
        try {
            URL url = new URL(rawUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            return PARSER.parse(result.toString());
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

    public static Object postJson(String url, JSONArray body) {
        String rawResponse = post(url, body.toJSONString());
        if (rawResponse == null) return null;
        try {
            return PARSER.parse(rawResponse);
        } catch (Exception e) {
            return null;
        }
    }

    public static String post(String rawUrl, String body) {
        BufferedReader reader = null;
        OutputStream out = null;

        try {
            URL url = new URL(rawUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            out = connection.getOutputStream();
            out.write(body.getBytes());
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer result = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (out != null) out.close();
                if (reader != null) reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

}
