/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestion.de.produit.Logique;

/**
 *
 * @author rahim
 */
public class Authontification {

    public static String STOCK = "stock.prod";
    public static String PROD = "prod.prod";
    public static String ACH = "achat.prod";
    public static String VENT = "vent.prod";
    public static String PAIE = "paie.prod";

    public static boolean[] access
            = {
                false, // produit 0
                false, // production 1
                false, // commande 2
                false, // vante 3
                false, // client 4
                false, // fournisseur 5
                false // statistique 6
            };

    public static void login(String role) {
        if(role.equals(STOCK))
        {
            System.out.println("entered");
            access[2]= true;
            access[1]= true;
            access[6]= true;
            //access[0]= true;
        }
        else if (role.equals(PROD)){
            access[1]= true;
            access[0]= true;
            access[1]= true;
        }
        else if (role.equals ( ACH)){
            access[2]= true;
            access[5]= true;
            access[1]= true;
        }
        else if (role.equals(VENT)){
            access[0]= true;
            access[1]= true;
            access[2]= true;
            access[3]= true;
            access[4]= true;
            access[5]= true;
            access[6]= true;
        }
        else if (role.equals(PAIE)){
            access[2]= true;
            //access[0]= true;
        }
    }

}
