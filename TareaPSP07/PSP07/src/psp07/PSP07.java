package psp07;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author HugoGuillin
 */
public class PSP07 {

    private static String archivo = "fichero";
    private static String usuario = "Fulanito";
    private static String contrasena = "abc123.";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        SecretKey clave;
        
        crearArchivo(archivo);
        clave = cifrarArchivo(archivo);
        descifrarFichero(archivo+".cifrado", clave, archivo+".txt");
    }
    
    public static void crearArchivo(String archivo){
        File file = new File(archivo);
        FileOutputStream fos = null;
        
        try {
            fos = new FileOutputStream(file);
            String contenido = "Esto es un texto de ejemplo para rellenar un archivo con algo de contenido y poder cifrarlo";
            byte[] b = contenido.getBytes();
            fos.write(b);
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally{
            if(fos != null){
                try {                
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        
    }
    
    public static SecretKey cifrarArchivo(String archivo) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        SecretKey clave = null;
        
        System.out.println("1.-Generar número aleatorio");
        //Se genera la base para la semilla
        byte[] bytes = (usuario + contrasena).getBytes();       
        //Se genera un número aleatorio en base a una semilla
        SecureRandom sr = new SecureRandom();
        sr.setSeed(bytes);
        
        try{
            System.out.println("2.-Genera clave AES");
            //crea un objeto para generar la clave usando algoritmo AES
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128,sr); //se indica el tamaño de la clave
            clave = keyGen.generateKey(); //genera la clave privada


            //Se Crea el objeto Cipher para cifrar, utilizando el algoritmo DES
            Cipher cifrador = Cipher.getInstance("Rijndael/ECB/PKCS5Padding");
            //Se inicializa el cifrador en modo CIFRADO o ENCRIPTACIÓN
            cifrador.init(Cipher.ENCRYPT_MODE, clave);
            System.out.println("3.- Cifrar con AES el fichero: " + archivo
                    + ", y dejar resultado en " + archivo + ".cifrado");

            byte[] buffer = new byte[1000];
            byte[] cifrado;
            fis = new FileInputStream(archivo);
            fos = new FileOutputStream(archivo+".cifrado");
            int bytesLeidos = fis.read(buffer, 0, 1000);
            while (bytesLeidos != -1) {            
                cifrado = cifrador.update(buffer, 0, bytesLeidos);
                fos.write(cifrado);
                bytesLeidos = fis.read(buffer, 0, 1000);
            }

            cifrado = cifrador.doFinal();
            fos.write(cifrado);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }finally{
            if(fis != null){
                try {                
                    fis.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if(fos != null){
                try {                
                    fos.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
        return clave;
        
    }
    
    
    
    
    private static void descifrarFichero(String file1, SecretKey key, String file2){
        FileInputStream fe = null; //fichero de entrada
        FileOutputStream fs = null; //fichero de salida
        BufferedReader br = null;
        int bytesLeidos;
        
        try{
            Cipher cifrador = Cipher.getInstance("AES");
            //3.- Poner cifrador en modo DESCIFRADO o DESENCRIPTACIÓN
            cifrador.init(Cipher.DECRYPT_MODE, key);
            System.out.println("4.- Descifrar con AES el fichero: " + file1
                    + ", y dejar en  " + file2);
            fe = new FileInputStream(file1);
            fs = new FileOutputStream(file2);
            byte[] bufferClaro;
            byte[] buffer = new byte[1000]; //array de bytes
            //lee el fichero de 1k en 1k y pasa los fragmentos leidos al cifrador
            bytesLeidos = fe.read(buffer, 0, 1000);
            while (bytesLeidos != -1) {//mientras no se llegue al final del fichero
                //pasa texto cifrado al cifrador y lo descifra, asignándolo a bufferClaro
                bufferClaro = cifrador.update(buffer, 0, bytesLeidos);
                fs.write(bufferClaro); //Graba el texto claro en fichero
                bytesLeidos = fe.read(buffer, 0, 1000);
            }
            bufferClaro = cifrador.doFinal(); //Completa el descifrado
            fs.write(bufferClaro); //Graba el final del texto claro, si lo hay

            //Se muestra el contenido del archivo descifrado por pantalla
            System.out.println("5.-Contenido del archivo descifrado:");
            br = new BufferedReader(new FileReader(file2));
            do{
                System.out.println("\t"+br.readLine());
            }while((br.read()) != -1);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }finally{
        //cierra archivos
            if(fe != null){
                try {                
                    fe.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if(fs != null){
                try {
                    fs.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if(br != null){
                try {
                    br.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }         
}
