package com.example.controller;

import com.example.model.Order;
import com.example.model.Searcher;
import com.example.service.ExchangeRateService;
import com.example.view.OrderView;
import com.example.view.OrderCreationDialog;
import com.example.view.OrderEditDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    private final OrderView view;
    private final List<Order> orders;
    private final Searcher searcher;
    private final ExchangeRateService exchangeRateService;

    public OrderController(OrderView view, List<Order> orders) {
        this.view = Objects.requireNonNull(view, "view must not be null");
        this.orders = Objects.requireNonNull(orders, "orders must not be null");
        this.searcher = new Searcher();
        this.exchangeRateService = new ExchangeRateService();

        // IDs iniciales
        refreshOrderIdsInView();

        // Botón buscar
        this.view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrder();
            }
        });

        // Botón crear
        this.view.getCreateButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createOrder();
            }
        });

        // Botón borrar
        this.view.getDeleteButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

        // Botón editar
        this.view.getEditButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editOrder();
            }
        });

        log.debug("OrderController inicializado con {} órdenes.", this.orders.size());
    }

    // --------- Utilidad: refrescar lista de IDs ---------
    private void refreshOrderIdsInView() {
        List<String> ids = this.orders.stream()
                                      .map(Order::getId)
                                      .collect(Collectors.toList());
        this.view.setOrderIds(ids);
    }

    // --------- Buscar pedido ---------
    private void searchOrder() {
        String id = view.getSearchId();
        if (id == null || id.isBlank()) {
            log.warn("Búsqueda cancelada: id vacío o nulo.");
            view.showMessage("Please enter an order id.");
            return;
        }

        try {
            Order found = searcher.findById(orders, id);

            if (found != null) {
                log.info("Orden {} encontrada.", id);

                String currency = view.getSelectedCurrency();
                double rate = resolveRateBasedOnCurrency(currency);

                log.debug("Selected currency = {}, resolved rate = {}", currency, rate);

                view.displayOrder(found, rate, currency);

            } else {
                log.info("Orden {} no encontrada.", id);
                view.showMessage("Order not found.");
                view.clearOrderDetails();
            }
        } catch (Exception ex) {
            log.error("Error buscando la orden {}: {}", id, ex.getMessage(), ex);
            view.showMessage("An unexpected error occurred while searching the order.");
        }
    }

    // --------- Cambio de divisa ---------
    private double resolveRateBasedOnCurrency(String currency) {
        if (currency == null || "EUR".equalsIgnoreCase(currency)) {
            log.debug("Mostrando solo en EUR (factor = 1.0)");
            return 1.0;
        }

        try {
            double rate = exchangeRateService.getEuroToUsdRate();
            log.debug("Tipo de cambio EUR->USD obtenido del servicio: {}", rate);
            return rate;
        } catch (IOException ioe) {
            log.warn("No se pudo obtener el tipo de cambio EUR/USD online. Se mostraran solo valores en EUR.", ioe);
            view.showMessage("No se pudo obtener el tipo de cambio actual. Se muestran solo importes en EUR.");
            return 1.0;
        }
    }

    // --------- Crear pedido ---------
    private void createOrder() {
        SwingUtilities.invokeLater(() -> {
            OrderCreationDialog dialog = new OrderCreationDialog(view);
            dialog.setVisible(true); // modal

            Order newOrder = dialog.getCreatedOrder();
            if (newOrder == null) {
                log.debug("Creacion de pedido cancelada por el usuario.");
                return;
            }

            String rawId = newOrder.getId();
            if (rawId == null) {
                view.showMessage("Order ID cannot be empty.");
                return;
            }

            String id = rawId.trim();
            if (id.isEmpty()) {
                view.showMessage("Order ID cannot be empty.");
                return;
            }

            // Unicidad de ID (sin formato especial)
            boolean exists = orders.stream().anyMatch(o -> id.equals(o.getId()));
            if (exists) {
                view.showMessage("Order ID already exists. Please use a different ID.");
                return;
            }

            newOrder.setId(id);

            orders.add(newOrder);
            log.info("Nuevo pedido {} creado.", id);

            refreshOrderIdsInView();

            boolean saved = saveOrdersToJson();
            if (saved) {
                view.showMessage("Order created and saved successfully.");
            }
        });
    }

    // --------- Borrar pedido ---------
    private void deleteOrder() {
        String id = view.getSearchId();
        if (id == null || id.isBlank()) {
            view.showMessage("Please enter an order ID to delete.");
            return;
        }

        Order found = searcher.findById(orders, id);
        if (found == null) {
            view.showMessage("Order not found. Nothing to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Delete order " + id + "?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Iterator<Order> it = orders.iterator();
        while (it.hasNext()) {
            Order o = it.next();
            if (id.equals(o.getId())) {
                it.remove();
                break;
            }
        }

        log.info("Pedido {} borrado.", id);

        refreshOrderIdsInView();
        view.clearOrderDetails();

        boolean saved = saveOrdersToJson();
        if (saved) {
            view.showMessage("Order deleted and changes saved.");
        }
    }

    // --------- Editar pedido (cantidad / descuento) ---------
    private void editOrder() {
        String id = view.getSearchId();
        if (id == null || id.isBlank()) {
            view.showMessage("Please enter an order ID to edit.");
            return;
        }

        Order found = searcher.findById(orders, id);
        if (found == null) {
            view.showMessage("Order not found. Nothing to edit.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            OrderEditDialog dialog = new OrderEditDialog(view, found);
            dialog.setVisible(true);

            if (!dialog.isSaved()) {
                log.debug("Edicion de pedido {} cancelada por el usuario.", id);
                return;
            }

            // Cambios ya aplicados sobre el pedido 'found'
            boolean saved = saveOrdersToJson();
            if (saved) {
                view.showMessage("Order edited and saved successfully.");

                // Refrescar detalle segun moneda actual
                String currency = view.getSelectedCurrency();
                double rate = resolveRateBasedOnCurrency(currency);
                view.displayOrder(found, rate, currency);
            }
        });
    }

    // --------- Guardar JSON ---------
    private boolean saveOrdersToJson() {
    try {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Intentar localizar el JSON en las rutas mas probables
        Path pathModule = Paths.get("uem/src/main/resources/orders.json");
        Path pathSimple = Paths.get("src/main/resources/orders.json");

        Path targetPath;
        if (Files.exists(pathModule)) {
            targetPath = pathModule;
        } else {
            targetPath = pathSimple;
        }

        log.info("Intentando guardar pedidos en: {}", targetPath.toAbsolutePath());

        mapper.writerFor(new TypeReference<List<Order>>() {})
              .writeValue(targetPath.toFile(), orders);

        log.info("Orders saved to JSON file: {}", targetPath.toAbsolutePath());
        return true;
    } catch (IOException e) {
        log.error("Error saving orders to JSON file", e);
        view.showMessage("Error saving orders to JSON file.");
        return false;
    }
    }
}
