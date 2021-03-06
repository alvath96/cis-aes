package GUI;

import AES.AESService;
import AES.AESServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Main GUI Controller
 */
public class Controller {

    final Stage stage;
    final FileChooser fileChooser;
    final ConfigManager configManager;
    final AESService aesService;
    Config config;

    boolean isKeyEncryptValid;
    boolean isKeyDecryptValid;

    @FXML TextField textFieldEncryptPlainText;
    @FXML TextField textFieldEncryptKey;
    @FXML TextField textFieldEncryptCipherText;
    @FXML TextField textFieldDecryptPlainText;
    @FXML TextField textFieldDecryptKey;
    @FXML TextField textFieldDecryptCipherText;
    @FXML TextField textFieldIV;
    @FXML Label textFieldEncryptKeyInfo;
    @FXML Label textFieldDecryptKeyInfo;
    @FXML Label textFieldEncryptInputInfo;
    @FXML Label textFieldDecryptInputInfo;

    File filePlainTextInEncrypt;
    File fileKeyInEncrypt;
    File fileCipherTextInEncrypt;
    File filePlainTextInDecrypt;
    File fileKeyInDecrypt;
    File fileCipherTextInDecrypt;

    public Controller() {
        stage = new Stage();
        fileChooser = new FileChooser();
        configManager = new ConfigManager();
        aesService = new AESServiceImpl();
    }

    private void setIV(byte[] iv) {
        this.textFieldIV.setText(Utils.byteToHexString(iv));
        ((AESServiceImpl) this.aesService).setNonce(iv);
    }

    public void initConfig(Config config) {
        this.config = config;
        this.setIV(config.getIV());
    }

    @FXML
    public void initialize() {

    }

    private String humanReadableByteCount(long bytes) {
        int unit = 1000;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "kMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private boolean checkKeyValid(String key) {
        return (key.matches("^[0-9a-fA-F]+$") &&
                (key.length() == (128 / 4) || key.length() == (192 / 4) || key.length() == (256 / 4)));
    }

    private boolean checkIVValid(String iv) {
        return (iv.matches("^[0-9a-fA-F]+$"));
    }

    private String generateKeyInfoEncrypt(File fileKey) {
        try {
            String key = Files.readAllLines(fileKey.toPath()).get(0);
            isKeyEncryptValid = checkKeyValid(key);
            if (isKeyEncryptValid) {
                return String.format("Key: %s\nKey Length: %d bit", key, key.length() * 4);
            } else {
                return "Key invalid";
            }
        } catch (IOException e) {
            return "Key invalid";
        }
    }

    private String generateKeyInfoDecrypt(File fileKey) {
        try {
            String key = Files.readAllLines(fileKey.toPath()).get(0);
            isKeyDecryptValid = checkKeyValid(key);
            if (isKeyDecryptValid) {
                return String.format("Key: %s\nKey Length: %d bit", key, key.length() * 4);
            } else {
                return "Key invalid";
            }
        } catch (IOException e) {
            return "Key invalid";
        }
    }

    private String generateInputInfo(File file) {
        String type;
        try {
            type = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            type = "Unrecognized";
        }
        return String.format("File size: %s , File type: %s", humanReadableByteCount(file.length()), type);
    }

    public void doChoosePlainTextInEncrypt(ActionEvent actionEvent) {
        filePlainTextInEncrypt = fileChooser.showOpenDialog(stage);
        if (filePlainTextInEncrypt != null) {
            textFieldEncryptPlainText.setText(filePlainTextInEncrypt.getAbsolutePath());
            textFieldEncryptInputInfo.setText(generateInputInfo(filePlainTextInEncrypt));
        }
    }

    public void doChooseKeyInEncrypt(ActionEvent actionEvent) {
        fileKeyInEncrypt = fileChooser.showOpenDialog(stage);
        if (fileKeyInEncrypt != null) {
            textFieldEncryptKey.setText(fileKeyInEncrypt.getAbsolutePath());
            textFieldEncryptKeyInfo.setText(generateKeyInfoEncrypt(fileKeyInEncrypt));
        }
    }

    public void doChooseCipherTextInEncrypt(ActionEvent actionEvent) {
        fileCipherTextInEncrypt = fileChooser.showSaveDialog(stage);
        if (fileCipherTextInEncrypt != null) {
            textFieldEncryptCipherText.setText(fileCipherTextInEncrypt.getAbsolutePath());
        }
    }

    public void doChoosePlainTextInDecrypt(ActionEvent actionEvent) {
        filePlainTextInDecrypt = fileChooser.showSaveDialog(stage);
        if (filePlainTextInDecrypt != null) {
            textFieldDecryptPlainText.setText(filePlainTextInDecrypt.getAbsolutePath());
        }
    }

    public void doChooseKeyInDecrypt(ActionEvent actionEvent) {
        fileKeyInDecrypt = fileChooser.showOpenDialog(stage);
        if (fileKeyInDecrypt != null) {
            textFieldDecryptKey.setText(fileKeyInDecrypt.getAbsolutePath());
            textFieldDecryptKeyInfo.setText(generateKeyInfoDecrypt(fileKeyInDecrypt));
        }
    }

    public void doChooseCipherTextInDecrypt(ActionEvent actionEvent) {
        fileCipherTextInDecrypt = fileChooser.showOpenDialog(stage);
        if (fileCipherTextInDecrypt != null) {
            textFieldDecryptCipherText.setText(fileCipherTextInDecrypt.getAbsolutePath());
            textFieldDecryptInputInfo.setText(generateInputInfo(fileCipherTextInDecrypt));
        }
    }

    public void doChangeIV(ActionEvent actionEvent) {
        String newIV = textFieldIV.getText();
        if (checkIVValid(newIV)) {
            this.config.setIV(Utils.stringToByteArray(textFieldIV.getText()));
            this.configManager.saveConfig(this.config);
            this.setIV(Utils.stringToByteArray(textFieldIV.getText()));
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Change Nonce Failed");
            alert.setContentText("Please input Hexadecimal numbers");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        }
    }

    public void doResetIV(ActionEvent actionEvent) {
        this.textFieldIV.setText(Utils.byteToHexString(config.getIV()));
    }

    public void doEncrypt(ActionEvent actionEvent) {
        if (filePlainTextInEncrypt == null || fileKeyInEncrypt == null || fileCipherTextInEncrypt == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values!");
            alert.setContentText("Encryption failed, all necessary fields are not filled");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
            return;
        }

        try {
            aesService.encryptFile(filePlainTextInEncrypt, fileKeyInEncrypt, fileCipherTextInEncrypt);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Encrypt Success");
            alert.setContentText(
                    String.format("Encryption success, encrypted file is outputted to %s",
                            fileCipherTextInEncrypt.getAbsolutePath()));
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Encrypt failed");
            alert.setContentText("Encryption failed, File IO error found");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Encrypt failed");
            alert.setContentText("Encryption failed, Wrong length of key");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        }
        clearAll();
    }

    public void doDecrypt(ActionEvent actionEvent) {
        if (filePlainTextInDecrypt == null || fileKeyInDecrypt == null || fileCipherTextInDecrypt == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Values!");
            alert.setContentText("Decryption failed, all necessary fields are not filled");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
            return;
        }

        try {
            aesService.decryptFile(fileCipherTextInDecrypt, fileKeyInDecrypt, filePlainTextInDecrypt);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Decrypt Success");
            alert.setContentText(
                    String.format("Decryption success, decrypted file is outputted to %s",
                            filePlainTextInDecrypt.getAbsolutePath()));
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Decrypt failed");
            alert.setContentText("Decryption failed, File IO error found");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Decrypt failed");
            alert.setContentText("Decryption failed, Nonce used might be invalid");
            alert.getDialogPane().setStyle("-fx-pref-height: 200px;");
            alert.showAndWait();
        }
        clearAll();
    }

    public void clearAll() {
        filePlainTextInEncrypt = null;
        fileKeyInEncrypt = null;
        fileCipherTextInEncrypt = null;
        filePlainTextInDecrypt = null;
        fileKeyInDecrypt = null;
        fileCipherTextInDecrypt = null;

        textFieldEncryptPlainText.setText("");
        textFieldEncryptKey.setText("");
        textFieldEncryptCipherText.setText("");
        textFieldDecryptPlainText.setText("");
        textFieldDecryptKey.setText("");
        textFieldDecryptCipherText.setText("");
        textFieldEncryptKeyInfo.setText("");
        textFieldDecryptKeyInfo.setText("");
        textFieldEncryptInputInfo.setText("");
        textFieldDecryptInputInfo.setText("");
    }
}
