package eu.michalnowicki.shoperdemo.backend.domain.incoming_payment;

import eu.michalnowicki.shoperdemo.backend.domain.order.Order;
import eu.michalnowicki.shoperdemo.backend.domain.order.OrderFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlueMediaCsvParserTest {
   
   @Mock
   private OrderFacade orderFacade;
   
   @BeforeEach
   public void init() {
      Order order226 = Order.builder().id(226L).build();
      Order order227 = Order.builder().id(227L).build();
      
      when(orderFacade.findById(226L)).thenReturn(order226);
      when(orderFacade.findById(227L)).thenReturn(order227);
   }
   
   @InjectMocks
   private BlueMediaCsvParser parser;
   
   
   @Test
   void shouldReturnOrders() {
//      given:
      
      String csv = "2020-09-07,2020-09-07,1,16766,TheCompany,SETT_0090885257,8205.90,2020-09-08\n" +
            "2020-09 07,u,226_1599482069_320ebda4fef721fce,F49HVIFZ,4224.61,42.25,42.25,3,mtransfer,,SETT_0090885257\n" +
            "2020-09-07,u,227_1599514398_41786a2bb23a9da1b,F465KK2C,5957.02,59.57,59.57,1064,IGO PBL,,SETT_0090885257\n" +
            "2020-09-07,w,223_1599490983_e42f8f5a1ab9a1b90,ZWTR_0090775726,1873.91,0,0,,,,null\n" +
            "2,10181.63,101.82,101.82,10079.81\n" +
            "1,1873.91,0,0,1873.91\n" +
            "8205.90,CDF60515";
      
      final List<String> lines = csv.lines().collect(Collectors.toList());

//      when:
      final Set<Order> orders = parser.getOrders(lines);

//      then:
      
      assertThat(orders)
            .isNotNull()
            .extracting(Order::getId).contains(226L, 227L)
            .size().isEqualTo(2);
      
   }
   
   
}