/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion.de.produit.interfaces;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author rahim
 */
public class CommandePanel extends JPanel {

    public CommandePanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("لوحة إدارة الطلبيات", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        add(label, BorderLayout.CENTER);
        setBackground(Color.WHITE);
    }
}
