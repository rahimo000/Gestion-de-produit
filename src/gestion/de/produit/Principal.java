package gestion.de.produit;




import com.toedter.calendar.JDateChooser;
import gestion.de.produit.interfaces.ClientPanel;
import gestion.de.produit.interfaces.CommandePanel;
import gestion.de.produit.interfaces.FournisseurPanel;
import gestion.de.produit.interfaces.ProductionPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.io.File;
import java.util.regex.Pattern;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Date;
import gestion.de.produit.interfaces.ProduitPanel;
import gestion.de.produit.interfaces.StatistiquePanel;
import gestion.de.produit.interfaces.VentePanel;
import static gestion.de.produit.Logique.Authontification.*;

public class Principal extends JFrame {
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private CardLayout cardLayout;

    public Principal() {
        
        setTitle("");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        mainPanel = new JPanel(new BorderLayout());
        
            
        
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0, 200, 204));
        leftPanel.setPreferredSize(new Dimension(200, getHeight()));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        
        JLabel titleLabel = new JLabel("𝔾𝕖𝕤𝕥𝕚𝕠𝕟 𝕡𝕣𝕠𝕕𝕦𝕚𝕥 ");
        titleLabel.setFont(new Font("Segoe UI Symbol", Font.BOLD, 15));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(titleLabel);
       
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
    
        String[] buttonNames = {" 📦 Produit", "  🛠 Production", " 🛍  Commande", " 💰 Vente", "  👤  Client", " 👤  Fournisseur", "  📊 Statistique"};
        JButton[] buttons = new JButton[buttonNames.length];
        
       
        rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);
        
      
        ProduitPanel produitPanel = new ProduitPanel();
        ProductionPanel productionPanel = new ProductionPanel();
        CommandePanel commandePanel = new CommandePanel();
        VentePanel ventePanel = new VentePanel();
        ClientPanel clientPanel = new ClientPanel();
        FournisseurPanel fournisseurPanel = new FournisseurPanel();
        StatistiquePanel statistiquePanel = new StatistiquePanel();
        
      
        rightPanel.add(produitPanel, " 📦 Produit");
        rightPanel.add(productionPanel, "  🛠 Production");
        rightPanel.add(commandePanel, " 🛍  Commande");
        rightPanel.add(ventePanel, " 💰 Vente");
        rightPanel.add(clientPanel, "  👤  Client");
        rightPanel.add(fournisseurPanel, " 👤  Fournisseur");
        rightPanel.add(statistiquePanel, "  📊 Statistique");
        
        // إنشاء الأزرار وإضافة مستمع الأحداث
        for (int i = 0; i < buttonNames.length; i++) {
            buttons[i] = new JButton(buttonNames[i]);
            buttons[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            buttons[i].setMaximumSize(new Dimension(180, 40));
            buttons[i].setPreferredSize(new Dimension(180, 40));
            buttons[i].setFont(new Font("emojiFont", Font.PLAIN, 14));
            
            // تنسيق الأزرار
            buttons[i].setBackground(new Color(0, 102, 204));
            buttons[i].setForeground(Color.WHITE);
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            buttons[i].setEnabled(access[i]);
            System.out.println(access[i]);
            // إضافة فراغ بين الأزرار
            if (i > 0) {
                leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
            
            leftPanel.add(buttons[i]);
            
            // إضافة مستمع الأحداث لكل زر
            final String panelName = buttonNames[i];
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardLayout.show(rightPanel, panelName);
                }
            });
        }
        
        // إضافة اللوحات إلى اللوحة الرئيسية
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        // إضافة اللوحة الرئيسية إلى الإطار
        add(mainPanel);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
}