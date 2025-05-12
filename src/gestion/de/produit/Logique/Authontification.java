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
                false, // statistique 6
                false, // Livraison 7
                    
                false, // produit acces complet , juste consultion sinon 8
                false, // production access complete , juste validation sinon 9 
                false, // livraison acces complete  10
                false, // livraison juste validation  11
                false, // livraison juste l'ajoute d'info paiment 12
                false, // commande access complete  13
                false, // commande juste validation 14
                false, // commande juste l'ajoute d'info paiment 15
                
                
                
            };

    public static void login(String role) {
        if(role.equals(STOCK))
        {
            //acces fenetre 
            access[0]= true;
            access[1]= true;
            access[7]= true;
            access[2]= true;
            //acces fonctionement
            access[11]= true;
            access[14]= true;
        }
        else if (role.equals(PROD)){
            access[1]= true;
            access[0]= true;
            
            access[8]= true;
            access[9]= true;
        }
        else if (role.equals ( ACH)){
            access[0]= true;
            access[2]= true;
            access[5]= true;
            
            access[8]= true;
            access[13]= true;
            access[15]= true;
        }
        else if (role.equals(VENT)){
            access[4]= true;
            access[7]= true;
            
            access[10]= true;
        }
        else if (role.equals(PAIE)){
            access[2]= true;
            access[7]= true;
            
            access[12]= true;
            access[15]= true;
        }
    }

}
