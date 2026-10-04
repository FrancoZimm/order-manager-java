package com.example;

import java.io.IOException;

import com.example.controller.*;
import com.example.model.*;
import com.example.view.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import javax.swing.SwingUtilities;  // añadido

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Starting Order Management System...");

        // ensure 'orders' is visible after the try/catch
        List<Order> orders = List.of();

        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = Main.class.getResourceAsStream("/orders.json");
            if (is == null) {
                log.error(" No se encontró 'orders.json' en src/main/resources.");
                orders = List.of(); // keep non-null
            } else {
                orders = mapper.readValue(is, new TypeReference<List<Order>>() {});
                for (Order order : orders) {
                    log.debug("Loaded order: {}", order.getId());
                }
            }

            if (orders.isEmpty()) {
                log.warn(" No se cargó ningún pedido desde el archivo JSON.");
            } else {
                log.info(" Se cargaron {} pedidos correctamente.", orders.size());
            }

            // Initialize MVC en el hilo de eventos de Swing (EDT)
            final List<Order> finalOrders = orders;
            SwingUtilities.invokeLater(() -> {
                OrderView view = new OrderView();
                // crear el controlador es suficiente, no hace falta start()
                new OrderController(view, finalOrders);
            });

        } catch (IOException e) {
            log.error(" Error cargando los pedidos", e);
        }
    }
}
