package com.shopStudy.entity;

import com.shopStudy.constant.ItemSellStatus;
import com.shopStudy.repository.ItemRepository;
import com.shopStudy.repository.MemberRepository;
import com.shopStudy.repository.OrderItemRepository;
import com.shopStudy.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order = new Order(); // 주문서 객체 만들기
        for(int i=0;i<3;i++){
            Item item = this.createItem(); // 상품 만들기
            itemRepository.save(item); // 상품 디비에 저장
            OrderItem orderItem = new OrderItem(); // 주문상품 객체 만들기
            orderItem.setItem(item); // 주문 상품에 위에서 만든 상품 넣기
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order); // 주문 상품에 주문서 넣기
            order.getOrderItems().add(orderItem); // 주문서의 주문상품 리스트 가져와서 위에서 만든 주문상품 넣어주기
        }

        orderRepository.saveAndFlush(order); // 위에서 만든 주문서 3개 디비에 저장
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId()) //
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }

    public Order createOrder() {
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = this.createItem(); // 상품 만들기
            itemRepository.save(item); // 상품 디비에 저장
            OrderItem orderItem = new OrderItem(); // 주문상품 객체 만들기
            orderItem.setItem(item); // 주문 상품에 위에서 만든 상품 넣기
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order); // 주문 상품에 주문서 넣기
            order.getOrderItems().add(orderItem); // 주문서의 주문상품 리스트 가져와서 위에서 만든 주문상품 넣어주기
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        em.flush();
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLodingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class : "+orderItem.getOrder().getClass());
        System.out.println("==========================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("=========================================");
    }
}