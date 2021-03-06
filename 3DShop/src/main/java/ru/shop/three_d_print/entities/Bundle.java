package ru.shop.three_d_print.entities;

import ru.shop.three_d_print.formatting.FormatText;

import javax.persistence.*;

/**
 * This class is a wrapper around the product class and stores the number of products of the same type.
 */
@Entity
@Table(name = "t_bundle")
public class Bundle
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Product product;
    private int quantity;

    public Bundle(){}

    public Bundle(Product product, int quantity)
    {
        this.product = product;
        this.quantity = quantity;
    }

    public String getName() { return product.getName(); }

    public Product getProduct() { return product; }
    
    public void setProduct(Product product) { this.product = product; }

    public Long getProductId() { return product.getId(); }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return product.getPrice() * quantity; }

    public String getViewPrice() { return FormatText.formatIntToViewPrice(getPrice()); }
}
