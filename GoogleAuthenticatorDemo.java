package org.asaph.twofactorauth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.ietf.tools.TOTP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class GoogleAuthenticatorDemo {

    public static String getRandomSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secretKey = base32.encodeToString(bytes);
        return secretKey.toLowerCase().replaceAll("(.{4})(?=.{4})", "$1 ");
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        try {
            return "otpauth://totp/"
                + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
                + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width)
                throws WriterException, IOException {
        // Ensure the directory exists
        File qrCodeDir = new File(filePath).getParentFile();
        if (!qrCodeDir.exists()) {
            qrCodeDir.mkdirs(); // Create directories if they don't exist
        }

        // Generate the QR code
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

    public static String getTOTPCode(String secretKey) {
        String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(normalizedBase32Key);
        String hexKey = Hex.encodeHexString(bytes);
        long time = (System.currentTimeMillis() / 1000) / 30;
        String hexTime = Long.toHexString(time);
        return TOTP.generateTOTP(hexKey, hexTime, "6");
    }

    // Method to write the user details to the file
    public static void saveUserToFile(String username, String password, String secretKey, String filePath) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(username + ":" + password + ":" + secretKey + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to validate login credentials and get the secret key for OTP generation
    public static String validateLogin(String username, String password, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(":");
                if (userDetails.length == 3 && userDetails[0].equals(username) && userDetails[1].equals(password)) {
                    return userDetails[2]; // Return the secret key for OTP generation
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        String currentDir = System.getProperty("user.dir");
        String filePath = currentDir + File.separator + "totp_codes.txt"; // File to store the generated TOTP codes

        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Define the path to save QR codes
        String qrCodeFolderPath = currentDir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "images" + File.separator;

        if (option == 1) {
            // User Registration
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String secretKey = getRandomSecretKey();
            String barCode = getGoogleAuthenticatorBarCode(secretKey, username, "Example Company");

            // Save user details to file
            saveUserToFile(username, password, secretKey, filePath);

            // Generate QR code with username in the filename
            String qrCodePath = qrCodeFolderPath + username + "-2fa-qr-code.png";
            createQRCode(barCode, qrCodePath, 400, 400);

            System.out.println("\nUser registered successfully!");
            System.out.println("Please scan the QR code or enter the secret key manually in the Google Authenticator app.");
            System.out.println("QR Code saved at: " + qrCodePath);

        } else if (option == 2) {
            // User Login
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String secretKey = validateLogin(username, password, filePath);

            if (secretKey == null) {
                System.out.println("Invalid username or password!");
            } else {
                System.out.println("Enter the OTP generated by Google Authenticator:");
                String userOtp = scanner.nextLine();

                String generatedOtp = getTOTPCode(secretKey);
                if (generatedOtp.equals(userOtp)) {
                    System.out.println("OTP verified successfully!");
                } else {
                    System.out.println("Invalid OTP!");
                }
            }
        } else {
            System.out.println("Invalid option!");
        }

        scanner.close();
    }
}
