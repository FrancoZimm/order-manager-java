package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Article {

    private String name;
    private int quantity;

    @JsonProperty("unitPrice")   // ← mapea "unitPrice" del JSON a este campo
    private double price;
    
    private double discount; 


    public Article(String name, int quantity, double price, double discount) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }
    
    public Article() {}

    public String getName() { 
        return name; }

    public void setName(String name) { 
        this.name = name; 
    }

    public int getQuantity() { 
        return quantity; 
    }

    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }

    public double getPrice() { 
        return price; 
    }

    public void setPrice(double price) { 
        this.price = price; 
    }

    public double getDiscount() { 
        return discount; 
    }

    public void setDiscount(double discount) { 
        this.discount = discount; 
    }


    public double getGrossAmount() {
        return quantity * price;
    }

    public double getDiscountedAmount() {
        Calculator calculator = new Calculator();
        return calculator.discount(getGrossAmount(), discount);
    }

    @Override
    public String toString() {
        return "Article {" + "name='" + name + '\'' + ", quantity=" + quantity + ", price=" + price + ", discount="          
                + discount + ", grossAmount=" + getGrossAmount() + ", discountedAmount=" + getDiscountedAmount() + '}';
    }
}
