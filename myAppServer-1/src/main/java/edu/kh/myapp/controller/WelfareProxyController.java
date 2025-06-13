package edu.kh.myapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/welfare-curl")
public class WelfareProxyController {

    @Value("${welfare.api.service-key}")
    private String serviceKey;

    private final XmlMapper xmlMapper = new XmlMapper();

    @GetMapping("/list")
    public Map<String, Object> getWelfareList(
            @RequestParam(name = "pageNo", required = false, defaultValue = "1") String pageNo,
            @RequestParam(name = "numOfRows", required = false, defaultValue = "10") String numOfRows) {

        try {
            String urlStr = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B554287/LocalGovernmentWelfareInformations/LcgvWelfarelist")
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .build(false)
                    .toUriString();

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.setRequestProperty("User-Agent", "curl/8.0");
            conn.setRequestProperty("Accept", "*/*");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder responseContent = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                responseContent.append(inputLine);
            }
            in.close();

            JsonNode node = xmlMapper.readTree(responseContent.toString());
            Map<String, Object> result = xmlMapper.convertValue(node, Map.class);

            System.out.println(result);  // 디버그 확인

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call API via curl style", e);
        }
    }
    
    @GetMapping("/reverse-geocode")
    public ResponseEntity<String> reverseGeocode(@RequestParam("lon") double lon, @RequestParam("lat") double lat) throws Exception {
        String apiKey = "6D96057A-FC81-30BE-B98E-39266237CBD1";
        String url = "https://api.vworld.kr/req/address?service=address&request=getAddress" +
                     "&version=2.0&format=json&type=PARCEL" +
                     "&point=" + lon + "," + lat +
                     "&crs=epsg:4326&key=" + apiKey;

        URL requestUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) requestUrl.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return ResponseEntity.ok(response.toString());
    }
}