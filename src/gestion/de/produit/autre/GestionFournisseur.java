package gestion.de.produit.autre;



import gestion.de.produit.DBConnection;
import gestion.de.produit.autre.Main;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.sql.*;

public class GestionFournisseur extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel mainContentPanel;
    private JPanel formPanel;

    public GestionFournisseur() {
        setTitle("Gestion Fournisseurs");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Color bgColor = new Color(245, 247, 250);
        getContentPane().setBackground(bgColor);

        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(bgColor);

       
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(bgColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        
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

       
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(bgColor);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10))
        );
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

        // Header layout
        headerPanel.add(buttonPanel, BorderLayout.CENTER);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        // Table Setup
        String[] columns = {"N° Fournisseur", "Nom", "Email", "Téléphone", "Adresse", "Registre", "Statut", "Responsable"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (isRowSelected(row)) {
                    c.setBackground(new Color(240, 245, 255));
                    c.setForeground(new Color(50, 50, 50));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
                    c.setForeground(new Color(70, 70, 70));
                }
                
                return c;
            }
        };
        
        table.setRowHeight(45);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                setHorizontalAlignment(JLabel.CENTER);
                
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
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
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

      
        JPanel tableMainPanel = new JPanel(new BorderLayout());
        tableMainPanel.setBackground(bgColor);
        tableMainPanel.add(headerPanel, BorderLayout.NORTH);
        tableMainPanel.add(tableContainer, BorderLayout.CENTER);

        mainContentPanel.add(tableMainPanel, "table");
        
       
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
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
         retourButton.addActionListener(e -> {
            this.dispose();
            new Main().setVisible(true); 
        });

        addButton.addActionListener(e -> showFournisseurForm("Ajouter un Fournisseur", -1));
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                showFournisseurForm("Modifier Fournisseur", row);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner un fournisseur", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String fournisseurId = model.getValueAt(row, 0).toString();
                
                if (isFournisseurInCommande(fournisseurId)) {
                    JOptionPane.showMessageDialog(this, 
                        "Vous ne pouvez pas supprimer ce fournisseur car il est associé à une ou plusieurs commandes", 
                        "Suppression impossible", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmez la suppression de ce fournisseur?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteFournisseurFromDB(fournisseurId);
                    model.removeRow(row);
                    loadFournisseurFromDB();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez sélectionner un Fournisseur", 
                    "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
       
        formPanel = new JPanel();
        formPanel.setBackground(bgColor);
        mainContentPanel.add(formPanel, "form");
        
        add(mainContentPanel);
        
      
        loadFournisseurFromDB();
    }
    
    private boolean isFournisseurInCommande(String fournisseurId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM commande WHERE idFournisseur = ?");
            pstmt.setString(1, fournisseurId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la vérification des commandes: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
    
    private void loadFournisseurFromDB() {
        model.setRowCount(0); 
        
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idFournisseur, nom, Email, Num, Adresse, RegistreCommerce, statut, NomResponsable FROM fournisseur ORDER BY idFournisseur");
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("idFournisseur"),
                    rs.getString("nom"),
                    rs.getString("Email"),
                    rs.getString("Num"),
                    rs.getString("Adresse"),
                    rs.getString("RegistreCommerce"),
                    rs.getString("statut"),
                    rs.getString("NomResponsable")
                });
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des fournisseurs: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void addFournisseurToDB(String[] fournisseurData) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO fournisseur (nom, Email, Num, Adresse, RegistreCommerce, statut, NomResponsable) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            pstmt.setString(1, fournisseurData[1]);
            pstmt.setString(2, fournisseurData[2]);
            pstmt.setString(3, fournisseurData[3]);
            pstmt.setString(4, fournisseurData[4]);
            pstmt.setString(5, fournisseurData[5]);
            pstmt.setString(6, fournisseurData[6]);
            pstmt.setString(7, fournisseurData[7]);
            
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Erreur lors de l'ajout du fournisseur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void updateFournisseurInDB(String[] fournisseurData) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                "UPDATE fournisseur SET nom=?, Email=?, Num=?, Adresse=?, RegistreCommerce=?, statut=?, NomResponsable=? " +
                "WHERE idFournisseur=?");
            
            pstmt.setString(1, fournisseurData[1]);
            pstmt.setString(2, fournisseurData[2]);
            pstmt.setString(3, fournisseurData[3]);
            pstmt.setString(4, fournisseurData[4]);
            pstmt.setString(5, fournisseurData[5]);
            pstmt.setString(6, fournisseurData[6]);
            pstmt.setString(7, fournisseurData[7]);
            pstmt.setInt(8, Integer.parseInt(fournisseurData[0]));
            
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la mise à jour du fournisseur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void deleteFournisseurFromDB(String fournisseurId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fournisseur WHERE idFournisseur=?");
            pstmt.setInt(1, Integer.parseInt(fournisseurId));
            pstmt.executeUpdate();
            pstmt.close();
            
            reorganizeIDs();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la suppression du fournisseur: " + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { conn.close(); } catch (SQLException e) {}
        }
    }
    
    private void reorganizeIDs() {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return;
        
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TEMPORARY TABLE temp_fournisseur AS SELECT * FROM fournisseur ORDER BY idFournisseur");
            
            stmt.execute("TRUNCATE TABLE fournisseur");
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM temp_fournisseur");
            PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO fournisseur (nom, Email, Num, Adresse, RegistreCommerce, statut, NomResponsable) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)");
            
            int newId = 1;
            while (rs.next()) {
                pstmt.setString(1, rs.getString("nom"));
                pstmt.setString(2, rs.getString("Email"));
                pstmt.setString(3, rs.getString("Num"));
                pstmt.setString(4, rs.getString("Adresse"));
                pstmt.setString(5, rs.getString("RegistreCommerce"));
                pstmt.setString(6, rs.getString("statut"));
                pstmt.setString(7, rs.getString("NomResponsable"));
                pstmt.executeUpdate();
                newId++;
            }
            
            stmt.execute("DROP TABLE temp_fournisseur");
            
            rs.close();
            pstmt.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la réorganisation des IDs: " + e.getMessage(),
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
    private void showFournisseurForm(String title, int row) {
        formPanel.removeAll();
        formPanel.setLayout(new BorderLayout());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 60, 60));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JPanel personalPanel = new JPanel();
        personalPanel.setLayout(new BoxLayout(personalPanel, BoxLayout.Y_AXIS));
        personalPanel.setBackground(Color.WHITE);
        personalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] personalLabels = {"Nom*", "Email*", "Téléphone*", "Adresse*"};
        JTextField[] personalFields = new JTextField[personalLabels.length];
        
        for (int i = 0; i < personalLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(personalLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(100, 30));
            fieldPanel.add(label);
            
            personalFields[i] = new JTextField();
            personalFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            personalFields[i].setPreferredSize(new Dimension(300, 30));
            personalFields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10))
            );
            
            if (row >= 0) {
                switch(i) {
                    case 0: personalFields[i].setText(model.getValueAt(row, 1).toString()); break;
                    case 1: personalFields[i].setText(model.getValueAt(row, 2).toString()); break;
                    case 2: personalFields[i].setText(model.getValueAt(row, 3).toString()); break;
                    case 3: personalFields[i].setText(model.getValueAt(row, 4).toString()); break;
                }
            }
            
            fieldPanel.add(personalFields[i]);
            personalPanel.add(fieldPanel);
        }
        
       
        JPanel professionalPanel = new JPanel();
        professionalPanel.setLayout(new BoxLayout(professionalPanel, BoxLayout.Y_AXIS));
        professionalPanel.setBackground(Color.WHITE);
        professionalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] professionalLabels = {"Registre*", "Statut*", "Responsable*"};
        JTextField[] professionalFields = new JTextField[professionalLabels.length];
        
        for (int i = 0; i < professionalLabels.length; i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            fieldPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(professionalLabels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setPreferredSize(new Dimension(150, 30));
            fieldPanel.add(label);
            
            professionalFields[i] = new JTextField();
            professionalFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            professionalFields[i].setPreferredSize(new Dimension(250, 30));
            professionalFields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10))
            );
            
            if (row >= 0 && (i+5) < model.getColumnCount()) {
                professionalFields[i].setText(model.getValueAt(row, i+5).toString());
            }
            
            fieldPanel.add(professionalFields[i]);
            professionalPanel.add(fieldPanel);
        }
        
        tabbedPane.addTab("Informations Personnelles", personalPanel);
        tabbedPane.addTab("Informations Professionnelles", professionalPanel);
        
        JButton actionButton = createModernButton(row >= 0 ? "Mettre à jour" : "Ajouter", 
            Color.GREEN, row >= 0 ? new Color(0, 120, 215) : new Color(0, 255, 0));
        JButton cancelButton = createModernButton("Retour", Color.GRAY, new Color(100, 100, 100));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(actionButton);
        
        actionButton.addActionListener(e -> {
           
            for (int i = 0; i < personalFields.length; i++) {
                if (personalFields[i].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(GestionFournisseur.this, 
                        "Le champ '" + personalLabels[i] + "' est obligatoire", 
                        "Champ manquant", JOptionPane.WARNING_MESSAGE);
                    tabbedPane.setSelectedIndex(0);
                    personalFields[i].requestFocus();
                    return;
                }
            }
            
            if (!Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                      .matcher(personalFields[1].getText()).matches()) {
                JOptionPane.showMessageDialog(GestionFournisseur.this, 
                    "Veuillez entrer une adresse email valide", 
                    "Email invalide", JOptionPane.WARNING_MESSAGE);
                tabbedPane.setSelectedIndex(0);
                personalFields[1].requestFocus();
                return;
            }
            
            if (!Pattern.compile("^[\\d\\s+]{8,}$")
                      .matcher(personalFields[2].getText()).matches()) {
                JOptionPane.showMessageDialog(GestionFournisseur.this, 
                    "Le téléphone doit contenir au moins 8 chiffres",
                    "Téléphone invalide", JOptionPane.WARNING_MESSAGE);
                tabbedPane.setSelectedIndex(0);
                personalFields[2].requestFocus();
                return;
            }
            
            for (int i = 0; i < professionalFields.length; i++) {
                if (professionalFields[i].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(GestionFournisseur.this, 
                        "Le champ '" + professionalLabels[i] + "' est obligatoire", 
                        "Champ manquant", JOptionPane.WARNING_MESSAGE);
                    tabbedPane.setSelectedIndex(1);
                    professionalFields[i].requestFocus();
                    return;
                }
            }
            
          
            String[] fournisseur = new String[]{
                row >= 0 ? model.getValueAt(row, 0).toString() : "",
                personalFields[0].getText().trim(),
                personalFields[1].getText().trim(),
                personalFields[2].getText().trim(),
                personalFields[3].getText().trim(),
                professionalFields[0].getText().trim(),
                professionalFields[1].getText().trim(),
                professionalFields[2].getText().trim()
            };
            
            if (row >= 0) {
                updateFournisseurInDB(fournisseur);
            } else {
                addFournisseurToDB(fournisseur);
            }
            
            loadFournisseurFromDB();
            showTable();
        });
        
        cancelButton.addActionListener(e -> showTable());
        
        formPanel.add(headerPanel, BorderLayout.NORTH);
        formPanel.add(tabbedPane, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        formPanel.revalidate();
        formPanel.repaint();
        
        CardLayout cl = (CardLayout)(mainContentPanel.getLayout());
        cl.show(mainContentPanel, "form");
    }
    
    private void showTable() {
        CardLayout cl = (CardLayout)(mainContentPanel.getLayout());
        cl.show(mainContentPanel, "table");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            GestionFournisseur frame = new GestionFournisseur();
            frame.setVisible(true);
        });
    }
}