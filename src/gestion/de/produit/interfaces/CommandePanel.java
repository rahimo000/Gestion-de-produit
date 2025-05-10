package gestion.de.produit.interfaces;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import gestion.de.produit.DBConnection;

/**
 * @author youcef-bl
 */
public class CommandePanel extends javax.swing.JPanel {

    // --- Constants for Field Sizes ---
    private static final int FIELD_HEIGHT = 35;
    private static final Dimension FIELD_DIMENSION = new Dimension(250, FIELD_HEIGHT);
    private static final Dimension BUTTON_DIMENSION = new Dimension(180, FIELD_HEIGHT);
    private static final Font LABEL_FONT = new Font("Roboto", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Roboto", Font.PLAIN, 14);

    // -----------------------------------
    // 1. المتغيرات الأساسية للنافذة الرئيسية
    // -----------------------------------
    private JPanel panneauPrincipal;
    private JPanel panneauContenu;
    private CardLayout cardLayoutContenu;

    // -----------------------------------
    // 2. المتغيرات الخاصة بواجهة Commandes
    // -----------------------------------
    private JPanel panneauCommandes;
    private JPanel panneauFormulaireCommandes;
    private JPanel panneauTableauCommandes;
    private JSplitPane splitPaneAffichage;
    private JTabbedPane tabbedPaneCommandes;
    private JPanel panneauBaseCommande;
    private JPanel panneauPaiementCommande;
    private JPanel panneauDetailsCommande;
    private JPanel panneauMenuCommandes;
    private JPanel panneauBoutonsContainerCommandes;
    private CardLayout cardLayoutBoutonsCommandes;
    private JPanel panneauBoutonsDetailsCommandes;
    private JButton boutonDetailsCommandeVisu;
    private JButton boutonSauvegarderCommande;
    private JButton boutonAjouterCommandeForm;
    private JButton boutonAffichageCommande;
    private JButton boutonSupprimerCommandeTableau;
    private JButton boutonRechercherCommandeTableau;
    private JButton boutonImprimerBonCommande;
    private JTextField champIdCommande;
    private JDateChooser dateCommandeChooser;
    private JComboBox<String> comboValideCommande;
    private JComboBox<String> comboPayeeCommande;
    private JComboBox<String> comboModePaiementCommande;
    private JTextField champInfosPaiementCommande;
    private JTextField champAnneeBonCommande;
    private JTextField champNumeroBonCommande;
    private JTextField champPrixTTCCommande;
    private JTextField champPrixTVACommande;
    private JTextField champPrixHTCommande;
    private JDateChooser datePaiementChooserCommande;
    private JComboBox<String> comboFournisseur;
    private JComboBox<String> comboProduitDetail;
    private JTextField champIdDetailCommandeSaisie;
    private JTextField champQuantiteCommandeDetail;
    private JTextField champPrixAchatCommandeDetail;
    private JDateChooser dateExpirationChooserDetail;
    private JDateChooser dateFabricationChooserDetail;

    private JTable tableauCommandes;
    private JTable tableauDetailsCommandeAffichage;
    private JTable tableauDetailsEnCoursSaisie;
    private DefaultTableModel modelDetailsEnCoursSaisie;
    private List<DetailCommandeData> detailsEnCoursList;

    private JButton boutonAjouterLigneDetailForm;
    private JButton boutonSupprimerLigneDetailForm;

    private JLabel labelNombreCommandes;
    private Color couleurSauvegarderInitialeCommande = new Color(80, 200, 150);
    private Color couleurSauvegarderActiveeCommande = new Color(40, 180, 70);
    private JDateChooser dateRechercheCommande;

    private final String PROMPT_SELECTIONNER = "Sélectionner...";
    private Object lastValideComboProduitSelection = PROMPT_SELECTIONNER;

    // Helper class to store detail data temporarily for the form
    private static class DetailCommandeData {

        String idDetail;
        String produitNom;
        int idProduit;
        float quantite;
        float prixAchat;
        Date dateExpiration;
        Date dateFabrication;

        public DetailCommandeData(String idDetail, String produitNom, int idProduit, float quantite, float prixAchat, Date dateExpiration, Date dateFabrication) {
            this.idDetail = (idDetail == null || idDetail.trim().isEmpty()) ? null : idDetail.trim();
            this.produitNom = produitNom;
            this.idProduit = idProduit;
            this.quantite = quantite;
            this.prixAchat = prixAchat;
            this.dateExpiration = dateExpiration;
            this.dateFabrication = dateFabrication;
        }
    }

    public CommandePanel() {
        detailsEnCoursList = new ArrayList<>();
        initComponents();
        loadFournisseurs();
        loadProduits();
        updateTableCommandes();
        updateNombreCommandesLabel();
        panneauFormulaireCommandes.setVisible(false);
        panneauTableauCommandes.setVisible(true);
        if (tableauDetailsCommandeAffichage != null && tableauDetailsCommandeAffichage.getModel() instanceof DefaultTableModel) {
            ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
        }
        if (comboProduitDetail.getItemCount() > 0) {
            lastValideComboProduitSelection = comboProduitDetail.getItemAt(0);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        panneauPrincipal = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(220, 220, 220), 0, getHeight(), new Color(245, 245, 245));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panneauPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panneauContenu = new JPanel();
        cardLayoutContenu = new CardLayout();
        panneauContenu.setLayout(cardLayoutContenu);
        panneauContenu.setOpaque(false);

        panneauCommandes = new JPanel();
        panneauCommandes.setLayout(new BoxLayout(panneauCommandes, BoxLayout.Y_AXIS));
        panneauCommandes.setOpaque(false);

        JPanel panneauMenuEtCardsCommandes = new JPanel(new BorderLayout());
        panneauMenuEtCardsCommandes.setOpaque(false);

        panneauMenuCommandes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panneauMenuCommandes.setOpaque(false);

        JButton boutonRetour = createModernButton("Retour", new Color(90, 100, 220));
        boutonRetour.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });

        panneauMenuCommandes.add(boutonRetour);

        boutonSauvegarderCommande = createModernButton("Sauvegarder", couleurSauvegarderInitialeCommande);
        boutonSauvegarderCommande.setEnabled(false);
        boutonSauvegarderCommande.setToolTipText("Enregistrer la commande actuelle et tous ses détails");
        boutonSauvegarderCommande.addActionListener(e -> saveAllData());
        panneauMenuCommandes.add(boutonSauvegarderCommande);

        boutonAjouterCommandeForm = createModernButton("Ajouter Commande", new Color(52, 199, 89));
        boutonAjouterCommandeForm.setToolTipText("Afficher le formulaire pour saisir une nouvelle commande");
        boutonAjouterCommandeForm.addActionListener(e -> {
            switchToFormView();
            clearFieldsCommandes();
            champIdCommande.setText("");
            tabbedPaneCommandes.setSelectedIndex(0);
            boutonSauvegarderCommande.setEnabled(false);
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
        });
        panneauMenuCommandes.add(boutonAjouterCommandeForm);

        boutonAffichageCommande = createModernButton("Affichage des données", new Color(100, 150, 220));
        boutonAffichageCommande.setToolTipText("Afficher le tableau des commandes existantes et leurs détails");
        boutonAffichageCommande.addActionListener(e -> {
            switchToTableView();
            updateTableCommandes();
            if (tableauDetailsCommandeAffichage != null && tableauDetailsCommandeAffichage.getModel() instanceof DefaultTableModel) {
                ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
            }
        });
        panneauMenuCommandes.add(boutonAffichageCommande);

        boutonDetailsCommandeVisu = createModernButton("Voir Détails Commande (Formulaire)", new Color(90, 100, 220));
        boutonDetailsCommandeVisu.setToolTipText("Aller à l'onglet des détails pour la commande en cours de saisie");
        boutonDetailsCommandeVisu.addActionListener(e -> {
            switchToFormView();
            tabbedPaneCommandes.setSelectedIndex(2);
        });
        panneauMenuCommandes.add(boutonDetailsCommandeVisu);

        boutonImprimerBonCommande = createModernButton("Imprimer Bon Commande", new Color(245, 166, 35));
        boutonImprimerBonCommande.setToolTipText("Imprimer le bon pour la commande sélectionnée dans le tableau");
        boutonImprimerBonCommande.addActionListener(e -> {
            int selectedRow = tableauCommandes.getSelectedRow();
            if (selectedRow != -1) {
                Object idObj = tableauCommandes.getValueAt(selectedRow, 0);
                if (idObj instanceof Integer) {
                    int idCommande = (Integer) idObj;
                    afficherDetailsPourImpression(idCommande);
                } else {
                    JOptionPane.showMessageDialog(this, "ID de commande invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            }
        });
        panneauMenuCommandes.add(boutonImprimerBonCommande);

        panneauMenuEtCardsCommandes.add(panneauMenuCommandes, BorderLayout.CENTER);

        JPanel cardNombreCommandes = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(110, 50, 220), getWidth(), getHeight(), new Color(150, 120, 240));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        cardNombreCommandes.setPreferredSize(new Dimension(200, 80));
        cardNombreCommandes.setOpaque(false);
        JLabel titleNombreCommandes = new JLabel("Nombre de commandes");
        titleNombreCommandes.setForeground(new Color(230, 230, 230));
        titleNombreCommandes.setFont(new Font("Roboto", Font.PLAIN, 13));
        titleNombreCommandes.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 0));
        cardNombreCommandes.add(titleNombreCommandes, BorderLayout.NORTH);
        labelNombreCommandes = new JLabel("0");
        labelNombreCommandes.setForeground(new Color(230, 230, 230));
        labelNombreCommandes.setFont(new Font("Roboto", Font.BOLD, 24));
        labelNombreCommandes.setHorizontalAlignment(SwingConstants.CENTER);
        cardNombreCommandes.add(labelNombreCommandes, BorderLayout.CENTER);

        JPanel cardContainerCommandes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        cardContainerCommandes.setOpaque(false);
        cardContainerCommandes.add(cardNombreCommandes);
        panneauMenuEtCardsCommandes.add(cardContainerCommandes, BorderLayout.EAST);
        panneauCommandes.add(panneauMenuEtCardsCommandes);

        panneauFormulaireCommandes = new JPanel(new BorderLayout(10, 10));
        panneauFormulaireCommandes.setOpaque(false);

        tabbedPaneCommandes = new JTabbedPane();
        tabbedPaneCommandes.setFont(FIELD_FONT);

        panneauBaseCommande = new JPanel(new GridBagLayout());
        panneauBaseCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Informations de base de la commande", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30, 30, 30)));
        panneauBaseCommande.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbcBase = new GridBagConstraints();
        gbcBase.insets = new Insets(4, 8, 4, 8);
        gbcBase.anchor = GridBagConstraints.WEST;
        gbcBase.fill = GridBagConstraints.HORIZONTAL;

        champIdCommande = new JTextField(10);
        champIdCommande.setEditable(false);
        champIdCommande.setFont(FIELD_FONT);

        addFormField(panneauBaseCommande, gbcBase, 0, "Date Commande:", dateCommandeChooser = new JDateChooser());
        addFormField(panneauBaseCommande, gbcBase, 1, "Validé:", comboValideCommande = new JComboBox<>(new String[]{"Oui", "Non"}));
        addFormField(panneauBaseCommande, gbcBase, 2, "Payé:", comboPayeeCommande = new JComboBox<>(new String[]{"Oui", "Non"}));
        addFormField(panneauBaseCommande, gbcBase, 3, "Annee Bon Commande:", champAnneeBonCommande = new JTextField());
        addFormField(panneauBaseCommande, gbcBase, 4, "Numéro Bon Commande:", champNumeroBonCommande = new JTextField());
        addFormField(panneauBaseCommande, gbcBase, 5, "Fournisseur:", comboFournisseur = new JComboBox<>());

        gbcBase.gridy = 6;
        gbcBase.gridx = 0;
        gbcBase.gridwidth = 2;
        gbcBase.weighty = 1.0;
        gbcBase.fill = GridBagConstraints.BOTH;
        panneauBaseCommande.add(new JPanel() {
            {
                setOpaque(false);
            }
        }, gbcBase);
        tabbedPaneCommandes.addTab("Informations de base", new JScrollPane(panneauBaseCommande) {
            {
                getViewport().setBackground(new Color(245, 245, 245));
                setBorder(null);
            }
        });

        panneauPaiementCommande = new JPanel(new GridBagLayout());
        panneauPaiementCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Informations de paiement", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30, 30, 30)));
        panneauPaiementCommande.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbcPaiement = (GridBagConstraints) gbcBase.clone();

        addFormField(panneauPaiementCommande, gbcPaiement, 0, "Mode de paiement:", comboModePaiementCommande = new JComboBox<>(new String[]{"Espèces", "Carte de crédit", "Virement bancaire", "Chèque"}));
        addFormField(panneauPaiementCommande, gbcPaiement, 1, "Date de paiement:", datePaiementChooserCommande = new JDateChooser());
        addFormField(panneauPaiementCommande, gbcPaiement, 2, "Infos Paiement:", champInfosPaiementCommande = new JTextField());
        addFormField(panneauPaiementCommande, gbcPaiement, 3, "Prix HT:", champPrixHTCommande = new JTextField());
        ((AbstractDocument) champPrixHTCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        addFormField(panneauPaiementCommande, gbcPaiement, 4, "Prix TVA:", champPrixTVACommande = new JTextField());
        ((AbstractDocument) champPrixTVACommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        addFormField(panneauPaiementCommande, gbcPaiement, 5, "Prix TTC:", champPrixTTCCommande = new JTextField());
        ((AbstractDocument) champPrixTTCCommande.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());

        gbcPaiement.gridy = 6;
        gbcPaiement.gridx = 0;
        gbcPaiement.gridwidth = 2;
        gbcPaiement.weighty = 1.0;
        gbcPaiement.fill = GridBagConstraints.BOTH;
        panneauPaiementCommande.add(new JPanel() {
            {
                setOpaque(false);
            }
        }, gbcPaiement);
        tabbedPaneCommandes.addTab("Informations de paiement", new JScrollPane(panneauPaiementCommande) {
            {
                getViewport().setBackground(new Color(245, 245, 245));
                setBorder(null);
            }
        });

        panneauDetailsCommande = new JPanel(new BorderLayout(10, 10));
        panneauDetailsCommande.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Saisie des lignes de produits", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, LABEL_FONT, new Color(30, 30, 30)));
        panneauDetailsCommande.setBackground(new Color(245, 245, 245));

        JPanel panneauSaisieLigneDetail = new JPanel(new GridBagLayout());
        panneauSaisieLigneDetail.setOpaque(false);
        GridBagConstraints gbcDetailsSaisie = (GridBagConstraints) gbcBase.clone();

        comboProduitDetail = new JComboBox<>();
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 0, "Produit:", comboProduitDetail);
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 1, "ID Détail (Optionnel):", champIdDetailCommandeSaisie = new JTextField());
        champIdDetailCommandeSaisie.setToolTipText("Laisser vide pour génération auto si non fourni par DB");
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 2, "Quantité:", champQuantiteCommandeDetail = new JTextField());
        ((AbstractDocument) champQuantiteCommandeDetail.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 3, "Prix d'achat Unit.:", champPrixAchatCommandeDetail = new JTextField());
        ((AbstractDocument) champPrixAchatCommandeDetail.getDocument()).setDocumentFilter(new NumberWithDecimalFilter());
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 4, "Date d'expiration:", dateExpirationChooserDetail = new JDateChooser());
        addFormField(panneauSaisieLigneDetail, gbcDetailsSaisie, 5, "Date de fabrication:", dateFabricationChooserDetail = new JDateChooser());

        gbcDetailsSaisie.gridy = 6;
        gbcDetailsSaisie.gridx = 0;
        gbcDetailsSaisie.gridwidth = 2;
        gbcDetailsSaisie.weighty = 0.1;
        panneauSaisieLigneDetail.add(new JPanel() {
            {
                setOpaque(false);
            }
        }, gbcDetailsSaisie);
        panneauDetailsCommande.add(new JScrollPane(panneauSaisieLigneDetail) {
            {
                getViewport().setBackground(new Color(245, 245, 245));
                setBorder(null);
            }
        }, BorderLayout.NORTH);

        String[] colDetailsSaisie = {"ID Détail", "Produit", "Qté", "Prix Achat", "Date Exp.", "Date Fab."};
        modelDetailsEnCoursSaisie = new DefaultTableModel(colDetailsSaisie, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableauDetailsEnCoursSaisie = new JTable(modelDetailsEnCoursSaisie);
        setupTableStyle(tableauDetailsEnCoursSaisie);
        JScrollPane scrollPaneDetailsSaisie = new JScrollPane(tableauDetailsEnCoursSaisie);
        scrollPaneDetailsSaisie.getViewport().setBackground(new Color(245, 245, 245));
        scrollPaneDetailsSaisie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 0, 0, 0),
                BorderFactory.createLineBorder(Color.LIGHT_GRAY)
        ));
        panneauDetailsCommande.add(scrollPaneDetailsSaisie, BorderLayout.CENTER);
        tabbedPaneCommandes.addTab("Détails de commande", panneauDetailsCommande);

        tabbedPaneCommandes.addChangeListener(e -> {
            int selectedIndex = tabbedPaneCommandes.getSelectedIndex();
            cardLayoutBoutonsCommandes.show(panneauBoutonsContainerCommandes, (selectedIndex == 2) ? "detailsButtons" : "noButtons");
        });
        panneauFormulaireCommandes.add(tabbedPaneCommandes, BorderLayout.CENTER);

        panneauBoutonsContainerCommandes = new JPanel();
        cardLayoutBoutonsCommandes = new CardLayout();
        panneauBoutonsContainerCommandes.setLayout(cardLayoutBoutonsCommandes);
        panneauBoutonsContainerCommandes.setOpaque(false);
        panneauBoutonsContainerCommandes.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        JPanel panneauNoButtons = new JPanel();
        panneauNoButtons.setOpaque(false);
        panneauBoutonsContainerCommandes.add(panneauNoButtons, "noButtons");

        panneauBoutonsDetailsCommandes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panneauBoutonsDetailsCommandes.setOpaque(false);

        boutonAjouterLigneDetailForm = createModernButton("Ajouter Ligne Produit", new Color(52, 199, 89));
        boutonAjouterLigneDetailForm.setToolTipText("Ajouter le produit saisi aux détails de cette commande");
        boutonAjouterLigneDetailForm.addActionListener(e -> ajouterLigneDetailEnCours());
        panneauBoutonsDetailsCommandes.add(boutonAjouterLigneDetailForm);

        boutonSupprimerLigneDetailForm = createModernButton("Supprimer Ligne Sélectionnée", new Color(255, 59, 48));
        boutonSupprimerLigneDetailForm.setToolTipText("Supprimer la ligne de produit sélectionnée dans le tableau ci-dessus");
        boutonSupprimerLigneDetailForm.addActionListener(e -> supprimerLigneDetailEnCours());
        panneauBoutonsDetailsCommandes.add(boutonSupprimerLigneDetailForm);

        panneauBoutonsContainerCommandes.add(panneauBoutonsDetailsCommandes, "detailsButtons");
        panneauFormulaireCommandes.add(panneauBoutonsContainerCommandes, BorderLayout.SOUTH);
        panneauCommandes.add(panneauFormulaireCommandes);

        panneauTableauCommandes = new JPanel(new BorderLayout(10, 10));
        panneauTableauCommandes.setOpaque(false);

        JPanel tableHeaderCommandes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tableHeaderCommandes.setOpaque(false);

        JLabel labelRecherche = new JLabel("Rechercher par Date Commande:");
        labelRecherche.setFont(LABEL_FONT);
        labelRecherche.setForeground(new Color(30, 30, 30));
        tableHeaderCommandes.add(labelRecherche);
        dateRechercheCommande = new JDateChooser();
        dateRechercheCommande.setFont(FIELD_FONT);
        dateRechercheCommande.setPreferredSize(new Dimension(150, FIELD_HEIGHT));
        tableHeaderCommandes.add(dateRechercheCommande);

        boutonRechercherCommandeTableau = createModernButton("Rechercher", new Color(0, 122, 255));
        boutonRechercherCommandeTableau.setPreferredSize(new Dimension(120, FIELD_HEIGHT));
        boutonRechercherCommandeTableau.setToolTipText("Filtrer les commandes par la date sélectionnée");
        boutonRechercherCommandeTableau.addActionListener(e -> rechercherCommandeParDate());
        tableHeaderCommandes.add(boutonRechercherCommandeTableau);

        JButton boutonResetRecherche = createModernButton("Afficher Tout", new Color(100, 100, 100));
        boutonResetRecherche.setPreferredSize(new Dimension(130, FIELD_HEIGHT));
        boutonResetRecherche.setToolTipText("Effacer le filtre et afficher toutes les commandes");
        boutonResetRecherche.addActionListener(e -> {
            dateRechercheCommande.setDate(null);
            updateTableCommandes();
            ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
        });
        tableHeaderCommandes.add(boutonResetRecherche);

        boutonSupprimerCommandeTableau = createModernButton("Supprimer Commande", new Color(255, 59, 48));
        boutonSupprimerCommandeTableau.setPreferredSize(BUTTON_DIMENSION);
        boutonSupprimerCommandeTableau.setToolTipText("Supprimer la commande sélectionnée et ses détails associés");
        boutonSupprimerCommandeTableau.addActionListener(e -> supprimerCommandeSelectionnee());
        tableHeaderCommandes.add(boutonSupprimerCommandeTableau);
        panneauTableauCommandes.add(tableHeaderCommandes, BorderLayout.NORTH);

        String[] columnNamesCommandes = {
            "ID", "Date Commande", "Validé", "Payé", "N° Bon", "Fournisseur",
            "Mode Paiement", "Date Paiement", "Prix HT", "Prix TVA", "Prix TTC"
        };
        DefaultTableModel tableModelCommandes = new DefaultTableModel(columnNamesCommandes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableauCommandes = new JTable(tableModelCommandes);
        setupTableStyle(tableauCommandes);
        JScrollPane scrollPaneCommandes = new JScrollPane(tableauCommandes);

        tableauCommandes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableauCommandes.getSelectedRow();
                if (selectedRow != -1) {
                    Object idObj = tableauCommandes.getValueAt(selectedRow, 0);
                    if (idObj instanceof Integer) {
                        updateTableDetailsCommandeAffichage((Integer) idObj);
                    }
                } else {
                    ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
                }
            }
        });

        String[] columnNamesDetailsAffichage = {
            "ID Détail", "Produit", "Qté Cmdée", "Prix Achat Unit.", "Date Exp.", "Date Fab."
        };
        DefaultTableModel tableModelDetailsAffichage = new DefaultTableModel(columnNamesDetailsAffichage, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableauDetailsCommandeAffichage = new JTable(tableModelDetailsAffichage);
        setupTableStyle(tableauDetailsCommandeAffichage);
        JScrollPane scrollPaneDetailsAffichage = new JScrollPane(tableauDetailsCommandeAffichage);

        splitPaneAffichage = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneCommandes, scrollPaneDetailsAffichage);
        splitPaneAffichage.setOpaque(false);
        splitPaneAffichage.setResizeWeight(0.6);
        splitPaneAffichage.setOneTouchExpandable(true);
        splitPaneAffichage.setContinuousLayout(true);

        panneauTableauCommandes.add(splitPaneAffichage, BorderLayout.CENTER);
        panneauCommandes.add(panneauTableauCommandes);

        panneauContenu.add(panneauCommandes, "commandes");
        panneauPrincipal.add(panneauContenu, BorderLayout.CENTER);
        setLayout(new BorderLayout()); // or whatever layout you want
        add(panneauPrincipal);

        Timer timerCommandes = new Timer(500, e -> checkFormCompletion());
        timerCommandes.start();
    }

    private class ProductComboBoxRenderer extends DefaultListCellRenderer {

        public ProductComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                String productName = value.toString();
                if (!productName.equals(PROMPT_SELECTIONNER)) {
                    int productId = getProduitIdFromName(productName);
                    boolean isDisabled = false;
                    if (productId != -1) {
                        for (DetailCommandeData detail : detailsEnCoursList) {
                            if (detail.idProduit == productId) {
                                isDisabled = true;
                                break;
                            }
                        }
                    }
                    if (isDisabled) {
                        setForeground(Color.LIGHT_GRAY);
                        if (isSelected) {
                            setBackground(list.getSelectionBackground());
                        } else {
                            setBackground(list.getBackground());
                        }
                    } else {
                        setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                    }
                } else {
                    setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                    setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                }
            }
            return this;
        }
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int yPos, String labelText, Component component) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 1;
        gbc.weightx = 0.1;
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(30, 30, 30));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        if (component instanceof JComboBox || component instanceof JDateChooser) {
            component.setPreferredSize(FIELD_DIMENSION);
            component.setFont(FIELD_FONT);
        } else if (component instanceof JTextField) {
            ((JTextField) component).setFont(FIELD_FONT);
            ((JTextField) component).setPreferredSize(FIELD_DIMENSION);
        }
        component.setForeground(new Color(30, 30, 30));
        panel.add(component, gbc);
    }

    private void setupTableStyle(JTable table) {
        table.setBackground(new Color(250, 250, 250));
        table.setGridColor(new Color(210, 210, 210));
        table.setRowHeight(FIELD_HEIGHT - 5);
        table.setFillsViewportHeight(true);
        table.setFont(FIELD_FONT);
        table.setForeground(new Color(30, 30, 30));
        table.setSelectionBackground(new Color(180, 210, 255));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.getTableHeader().setForeground(new Color(30, 30, 30));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        if (table == tableauCommandes) {
            int[] columnWidths = {50, 110, 50, 50, 70, 150, 120, 110, 80, 80, 90};
            if (table.getColumnCount() > 0 && table.getColumnCount() == columnWidths.length) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
                }
            }
        } else if (table == tableauDetailsCommandeAffichage || table == tableauDetailsEnCoursSaisie) {
            int[] columnWidths = {70, 180, 60, 100, 110, 110};
            if (table.getColumnCount() > 0 && table.getColumnCount() == columnWidths.length) {
                for (int i = 0; i < table.getColumnCount(); i++) {
                    table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
                }
            }
        }
    }

    private void switchToFormView() {
        panneauFormulaireCommandes.setVisible(true);
        panneauTableauCommandes.setVisible(false);
        this.revalidate();
        this.repaint();
    }

    private void switchToTableView() {
        panneauFormulaireCommandes.setVisible(false);
        panneauTableauCommandes.setVisible(true);
        this.revalidate();
        this.repaint();
    }

    private void checkFormCompletion() {
        if (!panneauFormulaireCommandes.isVisible() || dateCommandeChooser == null || comboFournisseur == null || comboModePaiementCommande == null) {
            if (boutonSauvegarderCommande != null) {
                boutonSauvegarderCommande.setEnabled(false);
                boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
            }
            return;
        }

        boolean baseComplete = dateCommandeChooser.getDate() != null
                && !champNumeroBonCommande.getText().trim().isEmpty()
                && !champAnneeBonCommande.getText().trim().isEmpty()
                && comboFournisseur.getSelectedIndex() > 0;

        boolean paiementComplete = comboModePaiementCommande.getSelectedIndex() != -1
                && !champPrixHTCommande.getText().trim().isEmpty()
                && !champPrixTTCCommande.getText().trim().isEmpty();

        boolean detailsPresent = !detailsEnCoursList.isEmpty();

        if (baseComplete && paiementComplete && detailsPresent) {
            boutonSauvegarderCommande.setBackground(couleurSauvegarderActiveeCommande);
            boutonSauvegarderCommande.setEnabled(true);
        } else {
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
            boutonSauvegarderCommande.setEnabled(false);
        }
    }

    private void loadFournisseurs() {
        if (comboFournisseur == null) {
            return;
        }
        comboFournisseur.removeAllItems();
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT idFournisseur, Nom FROM Fournisseur ORDER BY Nom")) {
            comboFournisseur.addItem(PROMPT_SELECTIONNER);
            while (rs.next()) {
                comboFournisseur.addItem(rs.getString("Nom"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement fournisseurs: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadProduits() {
        if (comboProduitDetail == null) {
            return;
        }
        comboProduitDetail.removeAllItems();
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT idProduit, Nom FROM Produit ORDER BY Nom")) {
            comboProduitDetail.addItem(PROMPT_SELECTIONNER);
            while (rs.next()) {
                comboProduitDetail.addItem(rs.getString("Nom"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement produits: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        comboProduitDetail.setRenderer(new ProductComboBoxRenderer());
        if (comboProduitDetail.getActionListeners().length == 0) {
            comboProduitDetail.addActionListener(e -> {
                Object selectedObject = comboProduitDetail.getSelectedItem();
                if (selectedObject == null) {
                    SwingUtilities.invokeLater(() -> comboProduitDetail.setSelectedItem(lastValideComboProduitSelection));
                    return;
                }
                String selectedProductName = selectedObject.toString();
                if (selectedProductName.equals(PROMPT_SELECTIONNER)) {
                    lastValideComboProduitSelection = selectedObject;
                    return;
                }
                int selectedProductId = getProduitIdFromName(selectedProductName);
                if (selectedProductId != -1) {
                    boolean alreadyAdded = false;
                    for (DetailCommandeData detail : detailsEnCoursList) {
                        if (detail.idProduit == selectedProductId) {
                            alreadyAdded = true;
                            break;
                        }
                    }
                    if (alreadyAdded) {
                        JOptionPane.showMessageDialog(CommandePanel.this,
                                "Ce produit est déjà dans la liste des détails pour cette commande.",
                                "Produit Dupliqué", JOptionPane.WARNING_MESSAGE);
                        SwingUtilities.invokeLater(() -> comboProduitDetail.setSelectedItem(lastValideComboProduitSelection));
                    } else {
                        lastValideComboProduitSelection = selectedObject;
                    }
                }
            });
        }
        if (comboProduitDetail.getItemCount() > 0) {
            lastValideComboProduitSelection = comboProduitDetail.getItemAt(0);
        }
    }

    private void updateTableCommandes() {
        if (tableauCommandes == null || tableauCommandes.getModel() == null) {
            return;
        }
        String[] columnNames = {
            "ID", "Date Commande", "Validé", "Payé", "N° Bon", "Fournisseur",
            "Mode Paiement", "Date Paiement", "Prix HT", "Prix TVA", "Prix TTC"
        };
        DefaultTableModel model = (DefaultTableModel) tableauCommandes.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(columnNames);

        String query = "SELECT c.idCommande, c.DateCommande, c.Valide, c.Payer, c.NumeroBonCommande, f.Nom AS FournisseurNom, "
                + "c.ModePaiement, c.DatePaiement, c.PrixHT, c.PrixTVA, c.PrixTTC "
                + "FROM Commande c "
                + "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur "
                + "ORDER BY c.DateCommande DESC, c.idCommande DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("idCommande"),
                    rs.getTimestamp("DateCommande"),
                    rs.getBoolean("Valide") ? "Oui" : "Non",
                    rs.getBoolean("Payer") ? "Oui" : "Non",
                    rs.getObject("NumeroBonCommande") != null ? rs.getInt("NumeroBonCommande") : null,
                    rs.getString("FournisseurNom"),
                    rs.getString("ModePaiement"),
                    rs.getDate("DatePaiement"),
                    rs.getObject("PrixHT") != null ? rs.getFloat("PrixHT") : null,
                    rs.getObject("PrixTVA") != null ? rs.getFloat("PrixTVA") : null,
                    rs.getObject("PrixTTC") != null ? rs.getFloat("PrixTTC") : null
                });
            }
            setupTableStyle(tableauCommandes);
            updateNombreCommandesLabel();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur màj tableau commandes: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTableDetailsCommandeAffichage(int idCommande) {
        if (tableauDetailsCommandeAffichage == null || tableauDetailsCommandeAffichage.getModel() == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tableauDetailsCommandeAffichage.getModel();
        model.setRowCount(0);
        String query = "SELECT dc.idDetailCommande, p.Nom AS ProduitNom, dc.QuantiteCommande, dc.PrixUnitaire, dc.DateExpiration, dc.DateFabrication "
                + "FROM DetailCommande dc "
                + "LEFT JOIN Produit p ON dc.idProduit = p.idProduit "
                + "WHERE dc.idCommande = ? "
                + "ORDER BY dc.idDetailCommande";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCommande);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("idDetailCommande"),
                        rs.getString("ProduitNom"),
                        rs.getObject("QuantiteCommande") != null ? rs.getFloat("QuantiteCommande") : null,
                        rs.getObject("PrixUnitaire") != null ? rs.getFloat("PrixUnitaire") : null,
                        rs.getDate("DateExpiration"),
                        rs.getDate("DateFabrication")
                    });
                }
            }
            setupTableStyle(tableauDetailsCommandeAffichage);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur màj tableau détails commande: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void rechercherCommandeParDate() {
        Date selectedDate = dateRechercheCommande.getDate();
        if (selectedDate == null) {
            updateTableCommandes();
            ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
            return;
        }
        if (tableauCommandes == null || tableauCommandes.getModel() == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tableauCommandes.getModel();
        model.setRowCount(0);
        ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
        String query = "SELECT c.idCommande, c.DateCommande, c.Valide, c.Payer, c.NumeroBonCommande, f.Nom AS FournisseurNom, "
                + "c.ModePaiement, c.DatePaiement, c.PrixHT, c.PrixTVA, c.PrixTTC "
                + "FROM Commande c "
                + "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur "
                + "WHERE DATE(c.DateCommande) = ? "
                + "ORDER BY c.DateCommande DESC, c.idCommande DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(selectedDate.getTime()));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("idCommande"), rs.getTimestamp("DateCommande"),
                        rs.getBoolean("Valide") ? "Oui" : "Non", rs.getBoolean("Payer") ? "Oui" : "Non",
                        rs.getObject("NumeroBonCommande") != null ? rs.getInt("NumeroBonCommande") : null, rs.getString("FournisseurNom"),
                        rs.getString("ModePaiement"), rs.getDate("DatePaiement"),
                        rs.getObject("PrixHT") != null ? rs.getFloat("PrixHT") : null,
                        rs.getObject("PrixTVA") != null ? rs.getFloat("PrixTVA") : null,
                        rs.getObject("PrixTTC") != null ? rs.getFloat("PrixTTC") : null
                    });
                }
            }
            setupTableStyle(tableauCommandes);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur recherche: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void supprimerCommandeSelectionnee() {
        int selectedRow = tableauCommandes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une commande.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object idObj = tableauCommandes.getValueAt(selectedRow, 0);
        if (!(idObj instanceof Integer)) {
            JOptionPane.showMessageDialog(this, "ID de commande invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idCommandeToDelete = (Integer) idObj;
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            String checkQuery = "SELECT COUNT(*) FROM DetailCommande WHERE idCommande = ?";
            boolean hasDetails = false;
            try (PreparedStatement stmtCheckDetails = conn.prepareStatement(checkQuery)) {
                stmtCheckDetails.setInt(1, idCommandeToDelete);
                try (ResultSet rsCheck = stmtCheckDetails.executeQuery()) {
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        hasDetails = true;
                    }
                }
            }
            String confirmationMessage = "Supprimer la commande ID: " + idCommandeToDelete + "?";
            if (hasDetails) {
                confirmationMessage += "\nATTENTION : Tous les détails associés seront supprimés !";
            }
            int confirm = JOptionPane.showConfirmDialog(this, confirmationMessage, "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                String deleteDetailsQuery = "DELETE FROM DetailCommande WHERE idCommande = ?";
                try (PreparedStatement stmtDeleteDetails = conn.prepareStatement(deleteDetailsQuery)) {
                    stmtDeleteDetails.setInt(1, idCommandeToDelete);
                    stmtDeleteDetails.executeUpdate();
                }
                String deleteCommandeQuery = "DELETE FROM Commande WHERE idCommande = ?";
                try (PreparedStatement stmtDeleteCommande = conn.prepareStatement(deleteCommandeQuery)) {
                    stmtDeleteCommande.setInt(1, idCommandeToDelete);
                    int rowsAffected = stmtDeleteCommande.executeUpdate();
                    if (rowsAffected > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Commande ID " + idCommandeToDelete + " et détails supprimés.", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        updateTableCommandes();
                        ((DefaultTableModel) tableauDetailsCommandeAffichage.getModel()).setRowCount(0);
                        updateNombreCommandesLabel();
                    } else {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Erreur : Commande ID " + idCommandeToDelete + " non trouvée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                conn.rollback();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Erreur suppression: " + ex.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void afficherDetailsPourImpression(int idCommande) {
        StringBuilder detailsText = new StringBuilder();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try (Connection conn = DBConnection.getConnection()) {
            String queryCommande = "SELECT c.*, f.Nom AS FournisseurNom "
                    + "FROM Commande c "
                    + "LEFT JOIN Fournisseur f ON c.idFournisseur = f.idFournisseur "
                    + "WHERE c.idCommande = ?";
            try (PreparedStatement stmtCommande = conn.prepareStatement(queryCommande)) {
                stmtCommande.setInt(1, idCommande);
                try (ResultSet rsCommande = stmtCommande.executeQuery()) {
                    if (rsCommande.next()) {
                        detailsText.append("====================================\n");
                        detailsText.append("         BON DE COMMANDE\n");
                        detailsText.append("====================================\n\n");
                        detailsText.append("COMMANDE N° : ").append(rsCommande.getInt("idCommande")).append("\n");
                        detailsText.append("Date Commande : ").append(rsCommande.getTimestamp("DateCommande") != null ? dateTimeFormat.format(rsCommande.getTimestamp("DateCommande")) : "N/A").append("\n");
                        detailsText.append("N° Bon        : ").append(rsCommande.getObject("NumeroBonCommande") != null ? rsCommande.getInt("NumeroBonCommande") : "N/A").append("\n");
                        detailsText.append("Année Bon     : ").append(rsCommande.getObject("AnneeBonCommande") != null ? rsCommande.getString("AnneeBonCommande") : "N/A").append("\n\n");
                        detailsText.append("--- Fournisseur ---\n");
                        detailsText.append("Nom           : ").append(rsCommande.getString("FournisseurNom") != null ? rsCommande.getString("FournisseurNom") : "N/A").append("\n\n");
                        detailsText.append("--- Statut Commande ---\n");
                        detailsText.append("Validé        : ").append(rsCommande.getBoolean("Valide") ? "Oui" : "Non").append("\n");
                        detailsText.append("Payé          : ").append(rsCommande.getBoolean("Payer") ? "Oui" : "Non").append("\n\n");
                        detailsText.append("--- Informations Paiement ---\n");
                        detailsText.append("Mode Paiement : ").append(rsCommande.getString("ModePaiement") != null ? rsCommande.getString("ModePaiement") : "N/A").append("\n");
                        detailsText.append("Date Paiement : ").append(rsCommande.getDate("DatePaiement") != null ? dateFormat.format(rsCommande.getDate("DatePaiement")) : "N/A").append("\n");
                        detailsText.append("Infos Paiement: ").append(rsCommande.getString("InfosPaiement") != null ? rsCommande.getString("InfosPaiement") : "N/A").append("\n");
                        detailsText.append("Prix HT       : ").append(String.format("%.2f", rsCommande.getFloat("PrixHT"))).append("\n");
                        detailsText.append("Prix TVA      : ").append(String.format("%.2f", rsCommande.getFloat("PrixTVA"))).append("\n");
                        detailsText.append("Prix TTC      : ").append(String.format("%.2f", rsCommande.getFloat("PrixTTC"))).append("\n\n");
                        detailsText.append("--- Détails des Produits Commandés ---\n");
                        detailsText.append("--------------------------------------------------------------------------\n");
                        detailsText.append(String.format("%-10s | %-25s | %-7s | %-12s | %-10s\n", "ID Détail", "Produit", "Qté", "Prix Unit.", "Total Ligne"));
                        detailsText.append("--------------------------------------------------------------------------\n");
                    } else {
                        JOptionPane.showMessageDialog(this, "Commande non trouvée: " + idCommande, "Non Trouvé", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            String queryDetails = "SELECT dc.*, p.Nom AS ProduitNom "
                    + "FROM DetailCommande dc "
                    + "LEFT JOIN Produit p ON dc.idProduit = p.idProduit "
                    + "WHERE dc.idCommande = ? ORDER BY dc.idDetailCommande";
            try (PreparedStatement stmtDetails = conn.prepareStatement(queryDetails)) {
                stmtDetails.setInt(1, idCommande);
                boolean detailsFound = false;
                try (ResultSet rsDetails = stmtDetails.executeQuery()) {
                    while (rsDetails.next()) {
                        detailsFound = true;
                        float qte = rsDetails.getFloat("QuantiteCommande");
                        float prixU = rsDetails.getFloat("PrixUnitaire");
                        detailsText.append(String.format("%-10s | %-25s | %-7.2f | %-12.2f | %-10.2f\n",
                                rsDetails.getString("idDetailCommande") != null ? rsDetails.getString("idDetailCommande") : "N/A",
                                rsDetails.getString("ProduitNom") != null ? rsDetails.getString("ProduitNom") : "N/A",
                                qte, prixU, (qte * prixU)));
                        String expDate = rsDetails.getDate("DateExpiration") != null ? dateFormat.format(rsDetails.getDate("DateExpiration")) : "N/A";
                        String fabDate = rsDetails.getDate("DateFabrication") != null ? dateFormat.format(rsDetails.getDate("DateFabrication")) : "N/A";
                        detailsText.append(String.format("           | Exp: %-23s | Fab: %s\n", expDate, fabDate));
                        detailsText.append("--------------------------------------------------------------------------\n");
                    }
                }
                if (!detailsFound) {
                    detailsText.append("Aucun détail de produit trouvé pour cette commande.\n");
                    detailsText.append("--------------------------------------------------------------------------\n");
                }
            }
            detailsText.append("\n====================================\n");

            // Print the content using PrinterJob
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                Font font = new Font("Monospaced", Font.PLAIN, 12);
                g2d.setFont(font);
                g2d.setColor(Color.BLACK);
                String[] lines = detailsText.toString().split("\n");
                int y = 0;
                for (String line : lines) {
                    g2d.drawString(line, 0, y += g2d.getFontMetrics().getHeight());
                    if (y > pageFormat.getImageableHeight()) {
                        break;
                    }
                }
                return Printable.PAGE_EXISTS;
            });

            if (printerJob.printDialog()) {
                try {
                    printerJob.print();
                    JOptionPane.showMessageDialog(this, "Bon de commande imprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'impression: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur impression: " + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateNombreCommandesLabel() {
        if (labelNombreCommandes != null) {
            labelNombreCommandes.setText(String.valueOf(getNombreCommandesFromDB()));
        }
    }

    private int getNombreCommandesFromDB() {
        int count = 0;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Commande"); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private void clearFieldsCommandes() {
        if (champIdCommande != null) {
            champIdCommande.setText("");
        }
        if (dateCommandeChooser != null) {
            dateCommandeChooser.setDate(null);
        }
        if (comboValideCommande != null) {
            comboValideCommande.setSelectedIndex(0);
        }
        if (comboPayeeCommande != null) {
            comboPayeeCommande.setSelectedIndex(0);
        }
        if (champAnneeBonCommande != null) {
            champAnneeBonCommande.setText("");
        }
        if (champNumeroBonCommande != null) {
            champNumeroBonCommande.setText("");
        }
        if (comboFournisseur != null) {
            comboFournisseur.setSelectedIndex(0);
        }
        if (comboModePaiementCommande != null) {
            comboModePaiementCommande.setSelectedIndex(-1);
        }
        if (champInfosPaiementCommande != null) {
            champInfosPaiementCommande.setText("");
        }
        if (champPrixTTCCommande != null) {
            champPrixTTCCommande.setText("");
        }
        if (champPrixTVACommande != null) {
            champPrixTVACommande.setText("");
        }
        if (champPrixHTCommande != null) {
            champPrixHTCommande.setText("");
        }
        if (datePaiementChooserCommande != null) {
            datePaiementChooserCommande.setDate(null);
        }
        clearLigneDetailFields();
        if (detailsEnCoursList != null) {
            detailsEnCoursList.clear();
        }
        if (modelDetailsEnCoursSaisie != null) {
            modelDetailsEnCoursSaisie.setRowCount(0);
        }
        if (comboProduitDetail != null) {
            comboProduitDetail.repaint();
            if (comboProduitDetail.getItemCount() > 0) {
                lastValideComboProduitSelection = comboProduitDetail.getItemAt(0);
                comboProduitDetail.setSelectedIndex(0);
            }
        }
        if (boutonSauvegarderCommande != null) {
            boutonSauvegarderCommande.setEnabled(false);
            boutonSauvegarderCommande.setBackground(couleurSauvegarderInitialeCommande);
        }
        if (dateCommandeChooser != null) {
            dateCommandeChooser.requestFocusInWindow();
        }
        if (tabbedPaneCommandes != null) {
            tabbedPaneCommandes.setSelectedIndex(0);
        }
    }

    private void clearLigneDetailFields() {
        if (champIdDetailCommandeSaisie != null) {
            champIdDetailCommandeSaisie.setText("");
        }
        if (champQuantiteCommandeDetail != null) {
            champQuantiteCommandeDetail.setText("");
        }
        if (champPrixAchatCommandeDetail != null) {
            champPrixAchatCommandeDetail.setText("");
        }
        if (dateExpirationChooserDetail != null) {
            dateExpirationChooserDetail.setDate(null);
        }
        if (dateFabricationChooserDetail != null) {
            dateFabricationChooserDetail.setDate(null);
        }
        if (comboProduitDetail != null && comboProduitDetail.getItemCount() > 0) {
            comboProduitDetail.setSelectedIndex(0);
        }
    }

    private void ajouterLigneDetailEnCours() {
        Object currentProduitSelection = comboProduitDetail.getSelectedItem();
        if (currentProduitSelection == null || currentProduitSelection.toString().equals(PROMPT_SELECTIONNER)) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Erreur Saisie Détail", JOptionPane.WARNING_MESSAGE);
            comboProduitDetail.requestFocusInWindow();
            return;
        }
        String produitNom = currentProduitSelection.toString();
        int idProduit = getProduitIdFromName(produitNom);
        if (idProduit == -1) {
            JOptionPane.showMessageDialog(this, "Produit invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (DetailCommandeData existingDetail : detailsEnCoursList) {
            if (existingDetail.idProduit == idProduit) {
                JOptionPane.showMessageDialog(this, "Ce produit est déjà dans la liste. Impossible de l'ajouter à nouveau.", "Produit Dupliqué", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (champQuantiteCommandeDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La quantité est obligatoire.", "Erreur Saisie Détail", JOptionPane.WARNING_MESSAGE);
            champQuantiteCommandeDetail.requestFocusInWindow();
            return;
        }
        if (champPrixAchatCommandeDetail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le prix d'achat est obligatoire.", "Erreur Saisie Détail", JOptionPane.WARNING_MESSAGE);
            champPrixAchatCommandeDetail.requestFocusInWindow();
            return;
        }
        try {
            String idDetailStr = champIdDetailCommandeSaisie.getText().trim();
            float quantite = Float.parseFloat(champQuantiteCommandeDetail.getText().trim());
            float prixAchat = Float.parseFloat(champPrixAchatCommandeDetail.getText().trim());
            Date dateExp = dateExpirationChooserDetail.getDate();
            Date dateFab = dateFabricationChooserDetail.getDate();
            DetailCommandeData detailData = new DetailCommandeData(idDetailStr, produitNom, idProduit, quantite, prixAchat, dateExp, dateFab);
            detailsEnCoursList.add(detailData);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            modelDetailsEnCoursSaisie.addRow(new Object[]{
                detailData.idDetail,
                detailData.produitNom,
                detailData.quantite,
                detailData.prixAchat,
                detailData.dateExpiration != null ? sdf.format(detailData.dateExpiration) : null,
                detailData.dateFabrication != null ? sdf.format(detailData.dateFabrication) : null
            });
            comboProduitDetail.repaint();
            clearLigneDetailFields();
            comboProduitDetail.requestFocusInWindow();
            checkFormCompletion();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantité ou Prix invalide.", "Erreur Format", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerLigneDetailEnCours() {
        int selectedRow = tableauDetailsEnCoursSaisie.getSelectedRow();
        if (selectedRow != -1) {
            detailsEnCoursList.remove(selectedRow);
            modelDetailsEnCoursSaisie.removeRow(selectedRow);
            comboProduitDetail.repaint();
            checkFormCompletion();
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne à supprimer.", "Aucune Sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAllData() {
        if (dateCommandeChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Date commande obligatoire.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(0);
            dateCommandeChooser.requestFocusInWindow();
            return;
        }
        if (champAnneeBonCommande.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Année Bon Commande obligatoire.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(0);
            champAnneeBonCommande.requestFocusInWindow();
            return;
        }
        if (champNumeroBonCommande.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Numéro Bon Commande obligatoire.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(0);
            champNumeroBonCommande.requestFocusInWindow();
            return;
        }
        if (comboFournisseur.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Fournisseur obligatoire.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(0);
            comboFournisseur.requestFocusInWindow();
            return;
        }
        if (champPrixTTCCommande.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Prix TTC obligatoire.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(1);
            champPrixTTCCommande.requestFocusInWindow();
            return;
        }
        if (detailsEnCoursList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Une commande doit avoir au moins un produit détaillé.", "Erreur Saisie", JOptionPane.WARNING_MESSAGE);
            tabbedPaneCommandes.setSelectedIndex(2);
            comboProduitDetail.requestFocusInWindow();
            return;
        }

        if (!champIdCommande.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mise à jour non implémentée. Utilisez 'Ajouter Commande'.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int generatedCommandeId;

            // Insert into Commande table
            String queryCommande = "INSERT INTO Commande (DateCommande, Valide, Payer, AnneeBonCommande, NumeroBonCommande, idFournisseur, "
                    + "ModePaiement, DatePaiement, InfosPaiement, PrixHT, PrixTVA, PrixTTC) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtCommande = conn.prepareStatement(queryCommande, Statement.RETURN_GENERATED_KEYS)) {
                stmtCommande.setTimestamp(1, new Timestamp(dateCommandeChooser.getDate().getTime()));
                stmtCommande.setBoolean(2, "Oui".equals(comboValideCommande.getSelectedItem()));
                stmtCommande.setBoolean(3, "Oui".equals(comboPayeeCommande.getSelectedItem()));
                try {
                    stmtCommande.setInt(4, Integer.parseInt(champAnneeBonCommande.getText().trim()));
                } catch (NumberFormatException nfe) {
                    throw new SQLException("Année Bon Commande '" + champAnneeBonCommande.getText() + "' invalide.", nfe);
                }
                try {
                    stmtCommande.setInt(5, Integer.parseInt(champNumeroBonCommande.getText().trim()));
                } catch (NumberFormatException nfe) {
                    throw new SQLException("Numéro Bon Commande '" + champNumeroBonCommande.getText() + "' invalide.", nfe);
                }

                String selectedFournisseurName = (String) comboFournisseur.getSelectedItem();
                int fournisseurId = getFournisseurIdFromName(selectedFournisseurName);
                if (fournisseurId == -1) {
                    throw new SQLException("ID Fournisseur non trouvé pour: " + selectedFournisseurName);
                }
                stmtCommande.setInt(6, fournisseurId);

                stmtCommande.setString(7, (String) comboModePaiementCommande.getSelectedItem());
                if (datePaiementChooserCommande.getDate() != null) {
                    stmtCommande.setDate(8, new java.sql.Date(datePaiementChooserCommande.getDate().getTime()));
                } else {
                    stmtCommande.setNull(8, Types.DATE);
                }

                String infosStr = champInfosPaiementCommande.getText().trim();
                if (infosStr.isEmpty()) {
                    stmtCommande.setNull(9, Types.VARCHAR);
                } else {
                    stmtCommande.setString(9, infosStr);
                }

                try {
                    stmtCommande.setFloat(10, Float.parseFloat(champPrixHTCommande.getText().trim()));
                } catch (NumberFormatException nfe) {
                    throw new SQLException("Prix HT '" + champPrixHTCommande.getText() + "' invalide.", nfe);
                }
                try {
                    String tvaStr = champPrixTVACommande.getText().trim();
                    if (tvaStr.isEmpty()) {
                        stmtCommande.setNull(11, Types.FLOAT);
                    } else {
                        stmtCommande.setFloat(11, Float.parseFloat(tvaStr));
                    }
                } catch (NumberFormatException nfe) {
                    throw new SQLException("Prix TVA '" + champPrixTVACommande.getText() + "' invalide.", nfe);
                }
                try {
                    stmtCommande.setFloat(12, Float.parseFloat(champPrixTTCCommande.getText().trim()));
                } catch (NumberFormatException nfe) {
                    throw new SQLException("Prix TTC '" + champPrixTTCCommande.getText() + "' invalide.", nfe);
                }

                int affectedRows = stmtCommande.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("La création de la commande a échoué, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = stmtCommande.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedCommandeId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("La création de la commande a échoué, impossible d'obtenir l'ID généré.");
                    }
                }
            }

            // Insert into DetailCommande table
            String queryDetailCommande = "INSERT INTO DetailCommande (idDetailCommande, QuantiteCommande, PrixUnitaire, idProduit, DateExpiration, DateFabrication, idCommande) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtDetailCommande = conn.prepareStatement(queryDetailCommande)) {
                for (DetailCommandeData detailData : detailsEnCoursList) {
                    String idDetailStr = detailData.idDetail;
                    if (idDetailStr == null || idDetailStr.isEmpty()) {
                        stmtDetailCommande.setNull(1, Types.VARCHAR);
                    } else {
                        stmtDetailCommande.setString(1, idDetailStr);
                    }
                    stmtDetailCommande.setFloat(2, detailData.quantite);
                    stmtDetailCommande.setFloat(3, detailData.prixAchat);
                    stmtDetailCommande.setInt(4, detailData.idProduit);
                    if (detailData.dateExpiration != null) {
                        stmtDetailCommande.setDate(5, new java.sql.Date(detailData.dateExpiration.getTime()));
                    } else {
                        stmtDetailCommande.setNull(5, Types.DATE);
                    }
                    if (detailData.dateFabrication != null) {
                        stmtDetailCommande.setDate(6, new java.sql.Date(detailData.dateFabrication.getTime()));
                    } else {
                        stmtDetailCommande.setNull(6, Types.DATE);
                    }
                    stmtDetailCommande.setInt(7, generatedCommandeId);
                    stmtDetailCommande.addBatch();
                }
                stmtDetailCommande.executeBatch();
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Commande (ID: " + generatedCommandeId + ") et ses " + detailsEnCoursList.size() + " détails sauvegardés!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            champIdCommande.setText(String.valueOf(generatedCommandeId));
            updateTableCommandes();
            updateNombreCommandesLabel();
            clearFieldsCommandes();
        } catch (SQLException e) {
            String userMessage = e.getMessage().contains("invalide") ? e.getMessage() : "❌ Erreur lors de la sauvegarde des données SQL.";
            JOptionPane.showMessageDialog(this, userMessage + "\n" + e.getClass().getSimpleName() + ": " + e.getMessage(), "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Erreur inattendue: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private int getFournisseurIdFromName(String name) {
        if (name == null || name.equals(PROMPT_SELECTIONNER)) {
            return -1;
        }
        int id = -1;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT idFournisseur FROM Fournisseur WHERE Nom = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("idFournisseur");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur ID fournisseur pour: " + name + "\n" + e.getMessage(), "Erreur DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return id;
    }

    private int getProduitIdFromName(String name) {
        if (name == null || name.equals(PROMPT_SELECTIONNER)) {
            return -1;
        }
        int id = -1;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT idProduit FROM Produit WHERE Nom = ?")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getInt("idProduit");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur ID produit pour: " + name + "\n" + e.getMessage());
        }
        return id;
    }

    private JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(new Color(240, 240, 240));
        button.setFont(new Font("Roboto", Font.BOLD, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        Color originalColor = backgroundColor;
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor.brighter());
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor.darker());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    if (e.getComponent().contains(e.getPoint())) {
                        button.setBackground(originalColor.brighter());
                    } else {
                        button.setBackground(originalColor);
                    }
                }
            }
        });
        button.addPropertyChangeListener("enabled", evt -> {
            if (!(Boolean) evt.getNewValue()) {
                button.setBackground(new Color(180, 180, 180));
                button.setForeground(new Color(100, 100, 100));
            } else {
                button.setBackground(originalColor);
                button.setForeground(new Color(240, 240, 240));
            }
        });
        return button;
    }

    class NumberOnlyFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    class NumberWithDecimalFilter extends DocumentFilter {

        private final String regex = "\\d*\\.?\\d*";

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("[0-9]*\\.?[0-9]*")) {
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (newText.matches(regex)) {
                    super.insertString(fb, offset, string, attr);
                }
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text != null && text.matches("[0-9]*\\.?[0-9]*")) {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newText.matches(regex) && (newText.indexOf('.') == newText.lastIndexOf('.') || newText.indexOf('.') == -1)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        }
    }
}
