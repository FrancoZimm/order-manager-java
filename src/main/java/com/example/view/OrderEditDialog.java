package com.example.view;

import com.example.model.Article;
import com.example.model.Order;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialogo para editar un pedido existente.
 * Solo permite cambiar cantidad y descuento de los articulos.
 */
public class OrderEditDialog extends JDialog {

    private final Order order;
    private final List<Article> articles;

    private final DefaultListModel<String> articleListModel = new DefaultListModel<>();
    private final JList<String> articleList = new JList<>(articleListModel);

    private final JTextField qtyField = new JTextField(5);
    private final JTextField discountField = new JTextField(5);

    private final JButton applyButton = new JButton("Apply to selected");
    private final JButton saveButton  = new JButton("Save changes");
    private final JButton cancelButton = new JButton("Cancel");

    private boolean saved = false;

    public OrderEditDialog(Frame owner, Order order) {
        super(owner, "Edit Order " + order.getId(), true);
        this.order = order;
        this.articles = order.getArticles();

        setLayout(new BorderLayout(8, 8));

        // Lista de artículos
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(
                BorderFactory.createTitledBorder("Articles in order"));
        fillArticleList();
        listPanel.add(new JScrollPane(articleList), BorderLayout.CENTER);
        add(listPanel, BorderLayout.CENTER);

        // Panel de edición de cantidad y descuento
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editPanel.setBorder(
                BorderFactory.createTitledBorder("Edit quantity / discount"));

        editPanel.add(new JLabel("Qty:"));
        editPanel.add(qtyField);

        editPanel.add(new JLabel("Discount (%):"));
        editPanel.add(discountField);

        editPanel.add(applyButton);

        add(editPanel, BorderLayout.NORTH);

        // Botones inferiores
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Listeners
        articleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int index = articleList.getSelectedIndex();
                if (index >= 0) {
                    Article a = articles.get(index);
                    qtyField.setText(String.valueOf(a.getQuantity()));
                    discountField.setText(String.valueOf(a.getDiscount()));
                }
            }
        });

        applyButton.addActionListener(this::onApplyToSelected);
        saveButton.addActionListener(this::onSave);
        cancelButton.addActionListener(e -> {
            saved = false;
            dispose();
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(550, 350);
        setLocationRelativeTo(owner);
    }

    private void fillArticleList() {
        articleListModel.clear();
        for (Article a : articles) {
            articleListModel.addElement(articleSummary(a));
        }
    }

    private String articleSummary(Article a) {
        return String.format("%s | qty=%d | price=%.2f | disc=%.2f%%",
                a.getName(), a.getQuantity(), a.getPrice(), a.getDiscount());
    }

    private void onApplyToSelected(ActionEvent e) {
        int index = articleList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an article first.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = qtyField.getText().trim();
        String discStr = discountField.getText().trim();

        if (qtyStr.isEmpty() || discStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Quantity and discount are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int qty = Integer.parseInt(qtyStr);
            double disc = Double.parseDouble(discStr);

            Article a = articles.get(index);
            a.setQuantity(qty);
            a.setDiscount(disc);

            // Actualizar resumen en la lista
            articleListModel.set(index, articleSummary(a));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be integer and discount numeric.",
                    "Validation", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave(ActionEvent e) {
        // Los cambios ya están aplicados sobre los articulos del pedido
        saved = true;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }
}
