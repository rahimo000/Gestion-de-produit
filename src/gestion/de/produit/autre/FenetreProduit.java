package gestion.de.produit.autre;


import gestion.de.produit.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.sql.*;

public class FenetreProduit extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private String[] risques = {"", "Inflammable", "Toxique", "Corrosif", "Explosif", "Comburant", "Nocif", "Acune"};
    private String[] niveauxRisque = {"", "Faible", "Moyen", "Élevé"};
    private String[] types = {"", "Acide", "Base", "Solvant", "Oxydant", "Réducteur", "Gaz", "Toxique", "Explosif", "Sel minéral", "Sel organique", "Liquide", "Poudre", "Wétal"};
    private String[] categories = {"", "Réactif de laboratoire", "Produit industriel", "Additif alimentaire", "Produit Pharmaceutique", "Produit de nettoyage", "Produit minier", "Produit Cosmétique", "Produit Agricole"};  
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    public FenetreProduit() {
        setTitle("Gestion Produits");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Color bgColor = new Color(245, 247, 250);
        getContentPane().setBackground(bgColor);

        // Setup CardLayout for switching between main view and form view
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(bgColor);

        // Main Panel
        JPanel mainViewPanel = createMainViewPanel();
        mainContentPanel.add(mainViewPanel, "mainView");

        // Add the main content panel to the frame
        add(mainContentPanel);

        // Load products from database on startup
        loadProduitsFromDB();
    }

    private JPanel createMainViewPanel() {
        Color bgColor = new Color(245, 247, 250);
        JPanel mainViewPanel = new JPanel(new BorderLayout());
        mainViewPanel.setBackground(bgColor);

        // Buttons Panel (moved to top)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bgColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton retourButton = createModernButton("Retour", Color.GRAY, new Color(100, 100, 100));
        JButton addButton = createModernButton("Saisir des infos", Color.BLUE, new Color(0, 120, 215));
        JButton editButton = createModernButton("Mettre à jour", Color.GRAY, new Color(100, 100, 100));
        JButton deleteButton = createModernButton("Supprimer", Color.RED, new Color(255, 0, 0));
        
        buttonPanel.add(retourButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(addButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(deleteButton);

        // Search Panel (moved to center)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(bgColor);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setBackground(Color.WHITE);
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Rechercher...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(new Color(150, 150, 150));
                    searchField.setText("Rechercher...");
                }
            }
        });
        
        searchField.setForeground(new Color(150, 150, 150));
        searchField.setText("Rechercher...");
        searchPanel.add(searchField);

        // Table Setup
        String[] columns = {"ID", "Nom", "Nom Commercial", "Description", "Formule Chimique", 
                          "Code National", "Code International", "Unité", "Densité", "Risque",
                          "Niveau Risque", "Seuil Minimal", "pH", "Matière Première", "Quantité",
                          "Type", "Catégorie"};
        
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 13) return Boolean.class;
                if (columnIndex == 8 || columnIndex == 11 || columnIndex == 12 || columnIndex == 14) 
                    return Double.class;
                return String.class;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                String niveauRisque = getValueAt(row, 10).toString();
                
                if (isRowSelected(row)) {
                    c.setBackground(new Color(240, 245, 255));
                    c.setForeground(new Color(50, 50, 50));
                } else {
                    switch (niveauRisque) {
                        case "Élevé":
                            c.setBackground(new Color(255, 200, 200));
                            break;
                        case "Moyen":
                            c.setBackground(new Color(255, 235, 156));
                            break;
                        default:
                            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
                    }
                    c.setForeground(new Color(70, 70, 70));
                }
                
                if (c instanceof JComponent) {
                    Object value = getValueAt(row, column);
                    String text = (value == null) ? "" : value.toString();
                    ((JComponent)c).setToolTipText(text.isEmpty() ? null : text);
                }
                
                return c;
            }
        };
        
        table.setRowHeight(40); 
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(240, 245, 255));
        table.setSelectionForeground(new Color(50, 50, 50));
        
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                table.setCursor(row >= 0 ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        header.setBackground(new Color(240, 242, 245));
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(JLabel.CENTER);
                
                if (!isSelected) {
                    String niveauRisque = table.getValueAt(row, 10).toString();
                    switch (niveauRisque) {
                        case "Élevé":
                            setBackground(new Color(255, 200, 200)); 
                            break;
                        case "Moyen":
                            setBackground(new Color(255, 235, 156)); 
                            break;
                        default:
                            setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
                    }
                }
                
                return this;
            }
        };
        
        for (int i = 0; i < columns.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(bgColor);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Main Panel with new layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        
        // Create a container panel for buttons and search
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(bgColor);
        topContainer.add(buttonPanel, BorderLayout.NORTH);
        topContainer.add(searchPanel, BorderLayout.CENTER);
        
        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(tableContainer, BorderLayout.CENTER);
        
        mainViewPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Search Filter
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            
            private void filter() {
                String text = searchField.getText();
                if (text.equals("Rechercher...") || text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1));
                    } catch (Exception ex) {}
                }
            }
        });
        
        // Button Actions
        addButton.addActionListener(e -> showProduitPanel("Ajouter un Produit", -1));
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                showProduitPanel("Modifier Produit", row);
            } else {
                showMessage("Veuillez sélectionner un produit", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmez la suppression de ce produit?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteProduitFromDB((String) model.getValueAt(row, 0));
                    model.removeRow(row);
                }
            } else {
                showMessage("Veuillez sélectionner un produit", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        retourButton.addActionListener(e -> {
            new Main().setVisible(true);
            dispose();
        });

        return mainViewPanel;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void loadProduitsFromDB() {
        model.setRowCount(0); // Clear existing data
        
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM produit ORDER BY idproduit");
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("idproduit"),
                    rs.getString("nom"),
                    rs.getString("NomCommercial"),
                    rs.getString("description"),
                    rs.getString("Formulchimique"),
                    rs.getString("Codenational"),
                    rs.getString("CodeInternational"),
                    rs.getString("Unite"),
                    rs.getDouble("Densité"),
                    rs.getString("Risque"),
                    rs.getString("NiveauRisque"),
                    rs.getDouble("SeuilMinimal"),
                    rs.getDouble("Ph"),
                    rs.getBoolean("MatierePremiere"),
                    rs.getDouble("Quantite"),
                    rs.getString("Type"),
                    rs.getString("Catigorie")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Erreur lors du chargement des produits: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void addProduitToDB(Object[] produitData) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO produit (nom, NomCommercial, description, Formulchimique, Codenational, " +
                "CodeInternational, Unite, Densité, Risque, NiveauRisque, SeuilMinimal, Ph, " +
                "MatierePremiere, Quantite, Type, Catigorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            
            pstmt.setString(1, (String) produitData[1]);
            pstmt.setString(2, (String) produitData[2]);
            pstmt.setString(3, (String) produitData[3]);
            pstmt.setString(4, (String) produitData[4]);
            pstmt.setString(5, (String) produitData[5]);
            pstmt.setString(6, (String) produitData[6]);
            pstmt.setString(7, (String) produitData[7]);
            pstmt.setDouble(8, (Double) produitData[8]);
            pstmt.setString(9, (String) produitData[9]);
            pstmt.setString(10, (String) produitData[10]);
            pstmt.setDouble(11, (Double) produitData[11]);
            pstmt.setDouble(12, (Double) produitData[12]);
            pstmt.setBoolean(13, (Boolean) produitData[13]);
            pstmt.setDouble(14, (Double) produitData[14]);
            pstmt.setString(15, (String) produitData[15]);
            pstmt.setString(16, (String) produitData[16]);
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Refresh the table
            loadProduitsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Erreur lors de l'ajout du produit: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void updateProduitInDB(Object[] produitData) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE produit SET nom=?, NomCommercial=?, description=?, Formulchimique=?, " +
                "Codenational=?, CodeInternational=?, Unite=?, Densité=?, Risque=?, NiveauRisque=?, " +
                "SeuilMinimal=?, Ph=?, MatierePremiere=?, Quantite=?, Type=?, Catigorie=? " +
                "WHERE idproduit=?");
            
            pstmt.setString(1, (String) produitData[1]);
            pstmt.setString(2, (String) produitData[2]);
            pstmt.setString(3, (String) produitData[3]);
            pstmt.setString(4, (String) produitData[4]);
            pstmt.setString(5, (String) produitData[5]);
            pstmt.setString(6, (String) produitData[6]);
            pstmt.setString(7, (String) produitData[7]);
            pstmt.setDouble(8, (Double) produitData[8]);
            pstmt.setString(9, (String) produitData[9]);
            pstmt.setString(10, (String) produitData[10]);
            pstmt.setDouble(11, (Double) produitData[11]);
            pstmt.setDouble(12, (Double) produitData[12]);
            pstmt.setBoolean(13, (Boolean) produitData[13]);
            pstmt.setDouble(14, (Double) produitData[14]);
            pstmt.setString(15, (String) produitData[15]);
            pstmt.setString(16, (String) produitData[16]);
            pstmt.setInt(17, Integer.parseInt(produitData[0].toString()));
            
            pstmt.executeUpdate();
            pstmt.close();
            
            // Refresh the table
            loadProduitsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Erreur lors de la mise à jour du produit: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void deleteProduitFromDB(String produitId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM produit WHERE idproduit=?");
            pstmt.setInt(1, Integer.parseInt(produitId));
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Erreur lors de la suppression du produit: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }

    private JButton createModernButton(String text, Color textColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(Color.WHITE);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(textColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(textColor);
                button.repaint();
            }
        });
        
        return button;
    }

    private void showProduitPanel(String title, int row) {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        formPanel.add(titleLabel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Informations de Base Panel
        JPanel basicInfoPanel = new JPanel();
        basicInfoPanel.setLayout(new BoxLayout(basicInfoPanel, BoxLayout.Y_AXIS));
        basicInfoPanel.setBackground(Color.WHITE);
        basicInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] basicLabels = {"Nom*", "Nom Commercial*", "Description*", "Formule Chimique*"};
        JTextField[] basicFields = new JTextField[basicLabels.length];
        
        for (int i = 0; i < basicLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(basicLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(150, 30));
            fieldPanel.add(label);
            
            basicFields[i] = new JTextField();
            basicFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            basicFields[i].setPreferredSize(new Dimension(300, 30));
            basicFields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10))
            );
            
            if (row >= 0) {
                basicFields[i].setText(model.getValueAt(row, i+1).toString());
            }
            
            fieldPanel.add(basicFields[i]);
            basicInfoPanel.add(fieldPanel);
        }
        
        // Informations Techniques Panel
        JPanel techInfoPanel = new JPanel();
        techInfoPanel.setLayout(new BoxLayout(techInfoPanel, BoxLayout.Y_AXIS));
        techInfoPanel.setBackground(Color.WHITE);
        techInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] techLabels = {"Code National*", "Code International*", "Unité*", "Densité*"};
        JTextField[] techFields = new JTextField[techLabels.length];
        
        for (int i = 0; i < techLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(techLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(150, 30));
            fieldPanel.add(label);
            
            techFields[i] = new JTextField();
            techFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            techFields[i].setPreferredSize(new Dimension(300, 30));
            techFields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            if (row >= 0) {
                techFields[i].setText(model.getValueAt(row, i+5).toString());
            }
            
            fieldPanel.add(techFields[i]);
            techInfoPanel.add(fieldPanel);
        }
        
        // Informations de Sécurité Panel
        JPanel securityInfoPanel = new JPanel();
        securityInfoPanel.setLayout(new BoxLayout(securityInfoPanel, BoxLayout.Y_AXIS));
        securityInfoPanel.setBackground(Color.WHITE);
        securityInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] securityLabels = {"Risque*", "Niveau Risque*", "Seuil Minimal*", "pH*"};
        JTextField[] securityFields = new JTextField[2]; // Pour Seuil Minimal et pH
        
        JComboBox<String> risqueCombo = new JComboBox<>(risques);
        JComboBox<String> niveauRisqueCombo = new JComboBox<>(niveauxRisque);
        
        if (row >= 0) {
            risqueCombo.setSelectedItem(model.getValueAt(row, 9).toString());
            niveauRisqueCombo.setSelectedItem(model.getValueAt(row, 10).toString());
            securityFields[0] = new JTextField(model.getValueAt(row, 11).toString());
            securityFields[1] = new JTextField(model.getValueAt(row, 12).toString());
        } else {
            securityFields[0] = new JTextField();
            securityFields[1] = new JTextField();
        }
        
        for (int i = 0; i < securityLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(securityLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(150, 30));
            fieldPanel.add(label);
            
            if (i == 0) {
                risqueCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                risqueCombo.setPreferredSize(new Dimension(300, 30));
                fieldPanel.add(risqueCombo);
            } else if (i == 1) {
                niveauRisqueCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                niveauRisqueCombo.setPreferredSize(new Dimension(300, 30));
                fieldPanel.add(niveauRisqueCombo);
            } else {
                securityFields[i-2].setFont(new Font("Segoe UI", Font.PLAIN, 14));
                securityFields[i-2].setPreferredSize(new Dimension(300, 30));
                securityFields[i-2].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                fieldPanel.add(securityFields[i-2]);
            }
            
            securityInfoPanel.add(fieldPanel);
        }
        
        // Informations Complémentaires Panel
        JPanel additionalInfoPanel = new JPanel();
        additionalInfoPanel.setLayout(new BoxLayout(additionalInfoPanel, BoxLayout.Y_AXIS));
        additionalInfoPanel.setBackground(Color.WHITE);
        additionalInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] additionalLabels = {"Matière Première*", "Quantité*", "Type*", "Catégorie*"};
        
        JCheckBox matiereCheck = new JCheckBox();
        if (row >= 0) {
            matiereCheck.setSelected((boolean) model.getValueAt(row, 13));
        }
        
        JTextField quantiteField = new JTextField();
        if (row >= 0) {
            quantiteField.setText(model.getValueAt(row, 14).toString());
        }
        
        // Modification pour rendre le champ quantité non éditable
        quantiteField.setEditable(false);
        quantiteField.setBackground(new Color(240, 240, 240));
        
        JComboBox<String> typeCombo = new JComboBox<>(types);
        JComboBox<String> categorieCombo = new JComboBox<>(categories);
        
        if (row >= 0) {
            typeCombo.setSelectedItem(model.getValueAt(row, 15).toString());
            categorieCombo.setSelectedItem(model.getValueAt(row, 16).toString());
        }
        
        for (int i = 0; i < additionalLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(additionalLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(150, 30));
            fieldPanel.add(label);
            
            if (i == 0) {
                matiereCheck.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                fieldPanel.add(matiereCheck);
            } else if (i == 1) {
                quantiteField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                quantiteField.setPreferredSize(new Dimension(300, 30));
                quantiteField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                fieldPanel.add(quantiteField);
            } else if (i == 2) {
                typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                typeCombo.setPreferredSize(new Dimension(300, 30));
                fieldPanel.add(typeCombo);
            } else {
                categorieCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                categorieCombo.setPreferredSize(new Dimension(300, 30));
                fieldPanel.add(categorieCombo);
            }
            
            additionalInfoPanel.add(fieldPanel);
        }
        
        tabbedPane.addTab("Informations de Base", basicInfoPanel);
        tabbedPane.addTab("Informations Techniques", techInfoPanel);
        tabbedPane.addTab("Sécurité", securityInfoPanel);
        tabbedPane.addTab("Complémentaires", additionalInfoPanel);
        
        JButton actionButton = createModernButton(row >= 0 ? "Mettre à jour" : "Ajouter", 
            Color.GREEN, row >= 0 ? new Color(0, 120, 215) : new Color(0, 255, 0));
        JButton cancelButton = createModernButton("Annuler", Color.GRAY, new Color(100, 100, 100));
        
        actionButton.addActionListener(e -> {
            // Validation des champs obligatoires
            for (int i = 0; i < basicFields.length; i++) {
                if (basicFields[i].getText().trim().isEmpty()) {
                    showMessage("Le champ '" + basicLabels[i] + "' est obligatoire", 
                        "Champ manquant", JOptionPane.WARNING_MESSAGE);
                    tabbedPane.setSelectedIndex(0);
                    basicFields[i].requestFocus();
                    return;
                }
            }
            
            // Validation des champs techniques
            for (int i = 0; i < techFields.length; i++) {
                if (techFields[i].getText().trim().isEmpty()) {
                    showMessage("Le champ '" + techLabels[i] + "' est obligatoire", 
                        "Champ manquant", JOptionPane.WARNING_MESSAGE);
                    tabbedPane.setSelectedIndex(1);
                    techFields[i].requestFocus();
                    return;
                }
            }
            
            // Validation des champs numériques
            try {
                Double.parseDouble(techFields[3].getText().trim());
                Double.parseDouble(securityFields[0].getText().trim());
                Double.parseDouble(securityFields[1].getText().trim());
            } catch (NumberFormatException ex) {
                showMessage("Veuillez entrer des valeurs numériques valides pour les champs numériques", 
                    "Erreur de format", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validation des risques
            if (risqueCombo.getSelectedIndex() == 0 || niveauRisqueCombo.getSelectedIndex() == 0) {
                showMessage("Veuillez sélectionner un risque et un niveau de risque", 
                    "Champ manquant", JOptionPane.WARNING_MESSAGE);
                tabbedPane.setSelectedIndex(2);
                return;
            }
            
            // Validation des champs complémentaires
            if (typeCombo.getSelectedIndex() == 0 || 
                categorieCombo.getSelectedIndex() == 0) {
                showMessage("Veuillez remplir tous les champs obligatoires dans l'onglet Complémentaires", 
                    "Champ manquant", JOptionPane.WARNING_MESSAGE);
                tabbedPane.setSelectedIndex(3);
                return;
            }
            
            // Toutes les validations passées - sauvegarde des données
            Object[] produit = new Object[]{
                row >= 0 ? model.getValueAt(row, 0) : null,
                basicFields[0].getText().trim(),
                basicFields[1].getText().trim(),
                basicFields[2].getText().trim(),
                basicFields[3].getText().trim(),
                techFields[0].getText().trim(),
                techFields[1].getText().trim(),
                techFields[2].getText().trim(),
                Double.parseDouble(techFields[3].getText().trim()),
                risqueCombo.getSelectedItem().toString(),
                niveauRisqueCombo.getSelectedItem().toString(),
                Double.parseDouble(securityFields[0].getText().trim()),
                Double.parseDouble(securityFields[1].getText().trim()),
                matiereCheck.isSelected(),
                row >= 0 ? model.getValueAt(row, 14) : 0.0, // La quantité reste inchangée
                typeCombo.getSelectedItem().toString(),
                categorieCombo.getSelectedItem().toString()
            };
            
            if (row >= 0) {
                updateProduitInDB(produit);
            } else {
                addProduitToDB(produit);
            }
            
            cardLayout.show(mainContentPanel, "mainView");
        });
        
        cancelButton.addActionListener(e -> cardLayout.show(mainContentPanel, "mainView"));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(actionButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(cancelButton);
        
        formPanel.add(tabbedPane, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(formPanel, "formView");
        cardLayout.show(mainContentPanel, "formView");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            FenetreProduit frame = new FenetreProduit();
            frame.setVisible(true);
        });
    }
}