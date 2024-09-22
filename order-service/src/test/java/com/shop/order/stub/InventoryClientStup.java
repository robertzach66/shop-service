package com.shop.order.stub;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStup {

    public static void stubInventoryCall(final String skuCode, final Integer quantity) {
        stubFor(get(urlEqualTo("/api/inventory/instock?skuCode=" + skuCode + "&quantity=" + quantity))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "aaplication/json")
                        .withBody("true")));
    }
}
