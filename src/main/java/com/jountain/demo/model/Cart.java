package com.jountain.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private Set<CartItem> items=new HashSet<>();

    private BigDecimal totalAmount=BigDecimal.ZERO;

    public void addItem(CartItem item) {
        this.items.add(item);
        item.setCart(this);
        updateTotalAmount();
    }
    public void removeItem(CartItem item) {
        this.items.remove(item);
        item.setCart(null);
        updateTotalAmount();
    }
    public void updateTotalAmount() {
        this.totalAmount = items.stream()
                .map(item->{
                            BigDecimal unitPrice = item.getUnitPrice();
                            if(unitPrice == null) {unitPrice = BigDecimal.ZERO;}
                            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                        })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
