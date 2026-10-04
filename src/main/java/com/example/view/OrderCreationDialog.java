package com.example.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.example.model.Article;
import com.example.model.Order;

/**
 * Ventana modal para crear un nuevo pedido.
 * Permite:
 *  - Introducir el ID del pedido
 *  - Añadir varios articulos (nombre, cantidad, precio, descuento)
 *  - Ver la lista de articulos antes de guardar
 */
public class OrderCreationDialog extends JDialog {

    private final JTextField orderIdField = new JTextField(15);

    private final JTextField articleNameField = new JTextField(10);
    private final JTextField qtyField = new JTextField(5);
    private final JTextField priceField = new JTextField(7);
    private final JTextField discountField = new JTextField(5);

    private final DefaultListModel<String> articleListModel = new DefaultListModel<>();
    private final JList<String> articleList = new JList<>(articleListModel);

    private final JButton addArticleButton = new JButton("Add article");
    private final JButton saveButton = new JButton("Save order");
    private final JButton cancelButton = new JButton("Cancel");

    private final List<Article> articles = new ArrayList<>();
    private Order createdOrder;  // null si se cancela

    public OrderCreationDialog(Frame owner) {
        super(owner, "Create Order", true); // modal

        setLayout(new BorderLayout(8, 8));

        // Panel superior: ID del pedido
        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        idPanel.add(new JLabel("Order ID:"));
        idPanel.add(orderIdField);
        add(idPanel, BorderLayout.NORTH);

        // Panel central: formulario de articulos + lista
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JPanel articleFormPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        articleFormPanel.setBorder(BorderFactory.createTitledBorder("New article"));

        articleFormPanel.add(new JLabel("Name (sin -):"));
        articleFormPanel.add(articleNameField);

        articleFormPanel.add(new JLabel("Qty:"));
        articleFormPanel.add(qtyField);

        articleFormPanel.add(new JLabel("Price (EUR):"));
        articleFormPanel.add(priceField);

        articleFormPanel.add(new JLabel("Discount (%):"));
        articleFormPanel.add(discountField);

        articleFormPanel.add(addArticleButton);

        centerPanel.add(articleFormPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Articles in order"));
        listPanel.add(new JScrollPane(articleList), BorderLayout.CENTER);

        centerPanel.add(listPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Panel inferior: botones de guardar / cancelar
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Listeners
        addArticleButton.addActionListener(this::onAddArticle);
        saveButton.addActionListener(this::onSaveOrder);
        cancelButton.addActionListener(e -> {
            createdOrder = null;
            dispose();
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    private void onAddArticle(ActionEvent e) {
        String name = articleNameField.getText().trim();
        String qtyStr = qtyField.getText().trim();
        String priceStr = priceField.getText().trim();
        String discStr = discountField.getText().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || discStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All article fields are required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);
            double disc = Double.parseDouble(discStr);

            Article article = new Article(name, qty, price, disc);
            articles.add(article);

            // Mostrar resumen en la lista
            String summary = String.format("%s | qty=%d | price=%.2f | disc=%.2f%%",
                    name, qty, price, disc);
            articleListModel.addElement(summary);

            // Limpiar campos de articulo para el siguiente
            articleNameField.setText("");
            qtyField.setText("");
            priceField.setText("");
            discountField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity, price and discount must be numeric.",
                    "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSaveOrder(ActionEvent e) {
        String id = orderIdField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Order ID cannot be empty.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (articles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Add at least one article to the order.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        createdOrder = new Order(id, new ArrayList<>(articles));
        dispose();
    }

    /**
     * Devuelve el pedido creado o null si se cancelo.
     */
    public Order getCreatedOrder() {
        return createdOrder;
    }
}
