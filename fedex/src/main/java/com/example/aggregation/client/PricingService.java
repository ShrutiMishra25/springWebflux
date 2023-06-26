
package com.example.aggregation.client;

import lombok.AllArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Service
public class PricingService {
    @Autowired
    private Client client;

    class Request {
        String orderId;
        CompletableFuture<Map<String, Double>> future;
    }

    LinkedBlockingDeque<Request> queue = new LinkedBlockingDeque<Request>();

    public Map<String, Double> queryOrderBatch(String pricing) throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, Double>> future = new CompletableFuture<>();
        Request request = new Request();
        request.future = future;
        request.orderId = pricing;
        queue.add(request);
        doBusiness();
        return future.get();

    }
    public void doBusiness() {


        List<Request> requests = new ArrayList<>();
        List<String> orders = new ArrayList<>();
        Set<String> strSet =new HashSet<>();
        if (queue.size() == 2) {
            for (int i = 0; i < queue.size(); i++) {
                Request request = queue.poll();
                requests.add(request);
                if(!orders.contains(request.orderId))
                {
                    orders.add(request.orderId);
                }
            }
            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add("order", String.join(",", strSet));

            System.out.println("Batch size: " + queue.size());
            Map<String, Double> responses = client.getPricingQueue(orders);
            for (Request request : requests) {
                //  for(String orderId :request.orderId){
                // if (orderId.equals(responses.get("OrderId"))) {
                request.future.complete(responses);
                // break;
                //}
            }

        }
    }

}






