import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.Security;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class KeyGeneration {

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        String clientId = "ID_DO_CLIENTE";

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(2048);

            KeyPair keyPair = keyGen.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
            MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
            com.mongodb.client.MongoClient mongoClient = MongoClients.create(settings);
            MongoDatabase database = mongoClient.getDatabase("database");
            MongoCollection<Document> collection = database.getCollection("chaves");

            Document doc = new Document("clienteId", clientId)
                    .append("publicKey", publicKey)
                    .append("privateKey", privateKey);

            collection.insertOne(doc);

            System.out.println("Chaves do cliente " + clientId + " foram armazenadas no banco de dados.");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Block {
    private int blockNumber;
    private String data;
    private String previousHash;
    private String hash;
    }

    public Block(int blockNumber, String data, String previousHash) {
        this.blockNumber = blockNumber;
        this.data = data;
        this.previousHash = previousHash;
        this.hash = calculateHash();

    }

     public void encryptData(String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        Key secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        this.encryptedData = Base64.getEncoder().encodeToString(cipher.doFinal(this.data.getBytes()));
    }

    public String getEncryptedData() {
        return this.encryptedData;
    }

    private String calculateHash() {
        return Integer.toString(blockNumber) + data + previousHash;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public String getData() {
        return data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getHash() {
        return hash;
    }

    public class SimpleBlockchain {

    private List<Block> blockchain;

        public Blockchain() {
        this.blockchain = new ArrayList<>();
        Block genesisBlock = new Block(0, "Genesis Data", "0");
        blockchain.add(genesisBlock);
    }

    public void addBlock(String data) {
        Block previousBlock = blockchain.get(blockchain.size() - 1);
        int newBlockNumber = previousBlock.getBlockNumber() + 1;
        String previousHash = previousBlock.getHash();

        Block newBlock = new Block(newBlockNumber, data, previousHash);
        blockchain.add(newBlock);
    }

    

    public void printBlockchain() {
        for (Block block : blockchain) {
            System.out.println("Block Number: " + block.getBlockNumber());
            System.out.println("Data: " + block.getData());
            System.out.println("Previous Hash: " + block.getPreviousHash());
            System.out.println("Hash: " + block.getHash());
            System.out.println("---------------------");
        }
    }

    public static void main(String[] args) {
        SimpleBlockchain blockchain = new SimpleBlockchain();

        blockchain.addBlock("Dados do Cliente 1");
        blockchain.addBlock("Dados do Cliente 2");
        blockchain.addBlock("Dados do Cliente 3");

        blockchain.printBlockchain();
    }
    public void storeDataInBlockchain(String clientData, String previousHash, String encryptionKey) {
        try {
            Block block = new Block(previousHash, clientData);
            block.encryptData(encryptionKey);
            blockchain.add(block);
            System.out.println("Dados do cliente armazenados na blockchain com criptografia AES.");
        } catch (Exception e) {
            System.out.println("Erro ao armazenar dados na blockchain: " + e.getMessage());
        }
    }
    }
}

public class TwoFactorAuthentication {

    public static boolean authenticateWithTwoFactors(String privateKey, byte[] facialBiometricData) {
        // Simulando...
        boolean facialRecognitionSuccessful = performFacialRecognition(facialBiometricData);

        boolean privateKeyValid = checkPrivateKey(privateKey);

        return facialRecognitionSuccessful && privateKeyValid;
    }

    public static boolean performFacialRecognition(byte[] facialBiometricData) {
        // Simulando...
        return true;
    }

    public static boolean checkPrivateKey(String privateKey) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Insira sua chave privada para autenticação: ");
        String userInput = scanner.nextLine();

        return userInput.equals(privateKey);
    }

    public static void main(String[] args) {
        String privateKey = "CHAVE_PRIVADA_DO_USUARIO";
        byte[] facialBiometricData = { /* Dados biométricos faciais, como uma imagem binária */ };

        boolean authenticated = authenticateWithTwoFactors(privateKey, facialBiometricData);

        if (authenticated) {
            System.out.println("Autenticação bem-sucedida com dois fatores!");
        } else {
            System.out.println("Falha na autenticação. Acesso negado!");
        }
    }
}
