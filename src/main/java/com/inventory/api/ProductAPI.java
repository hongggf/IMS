package com.inventory.api;

import java.net.URI;
import java.net.http.*;

public class ProductAPI {

    public static String getProducts() throws Exception {

        HttpClient client =
                HttpClient.newHttpClient();

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(new URI(
                          "https://fakestoreapi.com/products"))
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                  request,
                  HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}