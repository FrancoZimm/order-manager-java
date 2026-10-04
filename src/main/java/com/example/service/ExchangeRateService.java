package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Servicio para obtener el tipo de cambio EUR -> USD
 * usando la API HTTP y la librería OkHttp.
 */
public class ExchangeRateService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public double getEuroToUsdRate() throws IOException {

        // API externa de tipos de cambio. Base: EUR
        String url = "https://open.er-api.com/v6/latest/EUR";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("Error HTTP al obtener tipo de cambio: " + response.code());
            }

            String json = response.body().string();

            JsonNode root = mapper.readTree(json);
            double rate = root.path("rates").path("USD").asDouble();

            if (rate <= 0) {
                throw new IOException("Tipo de cambio inválido: " + rate);
            }

            return rate;
        }
    }
}
