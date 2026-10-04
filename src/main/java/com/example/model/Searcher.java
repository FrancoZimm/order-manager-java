package com.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Searcher {

    // Fix en el else if que funcionaba mal y retornaba false
    public boolean searchExactPhrase(String phrase, List<String> list) {
        for (String item : list) {
            if (item.equals(phrase)) {
                return true;
            } 
        }
        return false;
    }


    public boolean searchWord(String word, List<String> list) {
        return list.contains(word);
    }

    
    public String getWordByIndex(List<String> list, int index) {
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null; 
    }

    // New: find elements starting with a given prefix
    public List<String> searchByPrefix(String prefix, List<String> list) {
        List<String> results = new ArrayList<>();
        for (String element : list) {
            if (element.startsWith(prefix)) {
                results.add(element);
            }
        }
        return results;
    }

    // New: filter all elements that contain a given keyword
    public List<String> filterByKeyword(String keyword, List<String> list) {
        List<String> results = new ArrayList<>();
        for (String element : list) {
            if (element.contains(keyword)) {
                results.add(element);
            }
        }
        return results;
    }

    // New: find an Order by its ID from a list of Orders
    public Order findById(List<Order> orders, String id) {
        Objects.requireNonNull(orders, "orders must not be null");
        if (id == null || id.isBlank()) return null;
        for (Order o : orders) {
            if (id.equals(o.getId())) {
                return o;
            }
        }
        return null;
    }
}