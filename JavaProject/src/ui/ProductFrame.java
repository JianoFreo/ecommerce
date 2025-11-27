package src.ui;

import src.dao.ProductDAO; // Import Product DAO
import src.model.Product; // Import Product model
import javax.swing.*; // Swing UI
import javax.swing.table.*; // Table UI
import java.awt.*; // Layout
import java.awt.event.*; // Mouse and button events
import java.util.List; // List interface

public class ProductFrame extends JFrame { // Product management window
    private JTable table; // Table to show products
    private DefaultTableModel model; // Table model
    private JTextField txtName, txtPrice, txtQty; // Form fields
    private ProductDAO dao = new ProductDAO(); // DAO instance

    public ProductFrame() {
        setTitle("E-Commerce Product Management"); // Window title
        setSize(600, 400); // Window size
        setLayout(new BorderLayout()); // Border layout
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Close app on exit
        setLocationRelativeTo(null); // Center window

        // Table setup
        model = new DefaultTableModel(new String[]{"ID", "Name", "Price", "Quantity"}, 
        0); // Column headers
        table = new JTable(model); // Create table with model
        loadProducts(); // Load data from DB
        add(new JScrollPane(table), BorderLayout.CENTER); // Add table with scroll

        // Form setup
        JPanel form = new JPanel(new GridLayout(10, 2)); // 4 rows, 2 columns
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();
        JButton btnAdd = new JButton("Add item");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");

        form.add(new JLabel("Name:")); form.add(txtName);
        form.add(new JLabel("Price:")); form.add(txtPrice);
        form.add(new JLabel("Quantity:")); form.add(txtQty);
        form.add(btnAdd); form.add(btnUpdate);
        form.add(btnDelete);

        add(form, BorderLayout.SOUTH); // Add form to bottom

        // Button actions
        /**btnLogin.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        login(); // call the login method
    }
}); 

So e -> is just a shorter, cleaner way of writing an
 anonymous method that handles the button click
 
 e -> login()
This is a lambda expression.
e represents the event object (ActionEvent) automatically passed when the button is clicked.
-> means “maps to” or “do the following with this event”.
login() is the method we want to call when the button is clicked.*/
        btnAdd.addActionListener(e -> {
          
            dao.addProduct(new Product(0, txtName.getText(),
                    Double.parseDouble(txtPrice.getText()),
                    Integer.parseInt(txtQty.getText()))); // Add new product
            refresh(); // Refresh table
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow(); // Get selected row
            if (row >= 0) { // If a row is selected
                int id = (int) model.getValueAt(row, 0); // Get ID
                dao.updateProduct(new Product(id,
                        txtName.getText(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtQty.getText()))); // Update product
                refresh(); // Refresh table
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow(); // Selected row
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0); // Get ID
                dao.deleteProduct(id); // Delete product
                refresh(); // Refresh table
            }
        });

        // Table row click populates form fields
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtName.setText(model.getValueAt(row, 1).toString());
                txtPrice.setText(model.getValueAt(row, 2).toString());
                txtQty.setText(model.getValueAt(row, 3).toString());
            }
        });

        setVisible(true); // Show window
    }

    // Load products from DB into table
    private void loadProducts() {
        model.setRowCount(0); // Clear table
        List<Product> products = dao.getAllProducts(); // Get products from DAO
        for (Product p : products)
            model.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getQuantity()}); // Add row
    }

    // Refresh table and clear form fields
    private void refresh() {
        loadProducts();
        txtName.setText("");
        txtPrice.setText("");
        txtQty.setText("");
    }
}
