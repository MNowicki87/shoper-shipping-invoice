package eu.michalnowicki.shoperdemo.backend.shoper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderProduct;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderProductFactory;
import eu.michalnowicki.shoperdemo.backend.domain.order.StatusFacade;
import eu.michalnowicki.shoperdemo.backend.domain.product.Product;
import eu.michalnowicki.shoperdemo.backend.domain.product.ProductMapper;
import eu.michalnowicki.shoperdemo.backend.domain.product.ProductService;
import eu.michalnowicki.shoperdemo.backend.shoper.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static eu.michalnowicki.shoperdemo.backend.shoper.ShoperRestClient.sendRequest;

@Service
@Log
@RequiredArgsConstructor
public class ShoperFacade {
   private final ShoperRestData restData;
   private final OrderFacade orderFacade;
   private final StatusFacade statusFacade;
   private final ProductService productService;
   private final ProductMapper productMapper;
   
   private static final String AUTHORIZATION = "Authorization";
   private static final String AUTH_ENDPOINT = "auth";
   private static final String ORDERS_ENDPOINT = "orders";
   private static final String PRODUCTS_ENDPOINT = "products";
   private static final String STATUSES_ENDPOINT = "statuses";
   private static final String ORDER_PRODUCTS_ENDPOINT = "order-products";
   
   public void getOrderById(long orderId) {
      final var json = sendRequest(prepareGetRequest(ORDERS_ENDPOINT + "/" + orderId));
      var orderDto = getDto(json, OrderDto.class);
      orderFacade.processNewOrderInfo(orderDto);
   }
   
   public void getAllOrders() {
      processOrders(null);
   }
   
   public void getNewOrders() {
      processOrders(newOrdersFilter());
   }
   
   public void getProductsOfOrder(final Long orderId) {
      
      int page = 1;
      List<OrderProduct> orderProducts = new ArrayList<>();
      OrderProductsDtoList orderProductsList;
      
      do {
         final var url = createFilteredRequestUrl(ORDER_PRODUCTS_ENDPOINT, orderProductsFilter(orderId), page);
         final var request = prepareGetRequest(url);
         var json = sendRequest(request);
         orderProductsList = getDto(json, OrderProductsDtoList.class);
         if (null != orderProductsList) {
            for (OrderProductsDto p : orderProductsList.dtos) {
               final var orderProduct = mapOrderProduct(p);
               orderProducts.add(orderProduct);
            }
         }
         page = (orderProductsList != null ? orderProductsList.page : page) + 1;
         
      } while (page <= Objects.requireNonNull(orderProductsList).pages);
      
      orderFacade.setProductsForOrder(orderProducts);
   }
   
   public void updateBearerAuthToken() {
      final var url = restData.getRestApiBaseUrl() + AUTH_ENDPOINT;
      final var token = ShoperRestClient.getToken(restData.getClientId(), restData.getClientSecret(), url);
      restData.setToken(token);
   }
   
   public void syncStatusRepo() {
      final var request = prepareGetRequest(STATUSES_ENDPOINT + "?limit=50");
      final var json = sendRequest(request);
      List<StatusDto> statusDtos = getStatusDtos(json);
      assert statusDtos != null : "Fetching statuses failed";
      statusDtos.forEach(statusDto -> statusFacade.saveOrUpdate(StatusFacade.map(statusDto)));
   }
   
   private Request prepareGetRequest(final String endpointPath) {
      return new Request.Builder()
            .url(restData.getRestApiBaseUrl() + endpointPath)
            .get()
            .addHeader(AUTHORIZATION, restData.getToken())
            .build();
   }
   
   private <T> T getDto(final String json, Class<T> valueType) {
      final var mapper = new ObjectMapper();
      Optional<T> dto = Optional.empty();
      try {
         dto = Optional.ofNullable(mapper.readValue(json, valueType));
      } catch (JsonProcessingException e) {
         e.printStackTrace();
      }
      return dto.orElse(null);
   }
   
   private void processOrders(final String filter) {
      int page = 1;
      int pages = 1;
      List<OrderDto> ordersList = new ArrayList<>();
      do {
         var url = createFilteredRequestUrl(ORDERS_ENDPOINT, filter, page);
         final var request = prepareGetRequest(url);
         log.info("Requesting orders from page " + page + " of " + pages);
         var json = sendRequest(request);
         assert json != null : "Response for Orders is null";
         
         ObjectMapper mapper = new ObjectMapper();
         
         try {
            ArrayList<OrderDto> temp = mapper
                  .readerForListOf(OrderDto.class)
                  .readValue(mapper.readTree(json)
                        .path("list"));
            log.info("Got " + temp.size() + " orders from page " + page);
            
            page = mapper.readTree(json).path("page").asInt() + 1;
            pages = mapper.readTree(json).path("pages").asInt();
            ordersList.addAll(temp);
         } catch (IOException e) {
            log.warning(e.getMessage());
         }
         
      } while (page <= pages);
      log.info(String.format("Found %s orders", ordersList.size()));
      ordersList.forEach(orderFacade::processNewOrderInfo);
   }
   
   private HttpUrl createFilteredRequestUrl(final String endpoint, final String filter, final int page) {
      return HttpUrl.get(restData.getRestApiBaseUrl()).newBuilder()
            .addPathSegment(endpoint)
            .addQueryParameter("limit", "50")
            .addQueryParameter("page", String.valueOf(page))
            .addQueryParameter("filters", filter)
            .build();
   }
   
   private Request prepareGetRequest(final HttpUrl httpUrl) {
      return new Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader(AUTHORIZATION, restData.getToken())
            .build();
   }
   
   private String newOrdersFilter() {
      var latestOrder = orderFacade.getLatestOrderNumber();
      return String.format("{\"order_id\":{\">\":%d}}", latestOrder);
   }
   
   private String orderProductsFilter(final Long orderId) {
      return String.format("{\"order_id\":\"%d\"}", orderId);
   }
   
   private OrderProduct mapOrderProduct(final OrderProductsDto p) {
      final var product = productService.getOrSave(getProductById(p.productId));
      final var order = orderFacade.findById(p.orderId);
      
      return OrderProductFactory.map(p, order, product);
   }
   
   private Product getProductById(long productId) {
      final var request = prepareGetRequest(PRODUCTS_ENDPOINT + "/" + productId);
      final var json = sendRequest(request);
      final var productDto = Optional.ofNullable(getDto(json, ProductDto.class));
      var product = new Product();
      if (productDto.isPresent()) {
         product = productMapper.map(productDto.get());
      }
      return productService.getOrSave(product);
   }
   
   private List<StatusDto> getStatusDtos(final String json) {
      var mapper = new ObjectMapper();
      List<StatusDto> statusDtos = null;
      try {
         final var path = mapper.readTree(json).path("list");
         final var collectionType = TypeFactory.defaultInstance()
               .constructCollectionType(List.class, StatusDto.class);
         statusDtos = mapper.readerFor(collectionType).readValue(path);
      } catch (IOException e) {
         e.printStackTrace();
      }
      return statusDtos;
   }
}
