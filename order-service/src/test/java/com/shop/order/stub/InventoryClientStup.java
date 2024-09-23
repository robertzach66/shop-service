package com.shop.order.stub;

import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Slf4j
public class InventoryClientStup {

    public static void stubInventoryCall(final String skuCode, final Integer quantity) {
        String url = "/api/inventory/instock";
        log.info("stubbing url: {}", url);
        stubFor(get(urlPathEqualTo(url))
                .withQueryParam("skuCode", equalTo(skuCode))
                .withQueryParam("quantity", equalTo(String.valueOf(quantity)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "aaplication/json")
                        .withBody("true")));
    }
}
