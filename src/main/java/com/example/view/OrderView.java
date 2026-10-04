package com.example.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.example.model.Order;

public class OrderView extends JFrame {

    private final JTextField searchField = new JTextField(10);
    private final JButton searchButton = new JButton("Search");
    private final JButton createButton = new JButton("Create Order");
    private final JButton deleteButton = new JButton("Delete Order");
    private final JButton editButton   = new JButton("Edit Order");
    private final JTextArea resultArea = new JTextArea(18, 40);

    private final JComboBox<String> currencyCombo = new JComboBox<>(
            new String[] { "EUR", "USD" }
    );

    private final JList<String> orderIdList = new JList<>();

    public OrderView() {
        setTitle("Order Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/app.png"));
        setIconImage(icon.getImage());

        // ---------- PANEL IZQUIERDO: lista de IDs ----------
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.add(new JLabel("Available order IDs:"), BorderLayout.NORTH);

        JScrollPane idsScroll = new JScrollPane(orderIdList);
        idsScroll.setPreferredSize(new Dimension(150, 300));
        leftPanel.add(idsScroll, BorderLayout.CENTER);

        orderIdList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = orderIdList.getSelectedValue();
                if (selected != null) {
                    searchField.setText(selected);
                }
            }
        });

        add(leftPanel, BorderLayout.WEST);

        // ---------- PANEL CENTRAL: búsqueda + botones + moneda ----------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Fila 1: ID
        JPanel rowId = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowId.add(new JLabel("Order ID:"));
        rowId.add(searchField);
        centerPanel.add(rowId);

        // Fila 2: botones
        JPanel rowButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowButtons.add(searchButton);
        rowButtons.add(createButton);
        rowButtons.add(deleteButton);
        rowButtons.add(editButton);
        centerPanel.add(rowButtons);

        // Fila 3: moneda
        JPanel rowCurrency = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowCurrency.add(new JLabel("Currency:"));
        rowCurrency.add(currencyCombo);
        centerPanel.add(rowCurrency);

        add(centerPanel, BorderLayout.CENTER);

        // ---------- PANEL DERECHO: detalle del pedido ----------
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(new JLabel("Order details:"), BorderLayout.NORTH);

        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setPreferredSize(new Dimension(400, 300));
        rightPanel.add(resultScroll, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getSearchId() {
        return searchField.getText().trim();
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getCreateButton() {
        return createButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getEditButton() {
        return editButton;
    }

    public String getSelectedCurrency() {
        return (String) currencyCombo.getSelectedItem();
    }

    public void setOrderIds(List<String> orderIds) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String id : orderIds) {
            model.addElement(id);
        }
        orderIdList.setModel(model);
    }

    public void displayOrder(Order order, double eurToUsdRate, String currency) {
        if (order == null) {
            resultArea.setText("Order not found.");
            return;
        }

        boolean useUsd = "USD".equalsIgnoreCase(currency) && eurToUsdRate > 0;

        double factor = useUsd ? eurToUsdRate : 1.0;
        String symbol = useUsd ? "$" : "€";

        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(order.getId()).append("\n")
          .append("Currency: ").append(useUsd ? "USD" : "EUR")
          .append("\n\nItems:\n");

        order.getArticles().forEach(article -> {
            double basePriceEur = article.getPrice();
            double priceInCurrency = basePriceEur * factor;

            sb.append("- ")
              .append(article.getName())
              .append(" | Quantity: ").append(article.getQuantity())
              .append(" | Discount: ").append(article.getDiscount()).append("%")
              .append(" | Price (").append(symbol).append("): ")
              .append(String.format("%.2f", priceInCurrency))
              .append("\n");
        });

        double grossEur = order.getGrossTotal();
        double discountedEur = order.getDiscountedTotal();

        double grossInCurrency = grossEur * factor;
        double discountedInCurrency = discountedEur * factor;

        sb.append("\nTotal bruto (").append(symbol).append("): ")
          .append(String.format("%.2f", grossInCurrency))
          .append("\nTotal con descuento (").append(symbol).append("): ")
          .append(String.format("%.2f", discountedInCurrency));

        resultArea.setText(sb.toString());
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void clearOrderDetails() {
        resultArea.setText("");
    }
}
